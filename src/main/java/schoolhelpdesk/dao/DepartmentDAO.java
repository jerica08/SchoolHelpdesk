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
import schoolhelpdesk.model.Department;

public class DepartmentDAO {
    private MongoClient mongoClient;
    private MongoDatabase database;
    private MongoCollection<Document> departmentsCollection;

    public DepartmentDAO() {
        try {
            this.mongoClient = MongoClients.create((String)"mongodb://localhost:27017");
            this.database = this.mongoClient.getDatabase("helpdesk_db");
            this.departmentsCollection = this.database.getCollection("departments");
        }
        catch (Exception e) {
            System.err.println("Error connecting to database: " + e.getMessage());
        }
    }

    public boolean createDepartment(Department department) {
        try {
            Document deptDoc = new Document();
            deptDoc.append("departmentId", (Object)this.generateDepartmentId());
            deptDoc.append("name", (Object)department.getName());
            deptDoc.append("description", (Object)department.getDescription());
            deptDoc.append("staffMembers", department.getStaffMembers() != null ? department.getStaffMembers() : new ArrayList());
            deptDoc.append("headOfDepartment", (Object)department.getHeadOfDepartment());
            deptDoc.append("email", (Object)department.getEmail());
            deptDoc.append("location", (Object)department.getLocation());
            deptDoc.append("active", (Object)department.isActive());
            deptDoc.append("createdDate", (Object)new Date());
            this.departmentsCollection.insertOne(deptDoc);
            return true;
        }
        catch (Exception e) {
            System.err.println("Error creating department: " + e.getMessage());
            return false;
        }
    }

    private String generateDepartmentId() {
        return "DEPT" + System.currentTimeMillis();
    }

    public List<Document> getAllDepartments() {
        ArrayList<Document> departments = new ArrayList<Document>();
        try {
            FindIterable<Document> result = this.departmentsCollection.find().sort(Sorts.ascending((String[])new String[]{"name"}));
            for (Document dept : result) {
                departments.add(dept);
            }
        }
        catch (Exception e) {
            System.err.println("Error getting all departments: " + e.getMessage());
        }
        return departments;
    }

    public List<Document> getActiveDepartments() {
        ArrayList<Document> departments = new ArrayList<Document>();
        try {
            Bson filter = Filters.eq((String)"active", (Object)true);
            FindIterable<Document> result = this.departmentsCollection.find(filter).sort(Sorts.ascending((String[])new String[]{"name"}));
            for (Document dept : result) {
                departments.add(dept);
            }
        }
        catch (Exception e) {
            System.err.println("Error getting active departments: " + e.getMessage());
        }
        return departments;
    }

    public Document getDepartmentById(String departmentId) {
        try {
            Bson filter = Filters.eq((String)"departmentId", (Object)departmentId);
            FindIterable<Document> result = this.departmentsCollection.find(filter);
            return (Document)result.first();
        }
        catch (Exception e) {
            System.err.println("Error getting department by ID: " + e.getMessage());
            return null;
        }
    }

    public Document getDepartmentByName(String name) {
        try {
            Bson filter = Filters.eq((String)"name", (Object)name);
            FindIterable<Document> result = this.departmentsCollection.find(filter);
            return (Document)result.first();
        }
        catch (Exception e) {
            System.err.println("Error getting department by name: " + e.getMessage());
            return null;
        }
    }

    public boolean updateDepartment(String departmentId, Department department) {
        try {
            Bson filter = Filters.eq((String)"departmentId", (Object)departmentId);
            Bson update = Updates.combine((Bson[])new Bson[]{Updates.set((String)"name", (Object)department.getName()), Updates.set((String)"description", (Object)department.getDescription()), Updates.set((String)"headOfDepartment", (Object)department.getHeadOfDepartment()), Updates.set((String)"email", (Object)department.getEmail()), Updates.set((String)"location", (Object)department.getLocation()), Updates.set((String)"active", (Object)department.isActive()), Updates.set((String)"lastUpdated", (Object)new Date())});
            this.departmentsCollection.updateOne(filter, update);
            return true;
        }
        catch (Exception e) {
            System.err.println("Error updating department: " + e.getMessage());
            return false;
        }
    }

