package com.att.tdp.popcorn_palace.showTime;

import com.att.tdp.popcorn_palace.movies.exceptions.InvalidMovieIdException;
import com.att.tdp.popcorn_palace.showTime.exception.*;


public interface ShowtimeService {
    Showtime getShowtimeById(Long showtimeId)
            throws InvalidShowtimeIdNotFoundException;

    Showtime addShowtime(Showtime showTime)
            throws ShowtimeOverlapException,
            InvalidShowtimeDurationException,
            InvalidMovieIdException, InvalidShowtimeStartTimeEndTimeException, InvalidMovieIdException;

    void updateShowtime(Showtime showTime, Long showtimeId)
            throws ShowtimeOverlapException,
            InvalidShowtimeDurationException,
            InvalidMovieIdException,
            InvalidShowtimeIdNotFoundException, InvalidShowtimeStartTimeEndTimeException;



    void deleteShowtime(Long showtimeId)
            throws InvalidShowtimeIdNotFoundException;

}
