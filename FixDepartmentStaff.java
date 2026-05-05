import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import java.util.ArrayList;
import java.util.List;

public class FixDepartmentStaff {
    public static void main(String[] args) {
        try {
            System.out.println("Connecting to MongoDB...");
            MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017");
            MongoDatabase database = mongoClient.getDatabase("helpdesk_db");
            
            // Get collections
            MongoCollection<Document> departments = database.getCollection("departments");
            MongoCollection<Document> users = database.getCollection("users");
            
            System.out.println("Clearing existing departments...");
            departments.deleteMany(new Document());
            
            // Create staff lists
            List<String> itStaff = new ArrayList<>();
            itStaff.add("staff");
            
            List<String> academicStaff = new ArrayList<>();
            List<String> studentServicesStaff = new ArrayList<>();
            List<String> financeStaff = new ArrayList<>();
            
            System.out.println("Creating departments with staff assignments...");
            
            // IT Support Department
            Document itDept = new Document()
                .append("departmentId", "DEPT001")
                .append("name", "IT Support")
                .append("description", "Technical support and computer services")
                .append("headOfDepartment", "IT Manager")
                .append("email", "it@school.edu")
                .append("location", "Building A, Room 101")
                .append("staffMembers", itStaff)
                .append("active", true)
                .append("createdDate", new java.util.Date());
            departments.insertOne(itDept);
            
            // Academic Affairs Department
            Document academicDept = new Document()
                .append("departmentId", "DEPT002")
                .append("name", "Academic Affairs")
                .append("description", "Student academic services and records")
                .append("headOfDepartment", "Academic Dean")
                .append("email", "academic@school.edu")
                .append("location", "Building B, Room 205")
                .append("staffMembers", academicStaff)
                .append("active", true)
                .append("createdDate", new java.util.Date());
            departments.insertOne(academicDept);
            
            // Student Services Department
            Document studentServicesDept = new Document()
                .append("departmentId", "DEPT003")
                .append("name", "Student Services")
                .append("description", "Student welfare and support services")
                .append("headOfDepartment", "Student Services Director")
                .append("email", "studentservices@school.edu")
                .append("location", "Building C, Room 150")
                .append("staffMembers", studentServicesStaff)
                .append("active", true)
                .append("createdDate", new java.util.Date());
            departments.insertOne(studentServicesDept);
            
            // Finance Department
            Document financeDept = new Document()
                .append("departmentId", "DEPT004")
                .append("name", "Finance")
                .append("description", "Financial services and payments")
                .append("headOfDepartment", "Finance Manager")
                .append("email", "finance@school.edu")
                .append("location", "Building D, Room 300")
                .append("staffMembers", financeStaff)
                .append("active", true)
                .append("createdDate", new java.util.Date());
            departments.insertOne(financeDept);
            
            System.out.println("Verifying staff users exist...");
            
            // Check if staff user exists
            Document staffUser = users.find(new Document("username", "staff")).first();
            if (staffUser != null) {
                System.out.println("✓ Staff user found: " + staffUser.getString("fullName"));
            } else {
                System.out.println("✗ Staff user not found! Creating staff user...");
                Document newStaff = new Document()
                    .append("username", "staff")
                    .append("password", "staff123")
                    .append("role", "staff")
                    .append("email", "staff@school.edu")
                    .append("fullName", "Support Staff");
                users.insertOne(newStaff);
            }
            
            System.out.println("\n=== DEPARTMENT SETUP COMPLETE ===");
            System.out.println("Departments created with staff assignments:");
            
            List<Document> allDepts = departments.find().into(new ArrayList<>());
            for (Document dept : allDepts) {
                System.out.println("- " + dept.getString("name") + " (ID: " + dept.getString("departmentId") + ")");
                List<String> staff = (List<String>) dept.get("staffMembers");
                if (staff != null && !staff.isEmpty()) {
                    System.out.println("  Staff: " + staff);
                } else {
                    System.out.println("  Staff: No staff assigned");
                }
            }
            
            mongoClient.close();
            System.out.println("\n✓ Database fix completed successfully!");
            
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
