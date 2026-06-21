# Bank Account Kata

A RESTful bank account service API built with **Spring Boot 3.5** and **Java 17**.

## Requirements Covered

- **Deposit and Withdrawal** — add or remove money from the account
- **Account Statement** — see the history of operations with date, amount, and balance
- **Statement Printing** — retrieve a formatted statement via the API

## Architecture


AccountController -> AccountService -> operation (domain)

### Layers

| Layer | Description |
|---|---|
| **Controller** | REST endpoints (`POST /deposit`, `POST /withdraw`, `GET /statement/print`) |
| **Service** | Thread-safe business logic with `synchronized` blocks and `CopyOnWriteArrayList` |
| **Domain** | `Operation` (type, amount, date, balanceAfter) and `OperationType` enum |
| **DTOs** | Java records with `@Positive` validation for deposit/withdraw requests |
| **Exception Handler** | `@RestControllerAdvice` for validation and business errors |

## API Endpoints

| Method | Path | Description | Response |
|---|---|---|---|
| `POST` | `/api/account/deposit` | Deposit an amount | `{"message": "Deposit successful", "amount": 100}` |
| `POST` | `/api/account/withdraw` | Withdraw an amount | `{"message": "Withdraw successful", "amount": 50}` |
| `GET` | `/api/account/statement/print` | Get account statement | Plain text formatted table |

## Example Usage

```bash
# Deposit
curl -X POST http://localhost:8080/api/account/deposit \
  -H "Content-Type: application/json" \
  -d '{"amount": 100}'

# Withdraw
curl -X POST http://localhost:8080/api/account/withdraw \
  -H "Content-Type: application/json" \
  -d '{"amount": 30}'

# Print statement
curl http://localhost:8080/api/account/statement/print
```

### Statement Output

```
Date                 | Type         | Amount    | Balance
------------------------------------------------------------
2026-06-21 10:00:00 | DEPOSIT      | 100.00    | 100.00
2026-06-21 10:01:00 | WITHDRAWAL   | 30.00     | 70.00
```



## Running

```bash
./mvnw spring-boot:run
```

## Tests

```bash
./mvnw test
```

22 tests covering:
- Service: deposits, withdrawals, insufficient balance, validation, chronological order, concurrency, statement formatting (13 tests)
- Controller: happy paths, validation errors, service exceptions, statement retrieval (8 tests)
- Application context load (1 test)
