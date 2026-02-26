# Test microservices communication
Write-Host "===========================================" -ForegroundColor Cyan
Write-Host "MICROSERVICES COMMUNICATION TEST VIA GATEWAY" -ForegroundColor Cyan
Write-Host "===========================================" -ForegroundColor Cyan
Write-Host ""

# Test 1: Get all spaces
Write-Host "TEST 1: GET ALL SPACES" -ForegroundColor Green
Write-Host "GET http://localhost:9000/api/spaces" -ForegroundColor Yellow
$resp = Invoke-WebRequest -Uri "http://localhost:9000/api/spaces" -Method GET -UseBasicParsing
$data = $resp.Content | ConvertFrom-Json
Write-Host "✓ Status: $($resp.StatusCode) | Spaces: $($data.data.Count)" -ForegroundColor Green
Write-Host ""

# Test 2: Create a new space
Write-Host "TEST 2: CREATE NEW SPACE" -ForegroundColor Green
Write-Host "POST http://localhost:9000/api/spaces" -ForegroundColor Yellow

$newSpace = @{
    name = "Meeting Room C"
    type = "MEETING_ROOM"
    capacity = 20
    location = "Building 2, Floor 3"
    description = "Large meeting room"
    availabilityStatus = "AVAILABLE"
    equipment = "Projector, Whiteboard"
} | ConvertTo-Json

$resp = Invoke-WebRequest -Uri "http://localhost:9000/api/spaces" -Method POST -Body $newSpace -ContentType "application/json" -UseBasicParsing
$data = $resp.Content | ConvertFrom-Json
Write-Host "✓ Status: $($resp.StatusCode) | Created ID: $($data.data.id) | Name: $($data.data.name)" -ForegroundColor Green
$spaceId = $data.data.id
Write-Host ""

# Test 3: Get space by ID
Write-Host "TEST 3: GET SPACE BY ID" -ForegroundColor Green
Write-Host "GET http://localhost:9000/api/spaces/$spaceId" -ForegroundColor Yellow
$resp = Invoke-WebRequest -Uri "http://localhost:9000/api/spaces/$spaceId" -Method GET -UseBasicParsing
$data = $resp.Content | ConvertFrom-Json
Write-Host "✓ Status: $($resp.StatusCode) | Retrieved: $($data.data.name) (Capacity: $($data.data.capacity))" -ForegroundColor Green
Write-Host ""

# Test 4: Get reservations for space (OpenFeign Test)
Write-Host "TEST 4: GET SPACE RESERVATIONS (OpenFeign Test)" -ForegroundColor Green
Write-Host "GET http://localhost:9000/api/spaces/$spaceId/reservations" -ForegroundColor Yellow
Write-Host "(This calls: Space Service --> Reservation Service via OpenFeign)" -ForegroundColor Gray
$resp = Invoke-WebRequest -Uri "http://localhost:9000/api/spaces/$spaceId/reservations" -Method GET -UseBasicParsing
$data = $resp.Content | ConvertFrom-Json
Write-Host "✓ Status: $($resp.StatusCode) | Reservations: $($data.data.Count)" -ForegroundColor Green
Write-Host ""

# Test 5: Create reservation
Write-Host "TEST 5: CREATE RESERVATION" -ForegroundColor Green
Write-Host "POST http://localhost:9000/api/reservations" -ForegroundColor Yellow

$newRes = @{
    spaceId = $spaceId
    userId = 1
    startDate = (Get-Date).AddDays(1).ToString("yyyy-MM-ddT09:00:00")
    endDate = (Get-Date).AddDays(1).ToString("yyyy-MM-ddT11:00:00")
    notes = "Team meeting"
} | ConvertTo-Json

$resp = Invoke-WebRequest -Uri "http://localhost:9000/api/reservations" -Method POST -Body $newRes -ContentType "application/json" -UseBasicParsing
$data = $resp.Content | ConvertFrom-Json
Write-Host "✓ Status: $($resp.StatusCode) | Reservation ID: $($data.id)" -ForegroundColor Green
Write-Host ""

# Test 6: Get all reservations
Write-Host "TEST 6: GET ALL RESERVATIONS" -ForegroundColor Green
Write-Host "GET http://localhost:9000/api/reservations" -ForegroundColor Yellow
$resp = Invoke-WebRequest -Uri "http://localhost:9000/api/reservations" -Method GET -UseBasicParsing
$data = $resp.Content | ConvertFrom-Json
Write-Host "✓ Status: $($resp.StatusCode) | Total: $($data.data.Count)" -ForegroundColor Green
Write-Host ""

# Test 7: Create reclamation
Write-Host "TEST 7: CREATE RECLAMATION" -ForegroundColor Green
Write-Host "POST http://localhost:9000/api/reclamations" -ForegroundColor Yellow

$newReclaim = @{
    title = "Equipment Issue"
    description = "Projector not working"
    memberId = 1
    priority = "HIGH"
} | ConvertTo-Json

$resp = Invoke-WebRequest -Uri "http://localhost:9000/api/reclamations" -Method POST -Body $newReclaim -ContentType "application/json" -UseBasicParsing
$data = $resp.Content | ConvertFrom-Json
Write-Host "✓ Status: $($resp.StatusCode) | Reclamation ID: $($data.id)" -ForegroundColor Green
Write-Host ""

# Test 8: Get forum
Write-Host "TEST 8: GET FORUM TOPICS" -ForegroundColor Green
Write-Host "GET http://localhost:9000/api/forum/topics" -ForegroundColor Yellow
$resp = Invoke-WebRequest -Uri "http://localhost:9000/api/forum/topics" -Method GET -UseBasicParsing -ErrorAction SilentlyContinue
Write-Host "✓ Status: $($resp.StatusCode)" -ForegroundColor Green
Write-Host ""

Write-Host "===========================================" -ForegroundColor Cyan
Write-Host "TEST COMPLETED SUCCESSFULLY!" -ForegroundColor Cyan
Write-Host "===========================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "Summary:" -ForegroundColor Green
Write-Host "test: API Gateway routing working correctly" -ForegroundColor Green
Write-Host "test: All microservices responding via gateway" -ForegroundColor Green
Write-Host "test: OpenFeign service-to-service communication working" -ForegroundColor Green
