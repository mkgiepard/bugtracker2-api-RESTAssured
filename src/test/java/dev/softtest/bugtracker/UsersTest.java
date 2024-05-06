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



public class UsersTest {
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
        // System.out.println(env.get("USER1_USERNAME"));
        // System.out.println(env.get("USER1_PWD"));

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
    public void register_returns_409_on_duplicated_username_and_error() {

        Gson gson = new Gson();
        Map<String, String> userMap = new LinkedHashMap<>();
        userMap.put("username", "mario");
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
            statusCode(409).
            body("error", equalTo("User with this username already exists!"));
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
    public void get_users_returns_200_and_2_users() {
        given().
            port(3001).
            auth().oauth2(getAccessToken()).
        when().
            get("/app/users").
        then().
            statusCode(200).
            body("size()", is(2));
    }

    @Test
    public void get_users_by_username_returns_200_and_the_user() {
        given().
            port(3001).
            auth().oauth2(getAccessToken()).
        when().
            get("/app/users/mario").
        then().
            statusCode(200).
            body("username", equalTo("mario")).
            body("email", equalTo("mario@softtest.dev")).
            body("$", not(hasKey("password")));
    }

    @Test
    public void get_users_by_username_returns_404_when_user_not_found() {
        given().
            port(3001).
            auth().oauth2(getAccessToken()).
        when().
            get("/app/users/not-found").
        then().
            statusCode(404);
    }

    @Test
    public void put_users_by_username_returns_200_and_updates_the_email() {
        Gson gson = new Gson();
        Map<String, String> userMap = new LinkedHashMap<>();
        userMap.put("email", "mario-updated@softtest.dev");
        
        String userJson = gson.toJson(userMap);

        given().
            port(3001).
            auth().oauth2(getAccessToken()).
            contentType("application/json").
            body(userJson.toString()).
        when().
            put("/app/users/mario").
        then().
            statusCode(200).
            body("msg", equalTo("User 'mario' successfully updated!"));
    }


    @Test
    public void put_users_by_username_returns_404_when_user_not_found() {
        Gson gson = new Gson();
        Map<String, String> userMap = new LinkedHashMap<>();
        userMap.put("username", "mario-updated");
        userMap.put("email", "mario-updated@softtest.dev");
        
        String userJson = gson.toJson(userMap);

        given().
            port(3001).
            auth().oauth2(getAccessToken()).
            contentType("application/json").
            body(userJson.toString()).
        when().
            put("/app/users/not-found").
        then().
            statusCode(404);
    }

    @Test
    public void delete_users_by_username_returns_200() {
        given().
            port(3001).
            auth().oauth2(getAccessToken()).
        when().
            delete("/app/users/mario").
        then().
            statusCode(200).
            body("msg", equalTo("User 'mario' successfully deleted!"));
    }

    @Test
    public void delete_users_by_username_returns_404_when_user_not_found() {
        given().
            port(3001).
            auth().oauth2(getAccessToken()).
        when().
            delete("/app/users/not-found").
        then().
            statusCode(404);
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

    private static void wipe(String db, String collection) {
        String uri = "mongodb://127.0.0.1:27017";
        try (MongoClient mongoClient = MongoClients.create(uri)) {
            MongoDatabase database = mongoClient.getDatabase(db);
            database.getCollection(collection).drop();
            database.createCollection(collection);
        } catch (Exception e) {
            System.out.println("...while wiping");
            throw e;
        }
    }

    private static void seedUsers(String db) {

        String uri = "mongodb://127.0.0.1:27017";
        try (MongoClient mongoClient = MongoClients.create(uri)) {
            MongoDatabase database = mongoClient.getDatabase(db);
            MongoCollection<Document> collection = database.getCollection("users");

            IndexOptions indexOptions = new IndexOptions().unique(true);
            collection.createIndex(Indexes.text("username"), indexOptions);

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
        } catch (Exception e) {
            System.out.println("...while seeding");
            throw e;
        }
    }
}
