# Rail Booking Platform — Project Report

## 1. Introduction

This document outlines the design, implementation, and testing strategy for the Rail Booking Platform, a desktop application that allows travellers to reserve train tickets and enables administrators to manage the system's operations. The application has been developed using Core Java with the Swing GUI toolkit and communicates with a MySQL relational database through JDBC.

## 2. Objectives

- Deliver a functioning reservation system that supports the entire booking life-cycle: creation, viewing, cancellation, and searching.
- Implement role-based access with separate flows for travellers and administrators.
- Apply the Model-View-Controller (MVC) design pattern to ensure a clean separation of concerns.
- Provide an intuitive, visually appealing dark-themed user interface.
- Generate exportable text reports for administrative use.

## 3. Technology Stack

| Layer | Technology |
|-------|-----------|
| Language | Java SE 8+ |
| GUI Toolkit | Java Swing |
| Persistence | JDBC with PreparedStatement |
| Database | MySQL 8.0 |
| Build | Manual javac compilation (IDE-optional) |

## 4. System Architecture

The project is organised into six packages under `com.reservation`:

- **model** – Plain-old Java objects (POJOs) that mirror database rows.
- **dao** – Data-access objects that encapsulate all SQL operations.
- **controller** – Business-logic classes that mediate between views and DAOs.
- **view** – Swing-based screens for traveller and admin interactions.
- **database** – Singleton connection manager for the MySQL link.
- **util** – Cross-cutting helpers: input validation, fare calculation, unique-ID generation, and report export.

## 5. Module Descriptions

### 5.1 Authentication Module

Handles account creation with field-level validation (username format, e-mail pattern, password strength) and duplicate detection. Supports dual sign-in: traveller credentials are checked against the `users` table, admin credentials against `admins`.

### 5.2 Reservation Module

Guides the traveller through station selection (dynamically loaded), date entry, passenger details, and berth-class choice. The fare is computed in real time by applying a class-specific multiplier to the corridor's base price. On confirmation, a unique booking code is generated and a payment record is created automatically.

### 5.3 Booking Management Module

Travellers can view their complete booking history in a sortable table, search any booking by code, and cancel active reservations. Cancelled bookings are soft-deleted (status updated to CANCELLED) for audit purposes.

### 5.4 Admin Module

Administrators access a tabbed control panel showing all bookings, all registered users, and aggregate statistics (total bookings, active, cancelled, gross revenue). They can delete bookings or user accounts and export a formatted text report of all reservations.

## 6. Database Design

The schema comprises five tables:

1. **users** – traveller accounts (id, username, password, email, full_name, phone)
2. **admins** – administrator accounts (id, username, password, email)
3. **routes** – travel corridors with distance and base fare
4. **reservations** – booking records with foreign keys to users and routes
5. **payments** – one-to-one transaction records linked to reservations

Referential integrity is maintained through CASCADE delete constraints.

## 7. Testing Strategy

- **Unit checks** – Each DAO method was tested by inserting, querying, updating, and deleting rows, then verifying the returned objects.
- **Integration checks** – End-to-end flows (register → login → book → view → cancel) were exercised through the GUI.
- **Boundary checks** – Invalid inputs (empty fields, past dates, out-of-range ages) were tested against the validation utilities.

## 8. Limitations & Future Scope

- Passwords are stored in plain text; future versions should hash them with bcrypt.
- The application uses a shared singleton connection; a connection-pool library (HikariCP) would improve concurrency.
- Adding an ORM such as Hibernate could reduce boilerplate SQL.
- A web-based frontend (Spring Boot + Thymeleaf) would make the system accessible via browsers.

## 9. Conclusion

The Rail Booking Platform meets all stated objectives: it provides a complete reservation workflow, role-based access, MVC separation, and a polished graphical interface. The modular architecture makes it straightforward to extend with additional features.
