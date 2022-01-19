package com.github.jcleezer.reservation.integration;


import com.github.jcleezer.reservation.model.Reservation;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.springframework.web.server.ResponseStatusException;
import org.testng.annotations.Test;

import java.time.LocalDate;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.in;
import static org.hamcrest.Matchers.is;

@Test(groups = "integration")
public class ReservationCrudTests extends TestBase {

    public void validReservation_create_201Returned(){
        given().contentType(ContentType.JSON).body(randomReservation()).when().post("reservation").then().statusCode(200);
    }

    public void reservationExists_getById_reservationReturned(){
        Reservation createdReservation = given().contentType(ContentType.JSON).body(randomReservation()).when().post("reservation").then().extract().as(Reservation.class);
        given().when().get("reservation/"+createdReservation.getId()).then().statusCode(200).and().body("firstName",is(createdReservation.getFirstName()));
    }

    public void reservationDoesntExist_getById_404Returned(){
        given().when().get("reservation/100000").then().statusCode(404);
    }

    public void reservationDoesntExist_delete_404Returned(){
        given().when().delete("reservation/20000").then().statusCode(404);
    }

    public void reservationExists_reservationDeleted_getById_404Returned(){
        Reservation createdReservation = given().contentType(ContentType.JSON).body(randomReservation()).when().post("reservation").then().extract().as(Reservation.class);
        given().when().delete("reservation/"+createdReservation.getId()).then().statusCode(200);
        given().when().get("reservation/"+createdReservation.getId()).then().statusCode(404);
    }

    public Reservation randomReservation(){
        Reservation reservation = new Reservation(UUID.randomUUID().toString(),"Burton", "aburton@rocinate.com", LocalDate.now(), LocalDate.now().plusDays(4));
        return reservation;
    }

    public void invalidEmailAddress_post_400Returned(){
        Reservation reservation = new Reservation("Foo","Bar","nota-valid-email",LocalDate.now(),LocalDate.now().plusDays(5));
        given().contentType(ContentType.JSON).body(reservation).when().post("reservation").then().statusCode(400);
    }
    
    public void validReservation_invalidUpdate_reservationUnchanged(){
        // Create a reservation
        Reservation createdReservation = given().contentType(ContentType.JSON).body(randomReservation()).when().post("reservation").then().extract().as(Reservation.class);
        Reservation invalidReservation = new Reservation("Foo", "Bar", "aninvalidemail#dotcom", createdReservation.getArrivalDate(), createdReservation.getDepartureDate());
        // Update with bad email
        given().contentType(ContentType.JSON).body(invalidReservation).when().put("reservation/"+createdReservation.getId()).then().log().all().statusCode(400);
        // Make sure reservation still uses the original name
        given().when().get("reservation/"+createdReservation.getId()).then().statusCode(200).and().body("firstName",is(createdReservation.getFirstName()));   
    }
}
