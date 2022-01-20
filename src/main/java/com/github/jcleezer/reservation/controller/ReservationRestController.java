package com.github.jcleezer.reservation.controller;

import com.github.jcleezer.reservation.model.Reservation;
import com.github.jcleezer.reservation.service.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.Optional;

@RestController
public class ReservationRestController {

    private final ReservationService reservationService;

    @Autowired
    public ReservationRestController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }


    @PostMapping("reservation")
    public Mono<Reservation> createReservation(@RequestBody Reservation reservation) {
        return reservationService.createReservation(reservation);
    }

    @PutMapping("reservation/{id}")
    public Mono<Reservation> updateReservation(@PathVariable long id, @RequestBody Reservation reservation) {
        return reservationService.updateReservation(id,reservation);
    }

    @GetMapping("reservation")
    public Flux<Reservation> getAllReservations(@RequestParam(name = "arrival", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate arrivalDate,
                                                @RequestParam(name = "departure", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate departureDate){
        if (arrivalDate == null){
            arrivalDate = LocalDate.now();
        }
        if (departureDate == null){
            departureDate = arrivalDate.plusMonths(1);
        }
        return reservationService.getOverlappingReservations(arrivalDate,departureDate);
    }

 @GetMapping("reservation/{id}")
    public Mono<ResponseEntity<Reservation>> getById(@PathVariable long id){
        return reservationService.get(id).map(res -> ResponseEntity.of(Optional.of(res))).switchIfEmpty( Mono.just(ResponseEntity.notFound().build()));
    }


    @DeleteMapping("reservation/{id}")
    public Mono<ResponseEntity<Object>> deleteReservation(@PathVariable long id){
        return reservationService.delete(id).map(res -> ResponseEntity.ok().build()).switchIfEmpty( Mono.just(ResponseEntity.notFound().build()));
    }

}
