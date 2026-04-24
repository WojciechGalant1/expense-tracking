# Finance Management System

A web application for tracking personal finances — income, expenses, and balance — built with **Spring Boot**, **Thymeleaf**, and **MySQL**.

---

## Table of Contents

- [Overview](#overview)
- [Features](#features)
- [Tech Stack](#tech-stack)
- [Project Structure](#project-structure)
- [Prerequisites](#prerequisites)
- [Getting Started](#getting-started)
- [Configuration](#configuration)
- [Usage](#usage)
- [API Endpoints](#api-endpoints)

---

## Overview

Finance Management System is a CRUD web application that allows users to manage their financial transactions. It categorizes transactions as income or expenses, calculates the balance, and provides summaries grouped by category and month.

---

## Features

- Add, edit, and delete financial transactions
- Classify transactions as **income** or **expense**
- Assign transactions to categories (Health, Food, Bills, Payment, etc.)
- View balance (income − expenses)
- Breakdown of income/expenses **by category** with percentage share
- Breakdown of income/expenses **by month** with percentage share
- Input validation (server-side via Jakarta Validation)
- Global exception handling with user-friendly error pages
- Responsive UI with Bootstrap 5

---

## Tech Stack

| Layer        | Technology                          |
|--------------|-------------------------------------|
| Backend      | Java 17, Spring Boot 3.2.4          |
| Persistence  | Spring Data JPA, Hibernate          |
| Database     | MySQL 8                             |
| Frontend     | Thymeleaf, Bootstrap 5.3.3          |
| Build Tool   | Maven (Maven Wrapper 3.9.5)         |
| Validation   | Jakarta Validation (Bean Validation)|

---

## Project Structure

```
src/
└── main/
    ├── java/com/example/demo/
    │   ├── FinanceManagementSystemApplication.java   # Entry point
    │   ├── config/
    │   │   └── WebConfig.java                        # Date formatter config
    │   ├── controller/
    │   │   └── FinanceController.java                # MVC controller
    │   ├── dto/
    │   │   └── TransactionDTO.java                   # Data Transfer Object
    │   ├── entity/
    │   │   └── Transaction.java                      # JPA entity
    │   ├── exception/
    │   │   ├── DatabaseOperationException.java
    │   │   ├── GlobalExceptionHandler.java           # @ControllerAdvice
    │   │   ├── InvalidTransactionDataException.java
    │   │   └── TransactionNotFoundException.java
    │   ├── mapper/
    │   │   └── TransactionMapper.java                # Entity ↔ DTO mapping
    │   ├── repository/
    │   │   └── TransactionRepository.java            # JpaRepository
    │   └── service/
    │       ├── FinanceService.java                   # Service interface
    │       └── impl/
    │           └── FinanceServiceImpl.java           # Service implementation
    └── resources/
        ├── application.properties
        └── templates/
            ├── transactions.html                     # Transaction list + balance
            ├── create_transaction.html               # Add transaction form
            ├── edit_transaction.html                 # Edit transaction form
            ├── expenses.html                         # Expense summary
            ├── income.html                           # Income summary
            ├── error.html                            # Error page
            └── index.html
```

---

## Prerequisites

- **Java 17+**
- **Maven** (or use the included `mvnw` wrapper)
- **MySQL 8** running locally

---

## Getting Started

### 1. Clone the repository

```bash
git clone <repository-url>
cd finance-management-system
```

### 2. Create the MySQL database

```sql
CREATE DATABASE financemanagmentsystem;
```

### 3. Configure the database credentials

Edit `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/financemanagmentsystem?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=your_password
```

### 4. Build and run

```bash
# Using Maven Wrapper (recommended)
./mvnw spring-boot:run

# Or on Windows
mvnw.cmd spring-boot:run
```

### 5. Open in browser

```
http://localhost:8090/transactions
```

> The application runs on port **8090** by default.  
> Hibernate is configured with `ddl-auto=update`, so the `transaction` table will be created automatically on first run.

---

## Configuration

Key settings in `application.properties`:

| Property | Value | Description |
|---|---|---|
| `server.port` | `8090` | Application port |
| `spring.jpa.hibernate.ddl-auto` | `update` | Auto-updates DB schema |
| `spring.jpa.show-sql` | `true` | Logs SQL queries |
| `logging.level.com.example.demo` | `DEBUG` | App-level debug logging |

---

## Usage

### Transaction Categories

**Expenses:** Health, Entertainment, Bills, Subscriptions, Home Expenses, Education, Food, Gifts, Transport, Other

**Income:** Payment, Gifts, Investments, Other

### Pages

| URL | Description |
|---|---|
| `/transactions` | Full transaction list with current balance |
| `/transactions/new` | Form to add a new transaction |
| `/transactions/edit/{id}` | Form to edit an existing transaction |
| `/expenses` | Expense list with category & month summaries |
| `/income` | Income list with category & month summaries |

---

## API Endpoints

| Method | Path | Description |
|---|---|---|
| `GET` | `/transactions` | List all transactions |
| `GET` | `/transactions/new` | Show create form |
| `POST` | `/transactions` | Save new transaction |
| `GET` | `/transactions/edit/{id}` | Show edit form |
| `POST` | `/transactions/{id}` | Update transaction |
| `POST` | `/transactions/{id}/delete` | Delete transaction |
| `GET` | `/expenses` | Expense summary page |
| `GET` | `/income` | Income summary page |

