/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mongodb.client.AggregateIterable
 *  com.mongodb.client.FindIterable
 *  com.mongodb.client.MongoClient
 *  com.mongodb.client.MongoClients
 *  com.mongodb.client.MongoCollection
 *  com.mongodb.client.MongoDatabase
 *  com.mongodb.client.model.Accumulators
 *  com.mongodb.client.model.Aggregates
 *  com.mongodb.client.model.BsonField
 *  com.mongodb.client.model.Filters
 *  com.mongodb.client.model.Sorts
 *  com.mongodb.client.model.Updates
 *  org.bson.Document
 *  org.bson.conversions.Bson
 */
package schoolhelpdesk.dao;

import com.mongodb.client.AggregateIterable;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Accumulators;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.BsonField;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import com.mongodb.client.model.Updates;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import org.bson.Document;
import org.bson.conversions.Bson;
import schoolhelpdesk.model.Ticket;

public class TicketDAO {
    private MongoClient mongoClient;
    private MongoDatabase database;
    private MongoCollection<Document> ticketsCollection;

    public TicketDAO() {
        try {
            this.mongoClient = MongoClients.create((String)"mongodb://localhost:27017");
            this.database = this.mongoClient.getDatabase("helpdesk_db");
            this.ticketsCollection = this.database.getCollection("tickets");
        }
        catch (Exception e) {
            System.err.println("Error connecting to database: " + e.getMessage());
        }
    }

    public boolean createTicket(Ticket ticket) {
        try {
            Document ticketDoc = new Document();
            ticketDoc.append("ticketId", (Object)this.generateTicketId());
            ticketDoc.append("title", (Object)ticket.getTitle());
            ticketDoc.append("description", (Object)ticket.getDescription());
            ticketDoc.append("createdBy", (Object)ticket.getCreatedBy());
            ticketDoc.append("userId", (Object)ticket.getCreatedBy());
            ticketDoc.append("createdDate", (Object)ticket.getCreatedDate());
            ticketDoc.append("department", (Object)ticket.getDepartment());
            ticketDoc.append("status", (Object)ticket.getStatus());
            ticketDoc.append("priority", (Object)ticket.getPriority());
            ticketDoc.append("assignedTo", (Object)ticket.getAssignedTo());
            ticketDoc.append("attachments", ticket.getAttachments() != null ? ticket.getAttachments() : new ArrayList());
            ticketDoc.append("notes", ticket.getNotes() != null ? ticket.getNotes() : new ArrayList());
            ticketDoc.append("lastUpdated", (Object)new Date());
            this.ticketsCollection.insertOne(ticketDoc);
            return true;
        }
        catch (Exception e) {
            System.err.println("Error creating ticket: " + e.getMessage());
            return false;
        }
    }

    private String generateTicketId() {
        return "T" + System.currentTimeMillis();
    }

    public List<Document> getAllTickets() {
        ArrayList<Document> tickets = new ArrayList<Document>();
        try {
            FindIterable<Document> result = this.ticketsCollection.find().sort(Sorts.descending((String[])new String[]{"createdDate"}));
            for (Document ticket : result) {
                tickets.add(ticket);
            }
        }
        catch (Exception e) {
            System.err.println("Error getting all tickets: " + e.getMessage());
        }
        return tickets;
    }

    public List<Document> getTicketsByUser(String username) {
        ArrayList<Document> tickets = new ArrayList<Document>();
        try {
            Bson filter = Filters.or((Bson[])new Bson[]{Filters.eq((String)"createdBy", (Object)username), Filters.eq((String)"createdBy", (Object)(username != null ? username.toLowerCase() : null)), Filters.eq((String)"createdBy", (Object)(username != null ? username.toUpperCase() : null))});
            FindIterable<Document> result = this.ticketsCollection.find(filter).sort(Sorts.descending((String[])new String[]{"createdDate"}));
            for (Document ticket : result) {
                tickets.add(ticket);
            }
        }
        catch (Exception e) {
            System.err.println("Error getting tickets by user: " + e.getMessage());
        }
        return tickets;
    }

    public List<Document> getTicketsByUser(String username, String fullName) {
        ArrayList<Document> tickets = new ArrayList<Document>();
        try {
            ArrayList<Bson> filters = new ArrayList<Bson>();
            if (username != null && !username.trim().isEmpty()) {
                filters.add(Filters.eq((String)"userId", (Object)username));
                filters.add(Filters.eq((String)"createdBy", (Object)username));
                filters.add(Filters.eq((String)"createdBy", (Object)username.toLowerCase()));
                filters.add(Filters.eq((String)"createdBy", (Object)username.toUpperCase()));
            }
            if (fullName != null && !fullName.trim().isEmpty()) {
                filters.add(Filters.eq((String)"createdBy", (Object)fullName));
            }
            Bson filter = filters.isEmpty() ? new Document() : Filters.or(filters);
            FindIterable<Document> result = this.ticketsCollection.find(filter).sort(Sorts.descending((String[])new String[]{"createdDate"}));
            for (Document ticket : result) {
                tickets.add(ticket);
            }
        }
        catch (Exception e) {
            System.err.println("Error getting tickets by user identity: " + e.getMessage());
        }
        return tickets;
    }

