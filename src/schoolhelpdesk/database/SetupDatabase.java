package schoolhelpdesk.database;

import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoCollection;
import org.bson.Document;

public class SetupDatabase {
    public static void main(String[] args) {
        try {
            MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017");
            MongoDatabase database = mongoClient.getDatabase("helpdesk_db");
            
            System.out.println("Setting up School Helpdesk Database...");
            
            // Setup users collection
            setupUsersCollection(database);
            
            // Setup departments collection  
            setupDepartmentsCollection(database);
            
            // Setup tickets collection
            setupTicketsCollection(database);
            
            // Setup documents collection
            setupDocumentsCollection(database);
            
            // Setup notifications collection
            setupNotificationsCollection(database);
            
            System.out.println("\n✓ Database setup completed successfully!");
            
            mongoClient.close();
            
        } catch (Exception e) {
            System.err.println("✗ Error setting up database: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static void setupUsersCollection(MongoDatabase database) {
        MongoCollection<Document> users = database.getCollection("users");
        
        // Clear existing data
        users.deleteMany(new Document());
        
        // Insert sample users
        users.insertOne(new Document("username", "admin")
                .append("password", "admin123")
                .append("role", "admin")
                .append("email", "admin@school.edu")
                .append("fullName", "System Administrator"));
                
        users.insertOne(new Document("username", "staff")
                .append("password", "staff123")
                .append("role", "staff")
                .append("email", "staff@school.edu")
                .append("fullName", "Support Staff"));
                
        users.insertOne(new Document("username", "student")
                .append("password", "student123")
                .append("role", "student")
                .append("email", "student@school.edu")
                .append("fullName", "John Student"));
        
        System.out.println("✓ Users collection created with 3 sample users");
    }
    
    private static void setupDepartmentsCollection(MongoDatabase database) {
        MongoCollection<Document> departments = database.getCollection("departments");
        
        // Clear existing data
        departments.deleteMany(new Document());
        
        // Insert sample departments
        departments.insertOne(new Document("name", "IT Support")
                .append("description", "Technical support and computer services")
                .append("head", "IT Manager")
                .append("location", "Building A, Room 101"));
                
        departments.insertOne(new Document("name", "Academic Affairs")
                .append("description", "Student academic services and records")
                .append("head", "Academic Dean")
                .append("location", "Building B, Room 205"));
                
        departments.insertOne(new Document("name", "Student Services")
                .append("description", "Student welfare and support services")
                .append("head", "Student Services Director")
                .append("location", "Building C, Room 150"));
                
        departments.insertOne(new Document("name", "Finance")
                .append("description", "Financial services and payments")
                .append("head", "Finance Manager")
                .append("location", "Building D, Room 300"));
        
        System.out.println("✓ Departments collection created with 4 departments");
    }
    
    private static void setupTicketsCollection(MongoDatabase database) {
        MongoCollection<Document> tickets = database.getCollection("tickets");
        
        // Clear existing data
        tickets.deleteMany(new Document());
        
        // Insert sample tickets
        tickets.insertOne(new Document("ticketId", "T001")
                .append("title", "Cannot login to student portal")
                .append("description", "Student unable to access online portal with credentials")
                .append("status", "In Progress")
                .append("priority", "high")
                .append("userId", "student")
                .append("createdBy", "student")
                .append("assignedTo", "staff")
                .append("department", "IT Support")
                .append("createdAt", new java.util.Date())
                .append("updatedAt", new java.util.Date()));
                
        tickets.insertOne(new Document("ticketId", "T002")
                .append("title", "Request for transcript")
                .append("description", "Need official transcript for job application")
                .append("status", "Pending")
                .append("priority", "medium")
                .append("userId", "student")
                .append("createdBy", "student")
                .append("assignedTo", "")
                .append("department", "Academic Affairs")
                .append("createdAt", new java.util.Date())
                .append("updatedAt", new java.util.Date()));
        
        System.out.println("✓ Tickets collection created with 2 sample tickets");
    }
    
    private static void setupDocumentsCollection(MongoDatabase database) {
        MongoCollection<Document> documents = database.getCollection("documents");
        
        // Clear existing data
        documents.deleteMany(new Document());
        
        // Insert sample documents
        documents.insertOne(new Document("docId", "D001")
                .append("title", "Student Handbook 2024")
                .append("description", "Official student rules and regulations")
                .append("type", "PDF")
                .append("size", "2.5 MB")
                .append("uploadedBy", "admin")
                .append("uploadedAt", new java.util.Date())
                .append("category", "Policies"));
                
        documents.insertOne(new Document("docId", "D002")
                .append("title", "IT Support Guide")
                .append("description", "Common technical issues and solutions")
                .append("type", "PDF")
                .append("size", "1.2 MB")
                .append("uploadedBy", "staff")
                .append("uploadedAt", new java.util.Date())
                .append("category", "Technical"));
        
        System.out.println("✓ Documents collection created with 2 sample documents");
    }
    
    private static void setupNotificationsCollection(MongoDatabase database) {
        MongoCollection<Document> notifications = database.getCollection("notifications");
        
        // Clear existing data
        notifications.deleteMany(new Document());
        
        // Insert sample notifications
        notifications.insertOne(new Document("notificationId", "N001")
                .append("title", "System Maintenance")
                .append("message", "System will be down for maintenance on Saturday 10PM-2AM")
                .append("type", "system")
                .append("priority", "high")
                .append("targetRole", "all")
                .append("sentBy", "admin")
                .append("sentAt", new java.util.Date())
                .append("isRead", false));
                
        notifications.insertOne(new Document("notificationId", "N002")
                .append("title", "New Ticket Assigned")
                .append("message", "You have been assigned a new support ticket: T001")
                .append("type", "ticket")
                .append("priority", "medium")
                .append("targetUser", "staff")
                .append("sentBy", "system")
                .append("sentAt", new java.util.Date())
                .append("isRead", false));
        
        System.out.println("✓ Notifications collection created with 2 sample notifications");
    }
}
