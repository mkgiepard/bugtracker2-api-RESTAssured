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

    @Test
    public void get_bugreports_by_is_returns_200_and_the_bugreport() {
        given().
            port(3001).
        when().
            get("/app/bugreports/1001").
        then().
            statusCode(200).
            body("id", equalTo(1001)).
            body("title", equalTo("bug report 1001")).
            body("author", equalTo("mario"));
    }

    @Test 
    public void post_bugreports_returns_200_and_confirmation_msg() {
        Gson gson = new Gson();
        BugReport br = new BugReport.Builder(1099, "mario", BugReportSeeder.getDate("2024-04-13T11:33"))
                .title("bug report 1099")
                .priority(0)
                .status("New")
                .description("lorem epsum...")
                .updated(BugReportSeeder.getDate("2024-04-13T13:44"))
                .build();

        String userJson = gson.toJson(br);

        given().
            port(3001).
            contentType("application/json").
            body(userJson.toString()).
        when().
            post("/app/bugreports").
        then().
            statusCode(200).
            body("msg", equalTo("BugReport '1099' successfully registered!"));
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
