This is an Spring Boot application which serves the API endpoint for the Car Rental System.

To run this application in the terminal and run the application continuously even the terminal closed,

```bash 
nohup mvn spring-boot:run > spring-log.txt &```

To stop the application in ubuntu 

```bash 
ps
```
this will show you the process which are running, find for the java and take the PID and execute the command
```bash 
ps
kill -9 <PID>
```

This github repository doesn't conatains the application.properties file you have to create by yourself.

The application property need

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
spring.web.cors.allow-credentials=true
spring.web.cors.allowed-origins=*
spring.web.cors.allowed-methods=GET,POST,PUT,DELETE,OPTIONS
spring.web.cors.allowed-headers=*
app.jwtSecret=<your secret key>
app.url=<your deployed url> 


