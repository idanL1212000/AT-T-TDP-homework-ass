package com.att.tdp.popcorn_palace.booking;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.hibernate.validator.constraints.UUID;

public record BookingRequest(

        @NotNull(message = "Showtime ID is required")
        @Positive(message = "Showtime Id must be positive")
        Long showtimeId,

        @NotNull(message = "Seat number is required")
        @Positive(message = "Seat number must be positive")
        Integer seatNumber,

        @UUID(message = "User ID must be a valid UUID")
        @NotBlank(message = "User ID is required")
        String userId
) {}