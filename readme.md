# SecureBank

## Overview
SecureBank is a modern Java-based banking application built with Spring Boot. The application provides core banking functionality including account management, transaction processing, and notifications.

## Core Features

### Account Management
- Create and manage bank accounts
- Track account balances
- Generate account statements
- IBAN-based account identification

### Transaction Processing
- Deposits
- Withdrawals
- Account transfers
- Transaction history

### Notifications System
- Real-time transaction alerts
- Multiple delivery channels (App, Email, SMS, Push)
- Read/unread status tracking
- Customizable notification types

## Technical Architecture

### Technology Stack
- Java 21+
- Spring Boot
- Spring Data JPA
- Hibernate
- MySQL (Database)
- Lombok (Reducing boilerplate)
- JUnit & Mockito (Testing)

### Key Components

#### Transaction Service
The transaction service handles all financial operations with strict validation and atomic execution:
- Ensures positive transaction amounts
- Verifies sufficient funds
- Updates account balances
- Creates detailed transaction records

```java
@Transactional
public void processTransfer(Account sourceAccount, Account destinationAccount, BigDecimal amount) {
    validateAmount(amount);
    validateSufficientFunds(sourceAccount, amount);
    
    // Create transactions and update balances
    // ...
}
```

#### Notification Service
The notification service tracks all system events:
- Creates notifications for transactions
- Manages delivery status
- Tracks read/unread status
- Supports multiple delivery channels

```java
@Transactional
public Notification createNotification(Account account, Transaction transaction,
                                     Notification.NotificationType type,
                                     String message,
                                     Notification.DeliveryChannel deliveryChannel) {
    // Create and save notification
    // ...
}
```

## Database Schema

### Main Entities
- `User`: Stores user details
- `Account`: Stores account details and balances
- `Transaction`: Records all financial activities
- `Notification`: Tracks system messages and alerts

### Entity Relationships
- A User can have one Account
- An Account can have many Transactions
- A Transaction belongs to one Account
- An Account can have many Notifications
- A Notification may reference a Transaction

## Setup Instructions

### Prerequisites
- Java 21 or higher
- Maven
- MySQL database

### Configuration
1. Clone the repository
2. Configure database connection in `application.properties`:
   ```
   spring.datasource.url=jdbc:mysql://localhost:3306/banking_db
   spring.datasource.username=user
   spring.datasource.password=password
   ```
3. Set custom application port (default is 8080):
   ```
   server.port=8081
   ```

### Running the Application
```bash
./mvnw spring-boot:run
```

## API Documentation

### Transaction Endpoints
- `POST /api/account/{account-id}/deposit` - Make a deposit
- `POST /api/account/{account-id}/withdraw` - Make a withdrawal
- `POST /api/account/{account-id}/transfer` - Transfer between accounts
- `GET /api/transactions/account/{account-id}` - Get transactions for an account

### Notification Endpoints
- `GET /api/notifications/account/{account-id}` - Get all notifications
- `GET /api/notifications/account/{account-id}/unread` - Get unread notifications
- `PUT /api/notifications/{id}/read` - Mark notification as read
- `PUT /api/notifications/account/{account-id}/read-all` - Mark all notifications as read


## Future Enhancements
- Integration with accounting software
- Mobile application
- Scheduled payments and recurring transfers
- Reporting and analytics dashboard
