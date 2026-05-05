import schoolhelpdesk.dao.DepartmentDAO;
import schoolhelpdesk.dao.UserDAO;
import org.bson.Document;
import java.util.List;

public class TestDepartmentFiltering {
    public static void main(String[] args) {
        try {
            System.out.println("Testing Department-Based Staff Filtering...");
            
            DepartmentDAO deptDAO = new DepartmentDAO();
            UserDAO userDAO = new UserDAO();
            
            // Test 1: Get all departments
            System.out.println("\n=== All Departments ===");
            List<Document> departments = deptDAO.getAllDepartments();
            for (Document dept : departments) {
                System.out.println("Department: " + dept.getString("name"));
                List<String> staffMembers = (List<String>) dept.get("staffMembers");
                if (staffMembers != null) {
                    System.out.println("  Staff usernames: " + staffMembers);
                    System.out.println("  Staff full names:");
                    for (String username : staffMembers) {
                        Document user = userDAO.getUserByUsername(username);
                        if (user != null) {
                            System.out.println("    - " + username + " -> " + user.getString("fullName"));
                        } else {
                            System.out.println("    - " + username + " -> USER NOT FOUND");
                        }
                    }
                } else {
                    System.out.println("  No staff members assigned");
                }
                System.out.println();
            }
            
            // Test 2: Test specific department filtering
            System.out.println("\n=== Testing IT Support Department ===");
            Document itDept = deptDAO.getDepartmentByName("IT Support");
            if (itDept != null) {
                List<String> itStaff = (List<String>) itDept.get("staffMembers");
                if (itStaff != null) {
                    System.out.println("IT Support staff should show:");
                    for (String username : itStaff) {
                        Document user = userDAO.getUserByUsername(username);
                        if (user != null) {
                            System.out.println("  - " + user.getString("fullName"));
                        }
                    }
                }
            }
            
            deptDAO.close();
            userDAO.close();
            
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
