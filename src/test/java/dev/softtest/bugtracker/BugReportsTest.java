package dev.softtest.bugtracker;

import com.google.gson.Gson;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Indexes;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.result.InsertOneResult;

import io.github.cdimascio.dotenv.Dotenv;
import java.util.*;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.junit.BeforeClass;
import org.junit.Before;
import org.junit.Test;
import static com.mongodb.client.model.Filters.eq;
import static io.restassured.matcher.RestAssuredMatchers.*;
import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;


public class BugReportsTest {
    public static Dotenv env;

    @BeforeClass
    public static void setup() {
        env = Dotenv.load();
     }

     @Before
     public void beforeEach() {
        BugReportSeeder.wipe("test_usersdb");
        BugReportSeeder.seed("test_usersdb");
     }

    @Test
    public void testing_returns_200_with_expected_msg() {

        given().
            port(3001).
        when().
            get("/app/testing").
        then().
            statusCode(200).
            body("msg", equalTo("SUCCESS: not protected /testing route (3000)"));
    }

    @Test
    public void get_bugreports_returns_200_and_6_bugreports() {
        given().
            port(3001).
        when().
            get("/app/bugreports").
        then().
            statusCode(200).
            body("size()", is(6));
    }

    private String getAccessToken() {
        Gson gson = new Gson();
        Map<String, String> userMap = new LinkedHashMap<>();
        userMap.put("username", "mario");
        userMap.put("password", "magic");
        String userJson = gson.toJson(userMap);

        String accessToken = given().
                port(3001).
                contentType("application/json").
                body(userJson.toString()).
            when().
                post("/auth/login").
                jsonPath().
                get("accessToken");
        
        return accessToken;
    }

}
