# Portfolio Manager

A Spring Boot REST API for managing project portfolios, including project lifecycle management, member allocation, and portfolio reporting.

## Features

- **Project Management**: Create, update, delete, and list projects with pagination and filtering
- **Project Status Workflow**: Manage project lifecycle with defined status transitions
  - `IN_ANALYSIS` → `ANALYSIS_DONE` → `ANALYSIS_APPROVED` → `STARTED` → `PLANNED` → `IN_PROGRESS` → `CLOSED`
  - Projects can be `CANCELLED` from any state
- **Member Management**: Manage team members with different roles (EMPLOYEE, MANAGER)
- **Member Allocation**: Allocate and deallocate members to/from projects
- **Portfolio Reports**: Generate portfolio summary reports with statistics
- **JWT Authentication**: Secure API endpoints with JWT tokens
- **API Documentation**: Interactive API documentation with Swagger/OpenAPI

## Tech Stack

- **Java 17**
- **Spring Boot 3.2.0**
- **Spring Security** with JWT authentication
- **Spring Data JPA** with PostgreSQL
- **MapStruct** for object mapping
- **Lombok** for boilerplate reduction
- **SpringDoc OpenAPI** for API documentation
- **JaCoCo** for code coverage
- **H2** for testing

## Prerequisites

- Java 17 or higher
- Maven 3.8+ (or use the included Maven Wrapper)
- PostgreSQL 14+ (or use Docker)
- Docker & Docker Compose (optional, for containerized deployment)

## Getting Started

### Clone the repository

```bash
git clone <repository-url>
cd portfolio-manager
```

### Using Maven Wrapper

The project includes Maven Wrapper, so you don't need to install Maven locally.

**Linux/macOS:**
```bash
./mvnw clean install
```

**Windows:**
```cmd
mvnw.cmd clean install
```

### Database Setup

#### Option 1: Local PostgreSQL

Create a database named `portfolio_db`:

```sql
CREATE DATABASE portfolio_db;
```

Update `src/main/resources/application.yml` with your database credentials if different from defaults.

#### Option 2: Using Docker Compose

```bash
docker-compose up -d postgres
```

### Running the Application

**Using Maven:**
```bash
./mvnw spring-boot:run
```

**Using Java:**
```bash
./mvnw clean package -DskipTests
java -jar target/portfolio-manager-1.0.0.jar
```

**Using Docker Compose (full stack):**
```bash
docker-compose up -d
```

The application will be available at `http://localhost:8080`

## API Documentation

Once the application is running, access the Swagger UI at:

- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI Spec**: http://localhost:8080/api-docs

## Authentication

The API uses JWT authentication. Default credentials:

- **Username**: `admin`
- **Password**: `admin123`

### Getting a Token

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username": "admin", "password": "admin123"}'
```

Response:
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "type": "Bearer",
  "expiresIn": 1200000
}
```

### Using the Token

Include the token in the `Authorization` header:

```bash
curl -X GET http://localhost:8080/api/projects \
  -H "Authorization: Bearer <your-token>"
```

## API Endpoints

### Authentication
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/auth/login` | Authenticate and get JWT token |

### Projects
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/projects` | List all projects (paginated) |
| GET | `/api/projects/{id}` | Get project by ID |
| POST | `/api/projects` | Create a new project |
| PUT | `/api/projects/{id}` | Update a project |
| DELETE | `/api/projects/{id}` | Delete a project |
| PATCH | `/api/projects/{id}/status` | Update project status |
| POST | `/api/projects/{id}/members` | Allocate member to project |
| DELETE | `/api/projects/{id}/members/{memberId}` | Deallocate member |

### Members
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/members` | List all members |
| GET | `/api/members/{id}` | Get member by ID |
| POST | `/api/members` | Create a new member |
| PUT | `/api/members/{id}` | Update a member |
| DELETE | `/api/members/{id}` | Delete a member |

### External Members API (Public)
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/external/members` | List all members |
| GET | `/api/external/members/{id}` | Get member by ID |
| POST | `/api/external/members` | Create a member |

### Portfolio Reports
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/portfolio/report` | Get portfolio summary report |

## Project Status Workflow

```
IN_ANALYSIS → ANALYSIS_DONE → ANALYSIS_APPROVED → STARTED → PLANNED → IN_PROGRESS → CLOSED
     ↓              ↓               ↓                ↓          ↓           ↓
     └──────────────┴───────────────┴────────────────┴──────────┴───────────┴──→ CANCELLED
```

### Deletion Rules
- Projects can be deleted only in states: `IN_ANALYSIS`, `ANALYSIS_DONE`, `ANALYSIS_APPROVED`, `PLANNED`, `CANCELLED`
- Projects in `STARTED`, `IN_PROGRESS`, or `CLOSED` cannot be deleted

## Running Tests

```bash
# Run all tests
./mvnw test

# Run tests with coverage report
./mvnw test jacoco:report

# Coverage report will be available at: target/site/jacoco/index.html
```

## Docker

### Build the Image

```bash
docker build -t portfolio-manager:latest .
```

### Run with Docker Compose

```bash
# Start all services (app + postgres)
docker-compose up -d

# View logs
docker-compose logs -f app

# Stop all services
docker-compose down

# Stop and remove volumes
docker-compose down -v
```

### Environment Variables

| Variable | Description | Default |
|----------|-------------|---------|
| `SPRING_DATASOURCE_URL` | Database JDBC URL | `jdbc:postgresql://localhost:5432/portfolio_db` |
| `SPRING_DATASOURCE_USERNAME` | Database username | `postgres` |
| `SPRING_DATASOURCE_PASSWORD` | Database password | `postgres` |
| `JWT_SECRET` | JWT signing secret | (default in application.yml) |
| `JWT_EXPIRATION` | JWT expiration in ms | `1200000` |
| `SERVER_PORT` | Application port | `8080` |

## Project Structure

```
portfolio-manager/
├── src/
│   ├── main/
│   │   ├── java/com/portfolio/
│   │   │   ├── config/          # Configuration classes
│   │   │   ├── controller/      # REST controllers
│   │   │   ├── dto/             # Data Transfer Objects
│   │   │   │   ├── request/     # Request DTOs
│   │   │   │   └── response/    # Response DTOs
│   │   │   ├── entity/          # JPA entities
│   │   │   ├── enums/           # Enumerations
│   │   │   ├── exception/       # Custom exceptions
│   │   │   ├── mapper/          # MapStruct mappers
│   │   │   ├── repository/      # Spring Data repositories
│   │   │   ├── security/        # Security configuration
│   │   │   ├── service/         # Business logic
│   │   │   └── specification/   # JPA Specifications
│   │   └── resources/
│   │       └── application.yml  # Application configuration
│   └── test/
│       └── java/com/portfolio/  # Test classes
├── .mvn/wrapper/                # Maven Wrapper files
├── mvnw                         # Maven Wrapper script (Unix)
├── mvnw.cmd                     # Maven Wrapper script (Windows)
├── Dockerfile                   # Docker image definition
├── docker-compose.yml           # Docker Compose configuration
├── pom.xml                      # Maven configuration
└── README.md                    # This file
```

## License

This project is licensed under the MIT License.
