package com.github.jcleezer.reservation.model;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.lang.annotation.Annotation;

public class ValidReservationDatesValidator implements ConstraintValidator<ValidReservationDates, Reservation> {

    @Override
    public boolean isValid(Reservation value, ConstraintValidatorContext context) {
        return value.getArrivalDate().plusDays(2).isBefore(value.getDepartureDate());
    }
}