    public boolean addStaffToDepartment(String departmentId, String staffUsername) {
        try {
            Bson filter = Filters.eq((String)"departmentId", (Object)departmentId);
            Document department = this.getDepartmentById(departmentId);
            if (department != null) {
                ArrayList<String> staffMembers = (ArrayList<String>)department.get((Object)"staffMembers");
                if (staffMembers == null) {
                    staffMembers = new ArrayList<String>();
                }
                if (!staffMembers.contains(staffUsername)) {
                    staffMembers.add(staffUsername);
                    Bson update = Updates.combine((Bson[])new Bson[]{Updates.set((String)"staffMembers", staffMembers), Updates.set((String)"lastUpdated", (Object)new Date())});
                    this.departmentsCollection.updateOne(filter, update);
                    return true;
                }
            }
        }
        catch (Exception e) {
            System.err.println("Error adding staff to department: " + e.getMessage());
        }
        return false;
    }

    public boolean removeStaffFromDepartment(String departmentId, String staffUsername) {
        try {
            List staffMembers;
            Bson filter = Filters.eq((String)"departmentId", (Object)departmentId);
            Document department = this.getDepartmentById(departmentId);
            if (department != null && (staffMembers = (List)department.get((Object)"staffMembers")) != null && staffMembers.contains(staffUsername)) {
                staffMembers.remove(staffUsername);
                Bson update = Updates.combine((Bson[])new Bson[]{Updates.set((String)"staffMembers", (Object)staffMembers), Updates.set((String)"lastUpdated", (Object)new Date())});
                this.departmentsCollection.updateOne(filter, update);
                return true;
            }
        }
        catch (Exception e) {
            System.err.println("Error removing staff from department: " + e.getMessage());
        }
        return false;
    }

    public boolean deleteDepartment(String departmentId) {
        try {
            Bson filter = Filters.eq((String)"departmentId", (Object)departmentId);
            this.departmentsCollection.deleteOne(filter);
            return true;
        }
        catch (Exception e) {
            System.err.println("Error deleting department: " + e.getMessage());
            return false;
        }
    }

    public boolean deactivateDepartment(String departmentId) {
        try {
            Bson filter = Filters.eq((String)"departmentId", (Object)departmentId);
            Bson update = Updates.combine((Bson[])new Bson[]{Updates.set((String)"active", (Object)false), Updates.set((String)"lastUpdated", (Object)new Date())});
            this.departmentsCollection.updateOne(filter, update);
            return true;
        }
        catch (Exception e) {
            System.err.println("Error deactivating department: " + e.getMessage());
            return false;
        }
    }

    public long getDepartmentCount() {
        try {
            return this.departmentsCollection.countDocuments();
        }
        catch (Exception e) {
            System.err.println("Error getting department count: " + e.getMessage());
            return 0L;
        }
    }

    public long getActiveDepartmentCount() {
        try {
            Bson filter = Filters.eq((String)"active", (Object)true);
            return this.departmentsCollection.countDocuments(filter);
        }
        catch (Exception e) {
            System.err.println("Error getting active department count: " + e.getMessage());
            return 0L;
        }
    }

    public List<Document> searchDepartments(String searchTerm) {
        ArrayList<Document> departments = new ArrayList<Document>();
        try {
            Bson filter = Filters.or((Bson[])new Bson[]{Filters.regex((String)"departmentId", (String)searchTerm, (String)"i"), Filters.regex((String)"name", (String)searchTerm, (String)"i"), Filters.regex((String)"description", (String)searchTerm, (String)"i")});
            FindIterable<Document> result = this.departmentsCollection.find(filter).sort(Sorts.ascending((String[])new String[]{"name"}));
            for (Document dept : result) {
                departments.add(dept);
            }
        }
        catch (Exception e) {
            System.err.println("Error searching departments: " + e.getMessage());
        }
        return departments;
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

