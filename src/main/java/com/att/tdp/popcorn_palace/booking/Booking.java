package com.att.tdp.popcorn_palace.booking;

import jakarta.persistence.*;
import com.att.tdp.popcorn_palace.showTime.Showtime;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Entity
@Table(name = "bookings",
        uniqueConstraints = @UniqueConstraint(
                name = "unique_seat_per_showtime",
                columnNames = {"showtime_id", "seatNumber"}
        ))
@Getter
@Setter
@NoArgsConstructor
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne
    @JoinColumn(name = "showtime_id")
    private Showtime showtime;

    @Column(nullable = false)
    private Integer seatNumber;

    @Column(nullable = false)
    private String userId;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant createdAt;
}
