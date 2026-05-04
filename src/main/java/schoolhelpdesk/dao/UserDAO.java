/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mongodb.client.FindIterable
 *  com.mongodb.client.MongoClient
 *  com.mongodb.client.MongoClients
 *  com.mongodb.client.MongoCollection
 *  com.mongodb.client.MongoDatabase
 *  com.mongodb.client.model.Filters
 *  com.mongodb.client.model.Sorts
 *  com.mongodb.client.model.Updates
 *  org.bson.Document
 *  org.bson.conversions.Bson
 */
package schoolhelpdesk.dao;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import com.mongodb.client.model.Updates;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.bson.Document;
import org.bson.conversions.Bson;
import schoolhelpdesk.model.User;

public class UserDAO {
    private MongoClient mongoClient;
    private MongoDatabase database;
    private MongoCollection<Document> usersCollection;

    public UserDAO() {
        try {
            this.mongoClient = MongoClients.create((String)"mongodb://localhost:27017");
            this.database = this.mongoClient.getDatabase("helpdesk_db");
            this.usersCollection = this.database.getCollection("users");
        }
        catch (Exception e) {
            System.err.println("Error connecting to database: " + e.getMessage());
        }
    }

    public boolean createUser(User user) {
        try {
            Document userDoc = new Document();
            userDoc.append("userId", (Object)this.generateUserId());
            userDoc.append("username", (Object)user.getUsername());
            userDoc.append("password", (Object)user.getPassword());
            userDoc.append("role", (Object)user.getRole());
            userDoc.append("email", (Object)user.getEmail());
            userDoc.append("fullName", (Object)user.getFullName());
            userDoc.append("phone", (Object)user.getPhone());
            userDoc.append("department", (Object)user.getDepartment());
            userDoc.append("notes", (Object)user.getNotes());
            userDoc.append("active", (Object)user.isActive());
            userDoc.append("createdDate", (Object)new Date());
            userDoc.append("lastLogin", null);
            this.usersCollection.insertOne(userDoc);
            return true;
        }
        catch (Exception e) {
            System.err.println("Error creating user: " + e.getMessage());
            return false;
        }
    }

    private String generateUserId() {
        return "U" + System.currentTimeMillis();
    }