    public List<Document> getTicketsByUserId(String userId) {
        ArrayList<Document> tickets = new ArrayList<Document>();
        try {
            if (userId == null || userId.trim().isEmpty()) {
                return tickets;
            }
            Bson filter = Filters.or((Bson[])new Bson[]{Filters.eq((String)"userId", (Object)userId), Filters.eq((String)"createdBy", (Object)userId), Filters.eq((String)"createdBy", (Object)userId.toLowerCase()), Filters.eq((String)"createdBy", (Object)userId.toUpperCase())});
            FindIterable<Document> result = this.ticketsCollection.find(filter).sort(Sorts.descending((String[])new String[]{"createdDate"}));
            for (Document ticket : result) {
                tickets.add(ticket);
            }
        }
        catch (Exception e) {
            System.err.println("Error getting tickets by userId: " + e.getMessage());
        }
        return tickets;
    }

    public List<Document> getTicketsByStaff(String staffUsername) {
        ArrayList<Document> tickets = new ArrayList<Document>();
        try {
            Bson filter = Filters.eq((String)"assignedTo", (Object)staffUsername);
            FindIterable<Document> result = this.ticketsCollection.find(filter).sort(Sorts.descending((String[])new String[]{"createdDate"}));
            for (Document ticket : result) {
                tickets.add(ticket);
            }
        }
        catch (Exception e) {
            System.err.println("Error getting tickets by staff: " + e.getMessage());
        }
        return tickets;
    }

    public List<Document> getTicketsByStatus(String status) {
        ArrayList<Document> tickets = new ArrayList<Document>();
        try {
            Bson filter = Filters.eq((String)"status", (Object)status);
            FindIterable<Document> result = this.ticketsCollection.find(filter).sort(Sorts.descending((String[])new String[]{"createdDate"}));
            for (Document ticket : result) {
                tickets.add(ticket);
            }
        }
        catch (Exception e) {
            System.err.println("Error getting tickets by status: " + e.getMessage());
        }
        return tickets;
    }

    public List<Document> getTicketsByDepartment(String department) {
        ArrayList<Document> tickets = new ArrayList<Document>();
        try {
            Bson filter = Filters.eq((String)"department", (Object)department);
            FindIterable<Document> result = this.ticketsCollection.find(filter).sort(Sorts.descending((String[])new String[]{"createdDate"}));
            for (Document ticket : result) {
                tickets.add(ticket);
            }
        }
        catch (Exception e) {
            System.err.println("Error getting tickets by department: " + e.getMessage());
        }
        return tickets;
    }

    public Document getTicketById(String ticketId) {
        try {
            Bson filter = Filters.eq((String)"ticketId", (Object)ticketId);
            FindIterable<Document> result = this.ticketsCollection.find(filter);
            return (Document)result.first();
        }
        catch (Exception e) {
            System.err.println("Error getting ticket by ID: " + e.getMessage());
            return null;
        }
    }

    public boolean updateTicketStatus(String ticketId, String newStatus) {
        try {
            Bson filter = Filters.eq((String)"ticketId", (Object)ticketId);
            Bson update = Updates.combine((Bson[])new Bson[]{Updates.set((String)"status", (Object)newStatus), Updates.set((String)"lastUpdated", (Object)new Date())});
            this.ticketsCollection.updateOne(filter, update);
            return true;
        }
        catch (Exception e) {
            System.err.println("Error updating ticket status: " + e.getMessage());
            return false;
        }
    }

    public boolean updateTicketDepartment(String ticketId, String newDepartment) {
        try {
            Bson filter = Filters.eq((String)"ticketId", (Object)ticketId);
            Bson update = Updates.combine((Bson[])new Bson[]{Updates.set((String)"department", (Object)newDepartment), Updates.set((String)"lastUpdated", (Object)new Date())});
            this.ticketsCollection.updateOne(filter, update);
            return true;
        }
        catch (Exception e) {
            System.err.println("Error updating ticket department: " + e.getMessage());
            return false;
        }
    }

