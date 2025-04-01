# Popcorn Palace - Movie Ticket Booking System

## Overview
The Popcorn Palace Movie Ticket Booking System is a Spring Boot application that provides REST APIs for managing movies, showtimes, and ticket bookings. This document provides setup instructions and API documentation for developers.

## Prerequisites
1. Java SDK 21: Install from Oracle.
2. IDE: IntelliJ, Eclipse, or any Java IDE.
3. Docker: Install Docker Desktop.
4. Maven: Ensure Maven is installed (included with most IDEs).

## Quick Start Guide

### Download Git File
1. Clone the repository and navigate to the project directory:
   ```bash
   git clone https://github.com/idanL1212000/AT-T-TDP-homework-ass
   cd AT-T-TDP-homework-ass
   ```

### Building and Running the Application
1. Build the application:
   ```bash
   mvnw clean install
   ```
2Start the PostgreSQL database and app using Docker Compose:
   ```bash
   docker-compose -f compose.yml up -d  
   ```

3. This will start:
   - PostgreSQL database on `localhost:5432`
   - PgAdmin web interface on `http://localhost:5050`

4. Database credentials:
   - **Database**: popcorn-palace
   - **Username**: popcorn-palace
   - **Password**: popcorn-palace

5. PgAdmin access:
   - **URL**: http://localhost:5050
   - **Email**: popcorn@palace.com
   - **Password**: popcorn-palace

6. The API will be available at: `http://localhost:8080`

## Business Rules & Validation

### Movie Rules
- Each movie must have a unique title
- Movie duration must be less than 900 hours (longest movie ever)
- Movie release year must be between 1888 and (current year + 3)
- Movie rating must be between 0 and 10
- Movies with active showtimes cannot be updated

### Showtime Rules
- No overlapping showtimes for the same theater
- Valid movie ID must be provided
- Showtime duration must be more than movie duration (with 0-30 minutes)
- Showtimes with existing bookings cannot be updated

### Booking Rules
- Same seat cannot be booked twice for the same showtime
- Valid showtime ID must be provided
- UserId need to be UUID format

## Testing

### Execute all tests:
```bash
mvnw test
```

### Testing Notes
- H2 in-memory database is used for testing
- 154 tests in total
## APIs

### Movies  APIs

| API Description           | Endpoint               | Request Body                          | Response Status | Response Body |
|---------------------------|------------------------|---------------------------------------|-----------------|---------------|
| Get all movies | GET /movies/all | | 200 OK | [ { "id": 12345, "title": "Sample Movie Title 1", "genre": "Action", "duration": 120, "rating": 8.7, "releaseYear": 2025 }, { "id": 67890, "title": "Sample Movie Title 2", "genre": "Comedy", "duration": 90, "rating": 7.5, "releaseYear": 2024 } ] |
| Add a movie | POST /movies | { "title": "Sample Movie Title", "genre": "Action", "duration": 120, "rating": 8.7, "releaseYear": 2025 } | 200 OK | { "id": 1, "title": "Sample Movie Title", "genre": "Action", "duration": 120, "rating": 8.7, "releaseYear": 2025 }|
| Update a movie | POST /movies/update/{movieTitle} | { "title": "Sample Movie Title", "genre": "Action", "duration": 120, "rating": 8.7, "releaseYear": 2025 } | 200 OK | |
| DELETE /movies/{movieTitle} | | 200 OK | |

### Showtimes APIs

| API Description            | Endpoint                           | Request Body                                                                                                                                      | Response Status | Response Body                                                                                                                                                                                                                                                                   |
|----------------------------|------------------------------------|---------------------------------------------------------------------------------------------------------------------------------------------------|-----------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| Get showtime by ID | GET /showtimes/{showtimeId} |                                                                                                                                                   | 200 OK | { "id": 1, "price":50.2, "movieId": 1, "theater": "Sample Theater", "startTime": "2025-02-14T11:47:46.125405Z", "endTime": "2025-02-14T14:47:46.125405Z" }                                                                                                                      | | Delete a restaurant        | DELETE /restaurants/{id}           |                                                                              | 204 No Content  |                                                                                                        |
| Add a showtime | POST /showtimes | { "movieId": 1, "price":20.2, "theater": "Sample Theater", "startTime": "2025-02-14T11:47:46.125405Z", "endTime": "2025-02-14T14:47:46.125405Z" } | 200 OK | { "id": 1, "price":50.2,"movieId": 1, "theater": "Sample Theater", "startTime": "2025-02-14T11:47:46.125405Z", "endTime": "2025-02-14T14:47:46.125405Z" }                                                                                                                                    |
| Update a showtime | POST /showtimes/update/{showtimeId}| { "movieId": 1, "price":50.2, "theater": "Sample Theater", "startTime": "2025-02-14T11:47:46.125405Z", "endTime": "2025-02-14T14:47:46.125405Z" } | 200 OK |                                                                                                                                                                                                                                                                                 |
| Delete a showtime | DELETE /showtimes/{showtimeId} |                                                                                                                                                   | 200 OK |                                                                                                                                                                                                                                                                                 |

### bookings APIs

| API Description           | Endpoint       | Request Body                                     | Response Status | Response Body                                                                                                                                          |
|---------------------------|----------------|--------------------------------------------------|-----------------|--------------------------------------------------------------------------------------------------------------------------------------------------------|
| Book a ticket | POST /bookings | { "showtimeId": 1, "seatNumber": 15 , userId:"84438967-f68f-4fa0-b620-0f08217e76af"} | 200 OK | { "bookingId":"d1a6423b-4469-4b00-8c5f-e3cfc42eacae" }                                                                                                 |

## Troubleshooting

### Database Connection Issues
- Verify Docker containers are running: `docker ps`
- Check container logs: `docker logs <container-id>`
- Ensure no port conflicts (5432 for PostgreSQL, 5050 for PgAdmin)

### Application Issues
- Verify Java 21 is installed: `java -version`
- Check application logs
- Ensure port 8080 is available

### Maven Build Issues
- Verify Maven installation: `mvn -version`
- Run with debug output: `mvn clean install -X`

## Technologies Used
- Spring Boot 3.4.2
- JPA/Hibernate
- PostgreSQL
- Lombok
- H2 (for testing)