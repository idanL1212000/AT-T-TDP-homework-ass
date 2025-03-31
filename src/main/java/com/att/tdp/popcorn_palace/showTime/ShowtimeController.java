package com.att.tdp.popcorn_palace.showTime;

import com.att.tdp.popcorn_palace.movies.exceptions.InvalidMovieIdNotFoundException;
import com.att.tdp.popcorn_palace.showTime.exception.*;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/showtimes")
public class ShowtimeController {
    @Autowired
    private final ShowtimeService showtimeService;

    public ShowtimeController(ShowtimeService showtimeService) {
        this.showtimeService = showtimeService;
    }

    @GetMapping("/{showtimeId}")
    public ResponseEntity<Showtime> getShowtime(@PathVariable Long showtimeId)
            throws InvalidShowtimeIdNotFoundException {
        if (showtimeId <= 0){
            throw new InvalidShowTimeIdNegException();
        }
        return ResponseEntity.ok(showtimeService.getShowtimeById(showtimeId));
    }

    @PostMapping
    public ResponseEntity<Showtime> addShowtime(@Valid @RequestBody Showtime showTime)
            throws ShowtimeOverlapException,
            InvalidShowtimeDurationException,
            InvalidMovieIdNotFoundException,
            InvalidShowtimeStartTimeEndTimeException {
        Showtime savedShowtime = showtimeService.addShowtime(showTime);
        return ResponseEntity.ok(savedShowtime);
    }

    @PostMapping("/update/{showtimeId}")
    public ResponseEntity<Showtime> updateShowtime(
            @PathVariable Long showtimeId,
            @Valid @RequestBody Showtime showtime)
            throws ShowtimeOverlapException,
            InvalidShowtimeDurationException,
            InvalidMovieIdNotFoundException,
            InvalidShowtimeIdNotFoundException,
            InvalidShowtimeStartTimeEndTimeException, UpdateShowtimeWithBookingsException {

        if (showtimeId <= 0){
            throw new InvalidShowTimeIdNegException();
        }
        showtimeService.updateShowtime(showtime, showtimeId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{showtimeId}")
    public ResponseEntity<Void> deleteShowtime(@PathVariable Long showtimeId)
            throws InvalidShowtimeIdNotFoundException {
        if (showtimeId <= 0){
            throw new InvalidShowTimeIdNegException();
        }
        showtimeService.deleteShowtime(showtimeId);
        return ResponseEntity.ok().build();
    }
}

