package com.att.tdp.popcorn_palace.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    boolean existsByShowtimeIdAndSeatNumber(Long showtimeId, Integer seatNumber);
}