    public User authenticateUser(String username, String password) {
        try {
            Bson filter = Filters.and((Bson[])new Bson[]{Filters.eq((String)"username", (Object)username), Filters.eq((String)"password", (Object)password)});
            FindIterable<Document> result = this.usersCollection.find(filter);
            Document userDoc = (Document)result.first();
            if (userDoc != null) {
                if (userDoc.containsKey((Object)"active") && !userDoc.getBoolean((Object)"active", true)) {
                    System.out.println("User is inactive: " + username);
                    return null;
                }
                User user = new User();
                user.setUsername(userDoc.getString((Object)"username"));
                user.setPassword(userDoc.getString((Object)"password"));
                user.setRole(userDoc.getString((Object)"role"));
                user.setEmail(userDoc.getString((Object)"email"));
                user.setFullName(userDoc.getString((Object)"fullName"));
                try {
                    Bson update = Updates.set((String)"lastLogin", (Object)new Date());
                    this.usersCollection.updateOne(filter, update);
                }
                catch (Exception e) {
                    System.err.println("Warning: Could not update last login: " + e.getMessage());
                }
                return user;
            }
            System.out.println("No user found with username: " + username);
        }
        catch (Exception e) {
            System.err.println("Error authenticating user: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public List<Document> getAllUsers() {
        ArrayList<Document> users = new ArrayList<Document>();
        try {
            FindIterable<Document> result = this.usersCollection.find().sort(Sorts.ascending((String[])new String[]{"fullName"}));
            for (Document user : result) {
                users.add(user);
            }
        }
        catch (Exception e) {
            System.err.println("Error getting all users: " + e.getMessage());
        }
        return users;
    }

    public List<Document> getActiveUsers() {
        ArrayList<Document> users = new ArrayList<Document>();
        try {
            Bson filter = Filters.eq((String)"active", (Object)true);
            FindIterable<Document> result = this.usersCollection.find(filter).sort(Sorts.ascending((String[])new String[]{"fullName"}));
            for (Document user : result) {
                users.add(user);
            }
        }
        catch (Exception e) {
            System.err.println("Error getting active users: " + e.getMessage());
        }
        return users;
    }

    public List<Document> getUsersByRole(String role) {
        ArrayList<Document> users = new ArrayList<Document>();
        try {
            Bson filter = Filters.and((Bson[])new Bson[]{Filters.eq((String)"role", (Object)role), Filters.eq((String)"active", (Object)true)});
            FindIterable<Document> result = this.usersCollection.find(filter).sort(Sorts.ascending((String[])new String[]{"fullName"}));
            for (Document user : result) {
                users.add(user);
            }
        }
        catch (Exception e) {
            System.err.println("Error getting users by role: " + e.getMessage());
        }
        return users;
    }

    public Document getUserById(String userId) {
        try {
            Bson filter = Filters.eq((String)"userId", (Object)userId);
            FindIterable<Document> result = this.usersCollection.find(filter);
            return (Document)result.first();
        }
        catch (Exception e) {
            System.err.println("Error getting user by ID: " + e.getMessage());
            return null;
        }
    }

    public Document getUserByUsername(String username) {
        try {
            Bson filter = Filters.eq((String)"username", (Object)username);
            FindIterable<Document> result = this.usersCollection.find(filter);
            return (Document)result.first();
        }
        catch (Exception e) {
            System.err.println("Error getting user by username: " + e.getMessage());
            return null;
        }
    }

    public boolean updateUser(String userId, User user) {
        try {
            Bson filter = Filters.eq((String)"userId", (Object)userId);
            Bson update = Updates.combine((Bson[])new Bson[]{Updates.set((String)"username", (Object)user.getUsername()), Updates.set((String)"email", (Object)user.getEmail()), Updates.set((String)"fullName", (Object)user.getFullName()), Updates.set((String)"role", (Object)user.getRole()), Updates.set((String)"lastUpdated", (Object)new Date())});
            this.usersCollection.updateOne(filter, update);
            return true;
        }
        catch (Exception e) {
            System.err.println("Error updating user: " + e.getMessage());
            return false;
        }
    }

    public boolean deactivateUser(String userId) {
        try {
            Bson filter = Filters.eq((String)"userId", (Object)userId);
            Bson update = Updates.combine((Bson[])new Bson[]{Updates.set((String)"active", (Object)false), Updates.set((String)"deactivatedDate", (Object)new Date()), Updates.set((String)"lastUpdated", (Object)new Date())});
            this.usersCollection.updateOne(filter, update);
            return true;
        }
        catch (Exception e) {
            System.err.println("Error deactivating user: " + e.getMessage());
            return false;
        }
    }

    public boolean activateUser(String userId) {
        try {
            Bson filter = Filters.eq((String)"userId", (Object)userId);
            Bson update = Updates.combine((Bson[])new Bson[]{Updates.set((String)"active", (Object)true), Updates.unset((String)"deactivatedDate"), Updates.set((String)"lastUpdated", (Object)new Date())});
            this.usersCollection.updateOne(filter, update);
            return true;
        }
        catch (Exception e) {
            System.err.println("Error activating user: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteUser(String userId) {
        try {
            Bson filter = Filters.eq((String)"userId", (Object)userId);
            this.usersCollection.deleteOne(filter);
            return true;
        }
        catch (Exception e) {
            System.err.println("Error deleting user: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteUserByUsername(String username) {
        try {
            Bson filter = Filters.eq((String)"username", (Object)username);
            this.usersCollection.deleteOne(filter);
            return true;
        }
        catch (Exception e) {
            System.err.println("Error deleting user by username: " + e.getMessage());
            return false;
        }
    }

    public boolean usernameExists(String username) {
        try {
            Bson filter = Filters.eq((String)"username", (Object)username);
            return this.usersCollection.countDocuments(filter) > 0L;
        }
        catch (Exception e) {
            System.err.println("Error checking username existence: " + e.getMessage());
            return false;
        }
    }

    public boolean emailExists(String email) {
        try {
            Bson filter = Filters.eq((String)"email", (Object)email);
            return this.usersCollection.countDocuments(filter) > 0L;
        }
        catch (Exception e) {
            System.err.println("Error checking email existence: " + e.getMessage());
            return false;
        }
    }

    public long getUserCount() {
        try {
            return this.usersCollection.countDocuments();
        }
        catch (Exception e) {
            System.err.println("Error getting user count: " + e.getMessage());
            return 0L;
        }
    }

    public long getActiveUserCount() {
        try {
            Bson filter = Filters.eq((String)"active", (Object)true);
            return this.usersCollection.countDocuments(filter);
        }
        catch (Exception e) {
            System.err.println("Error getting active user count: " + e.getMessage());
            return 0L;
        }
    }

    public long getUserCountByRole(String role) {
        try {
            Bson filter = Filters.and((Bson[])new Bson[]{Filters.eq((String)"role", (Object)role), Filters.eq((String)"active", (Object)true)});
            return this.usersCollection.countDocuments(filter);
        }
        catch (Exception e) {
            System.err.println("Error getting user count by role: " + e.getMessage());
            return 0L;
        }
    }

    public List<Document> searchUsers(String searchTerm) {
        ArrayList<Document> users = new ArrayList<Document>();
        try {
            Bson filter = Filters.or((Bson[])new Bson[]{Filters.regex((String)"username", (String)searchTerm, (String)"i"), Filters.regex((String)"fullName", (String)searchTerm, (String)"i"), Filters.regex((String)"email", (String)searchTerm, (String)"i"), Filters.regex((String)"role", (String)searchTerm, (String)"i")});
            FindIterable<Document> result = this.usersCollection.find(filter).sort(Sorts.ascending((String[])new String[]{"fullName"}));
            for (Document user : result) {
                users.add(user);
            }
        }
        catch (Exception e) {
            System.err.println("Error searching users: " + e.getMessage());
        }
        return users;
    }

    public boolean updateUserPassword(String username, String newPassword) {
        try {
            Bson filter = Filters.eq((String)"username", (Object)username);
            Bson update = Updates.set((String)"password", (Object)newPassword);
            this.usersCollection.updateOne(filter, update);
            return true;
        }
        catch (Exception e) {
            System.err.println("Error updating user password: " + e.getMessage());
            return false;
        }
    }

    public boolean updateUserStatus(String username, boolean active) {
        try {
            Bson filter = Filters.eq((String)"username", (Object)username);
            Bson update = Updates.set((String)"active", (Object)active);
            this.usersCollection.updateOne(filter, update);
            return true;
        }
        catch (Exception e) {
            System.err.println("Error updating user status: " + e.getMessage());
            return false;
        }
    }

    public boolean updateUser(User user) {
        try {
            Bson filter = Filters.eq((String)"username", (Object)user.getUsername());
            Bson update = Updates.combine((Bson[])new Bson[]{Updates.set((String)"fullName", (Object)user.getFullName()), Updates.set((String)"email", (Object)user.getEmail()), Updates.set((String)"phone", (Object)user.getPhone()), Updates.set((String)"role", (Object)user.getRole()), Updates.set((String)"department", (Object)user.getDepartment()), Updates.set((String)"notes", (Object)user.getNotes()), Updates.set((String)"active", (Object)user.isActive())});
            if (user.getPassword() != null && !user.getPassword().isEmpty()) {
                update = Updates.combine((Bson[])new Bson[]{update, Updates.set((String)"password", (Object)user.getPassword())});
            }
            this.usersCollection.updateOne(filter, update);
            return true;
        }
        catch (Exception e) {
            System.err.println("Error updating user: " + e.getMessage());
            return false;
        }
    }

    public void close() {
        try {
            if (this.mongoClient != null) {
                this.mongoClient.close();
            }
        }
        catch (Exception e) {
            System.err.println("Error closing connection: " + e.getMessage());
        }
    }
}

