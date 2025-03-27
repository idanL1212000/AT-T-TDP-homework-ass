package com.att.tdp.popcorn_palace.movies;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Movie {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String title;

    @Column(nullable = false)
    private String genre;

    @Column(nullable = false)
    private int duration;// Duration in minutes

    @Column(nullable = false)
    private double rating;// Rating out of 10

    @Column(nullable = false)
    private int releaseYear;
}
