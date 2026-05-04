/*
 * Decompiled with CFR 0.152.
 */
package schoolhelpdesk.test;

public class MongoDBTest {
    public static void main(String[] args) {
        try {
            Class.forName("com.mongodb.client.MongoClient");
            System.out.println("SUCCESS: MongoDB classes are available!");
            System.out.println("MongoDB driver is properly loaded.");
        }
        catch (ClassNotFoundException e) {
            System.out.println("ERROR: MongoDB classes not found: " + e.getMessage());
            System.out.println("Please reload Maven dependencies in NetBeans.");
        }
        catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
        }
    }
}

