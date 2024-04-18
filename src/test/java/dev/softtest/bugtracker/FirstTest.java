package dev.softtest.bugtracker;

import org.junit.BeforeClass;
import org.junit.Test;

import static io.restassured.RestAssured.*;
import static io.restassured.matcher.RestAssuredMatchers.*;
import static org.hamcrest.Matchers.*;

import io.github.cdimascio.dotenv.Dotenv;

public class FirstTest {
    public static Dotenv env;

    @BeforeClass
    public static void setup() {
        env = Dotenv.load();
     }

    @Test
    public void testing_returns_200_with_expected_msg() {
        System.out.println(env.get("USER1_USERNAME"));
        System.out.println(env.get("USER1_PWD"));

        given().
            port(3000).
        when().
            get("/app/testing").
        then().
            statusCode(200).
            body("msg", equalTo("SUCCESS: not protected /testing route (3000)"));

    }
}
