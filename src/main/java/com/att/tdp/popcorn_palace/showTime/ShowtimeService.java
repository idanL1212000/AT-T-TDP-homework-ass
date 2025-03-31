package com.att.tdp.popcorn_palace.showTime;

import com.att.tdp.popcorn_palace.movies.exceptions.InvalidMovieIdNotFoundException;
import com.att.tdp.popcorn_palace.showTime.exception.*;

public interface ShowtimeService {

    Showtime getShowtimeById(Long showtimeId)
            throws InvalidShowtimeIdNotFoundException;

    Showtime addShowtime(Showtime showTime)
            throws ShowtimeOverlapException,
            InvalidShowtimeDurationException,
            InvalidMovieIdNotFoundException, InvalidShowtimeStartTimeEndTimeException;

    void updateShowtime(Showtime showTime, Long showtimeId)
            throws ShowtimeOverlapException,
            InvalidShowtimeDurationException,
            InvalidMovieIdNotFoundException,
            InvalidShowtimeIdNotFoundException, InvalidShowtimeStartTimeEndTimeException, UpdateShowtimeWithBookingsException;

    void deleteShowtime(Long showtimeId)
            throws InvalidShowtimeIdNotFoundException;

}