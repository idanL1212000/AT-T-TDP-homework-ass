package com.att.tdp.popcorn_palace.booking;

import com.att.tdp.popcorn_palace.booking.exception.SeatAlreadyBookedException;
import com.att.tdp.popcorn_palace.showTime.exception.InvalidShowtimeIdNotFoundException;

public interface BookingService {
    BookingResponse createBooking(BookingRequest request) throws SeatAlreadyBookedException, InvalidShowtimeIdNotFoundException;
}
