# Loan API Backend

This is a Spring Boot-based backend application for managing loans in a bank. It provides endpoints to create, list, and pay loans for customers. The system also supports handling installment payments, loan creation with interest rates, and due dates. It includes a robust authentication system with **JWT tokens** for security and user authorization.

## Features

-   **Create Loan**: Create a new loan for a customer with an amount, interest rate, and number of installments.
-   **List Loans**: List all loans for a specific customer.
-   **List Installments**: List installments for a given loan.
-   **Pay Loan**: Pay installments for a loan with rewards and penalties based on payment timing.
-   **JWT Authentication**: Secure the application using JSON Web Tokens (JWT) for both **ADMIN** and **CUSTOMER** users.

## Prerequisites

-   Java 21
-   Maven
-   H2 Database (configured by default for development)
-   Spring Security with JWT for user authentication and authorization

## Running the Application

### 1. Clone the Repository

bash

Copy code

`git clone https://github.com/erhanyil/loan-api.git
cd loan-api`

### 2. Build the Application

bash

Copy code

`mvn clean install`

### 3. Run the Application

You can run the Spring Boot application using Maven:

bash

Copy code

`mvn spring-boot:run`

By default, the application will start on port `8080`.

### 4. Access the Application

-   Open your browser or API client and navigate to `http://localhost:8080`.
-   Postman collection 'loan-api.postman_collection.json' in the root directory.

## API Endpoints

### Authentication

#### 1. **Generate JWT Token**

-   **URL**: `/auth/token`

-   **Method**: `POST`

-   **Request Body**:

    json

    Copy code

    `{
    "username": "admin@example.com",
    "password": "123"
    }`

-   **Description**: Generates a JWT token for the user. This token is required for accessing protected endpoints.

-   **Example Response**:

    json

    Copy code

    `{
    "token": "your_jwt_token_here"
    }`


#### 2. **Get User Credentials**

-   **URL**: `/auth`

-   **Method**: `GET`

-   **Description**: Returns the credentials of the currently authenticated user.

-   **Headers**: Include the JWT token in the Authorization header:  
    `Authorization: Bearer <your_jwt_token_here>`


----------

### Loan Management

#### 1. **Create Loan**

-   **URL**: `/api/loans`

-   **Method**: `POST`

-   **Headers**: Include the JWT token in the Authorization header:  
    `Authorization: Bearer <your_jwt_token_here>`

-   **Request Body**:

    json

    Copy code

    `{
    "customerId": 1,
    "loanAmount": 1000,
    "interestRate": 0.2,
    "numberOfInstallments": 6
    }`

-   **Description**: Creates a new loan for the specified customer, with the given loan amount, interest rate, and number of installments.

-   **Authorization**: Only **ADMIN** role users can create loans.


#### 2. **List Loans for a Customer**

-   **URL**: `/api/loans/customer/{customerId}`

-   **Method**: `GET`

-   **Parameters**: `customerId` (path variable)

-   **Description**: Lists all loans for the specified customer.

-   **Authorization**:

    -   **ADMIN** users can view loans for all customers.
    -   **CUSTOMER** users can only view their own loans.

#### 3. **List Installments for a Loan**

-   **URL**: `/api/loans/{loanId}/installments`

-   **Method**: `GET`

-   **Parameters**: `loanId` (path variable)

-   **Description**: Lists all installments for the specified loan.

-   **Authorization**:

    -   **ADMIN** users can view installments for any loan.
    -   **CUSTOMER** users can only view installments for their own loans.

#### 4. **Pay Loan Installment(s)**

-   **URL**: `/api/loans/{loanId}/pay`

-   **Method**: `POST`

-   **Request Body**:

    json

    Copy code

    `{
    "amount": 100
    }`

-   **Description**: Pays installments for a loan with the specified amount. Payments can be applied to multiple installments as long as the amount is sufficient. Rewards and penalties are applied based on payment timing (early or late payments).

-   **Authorization**:

    -   **ADMIN** users can pay for any loan.
    -   **CUSTOMER** users can only pay installments for their own loans.

----------

## User Roles

The system supports two types of users:

-   **ADMIN**
    -   Can manage loans and installments for all customers.
    -   Example user: `username: admin@example.com, password: 123, role: ADMIN`
-   **CUSTOMER**
    -   Can only manage loans and installments for their own account.
    -   Example users:
        -   `username: john.doe@example.com, password: 123, role: CUSTOMER`
        -   `username: jane.smith@example.com, password: 123, role: CUSTOMER`

### Authorization Header

All API endpoints are secured with basic authentication using JWT tokens. To generate a token, use the `/auth/token` endpoint. Once you have the token, include it in the `Authorization` header for subsequent requests:

bash

Copy code

`Authorization: Bearer <your_jwt_token_here>`

----------

## Database Schema

### Customer Table

-   `id`: Unique identifier
-   `name`: Customer's first name
-   `surname`: Customer's last name
-   `creditLimit`: Total available credit limit for the customer
-   `usedCreditLimit`: Amount of credit already used by the customer

### Loan Table

-   `id`: Unique identifier for the loan
-   `customerId`: Foreign key referencing the customer
-   `loanAmount`: The total amount of the loan
-   `numberOfInstallment`: Number of installments for the loan
-   `createDate`: Date when the loan was created
-   `isPaid`: Boolean indicating whether the loan is fully paid

### LoanInstallment Table

-   `id`: Unique identifier for the installment
-   `loanId`: Foreign key referencing the loan
-   `amount`: The amount of the installment
-   `paidAmount`: The amount that has been paid for the installment
-   `dueDate`: The due date for the installment (set to the first day of the month)
-   `paymentDate`: Date when the payment was made
-   `isPaid`: Boolean indicating whether the installment has been paid

----------

## Testing

Unit tests are written using JUnit and Mockito. To run tests:

bash

Copy code

`mvn test`

## Bonus Features

### 1. **Customer vs Admin Authorization**:

Customers can only access and manage their own loans, while Admin users can manage loans for all customers.

### 2. **Reward and Penalty**:

-   **Early Payment**: A discount is applied based on how many days before the due date the payment is made.
-   **Late Payment**: A penalty is applied based on how many days after the due date the payment is made.

## Conclusion

This Spring Boot application provides a comprehensive solution for managing loans, handling customer credit, and applying payment rewards or penalties. It’s designed to be deployed in a production or test environment and is fully secured with role-based authorization using JWT.
