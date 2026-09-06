# Architecture

## System overview

This is a single-module Spring Boot application: REST controllers accept JSON/multipart requests, delegate to service classes for business logic, and Spring Data JPA repositories persist to MySQL. Authentication is stateless — a client logs in once via `/api/auth/login`, gets back a JWT, and attaches it as `Authorization: Bearer <token>` on every subsequent call. A servlet filter (`JwtFilter`) validates that token on each request and populates Spring Security's context before the request reaches a controller. Uploaded car images are written to a local `/uploads` directory on the server's filesystem and served back through a dedicated endpoint rather than a CDN/object store. Outbound registration emails go through Gmail SMTP via Spring Mail.

This backend is the counterpart to a separate React frontend (`Car-Rental-Frontend`), which calls exactly the route prefixes documented below (`/api/auth`, `/api/cars`, `/api/bookings`, `/api/users/expired`) against a deployed instance of this service.

## Architecture diagram

```mermaid
flowchart LR
    subgraph Client
        FE[React Frontend]
    end

    subgraph "Spring Boot App"
        Filter[JwtFilter]
        Auth[AuthController]
        Cars[CarController]
        Bookings[BookingController]
        Users[UserController]
        SvcAuth[UserService / JwtService]
        SvcCar[CarService]
        SvcBooking[BookingService]
        SvcMail[EmailService]
        Disk[(Local /uploads dir)]
    end

    DB[(MySQL)]
    SMTP[[Gmail SMTP]]

    FE -->|HTTPS/JSON, Bearer JWT| Filter
    Filter --> Auth
    Filter --> Cars
    Filter --> Bookings
    Filter --> Users

    Auth --> SvcAuth
    Auth --> SvcMail
    Cars --> SvcCar
    Bookings --> SvcBooking
    Users --> SvcAuth

    SvcAuth --> DB
    SvcCar --> DB
    SvcCar --> Disk
    SvcBooking --> DB
    SvcMail --> SMTP
```

## Database schema

12 JPA entities map to 12 MySQL tables (confirmed against the schema dump in `output.sql`). Two additional entities (`Document`, `Place`) exist in code but have no repository or controller wiring them up — they're unused leftovers, omitted from the diagram below.

```mermaid
erDiagram
    USER ||--o{ CAR : owns
    USER ||--o{ BOOKING : "books as tenant"
    USER ||--o{ ADDRESS : has
    USER ||--o{ BILLING : has
    USER ||--o{ CARDDETAILS : has

    CAR }o--|| CARMODEL : "is a"
    CAR ||--o| CARSPECS : has
    CAR ||--o| CARPRICE : "priced by"
    CAR ||--o{ CARIMAGES : has
    CAR ||--o| ADDRESS : "located at"
    CAR ||--o{ BOOKING : "booked in"

    BOOKING ||--|| ORDER : "produces"
    BILLING ||--|| ORDER : "attached to"
    CARDDETAILS ||--o{ TRANSACTION : "charged via"
    TRANSACTION ||--|| ORDER : "settles"

    USER {
        bigint id PK
        string userName
        string email
        string password "BCrypt hash"
        string userType
    }
    CAR {
        bigint id PK
        bigint user_id FK
        int model_id FK
        string name
        string vin
        int year
    }
    CARMODEL {
        int id PK
        string brandName
        string modelName
        string body
        int seats
    }
    CARSPECS {
        int id PK
        int car_id FK
        string gearType
        string fuelType
        string drivetrain
    }
    CARPRICE {
        int id PK
        int car_id FK
        int amount
        int tax
        int refundableDeposit
    }
    CARIMAGES {
        bigint id PK
        bigint car_id FK
        string path
    }
    ADDRESS {
        bigint id PK
        bigint user_id FK
        bigint car_id FK
        string city
        string state
        int pinCode
    }
    BOOKING {
        int id PK
        int tenant_id FK
        int lessor_id FK
        bigint car_id FK
        date startDate
        date endDate
    }
    BILLING {
        int id PK
        int user_id FK
        string firstName
        string drivingLicenceNumber
    }
    CARDDETAILS {
        int id PK
        int user_id FK
        string cardNumber "stored in plaintext - see Known Issues"
        int cvv "stored in plaintext - see Known Issues"
    }
    TRANSACTION {
        int id PK
        int card_id FK
        string transactionNumber
        string status "SUCCESS or FAIL"
    }
    ORDER {
        int id PK
        string orderId "UUID"
        int transaction_id FK
        int billing_id FK
        int booking_id FK
    }
```

