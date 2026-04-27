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

public class DepartmentDAO {
    private MongoClient mongoClient;
    private MongoDatabase database;
    private MongoCollection<Document> departmentsCollection;
    
    public DepartmentDAO() {
        try {
            mongoClient = MongoClients.create("mongodb://localhost:27017");
            database = mongoClient.getDatabase("helpdesk_db");
            departmentsCollection = database.getCollection("departments");
        } catch (Exception e) {
            System.err.println("Error connecting to database: " + e.getMessage());
        }
    }
    
    public boolean createDepartment(schoolhelpdesk.model.Department department) {
        try {
            Document deptDoc = new Document();
            deptDoc.append("departmentId", generateDepartmentId());
            deptDoc.append("name", department.getName());
            deptDoc.append("description", department.getDescription());
            deptDoc.append("staffMembers", department.getStaffMembers() != null ? department.getStaffMembers() : new ArrayList<>());
            deptDoc.append("headOfDepartment", department.getHeadOfDepartment());
            deptDoc.append("email", department.getEmail());
            deptDoc.append("location", department.getLocation());
            deptDoc.append("active", department.isActive());
            deptDoc.append("createdDate", new java.util.Date());
            
            departmentsCollection.insertOne(deptDoc);
            return true;
        } catch (Exception e) {
            System.err.println("Error creating department: " + e.getMessage());
            return false;
        }
    }
    
    private String generateDepartmentId() {
        return "DEPT" + System.currentTimeMillis();
    }
    
    public List<Document> getAllDepartments() {
        List<Document> departments = new ArrayList<>();
        try {
            FindIterable<Document> result = departmentsCollection.find()
                .sort(Sorts.ascending("name"));
            for (Document dept : result) {
                departments.add(dept);
            }
        } catch (Exception e) {
            System.err.println("Error getting all departments: " + e.getMessage());
        }
        return departments;
    }
    
    public List<Document> getActiveDepartments() {
        List<Document> departments = new ArrayList<>();
        try {
            Bson filter = eq("active", true);
            FindIterable<Document> result = departmentsCollection.find(filter)
                .sort(Sorts.ascending("name"));
            for (Document dept : result) {
                departments.add(dept);
            }
        } catch (Exception e) {
            System.err.println("Error getting active departments: " + e.getMessage());
        }
        return departments;
    }
    
    public Document getDepartmentById(String departmentId) {
        try {
            Bson filter = eq("departmentId", departmentId);
            FindIterable<Document> result = departmentsCollection.find(filter);
            return result.first();
        } catch (Exception e) {
            System.err.println("Error getting department by ID: " + e.getMessage());
            return null;
        }
    }
    
    public Document getDepartmentByName(String name) {
        try {
            Bson filter = eq("name", name);
            FindIterable<Document> result = departmentsCollection.find(filter);
            return result.first();
        } catch (Exception e) {
            System.err.println("Error getting department by name: " + e.getMessage());
            return null;
        }
    }
    
    public boolean updateDepartment(String departmentId, schoolhelpdesk.model.Department department) {
        try {
            Bson filter = eq("departmentId", departmentId);
            Bson update = Updates.combine(
                Updates.set("name", department.getName()),
                Updates.set("description", department.getDescription()),
                Updates.set("headOfDepartment", department.getHeadOfDepartment()),
                Updates.set("email", department.getEmail()),
                Updates.set("location", department.getLocation()),
                Updates.set("active", department.isActive()),
                Updates.set("lastUpdated", new java.util.Date())
            );
            departmentsCollection.updateOne(filter, update);
            return true;
        } catch (Exception e) {
            System.err.println("Error updating department: " + e.getMessage());
            return false;
        }
    }
    
    public boolean addStaffToDepartment(String departmentId, String staffUsername) {
        try {
            Bson filter = eq("departmentId", departmentId);
            Document department = getDepartmentById(departmentId);
            if (department != null) {
                @SuppressWarnings("unchecked")
                List<String> staffMembers = (List<String>) department.get("staffMembers");
                if (staffMembers == null) {
                    staffMembers = new ArrayList<>();
                }
                if (!staffMembers.contains(staffUsername)) {
                    staffMembers.add(staffUsername);
                    Bson update = Updates.combine(
                        Updates.set("staffMembers", staffMembers),
                        Updates.set("lastUpdated", new java.util.Date())
                    );
                    departmentsCollection.updateOne(filter, update);
                    return true;
                }
            }
        } catch (Exception e) {
            System.err.println("Error adding staff to department: " + e.getMessage());
        }
        return false;
    }
    
    public boolean removeStaffFromDepartment(String departmentId, String staffUsername) {
        try {
            Bson filter = eq("departmentId", departmentId);
            Document department = getDepartmentById(departmentId);
            if (department != null) {
                @SuppressWarnings("unchecked")
                List<String> staffMembers = (List<String>) department.get("staffMembers");
                if (staffMembers != null && staffMembers.contains(staffUsername)) {
                    staffMembers.remove(staffUsername);
                    Bson update = Updates.combine(
                        Updates.set("staffMembers", staffMembers),
                        Updates.set("lastUpdated", new java.util.Date())
                    );
                    departmentsCollection.updateOne(filter, update);
                    return true;
                }
            }
        } catch (Exception e) {
            System.err.println("Error removing staff from department: " + e.getMessage());
        }
        return false;
    }
    
    public boolean deleteDepartment(String departmentId) {
        try {
            Bson filter = eq("departmentId", departmentId);
            departmentsCollection.deleteOne(filter);
            return true;
        } catch (Exception e) {
            System.err.println("Error deleting department: " + e.getMessage());
            return false;
        }
    }
    
    public boolean deactivateDepartment(String departmentId) {
        try {
            Bson filter = eq("departmentId", departmentId);
            Bson update = Updates.combine(
                Updates.set("active", false),
                Updates.set("lastUpdated", new java.util.Date())
            );
            departmentsCollection.updateOne(filter, update);
            return true;
        } catch (Exception e) {
            System.err.println("Error deactivating department: " + e.getMessage());
            return false;
        }
    }
    
    public long getDepartmentCount() {
        try {
            return departmentsCollection.countDocuments();
        } catch (Exception e) {
            System.err.println("Error getting department count: " + e.getMessage());
            return 0;
        }
    }
    
    public long getActiveDepartmentCount() {
        try {
            Bson filter = eq("active", true);
            return departmentsCollection.countDocuments(filter);
        } catch (Exception e) {
            System.err.println("Error getting active department count: " + e.getMessage());
            return 0;
        }
    }
    
    public List<Document> searchDepartments(String searchTerm) {
        List<Document> departments = new ArrayList<>();
        try {
            Bson filter = Filters.or(
                Filters.regex("departmentId", searchTerm, "i"),
                Filters.regex("name", searchTerm, "i"),
                Filters.regex("description", searchTerm, "i")
            );
            FindIterable<Document> result = departmentsCollection.find(filter)
                .sort(Sorts.ascending("name"));
            for (Document dept : result) {
                departments.add(dept);
            }
        } catch (Exception e) {
            System.err.println("Error searching departments: " + e.getMessage());
        }
        return departments;
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
