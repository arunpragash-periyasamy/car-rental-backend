# Car Rental Backend

REST API backend for a car rental platform — user auth, car listings with images, and a booking/checkout flow. Built with Spring Boot and MySQL.

## Status

**Prototype / demo project.** It runs end-to-end (auth, car CRUD, image upload, booking checkout) but has several rough edges typical of a learning project:
- No automated tests beyond the default Spring Boot context-load smoke test.
- No CI/CD pipeline.
- No role-based authorization — every authenticated user gets the same `USER` authority regardless of the `userType` field stored on the account.
- Car images are saved to local disk, not object storage, so they won't survive a redeploy on most hosting platforms.
- See [Known Issues](#known-issues) for security items that should be fixed before this handles real payment data.

## Overview

The API lets a user register, log in (JWT-based), list a car they own with specs/pricing/photos, browse all listed cars, and book someone else's car with a billing + card-details + checkout step that produces an order ID. It is the backend counterpart to a separate React frontend (`Car-Rental-Frontend`) — the route prefixes below (`/api/auth`, `/api/cars`, `/api/bookings`, `/api/users`) match what that frontend calls.

## Tech stack

- **Java 21**, **Spring Boot 3.3.1**
- **Spring Web** (REST controllers), **Spring Data JPA** (Hibernate), **Spring Security** (JWT-based, stateless)
- **MySQL** (`mysql-connector-j`) via Spring Data JPA
- **jjwt** (`io.jsonwebtoken`) for JWT signing/parsing
- **Spring Mail** for registration confirmation emails
- **Lombok** for boilerplate reduction
- Packaged with the **Spring Boot Maven plugin**; deployable via the included **Dockerfile**

## Features

- User registration with email/phone uniqueness checks and BCrypt password hashing
- JWT login (`/api/auth/login`) — stateless session, token carried as `Authorization: Bearer <token>`
- Registration confirmation email (Spring Mail / Gmail SMTP)
- Car listing create/update with nested model, specs, pricing, and address records, plus multi-image upload
- Public car browsing (`GET /api/cars`) and per-owner car listing (`GET /api/cars/myCars`)
- Car image retrieval by ID, streamed back from disk
- Booking flow: create a booking + billing record + card/transaction/order in one call, returns an order ID
- Per-user booking history (`GET /api/bookings`)
- Basic user profile update (`PUT /api/users/{id}`)

## Architecture

See [ARCHITECTURE.md](./ARCHITECTURE.md) for the component diagram, database schema (12 tables), and the booking request lifecycle.

## Folder structure

```
src/main/java/com/arunpragash/car_rental/
├── config/          # Security, CORS, and mail bean configuration
├── controller/       # REST controllers — one per resource (auth, cars, bookings, users)
├── model/
│   ├── table/        # JPA entities (12 @Entity classes = the DB schema)
│   ├── requestModel/  # DTOs for request/response bodies
│   └── UserPrincipal.java  # Spring Security UserDetails adapter
├── repository/        # Spring Data JPA repositories, one per entity
└── service/           # Business logic: auth/JWT, users, cars, bookings, email
```

## Getting started

Requires a running MySQL instance and Java 21. The repo does **not** include `src/main/resources/application.properties` — you must create it yourself (it's gitignored). At minimum it needs:

```properties
spring.application.name=car-rental
spring.datasource.url=<connection URL>
spring.datasource.username=<DB user name>
spring.datasource.password=<DB Password>
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=<your email id>
spring.mail.password=<email app password>
app.jwtSecret=<your secret key>
app.url=<your deployed url>
```

Run it:

```bash
./mvnw spring-boot:run
```

Or with Docker (build the jar into `target/` first, since the Dockerfile copies from there):

```bash
./mvnw clean package -DskipTests
docker build -t car-rental-backend .
docker run -p 8080:8080 car-rental-backend
```

To keep it running in the background on a Linux host:

```bash
nohup ./mvnw spring-boot:run > spring-log.txt &
```

## Usage example

```bash
# Register
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"name":"Jane Doe","userName":"janedoe","email":"jane@example.com","phoneNumber":9876543210,"password":"secret"}'

# Log in
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"userName":"janedoe","password":"secret"}'
# -> { "token": "...", "userName": "janedoe", "userType": "..." }

# List cars (public)
curl http://localhost:8080/api/cars

# Authenticated call
curl http://localhost:8080/api/cars/myCars -H "Authorization: Bearer <token>"
```

## Known Issues

These are real findings from reading the source — flagged here in the interest of honesty, not just as a to-do list:

- **Hardcoded email credentials in source control.** `src/main/java/com/arunpragash/car_rental/config/MailConfig.java` hardcodes a real Gmail address and what appears to be a Gmail app password directly in the class, committed to a public repo. These should be rotated immediately and moved to `application.properties` / environment variables (an `app.mail*`-style externalized config already exists as a pattern elsewhere in the app — `app.jwtSecret`, `app.url` — this should follow it).
- **Plaintext payment card data stored in the database.** `CardDetails` stores the raw card number and CVV as plain columns (`BookingService.createBooking`), with no encryption, tokenization, or PCI-style handling. This should never touch a real card processor's data as-is — a payment gateway/tokenization service is needed instead of storing PAN/CVV directly.
- **Broken object-level authorization (IDOR) on car updates.** `CarController.updateCar` / `CarService.saveCar` update a car by ID without checking that the authenticated user is the car's actual owner — any logged-in user can edit or reassign ownership of any car by guessing its ID.
- **Same issue on user updates.** `UserController.updateUser` (`PUT /api/users/{id}`) updates any user record by path ID with no check that it matches the authenticated caller.
- **No role enforcement.** `UserPrincipal.getAuthorities()` always returns a single hardcoded `USER` authority. The `userType` field on `User` (e.g. "admin") is stored but never used to gate anything — there's effectively no admin/user distinction at the security layer.
- **A stray leftover debug comment in `UserService.java`** prints what looks like one real user's full record — username, email, phone number, and a BCrypt password hash — as a trailing code comment. It should be removed; while the password is hashed, the email/phone/username of what appears to be a real account are sitting in the repo history.
- **CORS is wide open.** Both `WebConfig` (global `allowedOrigins("*")`) and `@CrossOrigin("*")` on every controller accept requests from any origin. Fine for a demo; too permissive for anything real without a fixed allowlist.
- **Possible route bug on public image access.** `SecurityConfig`'s permit-all list includes `"api/cars/images/*"` without a leading slash, alongside `"/api/auth/*"` and `"/"` (which do have one). If that inconsistency means the image route isn't actually being matched as public, `GET /api/cars/images/{id}` would require a JWT — which would break `<img>` tags on the frontend that don't send an `Authorization` header. Worth a manual check against a running instance.
- **Build artifacts and logs are committed to the repo.** A 50MB pre-built jar (`jar/car-rental-0.0.1-SNAPSHOT.jar`), a MySQL schema dump (`output.sql`), and a runtime log (`spring.log`) are tracked in git. None of these belong in version control.
- **Two dead entities.** `Document` and `Place` are mapped `@Entity` classes with no corresponding repository or controller — unused code from an earlier iteration.
- **No tests, no CI.** The only test is the default Spring Boot `contextLoads()` smoke test; there's no GitHub Actions workflow or other CI configuration.

## License

No license file is present in this repository.
