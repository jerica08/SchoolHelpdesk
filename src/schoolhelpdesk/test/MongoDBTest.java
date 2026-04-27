package schoolhelpdesk.test;

// Test to verify MongoDB imports are working
public class MongoDBTest {
    public static void main(String[] args) {
        try {
            // Test MongoDB imports
            Class.forName("com.mongodb.client.MongoClient");
            System.out.println("SUCCESS: MongoDB classes are available!");
            
            // Test if we can create a simple object
            System.out.println("MongoDB driver is properly loaded.");
            
        } catch (ClassNotFoundException e) {
            System.out.println("ERROR: MongoDB classes not found: " + e.getMessage());
            System.out.println("Please reload Maven dependencies in NetBeans.");
        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
        }
    }
}
