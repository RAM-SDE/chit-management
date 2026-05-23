# Chit Collection Management System

Spring Boot web app for managing chit fund collections.

## Tech Stack
- Java 21, Spring Boot 4.0.6
- Spring Security + JWT
- MySQL 8, Spring Data JPA
- Thymeleaf, Bootstrap 5
- Maven

## Setup

**1. Create Database**
```sql
CREATE DATABASE chit_management;
```

**2. Configure application.properties**
```properties
spring.application.name=task
spring.datasource.url=jdbc:mysql://localhost:3306/chit_management?useSSL=false&serverTimeZone=UTC
spring.datasource.username=root
spring.datasource.password=your_password
spring.jpa.hibernate.ddl-auto=update
jwt.secret=chit_jwt_secret_key_@2026!!_secure_key
jwt.name=chit
jwt.expiration=86400
```

**3. Run**
```bash
mvn spring-boot:run
```

**4. Open**
```
http://localhost:8080/login
```

## Login Credentials

| Role  | Email           | Password  |
|-------|-----------------|-----------|
| Admin | admin@chit.com  | Admin@123 |
| Agent | user@chit.com   | User@123  |

## Features
- JWT login with Admin / Agent roles
- Customer management
- Chit plan creation & enrollment
- Payment recording (multi-month)
- Receipt generation & print
- Dashboard with reports