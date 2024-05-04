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
        wipe("test_usersdb", "bugreports");
        seedUsers("test_usersdb");
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
            MongoCollection<Document> collection = database.getCollection("bugreports");

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
