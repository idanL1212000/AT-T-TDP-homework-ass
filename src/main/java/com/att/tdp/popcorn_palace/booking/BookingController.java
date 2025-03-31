package com.att.tdp.popcorn_palace.booking;

import com.att.tdp.popcorn_palace.booking.exception.SeatAlreadyBookedException;
import com.att.tdp.popcorn_palace.showTime.exception.InvalidShowtimeIdNotFoundException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/bookings")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public ResponseEntity<BookingResponse> createBooking(
            @Valid @RequestBody BookingRequest request) throws SeatAlreadyBookedException, InvalidShowtimeIdNotFoundException {
        BookingResponse response = bookingService.createBooking(request);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
