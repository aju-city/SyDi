# IPOS-PU Deliverable 2

## Public User Subsystem for the IPOS Project

---

## Overview

This project is the implementation of the **IPOS-PU (Public User)** subsystem of the Integrated Pharmacy Ordering System (IPOS).

The Public User subsystem allows users to:

- Browse available pharmaceutical products
- View active promotional campaigns
- Register and log in
- Add products to a shopping cart
- Checkout and place orders
- Make simulated online payments
- View order history
- Use guest checkout

The system also includes administrator functionality for campaign management and monitoring activity.

---

## Technologies Used

- Java
- Java Swing
- Java HTTP Server
- JDBC
- MySQL
- Maven
- Gson
- IntelliJ IDEA / NetBeans

---

## Project Structure

```text
ipos-pu/
│
├── backend/
│   ├── pom.xml
│   └── src/main/
│       ├── java/
│       │   ├── controller/
│       │   ├── dao/
│       │   ├── db/
│       │   ├── model/
│       │   ├── server/
│       │   │   ├── handlers/
│       │   │   └── api/
│       │   └── service/
│       │
│       └── resources/
│           └── config.properties
│
├── frontend/
│   └── src/JavaApplication/
│       ├── ipos_pu/
│       └── javaapplication/
│
├── database/
│   └── SQL scripts
│
└── README.md
```

---

## Databases Required

Create the following MySQL databases:

- `ipos_pu`
- `ipos_ca`
- `ipos_sa`

Run all SQL scripts before starting the system.

---

## Database Configuration

### Backend Configuration

Edit:

```properties
backend/src/main/resources/config.properties
```

Example:

```properties
db.url=jdbc:mysql://localhost:3306/ipos_pu
db.user=root
db.password=YourPassword
```

---

### Frontend Configuration

Edit:

```java
frontend/src/JavaApplication/ipos_pu/DBConnection.java
```

Ensure:

```java
private static final String URL  = "jdbc:mysql://localhost:3306/ipos_pu";
private static final String USER = "root";
private static final String PASS = "YourPassword";
```

---

## Required Dependencies

### Backend (Maven)

Dependencies used:

- Gson
- MySQL Connector J

---

### Frontend

Ensure MySQL JDBC JAR exists inside:

```text
frontend/lib/mysql-connector-j-8.4.0.jar
```

And is linked in project libraries.

---

## How to Run the System

### Step 1 - Start MySQL

Ensure MySQL server is running on:

```text
localhost:3306
```

---

### Step 2 - Run Backend

Open the backend project and run:

```text
server.Main
```

This starts the backend API server.

Expected:

```text
http://localhost:8080
```

---

### Step 3 - Run Frontend

Run:

```text
JavaApplication.java
```

This launches the Swing desktop application.

---

## Features Implemented

### Public Users

- Product browsing
- Search products
- Shopping cart
- Guest checkout
- Registration
- Login system
- Order history

### Promotions

- Active campaigns
- Discounted products
- Campaign browsing

### Orders

- Order creation
- Order item storage
- Payment records
- Order history tracking

### Admin

- Admin login
- Campaign creation
- Campaign management

### Logging

- User activity logs
- Checkout actions
- Promotion views
- Order logs

---

## Common Issues

### Backend Will Not Start

Check:

- `config.properties` exists
- MySQL running
- Correct username/password
- Correct database name

### Frontend Shows No Products

Check:

- Backend running
- Port 8080 active
- Products exist in stock table

### JDBC Driver Error

Ensure MySQL connector JAR is linked.

### Port Already In Use

Close old backend instance and restart.

---

## Authors

SyDi Team
