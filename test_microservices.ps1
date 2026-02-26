#!/usr/bin/env pwsh

Write-Host "════════════════════════════════════════════════════════════" -ForegroundColor Cyan
Write-Host "    MICROSERVICES COMMUNICATION & OPENFEIGN TEST             " -ForegroundColor Cyan
Write-Host "════════════════════════════════════════════════════════════" -ForegroundColor Cyan
Write-Host ""

# Test 1: Get all spaces (Gateway -> Space Service)
Write-Host "TEST 1: GET ALL SPACES" -ForegroundColor Green
Write-Host "Endpoint: GET http://localhost:9000/api/spaces" -ForegroundColor Yellow
Try {
    $response = Invoke-WebRequest -Uri "http://localhost:9000/api/spaces" -Method GET -UseBasicParsing
    $data = $response.Content | ConvertFrom-Json
    Write-Host "✓ Status: $($response.StatusCode)" -ForegroundColor Green
    Write-Host "✓ Message: $($data.message)" -ForegroundColor Green
    Write-Host "✓ Total Spaces: $($data.data.Count)" -ForegroundColor Green
    Write-Host ""
    $data.data | ForEach-Object {
        Write-Host "  - ID: $($_.id), Name: $($_.name), Type: $($_.type), Capacity: $($_.capacity)" -ForegroundColor Cyan
    }
} Catch {
    Write-Host "✗ Error: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host ""
Write-Host "────────────────────────────────────────────────────────────" -ForegroundColor Gray
Write-Host ""

# Test 2: Create a new space
Write-Host "TEST 2: CREATE NEW SPACE" -ForegroundColor Green
Write-Host "Endpoint: POST http://localhost:9000/api/spaces" -ForegroundColor Yellow

$newSpace = @{
    name = "Meeting Room C"
    type = "MEETING_ROOM"
    capacity = 20
    location = "Building 2, Floor 3"
    description = "Large meeting and training room"
    availabilityStatus = "AVAILABLE"
    equipment = "Projector, Whiteboard, Video Conference"
} | ConvertTo-Json

Write-Host "Request Body:" -ForegroundColor Yellow
Write-Host $newSpace -ForegroundColor Gray
Write-Host ""

Try {
    $response = Invoke-WebRequest -Uri "http://localhost:9000/api/spaces" -Method POST -Body $newSpace -ContentType "application/json" -UseBasicParsing
    $data = $response.Content | ConvertFrom-Json
    Write-Host "✓ Status: $($response.StatusCode)" -ForegroundColor Green
    Write-Host "✓ Message: $($data.message)" -ForegroundColor Green
    Write-Host "✓ Created Space ID: $($data.data.id)" -ForegroundColor Green
    Write-Host "✓ Space Name: $($data.data.name)" -ForegroundColor Green
    $newSpaceId = $data.data.id
} Catch {
    Write-Host "✗ Error: $($_.Exception.Message)" -ForegroundColor Red
    $newSpaceId = $null
}

Write-Host ""
Write-Host "────────────────────────────────────────────────────────────" -ForegroundColor Gray
Write-Host ""

# Test 3: Get the newly created space by ID
if ($newSpaceId) {
    Write-Host "TEST 3: GET SPACE BY ID (Testing the created space)" -ForegroundColor Green
    Write-Host "Endpoint: GET http://localhost:9000/api/spaces/$newSpaceId" -ForegroundColor Yellow
    
    Try {
        $response = Invoke-WebRequest -Uri "http://localhost:9000/api/spaces/$newSpaceId" -Method GET -UseBasicParsing
        $data = $response.Content | ConvertFrom-Json
        Write-Host "✓ Status: $($response.StatusCode)" -ForegroundColor Green
        Write-Host "✓ Retrieved Space:" -ForegroundColor Green
        Write-Host "  - ID: $($data.data.id)" -ForegroundColor Cyan
        Write-Host "  - Name: $($data.data.name)" -ForegroundColor Cyan
        Write-Host "  - Type: $($data.data.type)" -ForegroundColor Cyan
        Write-Host "  - Capacity: $($data.data.capacity)" -ForegroundColor Cyan
        Write-Host "  - Location: $($data.data.location)" -ForegroundColor Cyan
        Write-Host "  - Status: $($data.data.availabilityStatus)" -ForegroundColor Cyan
    } Catch {
        Write-Host "✗ Error: $($_.Exception.Message)" -ForegroundColor Red
    }
    
    Write-Host ""
    Write-Host "────────────────────────────────────────────────────────────" -ForegroundColor Gray
    Write-Host ""
    
    # Test 4: Get reservations for the space (Tests OpenFeign)
    Write-Host "TEST 4: GET SPACE RESERVATIONS (OpenFeign Test)" -ForegroundColor Green
    Write-Host "Endpoint: GET http://localhost:9000/api/spaces/$newSpaceId/reservations" -ForegroundColor Yellow
    Write-Host "This endpoint calls: Space Service -> (OpenFeign) -> Reservation Service" -ForegroundColor Yellow
    
    Try {
        $response = Invoke-WebRequest -Uri "http://localhost:9000/api/spaces/$newSpaceId/reservations" -Method GET -UseBasicParsing
        $data = $response.Content | ConvertFrom-Json
        Write-Host "✓ Status: $($response.StatusCode)" -ForegroundColor Green
        Write-Host "✓ Message: $($data.message)" -ForegroundColor Green
        Write-Host "✓ Reservations: $($data.data.Count)" -ForegroundColor Green
        if ($data.data.Count -eq 0) {
            Write-Host "  (No reservations for this new space - this is expected)" -ForegroundColor Gray
        } else {
            $data.data | ForEach-Object {
                Write-Host "  - Reservation ID: $($_.id), User: $($_.userId)" -ForegroundColor Cyan
            }
        }
    } Catch {
        Write-Host "✗ Error: $($_.Exception.Message)" -ForegroundColor Red
    }
}

Write-Host ""
Write-Host "────────────────────────────────────────────────────────────" -ForegroundColor Gray
Write-Host ""

# Test 5: Test Reclamation Service via Gateway
Write-Host "TEST 5: CREATE RECLAMATION (Testing Reclamation Service)" -ForegroundColor Green
Write-Host "Endpoint: POST http://localhost:9000/api/reclamations" -ForegroundColor Yellow

$newReclamation = @{
    title = "Broken Projector"
    description = "Projector in Conference Room is not working"
    memberId = 1
    priority = "HIGH"
    status = "OPEN"
} | ConvertTo-Json

Write-Host "Request Body:" -ForegroundColor Yellow
Write-Host $newReclamation -ForegroundColor Gray
Write-Host ""

Try {
    $response = Invoke-WebRequest -Uri "http://localhost:9000/api/reclamations" -Method POST -Body $newReclamation -ContentType "application/json" -UseBasicParsing
    $data = $response.Content | ConvertFrom-Json
    Write-Host "✓ Status: $($response.StatusCode)" -ForegroundColor Green
    Write-Host "✓ Message: Reclamation created successfully" -ForegroundColor Green
    Write-Host "✓ Reclamation ID: $($data.id)" -ForegroundColor Green
    Write-Host "✓ Title: $($data.title)" -ForegroundColor Green
    Write-Host "✓ Status: $($data.status)" -ForegroundColor Green
} Catch {
    Write-Host "✗ Error: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host ""
Write-Host "────────────────────────────────────────────────────────────" -ForegroundColor Gray
Write-Host ""

# Test 6: Create a reservation
Write-Host "TEST 6: CREATE RESERVATION (Testing Reservation Service)" -ForegroundColor Green
Write-Host "Endpoint: POST http://localhost:9000/api/reservations" -ForegroundColor Yellow

if ($newSpaceId) {
    $newReservation = @{
        spaceId = $newSpaceId
        userId = 1
        startDate = (Get-Date).AddDays(1).ToString("yyyy-MM-ddTHH:mm:ss")
        endDate = (Get-Date).AddDays(1).AddHours(2).ToString("yyyy-MM-ddTHH:mm:ss")
        notes = "Team meeting reservation"
    } | ConvertTo-Json
    
    Write-Host "Request Body:" -ForegroundColor Yellow
    Write-Host $newReservation -ForegroundColor Gray
    Write-Host ""
    
    Try {
        $response = Invoke-WebRequest -Uri "http://localhost:9000/api/reservations" -Method POST -Body $newReservation -ContentType "application/json" -UseBasicParsing
        $data = $response.Content | ConvertFrom-Json
        Write-Host "✓ Status: $($response.StatusCode)" -ForegroundColor Green
        Write-Host "✓ Reservation created successfully" -ForegroundColor Green
        Write-Host "✓ Reservation ID: $($data.id)" -ForegroundColor Green
        Write-Host "✓ Space ID: $($data.spaceId)" -ForegroundColor Green
        Write-Host "✓ Status: $($data.status)" -ForegroundColor Green
    } Catch {
        Write-Host "✗ Error: $($_.Exception.Message)" -ForegroundColor Red
    }
}

Write-Host ""
Write-Host "════════════════════════════════════════════════════════════" -ForegroundColor Cyan
Write-Host "                         TEST COMPLETE                       " -ForegroundColor Cyan
Write-Host "════════════════════════════════════════════════════════════" -ForegroundColor Cyan
