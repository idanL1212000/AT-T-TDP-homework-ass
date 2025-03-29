package com.att.tdp.popcorn_palace.showTime;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface ShowtimeRepository extends JpaRepository<Showtime,Long> {
    // Custom query to check for overlapping showtimes in the same theater
    @Query(value = "SELECT COUNT(*) > 0 FROM showtime s " +
            "WHERE s.theater = :theater " +
            "AND s.id != :excludeId " +
            "AND s.start_time < :endTime " +
            "AND s.end_time > :startTime " +
            "AND DATE_TRUNC('second', s.start_time) <> DATE_TRUNC('second', CAST(:endTime AS TIMESTAMP)) " +
            "AND DATE_TRUNC('second', s.end_time) <> DATE_TRUNC('second', CAST(:startTime AS TIMESTAMP))",
            nativeQuery = true)
    boolean hasOverlappingShowtime(
            @Param("theater") String theater,
            @Param("startTime") Instant startTime,
            @Param("endTime") Instant endTime,
            @Param("excludeId") Long excludeId
    );

    @Query(value = "SELECT * FROM showtime s " +
            "WHERE s.theater = :theater " +
            "AND s.id != :excludeId " +
            "AND s.start_time < :endTime " +
            "AND s.end_time > :startTime " +
            "AND DATE_TRUNC('second', s.start_time) <> DATE_TRUNC('second', CAST(:endTime AS TIMESTAMP)) " +
            "AND DATE_TRUNC('second', s.end_time) <> DATE_TRUNC('second', CAST(:startTime AS TIMESTAMP))",
            nativeQuery = true)
    List<Showtime> findOverlappingShowtimes(
            @Param("theater") String theater,
            @Param("startTime") Instant startTime,
            @Param("endTime") Instant endTime,
            @Param("excludeId") Long excludeId
    );
}
