package dev.softtest.bugtracker;

import com.google.gson.Gson;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
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



public class FirstTest {
    public static Dotenv env;

    @BeforeClass
    public static void setup() {
        env = Dotenv.load();
     }

     @Before
     public void beforeEach() {
        wipe("test_usersdb", "users");
        seedUsers("test_usersdb");
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

    @Test
    public void login_returns_200_access_and_refresh_tokens() {
        Gson gson = new Gson();
        Map<String, String> userMap = new LinkedHashMap<>();
        userMap.put("username", "mario");
        userMap.put("password", "magic");
        
        // Serialization
        String userJson = gson.toJson(userMap);

        given().
            port(3001).
            contentType("application/json").
            body(userJson.toString()).
        when().
            post("/auth/login").
        then().
            statusCode(200).
            body("accessToken", notNullValue()).
            body("refreshToken", notNullValue());
    }

    @Test
    public void login_returns_401_on_wrong_password() {
        Gson gson = new Gson();
        Map<String, String> userMap = new LinkedHashMap<>();
        userMap.put("username", "jhonny");
        userMap.put("password", "wrong!");
        
        // Serialization
        String userJson = gson.toJson(userMap);

        given().
            port(3001).
            contentType("application/json").
            body(userJson.toString()).
        when().
            post("/auth/login").
        then().
            statusCode(401).
            body("error", equalTo("Wrong user or password"));
    }

    @Test
    public void login_returns_401_on_wrong_user() {
        Gson gson = new Gson();
        Map<String, String> userMap = new LinkedHashMap<>();
        userMap.put("username", "no-such-user");
        userMap.put("password", "wrong!");
        
        // Serialization
        String userJson = gson.toJson(userMap);

        given().
            port(3001).
            contentType("application/json").
            body(userJson.toString()).
        when().
            post("/auth/login").
        then().
            statusCode(401).
            body("error", equalTo("Wrong user or password"));
    }

    @Test
    public void testSeeder() {
        seedUsers("test_usersdb");
    }

    private static void wipe(String db, String collection) {
        String uri = "mongodb://127.0.0.1:27017";
        try (MongoClient mongoClient = MongoClients.create(uri)) {
            MongoDatabase database = mongoClient.getDatabase(db);
            database.getCollection(collection).drop();
        }
    }

    private static void seedUsers(String db) {

        String uri = "mongodb://127.0.0.1:27017";
        try (MongoClient mongoClient = MongoClients.create(uri)) {
            MongoDatabase database = mongoClient.getDatabase(db);
            MongoCollection<Document> collection = database.getCollection("users");
            Document testUserOne = new Document("_id", new ObjectId())
                .append("username", "mario")
                .append("email", "mario@softtest.dev")
                .append("password", env.get("DEFAULT_PWD"));
            Document testUserTwo = new Document("_id", new ObjectId())
                .append("username", "bross")
                .append("email", "bross@softtest.dev")
                .append("password", env.get("DEFAULT_PWD"));

            InsertOneResult resultOne = collection.insertOne(testUserOne);
            InsertOneResult resultTwo = collection.insertOne(testUserTwo);
        }
    }
}
