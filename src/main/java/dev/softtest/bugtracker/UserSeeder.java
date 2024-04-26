package dev.softtest.bugtracker;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Indexes;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.result.InsertManyResult;

import org.bson.Document;
import org.bson.types.ObjectId;

import io.github.cdimascio.dotenv.Dotenv;

import java.util.List;
import java.util.ArrayList;

public class UserSeeder {
    public static Dotenv env = Dotenv.load();

    private static User[] data = new User[]{
        new User("jbravo", "jbravo@softtest.dev", "John", "Bravo", env.get("DEFAULT_PWD")),
        new User("dduck", "dduck@softtest.dev", "Donald", "Duck", env.get("DEFAULT_PWD")),
        new User("swawelski", "swawelski@softtest.dev", "Smok", "Wawelski",env.get("DEFAULT_PWD")),
        new User("muszatek", "muszatek@softtest.dev", "Mis", "Uszatek", env.get("DEFAULT_PWD")),
        new User("reksio", "reksio@softtest.dev", "Reksio", "", env.get("DEFAULT_PWD")),
        new User("mario", "one2@one.com", "Mario", "Bross", env.get("DEFAULT_PWD")),
        new User("qbtest", "qbtest@selftest.dev", "Qb", "Test", env.get("DEFAULT_PWD"))
    };

    public static void wipe(String db) {
        String uri = "mongodb://127.0.0.1:27017";
        try (MongoClient mongoClient = MongoClients.create(uri)) {
            MongoDatabase database = mongoClient.getDatabase(db);
            database.getCollection("users").drop();
        } catch (Exception e) {
            System.out.println("...while wiping");
            throw e;
        }
    }

    public static void seed(String db) {
        String uri = "mongodb://127.0.0.1:27017";

        List<Document> userData = new ArrayList<Document>();
        for (User u : data) {
            Document d = new Document("_id", new ObjectId())
                .append("username", u.getUsername())
                .append("email", u.getEmail())
                .append("firstName", u.getFirstName())
                .append("lastName", u.getLastName())
                .append("password", u.getPassword());
            System.out.println(d);
            userData.add(d);
        }

        try (MongoClient mongoClient = MongoClients.create(uri)) {
            MongoDatabase database = mongoClient.getDatabase(db);
            MongoCollection<Document> collection = database.getCollection("users");

            IndexOptions indexOptions = new IndexOptions().unique(true);
            collection.createIndex(Indexes.text("username"), indexOptions);

            InsertManyResult result = collection.insertMany(userData);
        } catch(Exception e) {
            throw e;
        }
    }

    public static void main(String[] args) {
        wipe("dev_usersdb");
        seed("dev_usersdb");
    }
    
}
