package dev.softtest.bugtracker;

import org.junit.BeforeClass;
import org.junit.Test;

import com.google.gson.Gson;

import static io.restassured.RestAssured.*;
import static io.restassured.matcher.RestAssuredMatchers.*;
import static org.hamcrest.Matchers.*;

import io.github.cdimascio.dotenv.Dotenv;

import java.util.*;

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
            port(3001).
        when().
            get("/app/testing").
        then().
            statusCode(200).
            body("msg", equalTo("SUCCESS: not protected /testing route (3000)"));

    }

    @Test 
    public void register_returns_200_and_confirmation_msg() {

        Gson gson = new Gson();
        Map<String, String> userMap = new LinkedHashMap<>();
        userMap.put("username", "jhonny");
        userMap.put("email", "jhonny@example.invalid");
        userMap.put("password", "magic");
        
        // Serialization
        String userJson = gson.toJson(userMap);

        given().
            port(3001).
            contentType("application/json").
            body(userJson.toString()).
        when().
            post("/auth/register").
        then().
            statusCode(200).
            body("msg", equalTo("User '" + userMap.get("username") + "' successfully registered!"));
    }
}
