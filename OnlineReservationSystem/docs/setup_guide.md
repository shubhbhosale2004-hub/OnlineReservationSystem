# Installation & Configuration Guide

## What You Need

| Software | Minimum Version | Download |
|----------|----------------|----------|
| JDK | 8 | https://www.oracle.com/java/technologies/downloads/ |
| MySQL Server | 5.7 | https://dev.mysql.com/downloads/mysql/ |
| MySQL Connector/J | 8.0.x | https://dev.mysql.com/downloads/connector/j/ |

## Step 1 — Install the JDK

1. Download and run the installer for your platform.
2. Configure the `JAVA_HOME` environment variable to point at the JDK root.
3. Append `%JAVA_HOME%\bin` (Windows) or `$JAVA_HOME/bin` (Linux/macOS) to your `PATH`.
4. Verify:
   ```
   java -version
   javac -version
   ```

## Step 2 — Install & Start MySQL

1. Run the MySQL installer; choose the "Developer Default" profile.
2. Set a root password during setup — **write it down**.
3. Ensure the MySQL service is running (`services.msc` on Windows, `systemctl status mysql` on Linux).
4. Test connectivity:
   ```
   mysql -u root -p
   ```

## Step 3 — Create the Database

Execute the provided SQL script to build the schema, tables, and seed data:

```bash
cd OnlineReservationSystem
mysql -u root -p < sql/database_setup.sql
```

Alternatively, open `sql/database_setup.sql` in MySQL Workbench and run it there.

Confirm the tables exist:
```sql
USE online_reservation_system;
SHOW TABLES;
-- Expected: admins, payments, reservations, routes, users
```

## Step 4 — Add the JDBC Driver

1. Download the "Platform Independent" archive of MySQL Connector/J.
2. Extract the JAR file (e.g. `mysql-connector-java-8.0.30.jar`).
3. Create a `lib/` directory at the project root and drop the JAR inside:
   ```
   OnlineReservationSystem/
   ├── lib/
   │   └── mysql-connector-java-8.0.30.jar
   ├── src/
   └── ...
   ```

## Step 5 — Set Your Database Credentials

Open `src/com/reservation/database/DBConnection.java` and update:

```java
private static final String DB_ACCOUNT = "root";
private static final String DB_SECRET  = "YOUR_MYSQL_PASSWORD";
```

## Step 6 — Compile

### Windows (PowerShell)
```powershell
mkdir out
javac -cp ".;lib\mysql-connector-java-8.0.30.jar" -d out `
  src\com\reservation\model\*.java `
  src\com\reservation\database\*.java `
  src\com\reservation\util\*.java `
  src\com\reservation\dao\*.java `
  src\com\reservation\controller\*.java `
  src\com\reservation\view\*.java `
  src\com\reservation\Main.java
```

### Linux / macOS
```bash
mkdir -p out
javac -cp ".:lib/mysql-connector-java-8.0.30.jar" -d out \
  src/com/reservation/model/*.java \
  src/com/reservation/database/*.java \
  src/com/reservation/util/*.java \
  src/com/reservation/dao/*.java \
  src/com/reservation/controller/*.java \
  src/com/reservation/view/*.java \
  src/com/reservation/Main.java
```

## Step 7 — Launch

### Windows
```powershell
java -cp ".;out;lib\mysql-connector-java-8.0.30.jar" com.reservation.Main
```

### Linux / macOS
```bash
java -cp ".:out:lib/mysql-connector-java-8.0.30.jar" com.reservation.Main
```

The sign-in window should appear.

## Default Credentials

| Role | User | Pass |
|------|------|------|
| Administrator | admin | admin123 |

Traveller accounts are created via the registration screen.

## Common Issues

| Symptom | Likely Cause | Fix |
|---------|-------------|-----|
| `ClassNotFoundException: com.mysql.cj.jdbc.Driver` | Connector JAR absent from classpath | Double-check the `lib/` folder and `-cp` argument |
| `Communications link failure` | MySQL is not running | Start the MySQL service |
| `Access denied for user` | Wrong password in `DBConnection.java` | Update `DB_SECRET` to match your root password |
| `Unknown database` | Setup script not executed | Run `mysql -u root -p < sql/database_setup.sql` |
| `UnsupportedClassVersionError` | JDK mismatch between compile and run | Use the same JDK version for both steps |

## Using an IDE

Import the project into IntelliJ IDEA, Eclipse, or NetBeans:

1. Mark `src/` as the source root.
2. Add the MySQL Connector JAR to the project's build path / module dependencies.
3. Run `com.reservation.Main` as the main class.
