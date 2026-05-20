# Reservation & Cancellation Engine

## Project Overview

Silver Heavens Resort manages hundreds of bungalow reservations daily. The reservation process includes booking creation, confirmation, cancellation, refund processing, and refund reconciliation.

This project is a backend-driven Reservation & Cancellation Engine built using Spring Boot and related enterprise technologies. The system automates reservation handling, cancellation workflows, refund tracking, and scheduled background processing.

The application was developed as a backend system design and implementation assignment focused on:

* REST API development
* Transaction management
* Batch processing
* Concurrency handling
* Excel import/export
* Refund lifecycle management
* Data consistency
* Background job automation

---

## Tech Stack

* Java 17
* Spring Boot 4
* Spring Data JPA
* MySQL
* RabbitMQ
* Spring Batch
* OpenAPI / Swagger
* Apache POI
* OpenPDF
* Gradle

---

## Project Structure

```text
src/main/java/com/reservation
│
├── controller
├── service
├── repository
├── entity
├── dto
├── mapper
├── batch
├── config
└── enums
```

---

## Main Modules

### Reservation Management

* Create reservations
* Update reservations
* Confirm reservations
* Complete reservations
* Track reservation status

### Guest Management

* Create guest
* Update guest
* Fetch guest details
* Search guest by email

### Payment Management

* Process payments
* Refund payments
* Fetch payments by reservation
* Revenue tracking per bungalow

### Cancellation Management

* Cancel reservations
* Refund calculation
* Refund status tracking
* Configurable cancellation policies

### Travel Agent Management

* Travel agent registration
* Commission handling

### Batch Jobs

* Reservation expiry handling
* Refund status updates

### Excel Features

* Export reservations to Excel
* Import reservations from Excel

---

## Reservation Statuses

```text
PENDING
CONFIRMED
CANCELLED
EXPIRED
WAITLIST
COMPLETED
```

---

## Payment Statuses

```text
COMPLETED
REFUNDED
CANCELLED
PENDING
```

---

## Refund Statuses

```text
PENDING
PROCESSED
OVERDUE
```

---

## Booking Sources

```text
DIRECT
TRAVEL_AGENCY
ONLINE_PORTAL
```

---

## API Documentation

Swagger UI:

```text
http://localhost:8080/swagger-ui.html
```

OpenAPI JSON:

```text
http://localhost:8080/v3/api-docs
```

---

## Installation & Setup

### Clone Repository

```bash
git clone https://github.com/m-tare27/reservation.git
cd reservation-system
```

---

### Configure MySQL

Create a database:

```sql
CREATE DATABASE reservation_db;
```

---

### Configure `application.properties`

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/reservation_db
spring.datasource.username=root
spring.datasource.password=your_password

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

spring.rabbitmq.host=localhost
spring.rabbitmq.port=5672
spring.rabbitmq.username=guest
spring.rabbitmq.password=guest
```

---

### Run RabbitMQ

Using Docker:

```bash
docker run -d --hostname rabbitmq \
--name rabbitmq \
-p 5672:5672 \
-p 15672:15672 \
rabbitmq:3-management
```

RabbitMQ dashboard:

```text
http://localhost:15672
```

Username: `guest`
Password: `guest`

---

## Run the Application

### Using Gradle

```bash
./gradlew bootRun
```

### Build JAR

```bash
./gradlew build
```

Run JAR:

```bash
java -jar build/libs/reservation-0.0.1-SNAPSHOT.jar
```

---

## Important APIs

### Reservations

| Method | Endpoint                         | Description          |
| ------ | -------------------------------- | -------------------- |
| POST   | `/api/reservations`              | Create reservation   |
| GET    | `/api/reservations`              | Get reservations     |
| PUT    | `/api/reservations/{id}`         | Update reservation   |
| PATCH  | `/api/reservations/{id}/confirm` | Confirm reservation  |
| PATCH  | `/api/reservations/complete`     | Complete reservation |

---

### Payments

| Method | Endpoint                                      | Description          |
| ------ | --------------------------------------------- | -------------------- |
| POST   | `/api/payments`                               | Process payment      |
| GET    | `/api/payments/{paymentId}`                   | Get payment          |
| PATCH  | `/api/payments/{paymentId}/refund`            | Refund payment       |
| GET    | `/api/payments/bungalow/{bungalowId}/revenue` | Get bungalow revenue |

---

### Guests

| Method | Endpoint               | Description        |
| ------ | ---------------------- | ------------------ |
| POST   | `/api/guests`          | Create guest       |
| GET    | `/api/guests`          | Get all guests     |
| GET    | `/api/guests/{id}`     | Get guest by ID    |
| GET    | `/api/guests/by-email` | Get guest by email |

---

### Cancellations

| Method | Endpoint                                | Description          |
| ------ | --------------------------------------- | -------------------- |
| POST   | `/api/cancellations`                    | Cancel reservation   |
| GET    | `/api/cancellations`                    | Get cancellations    |
| PATCH  | `/api/cancellations/refund-status/{id}` | Update refund status |

---

### Excel APIs

| Method | Endpoint            | Description         |
| ------ | ------------------- | ------------------- |
| GET    | `/api/excel/export` | Export reservations |
| POST   | `/api/excel/import` | Import reservations |

---

## Validation

The application includes validation for:

* Guest details
* Reservation dates
* Payment amounts
* Cancellation policies
* Travel agent commission limits

---

## Dependencies

Key dependencies used:

* Spring Boot Starter WebMVC
* Spring Boot Starter Data JPA
* Spring Batch
* Spring Mail
* RabbitMQ
* OpenPDF
* Apache POI
* Lombok
* SpringDoc OpenAPI

---

## Author

Manas Tare
