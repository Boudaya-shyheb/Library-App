# System Architecture - Microservices Communication

## Overview Architecture

```
┌─────────────────────────────────────────────────────────────────┐
│                        CLIENT APPLICATIONS                       │
│                     (Web, Mobile, Desktop)                       │
└────────────────────────────┬────────────────────────────────────┘
                             │
                    All requests via HTTP
                             │
                   Port 9000 (Single Entry Point)
                             │
                             ▼
        ╔════════════════════════════════════════════╗
        ║         API GATEWAY (Spring Cloud)         ║
        ║      Service Discovery & Load Balancing    ║
        ║          CORS & Request Routing            ║
        └────────────────┬─────────────────────────┘
                         │
        ┌────────────────┼────────────────┬────────────────┬────────────┐
        │                │                │                │            │
        ▼                ▼                ▼                ▼            ▼
    ┌─────────────┐ ┌──────────────┐ ┌──────────────┐ ┌───────────┐ ┌──────────┐
    │   SPACE     │ │ RESERVATION  │ │   FORUM      │ │FOURNISSEUR│ │INVENTAIRE│
    │  SERVICE    │ │   SERVICE    │ │   SERVICE    │ │ SERVICE   │ │ SERVICE  │
    │  :8081      │ │    :8082     │ │    :9094     │ │   :8090   │ │  :9090   │
    └──────┬──────┘ └──────────────┘ └──────────────┘ └───────────┘ └──────────┘
           │
           │         OpenFeign Call
           │    (Service Discovery)
           │
           └──────────────────────────────► Reservation Service
                     Returns
                   Reservations

        ┌────────────────────────────────────────────┬───────────────┐
        │                                            │               │
        ▼                                            ▼               ▼
    ┌──────────────┐                        ┌──────────────┐  ┌────────────┐
    │ RECLAMATION  │                        │   EMPRUNT    │  │   OTHER    │
    │   SERVICE    │                        │   SERVICE    │  │  SERVICES  │
    │    :9093     │                        │    :9095     │  │            │
    └──────────────┘                        └──────────────┘  └────────────┘
```

---

## Service Registry (Eureka Server)

```
Eureka Server (localhost:8761)
├── Maintains registry of all services
├── Provides health checks
├── Enables service discovery
└── All services auto-register on startup

Registered Services:
├── api-gateway (9000)
├── space-service (8081)
├── reservation-service (8082)
├── fournisseur-service (8090)
├── inventaire-service (9090)
├── reclamation-service (9093)
├── forum-service (9094)
└── service-emprunt (9095)
```

---

## Communication Flows

### Flow 1: Client Request Through API Gateway

```
┌────────┐
│ Client │
└───┬────┘
    │ HTTP GET /api/spaces
    │ (Port 9000)
    │
    ▼
┌─────────────────────┐
│   API GATEWAY       │
│ Spring Cloud    │
│ Route: /api/spaces║
│ → space-service     │
└─────┬───────────────┘
      │ Load Balancer
      │ discovers space-service:8081
      │
      ▼
┌──────────────────┐
│ SPACE SERVICE    │
│ (port 8081)      │
│ GET /api/spaces  │
└─────┬────────────┘
      │
      ▼
┌──────────────────┐
│ DATABASE (MySQL) │
│ library_spaces   │
└──────────────────┘

Response flows back:
Service → Gateway → Client ✓
```

### Flow 2: Service-to-Service Communication via OpenFeign

```
┌─────────────────┐
│   API Client    │
└────┬────────────┘
     │ GET /api/spaces/1/reservations
     │
     ▼
┌────────────────────────┐
│    API GATEWAY         │ (port 9000)
│  Routes to space-service
└────┬───────────────────┘
     │
     ▼
┌─────────────────────────┐
│   SPACE SERVICE         │ (port 8081)
│ @FeignClient(name =     │
│   "reservation-service")│
│ GET /api/spaces/1/      │
│     reservations        │
└────┬───────────────────┘
     │ Feign Call (OpenFeign)
     │ Service Discovery: "reservation-service"
     │ Load Balance
     │
     ▼
┌─────────────────────────┐
│ RESERVATION SERVICE     │ (port 8082)
│ GET /api/reservations   │
│ for space/1             │
└────┬────────────────────┘
     │
     ▼
┌──────────────────┐
│ DATABASE (MySQL) │
│ reservations     │
└──────────────────┘

Response flows back:
DB → Reservation Service → Space Service → Gateway → Client ✓
```

### Flow 3: Multiple Services Interacting

```
┌────────────────────────────┐
│  FRONTEND APPLICATION      │
│   (Single Entry Point)     │
└────────────┬───────────────┘
             │
      POST /api/spaces (9000)
      GET /api/reservations (9000)
      POST /api/reclamations (9000)
             │
             ▼
     ┌───────────────────┐
     │   API GATEWAY     │
     │   :9000           │
     └─────┬─────┬─────┬─┘
           │     │     │
     Route1│   Route2│  Route3│
         │     │     │
         ▼     ▼     ▼
    ┌──────────────┬──────────────┬───────────────┐
    │    SPACE     │ RESERVATION  │ RECLAMATION   │
    │   :8081      │    :8082     │    :9093      │
    └──┬────┬──────┴────┬─────────┴────┬───────┬──┘
       │    │OpenFeign  │OpenFeign     │       │
       │    └──────────►│◄─────────────┘       │
       │               │                       │
       ▼               ▼                       ▼
    SPACE DB      RESERVATION DB         RECLAMATION DB
    (MySQL)       (MySQL)                 (MySQL)

Result: Complex business logic across multiple services ✓
```

---

## Data Flow Diagram

