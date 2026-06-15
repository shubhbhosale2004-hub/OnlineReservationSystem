# Database Schema Reference

## Target Schema: `online_reservation_system`

### users

Holds traveller account information.

| Column | Datatype | Notes |
|--------|----------|-------|
| id | INT | Primary key, auto-incremented |
| username | VARCHAR(50) | Unique, mandatory |
| password | VARCHAR(255) | Mandatory |
| email | VARCHAR(100) | Unique, mandatory |
| full_name | VARCHAR(100) | Mandatory |
| phone | VARCHAR(15) | Optional |
| created_at | TIMESTAMP | Auto-set on row creation |

---

### admins

Stores operator credentials; pre-populated via the setup script.

| Column | Datatype | Notes |
|--------|----------|-------|
| id | INT | Primary key |
| username | VARCHAR(50) | Unique, mandatory |
| password | VARCHAR(255) | Mandatory |
| email | VARCHAR(100) | Optional |
| created_at | TIMESTAMP | Auto-set |

Default entry: `admin / admin123`

---

### routes

Represents a travel corridor between two cities.

| Column | Datatype | Notes |
|--------|----------|-------|
| id | INT | Primary key |
| source_station | VARCHAR(100) | Mandatory |
| destination_station | VARCHAR(100) | Mandatory |
| distance_km | DOUBLE | Optional |
| base_fare | DOUBLE | Mandatory — used for pricing |

Seeded corridors include Delhi–Mumbai, Mumbai–Chennai, Delhi–Kolkata, and seven more.

---

### reservations

Central booking table linking a traveller to a corridor.

| Column | Datatype | Notes |
|--------|----------|-------|
| id | INT | Primary key |
| reservation_id | VARCHAR(20) | Unique code (e.g. RES-20260615-4827) |
| user_id | INT | FK → users.id, CASCADE |
| route_id | INT | FK → routes.id, CASCADE |
| journey_date | DATE | Mandatory |
| passenger_name | VARCHAR(100) | Mandatory |
| age | INT | Mandatory |
| gender | VARCHAR(10) | Male / Female / Other |
| seat_type | VARCHAR(30) | SLEEPER, AC_3TIER, AC_2TIER, AC_FIRST |
| fare | DOUBLE | Computed at booking time |
| status | VARCHAR(20) | CONFIRMED or CANCELLED |
| created_at | TIMESTAMP | Auto-set |

---

### payments

Financial transaction for each booking, created automatically.

| Column | Datatype | Notes |
|--------|----------|-------|
| id | INT | Primary key |
| reservation_id | INT | FK → reservations.id, CASCADE |
| amount | DOUBLE | Matches the booking fare |
| payment_date | TIMESTAMP | Auto-set |
| payment_method | VARCHAR(30) | e.g. ONLINE |
| payment_status | VARCHAR(20) | COMPLETED by default |

---

## Foreign-Key Graph

```
users.id  ◀──  reservations.user_id   (ON DELETE CASCADE)
routes.id ◀──  reservations.route_id  (ON DELETE CASCADE)
reservations.id ◀── payments.reservation_id (ON DELETE CASCADE)
```

## Key Queries Employed

```sql
-- Traveller login
SELECT * FROM users WHERE username=? AND password=?;

-- Booking creation
INSERT INTO reservations (reservation_id,user_id,route_id,journey_date,
  passenger_name,age,gender,seat_type,fare,status) VALUES (?,?,?,?,?,?,?,?,?,?);

-- Fetch bookings with route names
SELECT r.*, rt.source_station, rt.destination_station
  FROM reservations r JOIN routes rt ON r.route_id=rt.id
 WHERE r.user_id=?;

-- Seat-availability check
SELECT COUNT(*) FROM reservations
 WHERE route_id=? AND journey_date=? AND seat_type=? AND status='CONFIRMED';

-- Keyword search
SELECT r.*, rt.source_station, rt.destination_station
  FROM reservations r JOIN routes rt ON r.route_id=rt.id
 WHERE r.reservation_id LIKE ? OR r.passenger_name LIKE ?;
```
