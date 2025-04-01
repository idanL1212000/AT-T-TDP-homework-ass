# Popcorn Palace - Movie Ticket Booking System

## Overview
The Popcorn Palace Movie Ticket Booking System is a Spring Boot application that provides REST APIs for managing movies, showtimes, and ticket bookings. This document provides setup instructions and API documentation for developers.

## Prerequisites
- Java 21 (JDK 21)
- Maven 3.6+
- Docker and Docker Compose
- IDE (IntelliJ IDEA recommended)

## Quick Start Guide

### Download Git File
1. Clone the repository and navigate to the project directory:
   ```bash
   git clone https://github.com/idanL1212000/AT-T-TDP-homework-ass
   cd AT-T-TDP-homework-ass
   ```

### Database Setup
1. Start the PostgreSQL database using Docker Compose:
   ```bash
   docker-compose up -d
   ```

2. This will start:
   - PostgreSQL database on `localhost:5432`
   - PgAdmin web interface on `http://localhost:5050`

3. Database credentials:
   - **Database**: popcorn-palace
   - **Username**: popcorn-palace
   - **Password**: popcorn-palace

4. PgAdmin access:
   - **URL**: http://localhost:5050
   - **Email**: popcorn@palace.com
   - **Password**: popcorn-palace

### Building and Running the Application
1. Build the application:
   ```bash
   mvnw clean install
   ```

2. Start the PostgreSQL database using Docker Compose:
   ```bash
   mvnw spring-boot:run
   ```

3. The API will be available at: `http://localhost:8080`

## API Documentation

### Movie Management

#### Get All Movies
- **Endpoint**: `GET /movies/all`
- **Response**: List of all movies in the system
- **Response Example**:
  ```json
  [
    {
      "id": 12345,
      "title": "Sample Movie Title 1",
      "genre": "Action",
      "duration": 120,
      "rating": 8.7,
      "releaseYear": 2025
    },
    {
      "id": 67890,
      "title": "Sample Movie Title 2",
      "genre": "Comedy",
      "duration": 90,
      "rating": 7.5,
      "releaseYear": 2024
    }
  ]
  ```

#### Add a Movie
- **Endpoint**: `POST /movies`
- **Request Body**:
  ```json
  {
    "title": "Sample Movie Title",
    "genre": "Action",
    "duration": 120,
    "rating": 8.7,
    "releaseYear": 2025
  }
  ```
- **Response Example**:
  ```json
  {
    "id": 1,
    "title": "Sample Movie Title",
    "genre": "Action",
    "duration": 120,
    "rating": 8.7,
    "releaseYear": 2025
  }
  ```

#### Update a Movie
- **Endpoint**: `POST /movies/update/{movieTitle}`
- **Request Body**:
  ```json
  {
    "title": "Updated Movie Title",
    "genre": "Action",
    "duration": 120,
    "rating": 8.7,
    "releaseYear": 2025
  }
  ```
- **Response**: 200 OK (success)

#### Delete a Movie
- **Endpoint**: `DELETE /movies/{movieTitle}`
- **Response**: 200 OK (success)

### Showtime Management

#### Get Showtime by ID
- **Endpoint**: `GET /showtimes/{showtimeId}`
- **Response Example**:
  ```json
  {
    "id": 1,
    "price": 50.2,
    "movieId": 1,
    "theater": "Sample Theater",
    "startTime": "2025-02-14T11:47:46.125405Z",
    "endTime": "2025-02-14T14:47:46.125405Z"
  }
  ```

#### Add a Showtime
- **Endpoint**: `POST /showtimes`
- **Request Body**:
  ```json
  {
    "movieId": 1,
    "price": 20.2,
    "theater": "Sample Theater",
    "startTime": "2025-02-14T11:47:46.125405Z",
    "endTime": "2025-02-14T14:47:46.125405Z"
  }
  ```
- **Response Example**: Same format as GET response

#### Update a Showtime
- **Endpoint**: `POST /showtimes/update/{showtimeId}`
- **Request Body**:
  ```json
  {
    "movieId": 1,
    "price": 50.2,
    "theater": "Updated Theater",
    "startTime": "2025-02-14T11:47:46.125405Z",
    "endTime": "2025-02-14T14:47:46.125405Z"
  }
  ```
- **Response**: 200 OK (success)

#### Delete a Showtime
- **Endpoint**: `DELETE /showtimes/{showtimeId}`
- **Response**: 200 OK (success)

### Booking Management

#### Book a Ticket
- **Endpoint**: `POST /bookings`
- **Request Body**:
  ```json
  {
    "showtimeId": 1,
    "seatNumber": 15,
    "userId": "84438967-f68f-4fa0-b620-0f08217e76af"
  }
  ```
- **Response Example**:
  ```json
  {
    "bookingId": "d1a6423b-4469-4b00-8c5f-e3cfc42eacae"
  }
  ```

## Business Rules & Validation

### Movie Rules
- Each movie must have a unique title
- Movies with active showtimes cannot be updated
- Movie release year must be between 1888 and (current year + 3)
- Movie rating must be between 0 and 10

### Showtime Rules
- No overlapping showtimes for the same theater
- Valid movie ID must be provided
- Showtime duration must match movie duration (with 0-30 minutes tolerance)
- Showtimes with existing bookings cannot be updated

### Booking Rules
- Same seat cannot be booked twice for the same showtime
- Valid showtime ID must be provided

## Testing

### Unit Tests
```bash
mvnw test
```

### Integration Tests
```bash
mvnw verify
```

### Testing Notes
- H2 in-memory database is used for testing
- To run specific tests:
  ```bash
  mvnw test -Dtest=MovieServiceImplIntegrationTest
  ```

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