package com.att.tdp.popcorn_palace.movies;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Year;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Movie {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Movie Title is required")
    private String title;

    @NotNull(message = "Movie Genre is required")
    private String genre;

    @Positive(message = "Duration must be positive")
    @NotNull(message = "Movie Duration is required")
    private int duration;// Duration in minutes

    @DecimalMin(value = "0.0", message = "Rating must be at least 0.0")
    @DecimalMax(value = "10.0", message = "Rating must not exceed 10.0")
    private double rating;// Rating out of 10

    @NotNull(message = "Movie Release Year is required")
    private int releaseYear;

    @AssertTrue(message = "Release year must be less than or equal to the next year.")
    private boolean isReleaseYearValid() {
        return this.releaseYear <= Year.now().getValue()+1;
    }
}
