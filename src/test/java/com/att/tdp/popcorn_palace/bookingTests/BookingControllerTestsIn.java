package com.att.tdp.popcorn_palace.bookingTests;

import com.att.tdp.popcorn_palace.booking.BookingRepository;
import com.att.tdp.popcorn_palace.booking.BookingRequest;
import com.att.tdp.popcorn_palace.movies.Movie;
import com.att.tdp.popcorn_palace.movies.MovieRepository;
import com.att.tdp.popcorn_palace.showTime.Showtime;
import com.att.tdp.popcorn_palace.showTime.ShowtimeRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

import static com.att.tdp.popcorn_palace.EntityFactoryForTests.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class BookingControllerTestsIn {

    @Autowired private MockMvc mockMvc;
    @Autowired private BookingRepository bookingRepository;
    @Autowired private ShowtimeRepository showtimeRepository;
    @Autowired private MovieRepository movieRepository;

    private ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
    private Movie testMovie;
    private Showtime testShowtime;
    private String validUserId;

    @BeforeEach
    void setup() {
        testMovie = movieRepository.save(makeMovie("Test Movie", "Action", 8.0, 120, 2023));

        testShowtime = showtimeRepository.save(makeShowtime(
                testMovie,
                "Theater 1",
                12.99,
                Instant.now().plus(Duration.ofDays(1)),
                Instant.now().plus(Duration.ofDays(1)).plus(Duration.ofHours(2))
        ));

        validUserId = UUID.randomUUID().toString();
    }

    @Test
    void createBooking_ValidRequest_ReturnsOk() throws Exception {
        BookingRequest request = new BookingRequest(
                testShowtime.getId(),
                1, // seat number
                validUserId
        );

        mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bookingId").exists());
    }

    @Test
    void createBooking_NullShowtimeId_ReturnsBadRequest() throws Exception {
        String requestJson = """
                {
                    "seatNumber": 1,
                    "userId": "%s"
                }
                """.formatted(validUserId);

        mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createBooking_NegativeShowtimeId_ReturnsBadRequest() throws Exception {
        BookingRequest request = new BookingRequest(
                -1L, // negative showtime ID
                1,
                validUserId
        );

        mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createBooking_ZeroShowtimeId_ReturnsBadRequest() throws Exception {
        BookingRequest request = new BookingRequest(
                0L, // zero showtime ID
                1,
                validUserId
        );

        mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createBooking_NonExistentShowtimeId_ReturnsNotFound() throws Exception {
        BookingRequest request = new BookingRequest(
                999L, // non-existent showtime ID
                1,
                validUserId
        );

        mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    void createBooking_NullSeatNumber_ReturnsBadRequest() throws Exception {
        String requestJson = """
                {
                    "showtimeId": %d,
                    "userId": "%s"
                }
                """.formatted(testShowtime.getId(), validUserId);

        mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createBooking_NegativeSeatNumber_ReturnsBadRequest() throws Exception {
        BookingRequest request = new BookingRequest(
                testShowtime.getId(),
                -1, // negative seat number
                validUserId
        );

        mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createBooking_ZeroSeatNumber_ReturnsBadRequest() throws Exception {
        BookingRequest request = new BookingRequest(
                testShowtime.getId(),
                0, // zero seat number
                validUserId
        );

        mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createBooking_NullUserId_ReturnsBadRequest() throws Exception {
        String requestJson = """
                {
                    "showtimeId": %d,
                    "seatNumber": 1
                }
                """.formatted(testShowtime.getId());

        mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createBooking_EmptyUserId_ReturnsBadRequest() throws Exception {
        BookingRequest request = new BookingRequest(
                testShowtime.getId(),
                1,
                "" // empty user ID
        );

        mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createBooking_InvalidUuidFormat_ReturnsBadRequest() throws Exception {
        BookingRequest request = new BookingRequest(
                testShowtime.getId(),
                1,
                "not-a-uuid" // invalid UUID format
        );

        mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createBooking_DuplicateSeat_ReturnsConflict() throws Exception {
        // First booking
        BookingRequest request1 = new BookingRequest(
                testShowtime.getId(),
                1,
                validUserId
        );

        mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request1)))
                .andExpect(status().isOk());

        // Second booking with same seat
        BookingRequest request2 = new BookingRequest(
                testShowtime.getId(),
                1, // same seat number
                UUID.randomUUID().toString() // different user
        );

        mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request2)))
                .andExpect(status().isConflict());
    }

    @Test
    void createBooking_MultipleDifferentSeats_ReturnsOk() throws Exception {
        // First booking
        BookingRequest request1 = new BookingRequest(
                testShowtime.getId(),
                1,
                validUserId
        );

        mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request1)))
                .andExpect(status().isOk());

        // Second booking with different seat
        BookingRequest request2 = new BookingRequest(
                testShowtime.getId(),
                2, // different seat number
                validUserId // same user
        );

        mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request2)))
                .andExpect(status().isOk());
    }

    @Test
    void createBooking_SameSeatDifferentShowtimes_ReturnsOk() throws Exception {
        // Create second showtime
        Showtime secondShowtime = showtimeRepository.save(makeShowtime(
                testMovie,
                "Theater 2",
                12.99,
                Instant.now().plus(Duration.ofDays(2)),
                Instant.now().plus(Duration.ofDays(2)).plus(Duration.ofHours(2))
        ));

        // First booking
        BookingRequest request1 = new BookingRequest(
                testShowtime.getId(),
                1,
                validUserId
        );

        mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request1)))
                .andExpect(status().isOk());

        // Second booking with same seat but different showtime
        BookingRequest request2 = new BookingRequest(
                secondShowtime.getId(),
                1, // same seat number
                validUserId // same user
        );

        mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request2)))
                .andExpect(status().isOk());
    }

    @Test
    void createBooking_MalformedJson_ReturnsBadRequest() throws Exception {
        String malformedJson = "{\"showtimeId\": " + testShowtime.getId() + ", \"seatNumber\": 1, \"userId\": \"" + validUserId + "\"";

        mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(malformedJson))
                .andExpect(status().isBadRequest());
    }
}