package com.att.tdp.popcorn_palace.showTime;

import com.att.tdp.popcorn_palace.showTime.exception.*;
import com.embarkx.FirstSpring.movies.exceptions.InvalidMovieIdException;


public interface ShowtimeService {
    Showtime getShowtimeById(Long showtimeId)
            throws InvalidShowtimeIdNotFoundException;

    Showtime addShowtime(Showtime showTime)
            throws ShowtimeOverlapException,
            InvalidShowtimeDurationException,
            InvalidMovieIdException, InvalidShowtimeStartTimeEndTimeException;

    void updateShowtime(Showtime showTime, Long showtimeId)
            throws ShowtimeOverlapException,
            InvalidShowtimeDurationException,
            InvalidMovieIdException,
            InvalidShowtimeIdNotFoundException, InvalidShowtimeStartTimeEndTimeException;



    void deleteShowtime(Long showtimeId)
            throws InvalidShowtimeIdNotFoundException;

}
