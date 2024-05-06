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

import java.util.List;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class BugReportSeeder {


    private static BugReport[] data = new BugReport[] {
            new BugReport.Builder(1001, "mario", getDate("2024-04-13T11:33"))
                .title("bug report 1001")
                .priority(0)
                .status("New")
                .description("lorem epsum...")
                .updated(getDate("2024-04-13T13:44"))
                .build(),
            new BugReport.Builder(1002, "dduck", getDate("2024-04-13T11:33"))
                .title("bug report 1002")
                .priority(1)
                .status("Assigned")
                .description("lorem epsum...")
                .updated(getDate("2024-04-13T13:44"))
                .build(),
            new BugReport.Builder(1003, "muszatek", getDate("2024-04-13T11:33"))
                .title("bug report 1003")
                .priority(1)
                .status("New")
                .description("lorem epsum...")
                .updated(getDate("2024-04-13T13:44"))
                .build(),
            new BugReport.Builder(1004, "muszatek", getDate("2024-04-13T11:33"))
                .title("bug report 1004")
                .priority(1)
                .status("Assigned")
                .description("lorem epsum...")
                .updated(getDate("2024-04-13T13:44"))
                .build(),
            new BugReport.Builder(1005, "jbravo", getDate("2024-04-13T11:33"))
                .title("bug report 1005")
                .priority(1)
                .status("WNF")
                .description("lorem epsum...")
                .updated(getDate("2024-04-13T13:44"))
                .build(),
            new BugReport.Builder(1006, "dduck", getDate("2024-04-13T11:33"))
                .title("bug report 1006")
                .priority(1)
                .status("Fixed")
                .description("lorem epsum...")
                .updated(getDate("2024-04-13T13:44"))
                .build()
    };
    
    public static void wipe(String db) {
        String uri = "mongodb://127.0.0.1:27017";
        try (MongoClient mongoClient = MongoClients.create(uri)) {
            MongoDatabase database = mongoClient.getDatabase(db);
            database.getCollection("bugreports").drop();
        } catch (Exception e) {
            System.out.println("...while wiping");
            throw e;
        }
    }

    public static void seed(String db) {
        String uri = "mongodb://127.0.0.1:27017";

        List<Document> bugReportData = new ArrayList<Document>();
        for (BugReport b : data) {
            Document d = new Document("_id", new ObjectId())
                .append("id", b.getId())
                .append("author", b.getAuthor())
                .append("created", b.getCreated())
                .append("title", b.getTitle())
                .append("priority", b.getPriority())
                .append("status", b.getStatus())
                .append("description", b.getDescription())
                .append("updated", b.getUpdated());
            System.out.println(d);
            bugReportData.add(d);
        }

        try (MongoClient mongoClient = MongoClients.create(uri)) {
            MongoDatabase database = mongoClient.getDatabase(db);
            MongoCollection<Document> collection = database.getCollection("bugreports");

            IndexOptions indexOptions = new IndexOptions().unique(true);
            collection.createIndex(Indexes.text("id"), indexOptions);

            InsertManyResult result = collection.insertMany(bugReportData);
        } catch(Exception e) {
            throw e;
        }
    }

    public static void main(String[] args) {
        wipe("test_usersdb");
        seed("test_usersdb");
    }

    public static Date getDate(String d) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
        Date result = new Date();
        try {
            result = dateFormat.parse(d);
        } catch (Exception e) {
            System.out.println(">>> error while parsing date\n" + e);
        }
        return result;
    }

}
