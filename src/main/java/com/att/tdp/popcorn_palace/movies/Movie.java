package com.att.tdp.popcorn_palace.movies;

import com.att.tdp.popcorn_palace.showTime.Showtime;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Year;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Movie {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Movie Title is required")
    @Column(nullable = false, unique = true)
    private String title;

    @NotBlank(message = "Movie Genre is required")
    private String genre;

    @DecimalMax(value = "5400", message = "Duration must not exceed 900 hours(longest movie ever)")
    @Positive(message = "Duration must be positive")
    @NotNull(message = "Movie Duration is required")
    private Integer duration;// Duration in minutes

    @DecimalMin(value = "0.0", message = "Rating must be at least 0.0")
    @DecimalMax(value = "10.0", message = "Rating must not exceed 10.0")
    @NotNull(message = "Movie rating is required")
    private Double rating;// Rating out of 10

    @NotNull(message = "Movie Release Year is required")
    @Min(value = 1888, message = "Release year must be 1888 or later") // First film was in 1888
    private Integer releaseYear;

    @JsonIgnore
    @OneToMany(mappedBy = "movie", cascade = CascadeType.ALL)
    private List<Showtime> showtimes;

    @AssertTrue(message = "Release year must be less than or equal to three years in the future.")
    private boolean isReleaseYearValid() {
        return this.releaseYear == null || this.releaseYear <= Year.now().getValue()+3;
    }
}

