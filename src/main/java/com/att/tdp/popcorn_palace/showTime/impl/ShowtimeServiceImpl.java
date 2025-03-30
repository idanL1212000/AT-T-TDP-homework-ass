package com.att.tdp.popcorn_palace.showTime.impl;

import com.att.tdp.popcorn_palace.movies.Movie;
import com.att.tdp.popcorn_palace.movies.MovieService;
import com.att.tdp.popcorn_palace.movies.exceptions.InvalidMovieIdNotFoundException;
import com.att.tdp.popcorn_palace.showTime.Showtime;
import com.att.tdp.popcorn_palace.showTime.ShowtimeRepository;
import com.att.tdp.popcorn_palace.showTime.ShowtimeService;
import com.att.tdp.popcorn_palace.showTime.exception.*;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ShowtimeServiceImpl implements ShowtimeService {

    private static final long EXTRA_SHOW_TIME_DURATION = 30;
    private final ShowtimeRepository showtimeRepository;
    private final MovieService movieService;

    public ShowtimeServiceImpl(ShowtimeRepository showtimeRepository, MovieService movieService) {
        this.showtimeRepository = showtimeRepository;
        this.movieService = movieService;
    }

    @Transactional(readOnly = true)
    public Showtime getShowtimeById(Long showtimeId) throws InvalidShowtimeIdNotFoundException {
        Optional<Showtime> optionalShowtime = showtimeRepository.findById(showtimeId);
        if (optionalShowtime.isEmpty()){
            throw new InvalidShowtimeIdNotFoundException();
        }
        return optionalShowtime.get();
    }

    @Transactional
    public Showtime addShowtime(Showtime showtime)
            throws ShowtimeOverlapException,
            InvalidShowtimeDurationException, InvalidShowtimeStartTimeEndTimeException, InvalidMovieIdNotFoundException {
        // Validate showtime
        validateShowtime(showtime);

        // Check for overlapping showtimes
        validateShowtimeOverlap(null, showtime);

        return showtimeRepository.save(showtime);
    }

    @Transactional
    public void updateShowtime(Showtime updatedShowtime, Long showtimeId)
            throws ShowtimeOverlapException,
            InvalidShowtimeDurationException, InvalidShowtimeIdNotFoundException, InvalidShowtimeStartTimeEndTimeException, InvalidMovieIdNotFoundException {
        Optional<Showtime> optionalShowtime = showtimeRepository.findById(showtimeId);
        if(optionalShowtime.isEmpty()){
            throw new InvalidShowtimeIdNotFoundException();
        }

        Showtime existingShowtime = optionalShowtime.get();

        validateShowtime(updatedShowtime);

        validateShowtimeOverlap(showtimeId, updatedShowtime);

        // Update existing showtime
        BeanUtils.copyProperties(updatedShowtime, existingShowtime, "id");
        showtimeRepository.save(existingShowtime);
    }


    @Transactional
    public void deleteShowtime(Long showtimeId) throws InvalidShowtimeIdNotFoundException {

        if (!showtimeRepository.existsById(showtimeId)) {
            throw new InvalidShowtimeIdNotFoundException();
        }

        showtimeRepository.deleteById(showtimeId);
    }

    // Validation method
    private void validateShowtime(Showtime showtime) throws InvalidShowtimeDurationException, InvalidShowtimeStartTimeEndTimeException, InvalidMovieIdNotFoundException {
        Movie movie = movieService.getMovieById(showtime.getMovieId()).orElseThrow(InvalidMovieIdNotFoundException::new);

        Duration showtimeDuration = Duration.between(
                showtime.getStartTime(),
                showtime.getEndTime()
        );

        if(showtimeDuration.toMinutes() < 0){
            throw new InvalidShowtimeStartTimeEndTimeException();
        }

        Duration movieDuration = Duration.ofMinutes(movie.getDuration());
        Duration maxAllowedDuration = movieDuration.plusMinutes(EXTRA_SHOW_TIME_DURATION);

        // Check if showtime duration is within the allowed range
        if (showtimeDuration.compareTo(movieDuration) < 0 ||
                showtimeDuration.compareTo(maxAllowedDuration) > 0) {
            throw new InvalidShowtimeDurationException(showtimeDuration.toMinutes(),
                    movieDuration.toMinutes(),
                    maxAllowedDuration.toMinutes()
            );
        }
    }

    private void validateShowtimeOverlap(Long excludeId, Showtime showtime)
            throws ShowtimeOverlapException{
        // Check if there are any overlapping showtimes in the same theater
        boolean hasOverlap = showtimeRepository.hasOverlappingShowtime(
                showtime.getTheater(),
                showtime.getStartTime(),
                showtime.getEndTime(),
                excludeId != null ? excludeId : -1L
        );

        if (hasOverlap) {
            // Find the conflicting showtimes to provide more details
            List<Showtime> conflictingShowtimes = showtimeRepository.findOverlappingShowtimes(
                    showtime.getTheater(),
                    showtime.getStartTime(),
                    showtime.getEndTime(),
                    excludeId != null ? excludeId : -1L
            );

            // Prepare detailed error message
            String conflictDetails = conflictingShowtimes.stream()
                    .map(st -> String.format("Showtime %d: %s - %s",
                            st.getId(), st.getStartTime(), st.getEndTime()))
                    .collect(Collectors.joining(", "));

            throw new ShowtimeOverlapException(
                    "Showtime conflicts with existing showtimes in " +
                            showtime.getTheater() + ". Conflicting showtimes: " + conflictDetails
            );
        }
    }
}