`CarModel`/`CarSpecs`/`CarPrice` are split out from `Car` so the same car model can vary independently in trim and pricing; `Booking`, `Billing`, `CardDetails`, `Transaction`, and `Order` are split into one row per concern of a single checkout rather than one wide table — `Order` is effectively the join record that ties a booking to its billing info and payment transaction. See `output.sql` for the full column list, including the Hibernate-generated `*_seq` sequence tables.

## Folder-by-folder breakdown

| Path | Contents | Why |
|---|---|---|
| `config/` | `SecurityConfig` (JWT filter chain, permit-all routes), `WebConfig` (global CORS), `MailConfig` (SMTP bean) | Cross-cutting setup that isn't tied to one resource |
| `controller/` | `AuthController`, `CarController`, `BookingController`, `UserController`, `TestController` | One `@RestController` per resource; thin — they extract the authenticated username from the request and hand off to a service |
| `model/table/` | 12 `@Entity` classes (`User`, `Car`, `CarModel`, `CarSpecs`, `CarPrice`, `CarImages`, `Address`, `Booking`, `Billing`, `CardDetails`, `Transaction`, `Order`) plus 2 unused (`Document`, `Place`) | This *is* the database schema — Hibernate generates/updates tables from these via `spring.jpa.hibernate.ddl-auto=update` |
| `model/requestModel/` | `CarRequest`/`CarResponse`/`CarResponseWithUser`, `BookingRequest`/`BookingResponse` | DTOs that shape the multipart/JSON request and response bodies separately from the persistence entities |
| `model/UserPrincipal.java` | Adapts `User` to Spring Security's `UserDetails` | Bridges the JPA entity into the auth framework |
| `repository/` | One Spring Data JPA interface per entity (11 repositories; `Document`/`Place` have none) | Standard repository-per-aggregate pattern; a few (e.g. `CarImagesRepository`, `CarRepository`) add custom finder methods |
| `service/` | `UserService` (register/update/lookup), `JwtService` (token issue/validate), `JwtFilter` (per-request auth), `MyUserDetailsService` (Spring Security lookup), `CarService` (car CRUD + image upload to disk), `BookingService` (multi-entity checkout), `EmailService` (registration email) | Business logic layer; controllers stay thin |

## Request lifecycle: creating a booking

The booking flow is the most representative multi-step operation in the app — it touches five tables in one call.

```mermaid
sequenceDiagram
    actor User
    participant FE as Frontend
    participant Filter as JwtFilter
    participant BC as BookingController
    participant BS as BookingService
    participant DB as MySQL

    User->>FE: Submits booking + billing + card form
    FE->>Filter: POST /api/bookings (Bearer JWT)
    Filter->>Filter: Validate JWT, resolve username
    Filter->>BC: Forward request, username attached
    BC->>BS: createBooking(bookingRequest, request)
    BS->>DB: Look up User by username
    BS->>DB: Look up Car by carId
    BS->>DB: INSERT Booking (tenant, lessor, car, dates)
    BS->>DB: INSERT Billing (name, license, headcount)
    BS->>DB: INSERT CardDetails (card number, CVV, expiry)
    BS->>DB: INSERT Transaction (status = SUCCESS, no real gateway call)
    BS->>DB: INSERT Order (links transaction + billing + booking)
    DB-->>BS: Generated order ID
    BS-->>BC: orderId
    BC-->>FE: 200 OK, orderId
```

Note that `Transaction.status` is hardcoded to `SUCCESS` in `BookingService.createBooking` — there is no integration with a real payment gateway; the "transaction" is a database record only.

## Known limitations

See the [Known Issues](./README.md#known-issues) section in the README — it covers the security and code-quality findings from this scan (hardcoded mail credentials, plaintext card storage, missing ownership checks, no role enforcement, committed build artifacts, no tests/CI) so they aren't duplicated here.
