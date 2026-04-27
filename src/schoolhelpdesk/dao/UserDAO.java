package schoolhelpdesk.dao;

import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.FindIterable;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import com.mongodb.client.model.Sorts;
import org.bson.Document;
import org.bson.conversions.Bson;
import static com.mongodb.client.model.Filters.eq;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import schoolhelpdesk.model.User;

public class UserDAO {
    private MongoClient mongoClient;
    private MongoDatabase database;
    private MongoCollection<Document> usersCollection;
    
    public UserDAO() {
        try {
            mongoClient = MongoClients.create("mongodb://localhost:27017");
            database = mongoClient.getDatabase("helpdesk_db");
            usersCollection = database.getCollection("users");
        } catch (Exception e) {
            System.err.println("Error connecting to database: " + e.getMessage());
        }
    }
    
    public boolean createUser(schoolhelpdesk.model.User user) {
        try {
            Document userDoc = new Document();
            userDoc.append("userId", generateUserId());
            userDoc.append("username", user.getUsername());
            userDoc.append("password", user.getPassword());
            userDoc.append("role", user.getRole());
            userDoc.append("email", user.getEmail());
            userDoc.append("fullName", user.getFullName());
            userDoc.append("phone", user.getPhone());
            userDoc.append("department", user.getDepartment());
            userDoc.append("notes", user.getNotes());
            userDoc.append("active", user.isActive());
            userDoc.append("createdDate", new java.util.Date());
            userDoc.append("lastLogin", null);
            
            usersCollection.insertOne(userDoc);
            return true;
        } catch (Exception e) {
            System.err.println("Error creating user: " + e.getMessage());
            return false;
        }
    }
    
    private String generateUserId() {
        return "U" + System.currentTimeMillis();
    }
    
