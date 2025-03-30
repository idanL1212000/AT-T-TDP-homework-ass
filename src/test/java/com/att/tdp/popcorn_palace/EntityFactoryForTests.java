package com.att.tdp.popcorn_palace;

import com.att.tdp.popcorn_palace.booking.Booking;
import com.att.tdp.popcorn_palace.movies.Movie;
import com.att.tdp.popcorn_palace.showTime.Showtime;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class EntityFactoryForTests {

    public static Movie makeMovie(String title, String genre, Double rating, Integer duration, Integer releaseYear){
        Movie movie = new Movie();
        movie.setTitle(title);
        movie.setGenre(genre);
        movie.setRating(rating);
        movie.setDuration(duration);
        movie.setReleaseYear(releaseYear);
        return movie;
    }

    public static Showtime makeShowtime(Movie movie, String theater, Double price, Instant startTime, Instant endTime){
        Showtime showtime = new Showtime();
        showtime.setMovie(movie);
        showtime.setMovieId(movie.getId());
        showtime.setTheater(theater);
        showtime.setPrice(price);
        showtime.setStartTime(startTime);
        showtime.setEndTime(endTime);
        return showtime;
    }

    public static Booking makeBooking(Showtime showtime, Integer seatNumber, String userId){
        Booking booking = new Booking();
        booking.setShowtime(showtime);
        booking.setSeatNumber(seatNumber);
        booking.setUserId(userId);
        return booking;
    }

    public static List<Booking> makeBookings(Showtime showtime, String userId){
        List<Booking> bookings = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            bookings.add(makeBooking(showtime,i,userId));
        }
        return bookings;
    }
}
