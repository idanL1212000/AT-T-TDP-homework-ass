package com.att.tdp.popcorn_palace.movies;


import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface MovieRepository extends JpaRepository<Movie, Long>{
    Optional<Movie> findByTitle(String title);
    Long deleteByTitle(String title);
}
