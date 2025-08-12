# Addison Global Backend Technical Assessment

This project implements the Addison Global Backend Technical Assessment, providing a token issuance system using Akka Typed for asynchronous processing and Spring Boot for a REST API. The system authenticates users, generates tokens with random delays, and fails token generation for user IDs starting with 'A', as per the requirements.

## Project Structure

- **Language**: Java 17
- **Framework**: Spring Boot 2.7.18
- **Actor System**: Akka Typed 2.8.5
- **Build Tool**: Maven
- **IDE**: Visual Studio Code (recommended, but compatible with any Java IDE)

The project is structured into three stages:
- **Etapa 1**: Interfaces `SyncTokenService` and `AsyncTokenService` defining the token issuance contract.
- **Etapa 2**: Implementation using Akka actors (`AuthActor`, `TokenActor`, `TokenOrchestratorActor`) and `SimpleAsyncTokenService` for asynchronous token requests with random delays (0-5000ms).
- **Etapa 3**: REST API with Spring Boot, exposing a `/auth` endpoint for token issuance.

## Requirements

- **Java**: OpenJDK 17.0.16 or compatible
- **Maven**: 3.8.6 or higher
- **Git**: Latest version
- **Visual Studio Code**: Optional, for editing and debugging
- **Operating System**: Windows or Linux

## Setup Instructions

### 1. Clone the Repository
```bash
git clone https://github.com/max-ramos-rod/addison-backend-akka.git
cd addison-backend-akka
```

### 2. Build the Project
```bash
mvn clean package
```

### 3. Run the Application
Run the compiled JAR:
```bash
java -jar target/addison-backend-akka-1.0-SNAPSHOT.jar
```
Or use the Spring Boot Maven plugin:
```bash
mvn spring-boot:run
```

On Windows, ensure the console uses UTF-8 to display logs correctly:
```cmd
chcp 65001
mvn spring-boot:run
```

### 4. Test the `/auth` Endpoint
- **Valid Credentials** (username not starting with 'A', password in uppercase):
  ```bash
  curl -X POST http://localhost:8080/auth -H "Content-Type: application/json" -d "{\"username\":\"house\",\"password\":\"HOUSE\"}"
  ```
  **Expected Response**:
  ```json
  {"success":true,"token":"house_2025-08-12T10:48:00Z","error":null}
  ```

- **Invalid User ID** (username starting with 'A'):
  ```bash
  curl -X POST http://localhost:8080/auth -H "Content-Type: application/json" -d "{\"username\":\"alice\",\"password\":\"ALICE\"}"
  ```
  **Expected Response**:
  ```json
  {"success":false,"token":null,"error":"Authentication or token generation failed"}
  ```

- **Invalid Credentials** (password not matching username in uppercase):
  ```bash
  curl -X POST http://localhost:8080/auth -H "Content-Type: application/json" -d "{\"username\":\"house\",\"password\":\"house\"}"
  ```
  **Expected Response**:
  ```json
  {"success":false,"token":null,"error":"Authentication or token generation failed"}
  ```

### 5. Run Unit Tests
Unit tests are provided for the `AuthController` using JUnit 5 and Spring Boot Test.
```bash
mvn test
```

## Technical Decisions

- **Akka Typed**: Chosen for its actor-based