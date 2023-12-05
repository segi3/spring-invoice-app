## Springboot-invoice-app

An app for managing invoices including CRUD operations.

Framework: Spring-boot 3.2.0

Database: Postgresql

## Tested Environment

OS: MacOS 12.7.1

Java: Java 17

Maven: Maven 3.9.3

## How to run

1. Clone repository
```bash
git clone https://github.com/segi3/spring-invoice-app.git
```
2. Fill database properties inside `resources/application_properties`. If needed could also change jwt properties.
3. Build the application using `mvn clean install`
4. Run the program using `mvn spring-boot:run`
5. Run sql script inside `script/sql` to create dummy records if needed.

By default app will run on port `8080`, swagger API dashboard can be accessed on `http://localhost:8080/swagger-ui/index.html`

## Swagger Dashboard

![swag!](https://raw.githubusercontent.com/segi3/spring-invoice-app/main/readme_files/swagger-dashboard.png "swag")



