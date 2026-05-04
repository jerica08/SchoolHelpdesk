/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mongodb.client.MongoClient
 *  com.mongodb.client.MongoClients
 *  com.mongodb.client.MongoCollection
 *  com.mongodb.client.MongoDatabase
 *  org.bson.Document
 *  org.bson.conversions.Bson
 */
package schoolhelpdesk.database;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import java.util.Date;
import org.bson.Document;
import org.bson.conversions.Bson;

public class SetupDatabase {
    public static void main(String[] args) {
        try {
            MongoClient mongoClient = MongoClients.create((String)"mongodb://localhost:27017");
            MongoDatabase database = mongoClient.getDatabase("helpdesk_db");
            System.out.println("Setting up School Helpdesk Database...");
            SetupDatabase.setupUsersCollection(database);
            SetupDatabase.setupDepartmentsCollection(database);
            SetupDatabase.setupTicketsCollection(database);
            SetupDatabase.setupDocumentsCollection(database);
            SetupDatabase.setupNotificationsCollection(database);
            System.out.println("\n\u2713 Database setup completed successfully!");
            mongoClient.close();
        }
        catch (Exception e) {
            System.err.println("\u2717 Error setting up database: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void setupUsersCollection(MongoDatabase database) {
        MongoCollection users = database.getCollection("users");
        users.deleteMany((Bson)new Document());
        users.insertOne(new Document("username", (Object)"admin").append("password", (Object)"admin123").append("role", (Object)"admin").append("email", (Object)"admin@school.edu").append("fullName", (Object)"System Administrator"));
        users.insertOne(new Document("username", (Object)"staff").append("password", (Object)"staff123").append("role", (Object)"staff").append("email", (Object)"staff@school.edu").append("fullName", (Object)"Support Staff"));
        users.insertOne(new Document("username", (Object)"student").append("password", (Object)"student123").append("role", (Object)"student").append("email", (Object)"student@school.edu").append("fullName", (Object)"John Student"));
        System.out.println("\u2713 Users collection created with 3 sample users");
    }

    private static void setupDepartmentsCollection(MongoDatabase database) {
        MongoCollection departments = database.getCollection("departments");
        departments.deleteMany((Bson)new Document());
        departments.insertOne(new Document("name", (Object)"IT Support").append("description", (Object)"Technical support and computer services").append("head", (Object)"IT Manager").append("location", (Object)"Building A, Room 101"));
        departments.insertOne(new Document("name", (Object)"Academic Affairs").append("description", (Object)"Student academic services and records").append("head", (Object)"Academic Dean").append("location", (Object)"Building B, Room 205"));
        departments.insertOne(new Document("name", (Object)"Student Services").append("description", (Object)"Student welfare and support services").append("head", (Object)"Student Services Director").append("location", (Object)"Building C, Room 150"));
        departments.insertOne(new Document("name", (Object)"Finance").append("description", (Object)"Financial services and payments").append("head", (Object)"Finance Manager").append("location", (Object)"Building D, Room 300"));
        System.out.println("\u2713 Departments collection created with 4 departments");
    }

    private static void setupTicketsCollection(MongoDatabase database) {
        MongoCollection tickets = database.getCollection("tickets");
        tickets.deleteMany((Bson)new Document());
        tickets.insertOne(new Document("ticketId", (Object)"T001").append("title", (Object)"Cannot login to student portal").append("description", (Object)"Student unable to access online portal with credentials").append("status", (Object)"In Progress").append("priority", (Object)"high").append("userId", (Object)"student").append("createdBy", (Object)"student").append("assignedTo", (Object)"staff").append("department", (Object)"IT Support").append("createdAt", (Object)new Date()).append("updatedAt", (Object)new Date()));
        tickets.insertOne(new Document("ticketId", (Object)"T002").append("title", (Object)"Request for transcript").append("description", (Object)"Need official transcript for job application").append("status", (Object)"Pending").append("priority", (Object)"medium").append("userId", (Object)"student").append("createdBy", (Object)"student").append("assignedTo", (Object)"").append("department", (Object)"Academic Affairs").append("createdAt", (Object)new Date()).append("updatedAt", (Object)new Date()));
        System.out.println("\u2713 Tickets collection created with 2 sample tickets");
    }

    private static void setupDocumentsCollection(MongoDatabase database) {
        MongoCollection documents = database.getCollection("documents");
        documents.deleteMany((Bson)new Document());
        documents.insertOne(new Document("docId", (Object)"D001").append("title", (Object)"Student Handbook 2024").append("description", (Object)"Official student rules and regulations").append("type", (Object)"PDF").append("size", (Object)"2.5 MB").append("uploadedBy", (Object)"admin").append("uploadedAt", (Object)new Date()).append("category", (Object)"Policies"));
        documents.insertOne(new Document("docId", (Object)"D002").append("title", (Object)"IT Support Guide").append("description", (Object)"Common technical issues and solutions").append("type", (Object)"PDF").append("size", (Object)"1.2 MB").append("uploadedBy", (Object)"staff").append("uploadedAt", (Object)new Date()).append("category", (Object)"Technical"));
        System.out.println("\u2713 Documents collection created with 2 sample documents");
    }

    private static void setupNotificationsCollection(MongoDatabase database) {
        MongoCollection notifications = database.getCollection("notifications");
        notifications.deleteMany((Bson)new Document());
        notifications.insertOne(new Document("notificationId", (Object)"N001").append("title", (Object)"System Maintenance").append("message", (Object)"System will be down for maintenance on Saturday 10PM-2AM").append("type", (Object)"system").append("priority", (Object)"high").append("targetRole", (Object)"all").append("sentBy", (Object)"admin").append("sentAt", (Object)new Date()).append("isRead", (Object)false));
        notifications.insertOne(new Document("notificationId", (Object)"N002").append("title", (Object)"New Ticket Assigned").append("message", (Object)"You have been assigned a new support ticket: T001").append("type", (Object)"ticket").append("priority", (Object)"medium").append("targetUser", (Object)"staff").append("sentBy", (Object)"system").append("sentAt", (Object)new Date()).append("isRead", (Object)false));
        System.out.println("\u2713 Notifications collection created with 2 sample notifications");
    }
}

