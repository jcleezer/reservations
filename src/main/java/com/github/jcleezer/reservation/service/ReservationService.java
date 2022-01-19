package com.github.jcleezer.reservation.service;

import com.github.jcleezer.reservation.model.Reservation;
import com.github.jcleezer.reservation.repository.ReservationRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.LocalDate;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final Validator validator;

    @Autowired
    public ReservationService(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        this.validator = factory.getValidator();
    }

    @Transactional
    public Mono<Reservation> createReservation( Reservation reservation){
        return reservationRepository.findOverlappingReservations(reservation.getArrivalDate(),reservation.getDepartureDate())
                .hasElements().<Reservation>handle( (taken,sink) -> {
                    if (taken){
                        sink.error(new ResponseStatusException(HttpStatus.BAD_REQUEST,"Dates overlap with existing reservation"));
                    } else {
                        sink.next(reservation);
                    }
                }).<Reservation>handle( (res,sink) -> {
                    Set<ConstraintViolation<Reservation>> violations = validator.validate(res);
                    if (violations.isEmpty()){
                        sink.next(res);
                    } else {
                        log.info(violations.toString());
                        sink.error(new ResponseStatusException(HttpStatus.BAD_REQUEST,violations.stream().map(cv -> cv.getMessage()).collect(Collectors.joining(". "))));
                    }
                }).flatMap( res -> reservationRepository.save(res));

    }

    @Transactional(readOnly = true)
    public Mono<Reservation> get(long id){
        return reservationRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Flux<Reservation> getOverlappingReservations(LocalDate arrivalDate, LocalDate departureDate){
        return reservationRepository.findOverlappingReservations(arrivalDate,departureDate);
    }

    @Transactional
    public Mono<Void> delete(long id){
        return reservationRepository.deleteById(id);
    }

    @Transactional
    public Mono<Reservation> updateReservation(final long id, final Reservation reservation) {
        reservation.setId(id);
        return createReservation(reservation);
    }
}
