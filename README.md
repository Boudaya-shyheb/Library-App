# 📚 Library Microservices — Space Service

A production-ready distributed Web Application built with **Spring Boot 3**, **Spring Cloud**, **MySQL**, and **Maven**.

---

## 🏗️ Architecture Overview

```
                        ┌──────────────────────────────┐
                        │      API Gateway :8080        │  ← Only exposed port
                        │  (Spring Cloud Gateway)       │
                        └────────┬─────────────┬────────┘
                                 │             │
               ┌─────────────────┘             └──────────────────┐
               ▼                                                   ▼
   ┌───────────────────────┐                      ┌───────────────────────────┐
   │   Space Service :8081 │ ──── OpenFeign ────► │ Reservation Service :8082 │
   │  (Library Spaces CRUD)│                      │ (Reservations CRUD)       │
   └───────────────────────┘                      └───────────────────────────┘
               │                                                   │
               └──────────────────┬────────────────────────────────┘
                                  ▼
                    ┌─────────────────────────┐
                    │  Eureka Server :8761     │
                    │  (Service Registry)      │
                    └─────────────────────────┘
```

---

## 📦 Project Structure

```
library-microservices/
│
├── eureka-server/                        # Service registry (port 8761)
│   ├── pom.xml
│   └── src/main/
│       ├── java/com/library/eurekaserver/
│       │   ├── EurekaServerApplication.java
│       │   └── config/SecurityConfig.java
│       └── resources/application.properties
│
├── api-gateway/                          # Single entry point (port 8080)
│   ├── pom.xml
│   └── src/main/
│       ├── java/com/library/apigateway/
│       │   └── ApiGatewayApplication.java
│       └── resources/application.properties
│
├── space-service/                        # Library Space Management (port 8081)
│   ├── pom.xml
│   └── src/main/java/com/library/spaceservice/
│       ├── SpaceServiceApplication.java
│       ├── entity/
│       │   ├── LibrarySpace.java         # JPA Entity
│       │   ├── SpaceType.java            # Enum: ROOM, STUDY_AREA, LAB...
│       │   └── SpaceAvailability.java    # Enum: AVAILABLE, OCCUPIED...
│       ├── repository/
│       │   └── LibrarySpaceRepository.java
│       ├── dto/
│       │   ├── LibrarySpaceRequest.java
│       │   ├── LibrarySpaceResponse.java
│       │   ├── ReservationResponse.java  # Feign response DTO
│       │   └── ApiResponse.java          # Generic envelope
│       ├── mapper/
│       │   └── LibrarySpaceMapper.java
│       ├── service/
│       │   ├── LibrarySpaceService.java  # Interface
│       │   └── impl/LibrarySpaceServiceImpl.java
│       ├── controller/
│       │   └── LibrarySpaceController.java
│       ├── client/
│       │   └── ReservationClient.java    # OpenFeign client
│       └── exception/
│           ├── ResourceNotFoundException.java
│           ├── BusinessException.java
│           └── GlobalExceptionHandler.java
│
└── reservation-service/                  # Reservation Management (port 8082)
    ├── pom.xml
    └── src/main/java/com/library/reservationservice/
        ├── ReservationServiceApplication.java
        ├── entity/
        │   ├── Reservation.java
        │   └── ReservationStatus.java
        ├── repository/
        │   └── ReservationRepository.java
        ├── dto/
        │   ├── ReservationRequest.java
        │   └── ReservationResponse.java
        ├── service/
        │   └── ReservationService.java
        ├── controller/
        │   └── ReservationController.java
        └── exception/
            └── GlobalExceptionHandler.java
```

---

## 🔌 Port Configuration

| Service              | Port  | Description                        |
|----------------------|-------|------------------------------------|
| `eureka-server`      | 8761  | Eureka dashboard (internal)        |
| `api-gateway`        | **8080** | ✅ **Only external-facing port** |
| `space-service`      | 8081  | Internal — reached via gateway     |
| `reservation-service`| 8082  | Internal — reached via gateway     |
| `MySQL`              | 3306  | Database (local)                   |

---

## 🗄️ MySQL Databases

Two separate schemas (auto-created on startup via `createDatabaseIfNotExist=true`):

| Database              | Used by              |
|-----------------------|----------------------|
| `library_spaces`      | `space-service`      |
| `library_reservations`| `reservation-service`|

> **Before starting**, update the MySQL password in `application.properties` of both `space-service` and `reservation-service`.

---

## 🚀 Running the Project

