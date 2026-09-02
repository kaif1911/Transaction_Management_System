## Understanding of the Problem

- The purpose of this application is to provide a simple REST API for managing customer transactions.

- I understood the main requirement as maintaining transaction information and providing APIs to create a transaction, retrieve a transaction using its transaction ID, update its status, and retrieve all transactions belonging to a particular customer.
- I decided to keep the application simple and follow a standard Spring Boot layered approach.
The controller is responsible for receiving HTTP requests, the service layer contains the business logic and validations, and the repository layer handles communication with the H2 database.

## Assumptions I Made

- I assumed that every transaction must have a unique `transactionId`. If the same ID is submitted again, I treat it as a duplicate transaction.

- I assumed that `customerId` is required because every transaction should belong to a customer.

- I assumed that searching for transactions by customer ID should return an empty list when the customer has no transactions rather than treating it as an error.

- I used H2 as the database because it is already part of the starter project and is sufficient for this exercise.

- I added status and currency lookup APIs as additional functionality because the assignment allows the developer to decide on additional API design.


## Validation Rules

   ### 1.Request Validation
- `transactionId` is mandatory and cannot be blank.
- `customerId` is mandatory and cannot be blank.

### 2.Business Validation
- Currency must be one of `INR`, `USD`, or `EUR`.
- Transaction type must be either `PAYMENT` or `REFUND`.
- Transaction status must be `PENDING`, `COMPLETED`, or `FAILED`.


### 3.Invalid Requests
 - If any validation rule fails, the request is rejected with an appropriate `400 Bad Request` response. Business-specific errors such as duplicate transaction IDs are handled using custom exceptions and the global exception handler.


## API Endpoints I Built
implemented the four required transaction operations and added one additional lookup APIs.

| Method | Endpoint                                    | Purpose |
|---|---------------------------------------------|---|
| POST | `/api/transactions`                         | Create a new transaction |
| GET | `/api/transactions/{transactionId}`         | Get a transaction by transaction ID |
| PATCH |  `/api/transactions/{transactionId}/status` | Update the transaction status |
| GET | `/api/transactions/customer/{customerId}`   | Get all transactions for a customer |
| GET | `/api/transactions/status/{status}`         | Get transactions by status |

## How I Approached Testing

I focused on testing both successful operations and the main failure scenarios.

For the service layer, I used JUnit 5 and Mockito. I mocked the repository so that I could test the service business logic without depending on the database.

The main scenarios I tested were:

- Creating a transaction successfully.
- Rejecting a transaction when the transaction ID already exists.
- Getting an existing transaction successfully.
- Returning an error when a transaction does not exist.
- Successfully changing a transaction from `PENDING` to `COMPLETED`.
- Rejecting an invalid status transition.
- Getting all transactions for a customer.
- Handling a customer with no transactions.

## Known Limitations
There are a few limitations in the current implementation.

- H2 is used as an in-memory database, so it is suitable for this exercise but not intended as a production database.
- There is no authentication or authorization because it was outside the scope of the exercise.
- The lookup APIs currently return all matching records and do not support pagination.

## What I Would Improve With More Time
I focused first on understanding the requirements and implementing the main functionality correctly:

- I would add more test cases, especially for validation, exception scenarios, and controller APIs.

- I would improve the API documentation by adding more request and response examples in Swagger/OpenAPI.

- I would add pagination to the customer transaction lookup because a customer could have a large number of transactions.

- I would consider using enums for transaction status, transaction type, and currency instead of plain strings.

- I would improve the error response format so that all errors have a consistent structure.





