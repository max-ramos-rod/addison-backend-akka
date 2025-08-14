# Addison Global Backend Technical Assessment

This project implements the Addison Global Backend Technical Assessment, providing a token issuance system using Akka Typed for asynchronous processing and Spring Boot for a REST API. The system authenticates users, generates tokens with random delays, and fails token generation for user IDs starting with 'A', as per the requirements.

## Project Structure

### Folder Structure
```
addison-backend-akka
├── .gitignore                  # Ignores target/, .vscode/, etc.
├── README.md                   # Project documentation
├── pom.xml                     # Maven configuration
├── src
│   ├── main
│   │   ├── java
│   │   │   ├── com
│   │   │   │   ├── addisonglobal
│   │   │   │   │   ├── Main.java                   # Spring Boot main class
│   │   │   │   │   ├── actors
│   │   │   │   │   │   ├── AuthActor.java          # Actor for authentication
│   │   │   │   │   │   ├── TokenActor.java         # Actor for token generation
│   │   │   │   │   │   ├── TokenOrchestratorActor.java  # Orchestrator actor
│   │   │   │   │   ├── api
│   │   │   │   │   │   ├── AuthRequestBody.java    # JSON request body
│   │   │   │   │   │   ├── AuthResponseBody.java   # JSON response body
│   │   │   │   │   ├── config
│   │   │   │   │   │   ├── AkkaConfiguration.java  # ActorSystem as Spring bean
│   │   │   │   │   ├── controller
│   │   │   │   │   │   ├── AuthController.java     # REST controller for /auth
│   │   │   │   │   ├── messages
│   │   │   │   │   │   ├── AuthRequest.java        # Authentication message
│   │   │   │   │   │   ├── AuthResult.java         # Authentication result
│   │   │   │   │   │   ├── Credentials.java        # Credentials class
│   │   │   │   │   │   ├── IssueTokenCommand.java  # Token issuance command
│   │   │   │   │   │   ├── TokenRequest.java       # Token generation message
│   │   │   │   │   │   ├── TokenResponse.java      # Token response
│   │   │   │   │   │   ├── TokenResult.java        # Token generation result
│   │   │   │   │   │   ├── User.java               # User class
│   │   │   │   │   │   ├── UserToken.java          # Token class
│   │   │   │   │   ├── service
│   │   │   │   │       ├── AsyncTokenService.java  # Asynchronous token interface
│   │   │   │   │       ├── SimpleAsyncTokenService.java  # Async service implementation
│   │   │   │   │       ├── SyncTokenService.java   # Synchronous token interface
│   │   └── resources
│   │       ├── logback-spring.xml                  # Logback configuration for UTF-8
│   └── test
│       └── java
│           ├── com
│               ├── addisonglobal
│                   ├── actors
│                   │   ├── AuthActorTest.java      # Tests for AuthActor
│                   │   ├── TokenActorTest.java     # Tests for TokenActor
│                   ├── controller
│                       ├── AuthControllerTest.java  # Tests for AuthController
```

### Interfaces
- **`SyncTokenService`**: Defines a synchronous contract for token issuance, with a method `issueToken(Credentials credentials)` that returns a `UserToken`. This interface is provided for completeness but is not used in the current implementation.

- **`AsyncTokenService`**: Defines an asynchronous contract with `requestToken(Credentials credentials)` returning a `CompletableFuture<UserToken>`. The `SimpleAsyncTokenService` implements this interface by delegating to the `TokenOrchestratorActor` via Akka's `ask` pattern, enabling non-blocking, resilient token issuance.

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
  {"success":true,"token":"house_2025-08-12T11:08:00Z","error":null}
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
Unit tests cover `AuthController`, `AuthActor`, and `TokenActor` using JUnit 5 and Akka TestKit.
```bash
mvn test
```

## Technical Decisions

- **Akka Typed**: Chosen for its actor-based model, enabling concurrent, non-blocking token issuance with random delays (0-5000ms for authentication and token generation).
- **Spring Boot**: Used for the REST API due to its simplicity and robust ecosystem, instead of Akka HTTP.
- **SLF4J Conflict**: Resolved by removing `slf4j-simple` and using `logback-classic` (included in `spring-boot-starter-web`) for consistent logging.
- **Character Encoding**: Configured Logback to use UTF-8 (`logback-spring.xml`) and set console encoding (`chcp 65001` on Windows) to display special characters (e.g., `Autenticação`).
- **Timeout**: Set to 10 seconds to accommodate maximum delays (5000ms for authentication + 5000ms for token generation).
- **Testing**: Unit tests for `AuthController`, `AuthActor`, and `TokenActor` ensure correct behavior for authentication, token generation, and error cases.
- **VSCode**: Used as the primary IDE, with `.gitignore` excluding `.vscode/` settings.

## Arquitetura

O serviço `SimpleAsyncTokenService` implementa `requestToken` coordenando um fluxo orquestrado pelo `TokenOrchestratorActor`, conforme requisitado no desafio. O uso de Akka Typed permite concorrência, não-bloqueio e resiliência, enquanto o Spring Boot expõe uma API REST simples e testável.

## Performance and Timing

This section explains the timing behavior of the system, including artificial delays, timeouts, and how they differ between testing and production environments.


### Simulated Delays in Actors

To simulate real-world latency (e.g., network calls, database access), both `AuthActor` and `TokenActor` introduce **random delays** before responding:

- **Authentication delay**: 0 to 5000 milliseconds
- **Token generation delay**: 0 to 5000 milliseconds

These delays are **intentional** to test resilience under latency and ensure the system handles timeouts gracefully.

> Example: A full authentication + token generation flow may take up to **10 seconds** in the worst case.

### Timeout Configuration

The `AuthController` uses a **10-second timeout** when communicating with the `TokenOrchestratorActor` via Akka's `ask` pattern. This ensures:
- The HTTP request does not hang indefinitely.
- Clients receive a timely response even under high latency.

If the actor system takes longer than 10 seconds, a `504 Gateway Timeout` is returned.

### Behavior in Tests

To ensure fast and reliable unit tests, the system uses different configurations:

| Environment | Max Delay (Auth) | Max Delay (Token) | Timeout |
|-----------|------------------|-------------------|--------|
| **Production** | 5000 ms | 5000 ms | 10 s |
| **Test** | 500 ms | 500 ms | 10 s |

This is achieved using Akka configuration files:
- `src/main/resources/application.conf` → production values
- `src/test/resources/application-test.conf` → test values

As a result, tests run quickly (typically under 1 second per case) while maintaining the same business logic.

### Why These Values?

- **5000ms max delay**: Simulates worst-case external service response.
- **10s timeout**: Conservative upper bound for two sequential 5s operations.
- **Reduced test delays**: Improves feedback speed during development without sacrificing correctness.

## Known Issues

- Character encoding in logs may require `chcp 65001` on Windows or `logback-spring.xml` to display correctly.

## Future Improvements

- Implement a token validation endpoint.
- Enhance logging with structured formats (e.g., JSON logging).
- Add integration tests for the full authentication flow.

## Troubleshooting

- **Compilation Errors**: Ensure `spring-boot-starter-test` and `akka-actor-testkit-typed` are in `pom.xml`, and tests are in `src/test/java/`.
- **Log Encoding**: Use `chcp 65001` on Windows or add `src/main/resources/logback-spring.xml`.
- **Git Push Errors**: Run `git pull origin main --rebase` to resolve conflicts with the remote repository.

## Contact

For questions or issues, contact [ramos.max@gmail.com].