    public boolean assignTicket(String ticketId, String staffUsername) {
        try {
            Bson filter = Filters.eq((String)"ticketId", (Object)ticketId);
            Bson update = Updates.combine((Bson[])new Bson[]{Updates.set((String)"assignedTo", (Object)staffUsername), Updates.set((String)"status", (Object)"Assigned"), Updates.set((String)"lastUpdated", (Object)new Date())});
            this.ticketsCollection.updateOne(filter, update);
            return true;
        }
        catch (Exception e) {
            System.err.println("Error assigning ticket: " + e.getMessage());
            return false;
        }
    }

    public boolean addAttachmentToTicket(String ticketId, String attachmentPath) {
        try {
            Bson filter = Filters.eq((String)"ticketId", (Object)ticketId);
            Document ticket = this.getTicketById(ticketId);
            if (ticket != null) {
                ArrayList<String> attachments = (ArrayList<String>)ticket.get((Object)"attachments");
                if (attachments == null) {
                    attachments = new ArrayList<String>();
                }
                attachments.add(attachmentPath);
                Bson update = Updates.combine((Bson[])new Bson[]{Updates.set((String)"attachments", attachments), Updates.set((String)"lastUpdated", (Object)new Date())});
                this.ticketsCollection.updateOne(filter, update);
                return true;
            }
        }
        catch (Exception e) {
            System.err.println("Error adding attachment to ticket: " + e.getMessage());
        }
        return false;
    }

    public long getTicketCount() {
        try {
            return this.ticketsCollection.countDocuments();
        }
        catch (Exception e) {
            System.err.println("Error getting ticket count: " + e.getMessage());
            return 0L;
        }
    }

    public long getTicketCountByStatus(String status) {
        try {
            Bson filter = Filters.eq((String)"status", (Object)status);
            return this.ticketsCollection.countDocuments(filter);
        }
        catch (Exception e) {
            System.err.println("Error getting ticket count by status: " + e.getMessage());
            return 0L;
        }
    }

    public long getTicketCountByUser(String username, String fullName) {
        try {
            ArrayList<Bson> filters = new ArrayList<Bson>();
            if (username != null && !username.trim().isEmpty()) {
                filters.add(Filters.eq((String)"userId", (Object)username));
                filters.add(Filters.eq((String)"createdBy", (Object)username));
                filters.add(Filters.eq((String)"createdBy", (Object)username.toLowerCase()));
                filters.add(Filters.eq((String)"createdBy", (Object)username.toUpperCase()));
            }
            if (fullName != null && !fullName.trim().isEmpty()) {
                filters.add(Filters.eq((String)"createdBy", (Object)fullName));
            }
            if (filters.isEmpty()) {
                return 0L;
            }
            return this.ticketsCollection.countDocuments(Filters.or(filters));
        }
        catch (Exception e) {
            System.err.println("Error getting ticket count by user: " + e.getMessage());
            return 0L;
        }
    }

    public long getTicketCountByDepartment(String department) {
        try {
            Bson filter = Filters.eq((String)"department", (Object)department);
            return this.ticketsCollection.countDocuments(filter);
        }
        catch (Exception e) {
            System.err.println("Error getting ticket count by department: " + e.getMessage());
            return 0L;
        }
    }

    public long getTicketCountByPriority(String priority) {
        try {
            Bson filter = Filters.eq((String)"priority", (Object)priority);
            return this.ticketsCollection.countDocuments(filter);
        }
        catch (Exception e) {
            System.err.println("Error getting ticket count by priority: " + e.getMessage());
            return 0L;
        }
    }

    public List<Document> getTicketsByDateRange(String startDate, String endDate) {
        ArrayList<Document> tickets = new ArrayList<Document>();
        try {
            Bson filter = Filters.and((Bson[])new Bson[]{Filters.gte((String)"createdDate", (Object)startDate), Filters.lte((String)"createdDate", (Object)endDate)});
            FindIterable<Document> result = this.ticketsCollection.find(filter).sort(Sorts.descending((String[])new String[]{"createdDate"}));
            for (Document ticket : result) {
                tickets.add(ticket);
            }
        }
        catch (Exception e) {
            System.err.println("Error getting tickets by date range: " + e.getMessage());
        }
        return tickets;
    }

    public List<Document> getTicketAnalytics() {
        ArrayList<Document> analytics = new ArrayList<Document>();
        try {
            AggregateIterable<Document> deptStats = this.ticketsCollection.aggregate(Arrays.asList(Aggregates.group((Object)"$department", (BsonField[])new BsonField[]{Accumulators.sum((String)"count", (Object)1)}), Aggregates.sort((Bson)Sorts.descending((String[])new String[]{"count"}))));
            for (Document doc : deptStats) {
                analytics.add(doc);
            }
            AggregateIterable<Document> statusStats = this.ticketsCollection.aggregate(Arrays.asList(Aggregates.group((Object)"$status", (BsonField[])new BsonField[]{Accumulators.sum((String)"count", (Object)1)})));
            for (Document doc : statusStats) {
                analytics.add(doc);
            }
        }
        catch (Exception e) {
            System.err.println("Error getting ticket analytics: " + e.getMessage());
        }
        return analytics;
    }

