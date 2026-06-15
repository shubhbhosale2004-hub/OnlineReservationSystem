# Entity-Relationship Model

## Entities & Their Roles

| Entity | Purpose |
|--------|---------|
| users | Traveller accounts holding login credentials and profile info |
| admins | Privileged system operators (pre-seeded via SQL) |
| routes | Corridors between two stations with pricing data |
| reservations | Individual booking records linking a user to a route |
| payments | Financial transactions, one per reservation |

## Relationship Map

```
 ┌─────────────┐            ┌──────────────────┐            ┌─────────────┐
 │   users      │ ──1:N───▶ │   reservations    │ ◀──N:1──  │   routes     │
 │  (PK: id)    │            │  (PK: id)         │            │  (PK: id)    │
 │  username    │            │  reservation_id   │            │  source      │
 │  password    │            │  user_id  [FK]    │            │  destination │
 │  email       │            │  route_id [FK]    │            │  distance_km │
 │  full_name   │            │  journey_date     │            │  base_fare   │
 │  phone       │            │  passenger_name   │            └─────────────┘
 └─────────────┘            │  age, gender      │
                             │  seat_type, fare  │
 ┌─────────────┐            │  status           │            ┌─────────────┐
 │   admins     │            └──────────────────┘ ──1:1───▶ │   payments   │
 │  (PK: id)    │                                             │  (PK: id)    │
 │  username    │                                             │  res_id [FK] │
 │  password    │                                             │  amount      │
 │  email       │                                             │  pay_date    │
 └─────────────┘                                             │  pay_method  │
   (standalone)                                              │  pay_status  │
                                                              └─────────────┘
```

## Cardinality Details

| Relationship | Type | FK Column | On Delete |
|-------------|------|-----------|-----------|
| users → reservations | 1 : Many | reservations.user_id | CASCADE |
| routes → reservations | 1 : Many | reservations.route_id | CASCADE |
| reservations → payments | 1 : 1 | payments.reservation_id | CASCADE |

## Attribute Inventory

### users
`id` INT PK · `username` VARCHAR(50) UQ · `password` VARCHAR(255) · `email` VARCHAR(100) UQ · `full_name` VARCHAR(100) · `phone` VARCHAR(15) nullable · `created_at` TIMESTAMP

### admins
`id` INT PK · `username` VARCHAR(50) UQ · `password` VARCHAR(255) · `email` VARCHAR(100) nullable · `created_at` TIMESTAMP

### routes
`id` INT PK · `source_station` VARCHAR(100) · `destination_station` VARCHAR(100) · `distance_km` DOUBLE nullable · `base_fare` DOUBLE

### reservations
`id` INT PK · `reservation_id` VARCHAR(20) UQ · `user_id` INT FK · `route_id` INT FK · `journey_date` DATE · `passenger_name` VARCHAR(100) · `age` INT · `gender` VARCHAR(10) · `seat_type` VARCHAR(30) · `fare` DOUBLE · `status` VARCHAR(20) DEFAULT CONFIRMED · `created_at` TIMESTAMP

### payments
`id` INT PK · `reservation_id` INT FK · `amount` DOUBLE · `payment_date` TIMESTAMP · `payment_method` VARCHAR(30) nullable · `payment_status` VARCHAR(20) DEFAULT COMPLETED
