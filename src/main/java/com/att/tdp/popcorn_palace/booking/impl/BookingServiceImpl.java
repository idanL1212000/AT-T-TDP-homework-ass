package com.att.tdp.popcorn_palace.booking.impl;

import com.att.tdp.popcorn_palace.booking.*;
import com.att.tdp.popcorn_palace.booking.exception.SeatAlreadyBookedException;
import com.att.tdp.popcorn_palace.showTime.Showtime;
import com.att.tdp.popcorn_palace.showTime.ShowtimeRepository;
import com.att.tdp.popcorn_palace.showTime.exception.InvalidShowtimeIdNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final ShowtimeRepository showtimeRepository;

    public BookingServiceImpl(BookingRepository bookingRepository,
                              ShowtimeRepository showtimeRepository) {
        this.bookingRepository = bookingRepository;
        this.showtimeRepository = showtimeRepository;
    }

    @Transactional
    public BookingResponse createBooking(BookingRequest request) throws SeatAlreadyBookedException, InvalidShowtimeIdNotFoundException {
        // Verify showtime exists
        Showtime showtime = showtimeRepository.findById(request.showtimeId())
                .orElseThrow(InvalidShowtimeIdNotFoundException::new);

        // Check seat availability
        if (bookingRepository.existsByShowtimeIdAndSeatNumber(
                request.showtimeId(), request.seatNumber())) {
            throw new SeatAlreadyBookedException(request.showtimeId(), request.seatNumber());
        }

        // Create and save booking
        Booking booking = new Booking();
        booking.setShowtime(showtime);
        booking.setSeatNumber(request.seatNumber());
        booking.setUserId(request.userId());

        Booking savedBooking = bookingRepository.save(booking);

        return new BookingResponse(savedBooking.getId());
    }
}