    public double getAverageResolutionTime() {
        try {
            AggregateIterable<Document> result = this.ticketsCollection.aggregate(Arrays.asList(Aggregates.match((Bson)Filters.eq((String)"status", (Object)"Resolved")), Aggregates.group(null, (BsonField[])new BsonField[]{Accumulators.avg((String)"avgResolutionTime", (Object)"$resolutionTime")})));
            Document doc = (Document)result.first();
            if (doc != null && doc.containsKey((Object)"avgResolutionTime")) {
                return doc.getDouble((Object)"avgResolutionTime");
            }
        }
        catch (Exception e) {
            System.err.println("Error calculating average resolution time: " + e.getMessage());
        }
        return 0.0;
    }

    public boolean deleteTicket(String ticketId) {
        try {
            Bson filter = Filters.eq((String)"ticketId", (Object)ticketId);
            this.ticketsCollection.deleteOne(filter);
            return true;
        }
        catch (Exception e) {
            System.err.println("Error deleting ticket: " + e.getMessage());
            return false;
        }
    }

    public List<Document> searchTickets(String searchTerm) {
        ArrayList<Document> tickets = new ArrayList<Document>();
        try {
            Bson filter = Filters.or((Bson[])new Bson[]{Filters.regex((String)"ticketId", (String)searchTerm, (String)"i"), Filters.regex((String)"title", (String)searchTerm, (String)"i"), Filters.regex((String)"description", (String)searchTerm, (String)"i"), Filters.regex((String)"createdBy", (String)searchTerm, (String)"i")});
            FindIterable<Document> result = this.ticketsCollection.find(filter).sort(Sorts.descending((String[])new String[]{"createdDate"}));
            for (Document ticket : result) {
                tickets.add(ticket);
            }
        }
        catch (Exception e) {
            System.err.println("Error searching tickets: " + e.getMessage());
        }
        return tickets;
    }

    public long getTicketCountByAssignedStaff(String staffName) {
        try {
            Bson filter = Filters.eq((String)"assignedTo", (Object)staffName);
            return this.ticketsCollection.countDocuments(filter);
        }
        catch (Exception e) {
            System.err.println("Error getting assigned staff ticket count: " + e.getMessage());
            return 0L;
        }
    }

    public long getTicketCountByStatusAndStaff(String status, String staffName) {
        try {
            ArrayList<Bson> filters = new ArrayList<Bson>();
            filters.add(Filters.eq((String)"status", (Object)status));
            filters.add(Filters.eq((String)"assignedTo", (Object)staffName));
            Bson combinedFilter = Filters.and(filters);
            return this.ticketsCollection.countDocuments(combinedFilter);
        }
        catch (Exception e) {
            System.err.println("Error getting status and staff ticket count: " + e.getMessage());
            return 0L;
        }
    }

    public List<Document> getTicketsByAssignedStaff(String staffName) {
        ArrayList<Document> tickets = new ArrayList<Document>();
        try {
            FindIterable<Document> result = this.ticketsCollection.find(Filters.eq((String)"assignedTo", (Object)staffName)).sort(Sorts.descending((String[])new String[]{"createdDate"}));
            for (Document ticket : result) {
                tickets.add(ticket);
            }
        }
        catch (Exception e) {
            System.err.println("Error getting assigned staff tickets: " + e.getMessage());
        }
        return tickets;
    }

    public void addNoteToTicket(String ticketId, String note) {
        try {
            Bson filter = Filters.eq((String)"ticketId", (Object)ticketId);
            Document ticket = (Document)this.ticketsCollection.find(filter).first();
            if (ticket != null) {
                List<?> existing = (List<?>)ticket.get((Object)"notes");
                ArrayList<Object> notes = new ArrayList<Object>();
                if (existing != null) {
                    notes.addAll(existing);
                }
                notes.add((Object)("[" + String.valueOf(new Date()) + "] " + note));
                Document updateDoc = new Document("$set", (Object)new Document().append("notes", notes).append("lastUpdated", (Object)new Date().toString()));
                this.ticketsCollection.updateOne(filter, (Bson)updateDoc);
            }
        }
        catch (Exception e) {
            System.err.println("Error adding note to ticket: " + e.getMessage());
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

