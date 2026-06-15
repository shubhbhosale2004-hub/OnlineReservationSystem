-- =====================================================================
--  Rail Booking Platform – Schema Initialization Script
--  Target DBMS  : MySQL 8.0 or higher
--  Purpose      : Drops & recreates the full schema, seeds default
--                 admin account, and loads sample corridor data.
--  Last updated : June 2026
-- =====================================================================

-- 1. Bootstrap the schema
CREATE DATABASE IF NOT EXISTS online_reservation_system
    DEFAULT CHARACTER SET utf8mb4
    DEFAULT COLLATE utf8mb4_unicode_ci;

USE online_reservation_system;

-- 2. Tear down existing objects (child tables first)
DROP TABLE IF EXISTS payments;
DROP TABLE IF EXISTS reservations;
DROP TABLE IF EXISTS routes;
DROP TABLE IF EXISTS admins;
DROP TABLE IF EXISTS users;

-- 3. Traveler accounts
CREATE TABLE users (
    id          INT           AUTO_INCREMENT PRIMARY KEY,
    username    VARCHAR(50)   NOT NULL UNIQUE,
    password    VARCHAR(255)  NOT NULL,
    email       VARCHAR(100)  NOT NULL UNIQUE,
    full_name   VARCHAR(100)  NOT NULL,
    phone       VARCHAR(15),
    created_at  TIMESTAMP     DEFAULT CURRENT_TIMESTAMP,
    INDEX ix_usr_name  (username),
    INDEX ix_usr_email (email)
) ENGINE=InnoDB;

-- 4. Administrator accounts
CREATE TABLE admins (
    id          INT           AUTO_INCREMENT PRIMARY KEY,
    username    VARCHAR(50)   NOT NULL UNIQUE,
    password    VARCHAR(255)  NOT NULL,
    email       VARCHAR(100),
    created_at  TIMESTAMP     DEFAULT CURRENT_TIMESTAMP,
    INDEX ix_adm_name (username)
) ENGINE=InnoDB;

-- 5. Travel corridors
CREATE TABLE routes (
    id                   INT           AUTO_INCREMENT PRIMARY KEY,
    source_station       VARCHAR(100)  NOT NULL,
    destination_station  VARCHAR(100)  NOT NULL,
    distance_km          DOUBLE,
    base_fare            DOUBLE        NOT NULL,
    INDEX ix_rt_src (source_station),
    INDEX ix_rt_dst (destination_station)
) ENGINE=InnoDB;

-- 6. Booking records
CREATE TABLE reservations (
    id               INT           AUTO_INCREMENT PRIMARY KEY,
    reservation_id   VARCHAR(20)   NOT NULL UNIQUE,
    user_id          INT           NOT NULL,
    route_id         INT           NOT NULL,
    journey_date     DATE          NOT NULL,
    passenger_name   VARCHAR(100)  NOT NULL,
    age              INT           NOT NULL,
    gender           VARCHAR(10)   NOT NULL,
    seat_type        VARCHAR(30)   NOT NULL,
    fare             DOUBLE        NOT NULL,
    status           VARCHAR(20)   DEFAULT 'CONFIRMED',
    created_at       TIMESTAMP     DEFAULT CURRENT_TIMESTAMP,
    INDEX ix_bk_code    (reservation_id),
    INDEX ix_bk_user    (user_id),
    INDEX ix_bk_status  (status),
    CONSTRAINT fk_bk_user  FOREIGN KEY (user_id)  REFERENCES users(id)  ON DELETE CASCADE,
    CONSTRAINT fk_bk_route FOREIGN KEY (route_id) REFERENCES routes(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- 7. Financial transactions
CREATE TABLE payments (
    id               INT           AUTO_INCREMENT PRIMARY KEY,
    reservation_id   INT           NOT NULL,
    amount           DOUBLE        NOT NULL,
    payment_date     TIMESTAMP     DEFAULT CURRENT_TIMESTAMP,
    payment_method   VARCHAR(30),
    payment_status   VARCHAR(20)   DEFAULT 'COMPLETED',
    INDEX ix_pay_resid  (reservation_id),
    INDEX ix_pay_status (payment_status),
    CONSTRAINT fk_pay_res FOREIGN KEY (reservation_id) REFERENCES reservations(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- 8. Default administrator (credentials: admin / admin123)
INSERT INTO admins (username, password, email)
VALUES ('admin', 'admin123', 'admin@railbooking.in');

-- 9. Sample corridor data
INSERT INTO routes (source_station, destination_station, distance_km, base_fare)
VALUES
    ('Delhi',      'Mumbai',      1400, 500.00),
    ('Mumbai',     'Chennai',     1300, 450.00),
    ('Delhi',      'Kolkata',     1500, 520.00),
    ('Bangalore',  'Hyderabad',    570, 300.00),
    ('Chennai',    'Kolkata',     1650, 550.00),
    ('Delhi',      'Jaipur',       280, 200.00),
    ('Mumbai',     'Pune',         150, 120.00),
    ('Lucknow',    'Delhi',        500, 250.00),
    ('Patna',      'Kolkata',      590, 310.00),
    ('Hyderabad',  'Chennai',      630, 320.00);

-- 10. Quick sanity check
SELECT 'Schema initialization complete.' AS result;
