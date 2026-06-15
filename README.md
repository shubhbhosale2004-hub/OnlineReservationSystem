# Rail Booking Platform

A desktop reservation management application built with **Core Java**, **Swing**, **JDBC**, and **MySQL**. The system follows the MVC architectural pattern and is designed as an academic coursework project.

## Highlights

- Dual authentication (Traveler & Admin)
- Real-time fare calculation with berth-class multipliers
- Booking creation, cancellation, search, and history
- Administrative dashboard with statistics and report generation
- Professional dark-themed Swing interface

## Project Layout

```
OnlineReservationSystem/
├── sql/                           # Database initialisation script
│   └── database_setup.sql
├── src/com/reservation/
│   ├── model/                     # Data carriers (POJO)
│   ├── dao/                       # JDBC persistence layer
│   ├── controller/                # Business-logic mediators
│   ├── view/                      # Java Swing GUI screens
│   ├── database/                  # Connection manager
│   ├── util/                      # Validation, pricing, ID gen, reports
│   └── Main.java                  # Entry point
├── docs/                          # Documentation
└── README.md
```

## Prerequisites

| Item | Version |
|------|---------|
| JDK | 8 or later |
| MySQL Server | 5.7+ / 8.0 recommended |
| MySQL Connector/J | 8.0.x |

## Quick Start

1. **Initialise the database**
   ```bash
   mysql -u root -p < sql/database_setup.sql
   ```
2. **Set your MySQL password** in `src/com/reservation/database/DBConnection.java`

3. **Place** `mysql-connector-java-x.x.jar` into a `lib/` folder at the project root

4. **Compile**
   ```bash
   mkdir out
   javac -cp ".:lib/mysql-connector-java-8.0.30.jar" -d out src/com/reservation/**/*.java src/com/reservation/Main.java
   ```
5. **Run**
   ```bash
   java -cp ".:out:lib/mysql-connector-java-8.0.30.jar" com.reservation.Main
   ```

> On Windows, replace `:` with `;` in the classpath.

## Default Login

| Role | Username | Password |
|------|----------|----------|
| Admin | admin | admin123 |

Traveler accounts are created through the in-app registration screen.

## Berth-Class Pricing

| Class | Multiplier |
|-------|-----------|
| Sleeper | 1.0× |
| AC 3-Tier | 1.8× |
| AC 2-Tier | 2.5× |
| AC First | 3.5× |

## Acknowledgements

Developed as part of academic coursework – June 2026.
