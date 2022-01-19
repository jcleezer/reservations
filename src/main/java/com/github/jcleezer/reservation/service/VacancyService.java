package com.github.jcleezer.reservation.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.stream.Collectors;

@Component
public class VacancyService {

    final ReservationService reservationService;

    @Autowired
    public VacancyService(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    public Flux<LocalDate> getVacancies(LocalDate startDate, LocalDate endDate){
        if (endDate.isBefore(startDate)){
            return Flux.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Arrival date must occur before departure date"));
        } else {
            return reservationService.getOverlappingReservations(startDate,endDate).map( res -> res.getArrivalDate().datesUntil(endDate).collect(Collectors.toSet()))
                    .reduce(new HashSet<>(),(a,b) -> {a.addAll(b); return a;})
                    .flatMapMany( res ->
                    Flux.fromStream(startDate.datesUntil(endDate).filter(day -> !res.contains(day)))
            );
        }
    }
}
