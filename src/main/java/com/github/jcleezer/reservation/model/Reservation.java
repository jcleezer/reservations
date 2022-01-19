package com.github.jcleezer.reservation.model;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.annotation.Id;

import javax.validation.constraints.Email;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
@RequiredArgsConstructor
@ValidReservationDates(message = "Reservation must be made for three days.")
public class Reservation {

    @Id
    private Long id;
    @NotNull(message = "First name is required")
    private final String firstName;
    @NotNull(message = "Last name is required")
    private final String lastName;
    @Email(message = "Email should be valid")
    private final String emailAddress;
    @FutureOrPresent(message = "Reservation dates must be made for the future")
    private final LocalDate arrivalDate;
    @FutureOrPresent(message = "Reservation dates must be made for the future")
    private final LocalDate departureDate;
}
