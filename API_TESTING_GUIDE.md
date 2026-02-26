# API Testing Guide - Microservices Communication Examples

## Quick Test Commands

You can test the microservices using curl or Postman. Here are the key endpoints:

---

## Space Service (via API Gateway)

### 1. Get All Spaces
```bash
curl -X GET http://localhost:9000/api/spaces \
  -H "Accept: application/json"

# Response (200 OK):
{
  "status": 200,
  "message": "Retrieved 1 spaces",
  "data": [
    {
      "id": 1,
      "name": "Lab A",
      "type": "LAB",
      "capacity": 10,
      "location": "Floor 1",
      "availabilityStatus": "MAINTENANCE",
      "equipment": null
    }
  ]
}
```

### 2. Create a Space
```bash
curl -X POST http://localhost:9000/api/spaces \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Study Room B",
    "type": "STUDY_ROOM",
    "capacity": 15,
    "location": "Building 1, Floor 2",
    "description": "Quiet study area",
    "availabilityStatus": "AVAILABLE",
    "equipment": "Desks, WiFi"
  }'

# Response (201 Created):
{
  "status": 201,
  "message": "Space created",
  "data": {
    "id": 2,
    "name": "Study Room B",
    "type": "STUDY_ROOM",
    "capacity": 15,
    ...
  }
}
```

### 3. Get Space by ID
```bash
curl -X GET http://localhost:9000/api/spaces/1 \
  -H "Accept: application/json"

# Response (200 OK):
{
  "status": 200,
  "message": "...",
  "data": {
    "id": 1,
    "name": "Lab A",
    ...
  }
}
```

### 4. Get Reservations for a Space (OpenFeign Test!)
```bash
curl -X GET http://localhost:9000/api/spaces/1/reservations \
  -H "Accept: application/json"

# This call demonstrates OpenFeign communication:
# API Gateway -> Space Service -> (OpenFeign) -> Reservation Service

# Response (200 OK):
{
  "status": 200,
  "message": "Reservations for space id=1",
  "data": [
    {
      "id": 1,
      "spaceId": 1,
      "userId": 5,
      "status": "CONFIRMED"
    }
  ]
}
```

---

## Reservation Service (via API Gateway)

### 1. Get All Reservations
```bash
curl -X GET http://localhost:9000/api/reservations \
  -H "Accept: application/json"

# Response includes all reservations
```

### 2. Get Reservation by ID
```bash
curl -X GET http://localhost:9000/api/reservations/1 \
  -H "Accept: application/json"
```

### 3. Get Reservations by Space ID
```bash
curl -X GET http://localhost:9000/api/reservations/space/1 \
  -H "Accept: application/json"
```

---

## Forum Service (via API Gateway)

### 1. Get All Forum Topics
```bash
curl -X GET http://localhost:9000/api/forum/topics \
  -H "Accept: application/json"
```

---

## Fournisseur Service (via API Gateway)

### 1. Get All Suppliers
```bash
curl -X GET http://localhost:9000/api/fournisseurs \
  -H "Accept: application/json"
```

---

## Reclamation Service (via API Gateway)

### 1. Get All Reclamations
```bash
curl -X GET http://localhost:9000/api/reclamations \
  -H "Accept: application/json"
```

### 2. Get Reclamation by ID
```bash
curl -X GET http://localhost:9000/api/reclamations/1 \
  -H "Accept: application/json"
```

### 3. Create Reclamation
```bash
curl -X POST http://localhost:9000/api/reclamations \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Broken Equipment",
    "description": "Projector in Conference Room broken",
    "memberId": 1,
    "priority": "HIGH"
  }'
```

---

## Health Checks

### Eureka Server Status
```bash
curl -X GET http://admin:admin123@localhost:8761 \
  -H "Accept: application/json"
```

### View All Registered Services
```bash
curl -X GET http://admin:admin123@localhost:8761/eureka/apps \
  -H "Accept: application/json"
```

### Health of a Specific Service
```bash
curl -X GET http://admin:admin123@localhost:8761/eureka/apps/SPACE-SERVICE \
  -H "Accept: application/json"
```

---

## Common Patterns

### Pattern 1: Create & Retrieve
```
POST /api/spaces (Create) 
  ↓
GET /api/spaces/{id} (Retrieve the created resource)
```

### Pattern 2: List & Filter
```
GET /api/spaces (Get all)
GET /api/spaces/status/AVAILABLE (Filter by status)
GET /api/spaces/capacity?min=10 (Filter by capacity)
```

### Pattern 3: Service-to-Service Call
```
Client Request to API Gateway (9000)
  ↓
API Gateway routes to Service A (8081)
  ↓
Service A uses OpenFeign to call Service B (8082)
  ↓
Response flows back through Service A to API Gateway to Client
```

---

## Testing with PowerShell

```powershell
# Get all spaces
$response = Invoke-WebRequest -Uri "http://localhost:9000/api/spaces" -Method GET -UseBasicParsing
$data = $response.Content | ConvertFrom-Json
Write-Host "Spaces: $($data.data.Count)"

# Create a space
$body = @{
    name = "Test Room"
    type = "STUDY_ROOM"
    capacity = 25
    location = "Building 1"
    availabilityStatus = "AVAILABLE"
} | ConvertTo-Json

$response = Invoke-WebRequest -Uri "http://localhost:9000/api/spaces" `
  -Method POST `
  -Body $body `
  -ContentType "application/json" `
  -UseBasicParsing
$data = $response.Content | ConvertFrom-Json
Write-Host "Created Space ID: $($data.data.id)"
```

---

## Service Ports Reference

| Service | Port | Type | Database |
|---------|------|------|----------|
| Eureka Server | 8761 | Registry | N/A |
| API Gateway | 9000 | Gateway | N/A |
| Space Service | 8081 | Microservice | MySQL |
| Reservation | 8082 | Microservice | MySQL |
| Fournisseur | 8090 | Microservice | MySQL |
| Inventaire | 9090 | Microservice | H2 |
| Reclamation | 9093 | Microservice | MySQL |
| Forum | 9094 | Microservice | H2 |
| Emprunt | 9095 | Microservice | MySQL |

---

## Expected Results

All endpoints should return HTTP 2xx or 4xx status codes:
- **200 OK** - Successful GET
- **201 Created** - Successful POST
- **400 Bad Request** - Invalid input
- **404 Not Found** - Resource not found
- **500 Server Error** - Server error (rare in normal operation)

---

## Notes

1. All responses include a standard wrapper:
   - `status`: HTTP status code
   - `message`: Human-readable message
   - `data`: Response payload
   - `timestamp`: Request timestamp

2. OpenFeign communication is automatic - no configuration needed by clients
3. Load balancing is transparent - Eureka handles service discovery
4. CORS is enabled for development (wildcard allowed)

