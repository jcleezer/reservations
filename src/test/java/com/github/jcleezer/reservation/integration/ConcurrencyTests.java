package com.github.jcleezer.reservation.integration;

import com.github.jcleezer.reservation.model.Reservation;
import io.restassured.http.ContentType;
import org.testng.annotations.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.time.LocalDate;
import java.time.Period;
import java.util.stream.IntStream;

import static io.restassured.RestAssured.given;
import static org.testng.Assert.assertEquals;

@Test(groups = "integration")
public class ConcurrencyTests extends TestBase {

    public void testConcurrentBookingsForYear(){
        LocalDate.now().datesUntil(LocalDate.now().plusYears(1), Period.ofDays(3)).forEach( date -> concurrentBookingWithStartDate(5,date));
    }
    public void concurrentBookingWithStartDate(int numUsers, LocalDate arrivalDate){
        int numSuccessBookings = IntStream.range(0,numUsers).boxed().parallel().map(i -> new Reservation( "Foo","Bar"+i,"tester@foo.com",arrivalDate,arrivalDate.plusDays(3)))
                        .map( res -> given().contentType(ContentType.JSON).body(res).when().post("reservation").then().log().all().extract().statusCode())
                                .map( status -> status == 200 ? 1 : 0).reduce(0, Integer::sum);
        assertEquals(numSuccessBookings,1,"Only one concurrent booking should be successful");

        /*
        StepVerifier.create(Flux.fromStream(IntStream.range(0,numUsers).boxed()).map(i -> new Reservation(null, "Foo","Bar"+i,"tester@foo.com",arrivalDate,arrivalDate.plusDays(3)))
                        .map( res -> given().contentType(ContentType.JSON).body(res).when().post("reservation").then().extract().statusCode())
                .reduce(0, (left,right) ->  right==200 ? left+1: left )).expectNext(1).expectComplete().verify(Duration.ofMillis(500));
         */

    }
}