> ⚠️ **Order matters**. Always start Eureka first.

### Step 1 — Eureka Server
```bash
cd eureka-server
mvn spring-boot:run
# Visit: http://localhost:8761  (login: admin / admin123)
```

### Step 2 — Space Service
```bash
cd space-service
mvn spring-boot:run
```

### Step 3 — Reservation Service
```bash
cd reservation-service
mvn spring-boot:run
```

### Step 4 — API Gateway
```bash
cd api-gateway
mvn spring-boot:run
```

---

## 🧪 API Testing (via Gateway — port 8080 only)

### Space Service Endpoints

| Method | URL | Description |
|--------|-----|-------------|
| `GET` | `/api/spaces` | List all spaces |
| `GET` | `/api/spaces/{id}` | Get space by ID |
| `GET` | `/api/spaces/status/AVAILABLE` | Filter by status |
| `GET` | `/api/spaces/type/LAB` | Filter by type |
| `GET` | `/api/spaces/search?keyword=room` | Search by name |
| `GET` | `/api/spaces/capacity?min=20` | Filter by min capacity |
| `GET` | `/api/spaces/{id}/reservations` | Get reservations (via Feign) |
| `POST` | `/api/spaces` | Create a space |
| `PUT` | `/api/spaces/{id}` | Update a space |
| `PATCH` | `/api/spaces/{id}/status?status=OCCUPIED` | Update status |
| `DELETE` | `/api/spaces/{id}` | Soft-delete a space |

### Quick Test Examples

```bash
# Create a space
curl -X POST http://localhost:8080/api/spaces \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Study Room A",
    "type": "ROOM",
    "capacity": 10,
    "location": "Building B, Floor 2",
    "description": "Quiet study room",
    "availabilityStatus": "AVAILABLE",
    "equipment": "Whiteboard, Projector"
  }'

# List all spaces
curl http://localhost:8080/api/spaces

# Update availability status
curl -X PATCH "http://localhost:8080/api/spaces/1/status?status=OCCUPIED"

# Get reservations for a space (Feign call to reservation-service)
curl http://localhost:8080/api/spaces/1/reservations

# Create a reservation
curl -X POST http://localhost:8080/api/reservations \
  -H "Content-Type: application/json" \
  -d '{
    "spaceId": 1,
    "userId": "user-001",
    "userFullName": "Alice Martin",
    "startTime": "2026-03-01T09:00:00",
    "endTime": "2026-03-01T11:00:00",
    "purpose": "Group study session"
  }'
```

---

## 🔗 OpenFeign — Inter-Service Communication

`space-service` calls `reservation-service` via **OpenFeign** without any hardcoded URL:

```java
// ReservationClient.java (space-service)
@FeignClient(name = "reservation-service", path = "/api/reservations")
public interface ReservationClient {
    @GetMapping("/space/{spaceId}")
    List<ReservationResponse> getReservationsBySpaceId(@PathVariable Long spaceId);
}
```

Eureka resolves `reservation-service` → `localhost:8082` automatically through Spring Cloud LoadBalancer.

---

## 🛡️ SpaceAvailability Status Transitions

```
AVAILABLE ──► OCCUPIED
AVAILABLE ──► RESERVED
AVAILABLE ──► MAINTENANCE  (auto-cancels all reservations via Feign)
OCCUPIED  ──► AVAILABLE
OCCUPIED  ──► MAINTENANCE  (auto-cancels all reservations via Feign)
ANY       ──► CLOSED       (on soft-delete)
```

---

## 🔧 Dependencies Summary

| Dependency | Purpose |
|---|---|
| `spring-boot-starter-web` | REST API |
| `spring-boot-starter-data-jpa` | Database ORM |
| `spring-boot-starter-validation` | Bean Validation |
| `mysql-connector-j` | MySQL JDBC Driver |
| `spring-cloud-starter-netflix-eureka-server` | Eureka Registry |
| `spring-cloud-starter-netflix-eureka-client` | Service Registration |
| `spring-cloud-starter-gateway` | API Gateway (reactive) |
| `spring-cloud-starter-openfeign` | Declarative HTTP client |
| `spring-cloud-starter-loadbalancer` | Client-side load balancing |
| `spring-boot-starter-security` | Eureka dashboard auth |
| `spring-boot-starter-actuator` | Health/metrics endpoints |
| `lombok` | Boilerplate reduction |

**Spring Boot:** `3.2.3` | **Spring Cloud:** `2023.0.0` | **Java:** `17`
