package com.github.jcleezer.reservation.integration;

import io.restassured.RestAssured;

public class TestBase {

    static{
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = 8080;
    }
}
