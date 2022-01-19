package com.github.jcleezer.reservation.integration;

import org.testng.annotations.Test;

import static io.restassured.RestAssured.when;

@Test
public class SwaggerTests extends TestBase{

    public void swaggerApiAvailable(){
        when().get("/v2/api-docs").then().statusCode(200);
    }

    public void swaggerUiAvailable(){
        when().get("/swagger-ui/").then().statusCode(200);
    }
}
