/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mongodb.client.MongoClient
 *  com.mongodb.client.MongoClients
 *  com.mongodb.client.MongoDatabase
 */
package schoolhelpdesk.database;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

public class DatabaseConnection {
    public static void main(String[] args) {
        MongoClient client = MongoClients.create((String)"mongodb://localhost:27017/");
        MongoDatabase db = client.getDatabase("helpdesk_db");
        System.out.println("Connected to database: " + db.getName());
    }
}

