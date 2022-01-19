package com.github.jcleezer.reservation.repository;

import com.github.jcleezer.reservation.model.Reservation;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.time.LocalDate;

@Repository
public interface ReservationRepository extends ReactiveSortingRepository<Reservation, Long> {

    @Query("SELECT * FROM reservation WHERE (:arrivalDate <= arrival_date AND :departureDate >= departure_date)" + // Reservation wraps range
            " OR (:arrivalDate >= arrival_date AND :arrivalDate < departure_date) " + // Arrival in reservation
            " OR (:departureDate > arrival_date AND :departureDate <= departure_date)") // Departure in reservation
    Flux<Reservation> findOverlappingReservations(LocalDate arrivalDate, LocalDate departureDate);
}