```
                    REQUEST PHASE
┌──────────────────────────────────────────┐
│ 1. Client sends HTTP request             │
│ 2. API Gateway receives request          │
│ 3. Gateway routes based on path          │
│ 4. Service receives request              │
│ 5. Service processes (may call other svc)│
│ 6. Service accesses database             │
└──────────────────────────────────────────┘
                     │
                     ▼
              RESPONSE PHASE
┌──────────────────────────────────────────┐
│ 1. Database returns data                 │
│ 2. Service processes response            │
│ 3. Service returns to API Gateway        │
│ 4. Gateway returns to client             │
│ 5. Client receives JSON response         │
└──────────────────────────────────────────┘
```

---

## Load Balancing Strategy

```
API Gateway Receives Request for space-service
        │
        ▼
    Eureka Query: "Get all instances of space-service"
        │
        ├──► Instance 1: space-service:8081 (UP)
        ├──► Instance 2: space-service:8081 (UP - if scaled)
        └──► Instance 3: ...
        │
        ▼
    Spring Cloud LoadBalancer selects one instance
    (Round-robin, least-connection, etc.)
        │
        ▼
    Request forwarded to selected instance
        │
        ▼
    Instance processes & returns response
```

---

## Service Dependencies

```
API GATEWAY
├─ Depends on: Eureka (for service discovery)
├─ Calls: All services listed below
└─ Config: spring.cloud.gateway.routes[*]

SPACE SERVICE
├─ Depends on: Eureka, MySQL, ReservationFeignClient
├─ Database: library_spaces (MySQL)
├─ Calls: Reservation Service (OpenFeign)
└─ Endpoints: /api/spaces

RESERVATION SERVICE
├─ Depends on: Eureka, MySQL
├─ Database: library_reservations (MySQL)
├─ Calls: (called by Space, others)
└─ Endpoints: /api/reservations

FORUM SERVICE
├─ Depends on: Eureka, H2
├─ Database: forumdb (H2 in-memory)
├─ Calls: (configurable)
└─ Endpoints: /api/forum

FOURNISSEUR SERVICE
├─ Depends on: Eureka, MySQL
├─ Database: bibliotheque (MySQL)
├─ Calls: (configurable)
└─ Endpoints: /api/fournisseurs

INVENTAIRE SERVICE
├─ Depends on: Eureka, H2
├─ Database: inventory_db (H2 in-memory)
├─ Calls: (configurable)
└─ Endpoints: /api/inventaire

RECLAMATION SERVICE
├─ Depends on: Eureka, MySQL, Flyway
├─ Database: reclamationdb (MySQL)
├─ Calls: (configurable)
└─ Endpoints: /api/reclamations

EMPRUNT SERVICE
├─ Depends on: Eureka, MySQL
├─ Database: library_emprunt_db (MySQL)
├─ Calls: (configurable)
└─ Endpoints: /api/emprunts
```

---

## Configuration Highlights

### Service Discovery Configuration (All Services)
```properties
# Enable Eureka Client
spring.application.name=<service-name>
eureka.client.service-url.defaultZone=http://admin:admin123@localhost:8761/eureka/
eureka.client.register-with-eureka=true
eureka.client.fetch-registry=true
```

### OpenFeign Configuration (Services that Call Others)
```java
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients  // Enable OpenFeign
public class SpaceServiceApplication { }

// Declare Feign Client
@FeignClient(name = "reservation-service")
public interface ReservationFeignClient {
    @GetMapping("/api/reservations/space/{spaceId}")
    List<Reservation> getReservations(@PathVariable Long spaceId);
}
```

### API Gateway Configuration
```properties
spring.cloud.gateway.discovery.locator.enabled=true
# Auto-discover services from Eureka

spring.cloud.gateway.routes[0].id=space-service
spring.cloud.gateway.routes[0].uri=lb://space-service
spring.cloud.gateway.routes[0].predicates[0]=Path=/api/spaces/**
# Load-balance to space-service instances
```

---

## Scalability

The architecture supports horizontal scaling:

```
If you need more space-service instances:
1. Start another instance on different port (or use containers)
2. It auto-registers with Eureka
3. API Gateway & Feign clients automatically use all instances
4. Load balancing distributes traffic across instances

Example:
space-service:8081 (Instance 1)
space-service:8082 (Instance 2 - new)
space-service:8083 (Instance 3 - new)

API Gateway automatically routes to all 3 instances!
```

---

## Security Considerations

```
In Production:
1. Enable HTTPS/TLS on all endpoints
2. Implement OAuth2/JWT authentication
3. Add rate limiting to API Gateway
4. Configure service-to-service authentication
5. Enable Eureka security (dashboard/API)
6. Use environment variables for secrets
7. Implement circuit breakers for resilience
```

---

## Monitoring Points

Key elements to monitor:

```
1. Eureka Dashboard
   └─ Service registration status
   └─ Instance health/uptime
   └─ Renewal rate

2. API Gateway
   └─ Request rate/response time
   └─ Error rates
   └─ Route utilization

3. Individual Services
   └─ CPU/Memory usage
   └─ Database connection pool
   └─ Request latency
   └─ Error rates

4. OpenFeign Clients
   └─ Service call success rate
   └─ Timeout/retry count
   └─ Circuit breaker status

5. Databases
   └─ Query performance
   └─ Connection pool utilization
   └─ Data consistency
```

---

## Summary

The microservices architecture provides:

✅ **Single Entry Point** - API Gateway (9000)
✅ **Service Discovery** - Eureka Server (8761)
✅ **Load Balancing** - Spring Cloud LoadBalancer
✅ **Service-to-Service Communication** - OpenFeign
✅ **Scalability** - Easy horizontal scaling
✅ **Resilience** - Health checks, circuit breakers
✅ **Flexibility** - Add/remove services without configuration changes

