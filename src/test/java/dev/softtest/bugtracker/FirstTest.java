package dev.softtest.bugtracker;

import static org.junit.Assert.assertTrue;
import static io.restassured.RestAssured.*;
import static io.restassured.matcher.RestAssuredMatchers.*;
import static org.hamcrest.Matchers.*;

import org.junit.Test;

public class FirstTest {

    @Test
    public void testing_returns_200_with_expected_msg() {

        given().
            port(3000).
        when().
            get("/app/testing").
        then().
            statusCode(200).
            body("msg", equalTo("SUCCESS: not protected /testing route (3000)"));

    }
}