    public schoolhelpdesk.model.User authenticateUser(String username, String password) {
        try {
            // Try authentication without active field check first
            Bson filter = Filters.and(
                eq("username", username),
                eq("password", password)
            );
            FindIterable<Document> result = usersCollection.find(filter);
            Document userDoc = result.first();
            
            if (userDoc != null) {
                // Check if user is active (only if the field exists)
                if (userDoc.containsKey("active") && !userDoc.getBoolean("active", true)) {
                    System.out.println("User is inactive: " + username);
                    return null;
                }
                
                schoolhelpdesk.model.User user = new schoolhelpdesk.model.User();
                user.setUsername(userDoc.getString("username"));
                user.setPassword(userDoc.getString("password"));
                user.setRole(userDoc.getString("role"));
                user.setEmail(userDoc.getString("email"));
                user.setFullName(userDoc.getString("fullName"));
                
                // Update last login (try-catch to avoid errors if field doesn't exist)
                try {
                    Bson update = Updates.set("lastLogin", new java.util.Date());
                    usersCollection.updateOne(filter, update);
                } catch (Exception e) {
                    System.err.println("Warning: Could not update last login: " + e.getMessage());
                }
                
                return user;
            } else {
                System.out.println("No user found with username: " + username);
            }
        } catch (Exception e) {
            System.err.println("Error authenticating user: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
    
    public List<Document> getAllUsers() {
        List<Document> users = new ArrayList<>();
        try {
            FindIterable<Document> result = usersCollection.find()
                .sort(Sorts.ascending("fullName"));
            for (Document user : result) {
                users.add(user);
            }
        } catch (Exception e) {
            System.err.println("Error getting all users: " + e.getMessage());
        }
        return users;
    }
    
    public List<Document> getActiveUsers() {
        List<Document> users = new ArrayList<>();
        try {
            Bson filter = eq("active", true);
            FindIterable<Document> result = usersCollection.find(filter)
                .sort(Sorts.ascending("fullName"));
            for (Document user : result) {
                users.add(user);
            }
        } catch (Exception e) {
            System.err.println("Error getting active users: " + e.getMessage());
        }
        return users;
    }
    
    public List<Document> getUsersByRole(String role) {
        List<Document> users = new ArrayList<>();
        try {
            Bson filter = Filters.and(
                eq("role", role),
                eq("active", true)
            );
            FindIterable<Document> result = usersCollection.find(filter)
                .sort(Sorts.ascending("fullName"));
            for (Document user : result) {
                users.add(user);
            }
        } catch (Exception e) {
            System.err.println("Error getting users by role: " + e.getMessage());
        }
        return users;
    }
    
    public Document getUserById(String userId) {
        try {
            Bson filter = eq("userId", userId);
            FindIterable<Document> result = usersCollection.find(filter);
            return result.first();
        } catch (Exception e) {
            System.err.println("Error getting user by ID: " + e.getMessage());
            return null;
        }
    }
    
    public Document getUserByUsername(String username) {
        try {
            Bson filter = eq("username", username);
            FindIterable<Document> result = usersCollection.find(filter);
            return result.first();
        } catch (Exception e) {
            System.err.println("Error getting user by username: " + e.getMessage());
            return null;
        }
    }
    
    public boolean updateUser(String userId, schoolhelpdesk.model.User user) {
        try {
            Bson filter = eq("userId", userId);
            Bson update = Updates.combine(
                Updates.set("username", user.getUsername()),
                Updates.set("email", user.getEmail()),
                Updates.set("fullName", user.getFullName()),
                Updates.set("role", user.getRole()),
                Updates.set("lastUpdated", new java.util.Date())
            );
            usersCollection.updateOne(filter, update);
            return true;
        } catch (Exception e) {
            System.err.println("Error updating user: " + e.getMessage());
            return false;
        }
    }
    
        
    public boolean deactivateUser(String userId) {
        try {
            Bson filter = eq("userId", userId);
            Bson update = Updates.combine(
                Updates.set("active", false),
                Updates.set("deactivatedDate", new java.util.Date()),
                Updates.set("lastUpdated", new java.util.Date())
            );
            usersCollection.updateOne(filter, update);
            return true;
        } catch (Exception e) {
            System.err.println("Error deactivating user: " + e.getMessage());
            return false;
        }
    }
    
    public boolean activateUser(String userId) {
        try {
            Bson filter = eq("userId", userId);
            Bson update = Updates.combine(
                Updates.set("active", true),
                Updates.unset("deactivatedDate"),
                Updates.set("lastUpdated", new java.util.Date())
            );
            usersCollection.updateOne(filter, update);
            return true;
        } catch (Exception e) {
            System.err.println("Error activating user: " + e.getMessage());
            return false;
        }
    }
    
    public boolean deleteUser(String userId) {
        try {
            Bson filter = eq("userId", userId);
            usersCollection.deleteOne(filter);
            return true;
        } catch (Exception e) {
            System.err.println("Error deleting user: " + e.getMessage());
            return false;
        }
    }
    
    public boolean deleteUserByUsername(String username) {
        try {
            Bson filter = eq("username", username);
            usersCollection.deleteOne(filter);
            return true;
        } catch (Exception e) {
            System.err.println("Error deleting user by username: " + e.getMessage());
            return false;
        }
    }
    
    public boolean usernameExists(String username) {
        try {
            Bson filter = eq("username", username);
            return usersCollection.countDocuments(filter) > 0;
        } catch (Exception e) {
            System.err.println("Error checking username existence: " + e.getMessage());
            return false;
        }
    }
    
    public boolean emailExists(String email) {
        try {
            Bson filter = eq("email", email);
            return usersCollection.countDocuments(filter) > 0;
        } catch (Exception e) {
            System.err.println("Error checking email existence: " + e.getMessage());
            return false;
        }
    }
    
    public long getUserCount() {
        try {
            return usersCollection.countDocuments();
        } catch (Exception e) {
            System.err.println("Error getting user count: " + e.getMessage());
            return 0;
        }
    }
    
    public long getActiveUserCount() {
        try {
            Bson filter = eq("active", true);
            return usersCollection.countDocuments(filter);
        } catch (Exception e) {
            System.err.println("Error getting active user count: " + e.getMessage());
            return 0;
        }
    }
    
    public long getUserCountByRole(String role) {
        try {
            Bson filter = Filters.and(
                eq("role", role),
                eq("active", true)
            );
            return usersCollection.countDocuments(filter);
        } catch (Exception e) {
            System.err.println("Error getting user count by role: " + e.getMessage());
            return 0;
        }
    }
    
    public List<Document> searchUsers(String searchTerm) {
        List<Document> users = new ArrayList<>();
        try {
            Bson filter = Filters.or(
                Filters.regex("username", searchTerm, "i"),
                Filters.regex("fullName", searchTerm, "i"),
                Filters.regex("email", searchTerm, "i"),
                Filters.regex("role", searchTerm, "i")
            );
            FindIterable<Document> result = usersCollection.find(filter)
                .sort(Sorts.ascending("fullName"));
            for (Document user : result) {
                users.add(user);
            }
        } catch (Exception e) {
            System.err.println("Error searching users: " + e.getMessage());
        }
        return users;
    }
    
    public boolean updateUserPassword(String username, String newPassword) {
        try {
            Bson filter = eq("username", username);
            Bson update = Updates.set("password", newPassword);
            usersCollection.updateOne(filter, update);
            return true;
        } catch (Exception e) {
            System.err.println("Error updating user password: " + e.getMessage());
            return false;
        }
    }
    
    public boolean updateUserStatus(String username, boolean active) {
        try {
            Bson filter = eq("username", username);
            Bson update = Updates.set("active", active);
            usersCollection.updateOne(filter, update);
            return true;
        } catch (Exception e) {
            System.err.println("Error updating user status: " + e.getMessage());
            return false;
        }
    }
    
    public boolean updateUser(User user) {
        try {
            Bson filter = eq("username", user.getUsername());
            Bson update = Updates.combine(
                Updates.set("fullName", user.getFullName()),
                Updates.set("email", user.getEmail()),
                Updates.set("phone", user.getPhone()),
                Updates.set("role", user.getRole()),
                Updates.set("department", user.getDepartment()),
                Updates.set("notes", user.getNotes()),
                Updates.set("active", user.isActive())
            );
            
            // Only update password if it's provided
            if (user.getPassword() != null && !user.getPassword().isEmpty()) {
                update = Updates.combine(update, Updates.set("password", user.getPassword()));
            }
            
            usersCollection.updateOne(filter, update);
            return true;
        } catch (Exception e) {
            System.err.println("Error updating user: " + e.getMessage());
            return false;
        }
    }
    
        
    public void close() {
        try {
            if (mongoClient != null) {
                mongoClient.close();
            }
        } catch (Exception e) {
            System.err.println("Error closing connection: " + e.getMessage());
        }
    }
}
