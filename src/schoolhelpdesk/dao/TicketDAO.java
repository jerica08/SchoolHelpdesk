package schoolhelpdesk.dao;

import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.FindIterable;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.model.Accumulators;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import com.mongodb.client.model.Sorts;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.set;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class TicketDAO {
    private MongoClient mongoClient;
    private MongoDatabase database;
    private MongoCollection<Document> ticketsCollection;
    
    public TicketDAO() {
        try {
            mongoClient = MongoClients.create("mongodb://localhost:27017");
            database = mongoClient.getDatabase("helpdesk_db");
            ticketsCollection = database.getCollection("tickets");
        } catch (Exception e) {
            System.err.println("Error connecting to database: " + e.getMessage());
        }
    }
    
    public boolean createTicket(schoolhelpdesk.model.Ticket ticket) {
        try {
            Document ticketDoc = new Document();
            ticketDoc.append("ticketId", generateTicketId());
            ticketDoc.append("title", ticket.getTitle());
            ticketDoc.append("description", ticket.getDescription());
            ticketDoc.append("createdBy", ticket.getCreatedBy());
            ticketDoc.append("userId", ticket.getCreatedBy());
            ticketDoc.append("createdDate", ticket.getCreatedDate());
            ticketDoc.append("department", ticket.getDepartment());
            ticketDoc.append("status", ticket.getStatus());
            ticketDoc.append("priority", ticket.getPriority());
            ticketDoc.append("assignedTo", ticket.getAssignedTo());
            ticketDoc.append("attachments", ticket.getAttachments() != null ? ticket.getAttachments() : new ArrayList<>());
            ticketDoc.append("notes", ticket.getNotes() != null ? ticket.getNotes() : new ArrayList<>());
            ticketDoc.append("lastUpdated", new Date());
            
            ticketsCollection.insertOne(ticketDoc);
            return true;
        } catch (Exception e) {
            System.err.println("Error creating ticket: " + e.getMessage());
            return false;
        }
    }
    
    private String generateTicketId() {
        return "T" + System.currentTimeMillis();
    }
    
    public List<Document> getAllTickets() {
        List<Document> tickets = new ArrayList<>();
        try {
            FindIterable<Document> result = ticketsCollection.find()
                .sort(Sorts.descending("createdDate"));
            for (Document ticket : result) {
                tickets.add(ticket);
            }
        } catch (Exception e) {
            System.err.println("Error getting all tickets: " + e.getMessage());
        }
        return tickets;
    }
    
    public List<Document> getTicketsByUser(String username) {
        List<Document> tickets = new ArrayList<>();
        try {
            Bson filter = Filters.or(
                eq("createdBy", username),
                eq("createdBy", username != null ? username.toLowerCase() : null),
                eq("createdBy", username != null ? username.toUpperCase() : null)
            );
            FindIterable<Document> result = ticketsCollection.find(filter)
                .sort(Sorts.descending("createdDate"));
            for (Document ticket : result) {
                tickets.add(ticket);
            }
        } catch (Exception e) {
            System.err.println("Error getting tickets by user: " + e.getMessage());
        }
        return tickets;
    }

    public List<Document> getTicketsByUser(String username, String fullName) {
        List<Document> tickets = new ArrayList<>();
        try {
            List<Bson> filters = new ArrayList<>();
            if (username != null && !username.trim().isEmpty()) {
                filters.add(eq("userId", username));
                filters.add(eq("createdBy", username));
                filters.add(eq("createdBy", username.toLowerCase()));
                filters.add(eq("createdBy", username.toUpperCase()));
            }
            if (fullName != null && !fullName.trim().isEmpty()) {
                filters.add(eq("createdBy", fullName));
            }

            Bson filter = filters.isEmpty() ? new Document() : Filters.or(filters);
            FindIterable<Document> result = ticketsCollection.find(filter)
                .sort(Sorts.descending("createdDate"));
            for (Document ticket : result) {
                tickets.add(ticket);
            }
        } catch (Exception e) {
            System.err.println("Error getting tickets by user identity: " + e.getMessage());
        }
        return tickets;
    }

    public List<Document> getTicketsByUserId(String userId) {
        List<Document> tickets = new ArrayList<>();
        try {
            if (userId == null || userId.trim().isEmpty()) {
                return tickets;
            }
            Bson filter = Filters.or(
                eq("userId", userId),
                eq("createdBy", userId),
                eq("createdBy", userId.toLowerCase()),
                eq("createdBy", userId.toUpperCase())
            );
            FindIterable<Document> result = ticketsCollection.find(filter)
                .sort(Sorts.descending("createdDate"));
            for (Document ticket : result) {
                tickets.add(ticket);
            }
        } catch (Exception e) {
            System.err.println("Error getting tickets by userId: " + e.getMessage());
        }
        return tickets;
    }
    
    public List<Document> getTicketsByStaff(String staffUsername) {
        List<Document> tickets = new ArrayList<>();
        try {
            Bson filter = eq("assignedTo", staffUsername);
            FindIterable<Document> result = ticketsCollection.find(filter)
                .sort(Sorts.descending("createdDate"));
            for (Document ticket : result) {
                tickets.add(ticket);
            }
        } catch (Exception e) {
            System.err.println("Error getting tickets by staff: " + e.getMessage());
        }
        return tickets;
    }
    
    public List<Document> getTicketsByStatus(String status) {
        List<Document> tickets = new ArrayList<>();
        try {
            Bson filter = eq("status", status);
            FindIterable<Document> result = ticketsCollection.find(filter)
                .sort(Sorts.descending("createdDate"));
            for (Document ticket : result) {
                tickets.add(ticket);
            }
        } catch (Exception e) {
            System.err.println("Error getting tickets by status: " + e.getMessage());
        }
        return tickets;
    }
    
    public List<Document> getTicketsByDepartment(String department) {
        List<Document> tickets = new ArrayList<>();
        try {
            Bson filter = eq("department", department);
            FindIterable<Document> result = ticketsCollection.find(filter)
                .sort(Sorts.descending("createdDate"));
            for (Document ticket : result) {
                tickets.add(ticket);
            }
        } catch (Exception e) {
            System.err.println("Error getting tickets by department: " + e.getMessage());
        }
        return tickets;
    }
    
    public Document getTicketById(String ticketId) {
        try {
            Bson filter = eq("ticketId", ticketId);
            FindIterable<Document> result = ticketsCollection.find(filter);
            return result.first();
        } catch (Exception e) {
            System.err.println("Error getting ticket by ID: " + e.getMessage());
            return null;
        }
    }
    
    public boolean updateTicketStatus(String ticketId, String newStatus) {
        try {
            Bson filter = eq("ticketId", ticketId);
            Bson update = Updates.combine(
                set("status", newStatus),
                set("lastUpdated", new Date())
            );
            ticketsCollection.updateOne(filter, update);
            return true;
        } catch (Exception e) {
            System.err.println("Error updating ticket status: " + e.getMessage());
            return false;
        }
    }
    
    public boolean updateTicketDepartment(String ticketId, String newDepartment) {
        try {
            Bson filter = eq("ticketId", ticketId);
            Bson update = Updates.combine(
                set("department", newDepartment),
                set("lastUpdated", new Date())
            );
            ticketsCollection.updateOne(filter, update);
            return true;
        } catch (Exception e) {
            System.err.println("Error updating ticket department: " + e.getMessage());
            return false;
        }
    }
    
    public boolean assignTicket(String ticketId, String staffUsername) {
        try {
            Bson filter = eq("ticketId", ticketId);
            Bson update = Updates.combine(
                set("assignedTo", staffUsername),
                set("status", "Assigned"),
                set("lastUpdated", new Date())
            );
            ticketsCollection.updateOne(filter, update);
            return true;
        } catch (Exception e) {
            System.err.println("Error assigning ticket: " + e.getMessage());
            return false;
        }
    }
    
    
    public boolean addAttachmentToTicket(String ticketId, String attachmentPath) {
        try {
            Bson filter = eq("ticketId", ticketId);
            Document ticket = getTicketById(ticketId);
            if (ticket != null) {
                @SuppressWarnings("unchecked")
                List<String> attachments = (List<String>) ticket.get("attachments");
                if (attachments == null) {
                    attachments = new ArrayList<>();
                }
                attachments.add(attachmentPath);
                Bson update = Updates.combine(
                    set("attachments", attachments),
                    set("lastUpdated", new Date())
                );
                ticketsCollection.updateOne(filter, update);
                return true;
            }
        } catch (Exception e) {
            System.err.println("Error adding attachment to ticket: " + e.getMessage());
        }
        return false;
    }
    
    public long getTicketCount() {
        try {
            return ticketsCollection.countDocuments();
        } catch (Exception e) {
            System.err.println("Error getting ticket count: " + e.getMessage());
            return 0;
        }
    }
    
    public long getTicketCountByStatus(String status) {
        try {
            Bson filter = eq("status", status);
            return ticketsCollection.countDocuments(filter);
        } catch (Exception e) {
            System.err.println("Error getting ticket count by status: " + e.getMessage());
            return 0;
        }
    }

    public long getTicketCountByUser(String username, String fullName) {
        try {
            List<Bson> filters = new ArrayList<>();
            if (username != null && !username.trim().isEmpty()) {
                filters.add(eq("userId", username));
                filters.add(eq("createdBy", username));
                filters.add(eq("createdBy", username.toLowerCase()));
                filters.add(eq("createdBy", username.toUpperCase()));
            }
            if (fullName != null && !fullName.trim().isEmpty()) {
                filters.add(eq("createdBy", fullName));
            }

            if (filters.isEmpty()) {
                return 0;
            }
            return ticketsCollection.countDocuments(Filters.or(filters));
        } catch (Exception e) {
            System.err.println("Error getting ticket count by user: " + e.getMessage());
            return 0;
        }
    }
    
    public long getTicketCountByDepartment(String department) {
        try {
            Bson filter = eq("department", department);
            return ticketsCollection.countDocuments(filter);
        } catch (Exception e) {
            System.err.println("Error getting ticket count by department: " + e.getMessage());
            return 0;
        }
    }
    

    public long getTicketCountByPriority(String priority) {
        try {
            Bson filter = eq("priority", priority);
            return ticketsCollection.countDocuments(filter);
        } catch (Exception e) {
            System.err.println("Error getting ticket count by priority: " + e.getMessage());
            return 0;
        }
    }

    public List<Document> getTicketsByDateRange(String startDate, String endDate) {
        List<Document> tickets = new ArrayList<>();
        try {
            Bson filter = Filters.and(
                Filters.gte("createdDate", startDate),
                Filters.lte("createdDate", endDate)
            );
            FindIterable<Document> result = ticketsCollection.find(filter)
                .sort(Sorts.descending("createdDate"));
            for (Document ticket : result) {
                tickets.add(ticket);
            }
        } catch (Exception e) {
            System.err.println("Error getting tickets by date range: " + e.getMessage());
        }
        return tickets;
    }
    
    public List<Document> getTicketAnalytics() {
        List<Document> analytics = new ArrayList<>();
        try {
            // Tickets by department
            AggregateIterable<Document> deptStats = ticketsCollection.aggregate(
                Arrays.asList(
                    Aggregates.group("$department", Accumulators.sum("count", 1)),
                    Aggregates.sort(Sorts.descending("count"))
                )
            );
            
            for (Document doc : deptStats) {
                analytics.add(doc);
            }
            
            // Tickets by status
            AggregateIterable<Document> statusStats = ticketsCollection.aggregate(
                Arrays.asList(
                    Aggregates.group("$status", Accumulators.sum("count", 1))
                )
            );
            
            for (Document doc : statusStats) {
                analytics.add(doc);
            }
            
        } catch (Exception e) {
            System.err.println("Error getting ticket analytics: " + e.getMessage());
        }
        return analytics;
    }
    
    public double getAverageResolutionTime() {
        try {
            AggregateIterable<Document> result = ticketsCollection.aggregate(
                Arrays.asList(
                    Aggregates.match(Filters.eq("status", "Resolved")),
                    Aggregates.group(null, 
                        Accumulators.avg("avgResolutionTime", "$resolutionTime"))
                )
            );
            
            Document doc = result.first();
            if (doc != null && doc.containsKey("avgResolutionTime")) {
                return doc.getDouble("avgResolutionTime");
            }
        } catch (Exception e) {
            System.err.println("Error calculating average resolution time: " + e.getMessage());
        }
        return 0.0;
    }
    
    public boolean deleteTicket(String ticketId) {
        try {
            Bson filter = eq("ticketId", ticketId);
            ticketsCollection.deleteOne(filter);
            return true;
        } catch (Exception e) {
            System.err.println("Error deleting ticket: " + e.getMessage());
            return false;
        }
    }
    
    public List<Document> searchTickets(String searchTerm) {
        List<Document> tickets = new ArrayList<>();
        try {
            Bson filter = Filters.or(
                Filters.regex("ticketId", searchTerm, "i"),
                Filters.regex("title", searchTerm, "i"),
                Filters.regex("description", searchTerm, "i"),
                Filters.regex("createdBy", searchTerm, "i")
            );
            FindIterable<Document> result = ticketsCollection.find(filter)
                .sort(Sorts.descending("createdDate"));
            for (Document ticket : result) {
                tickets.add(ticket);
            }
        } catch (Exception e) {
            System.err.println("Error searching tickets: " + e.getMessage());
        }
        return tickets;
    }
    
    public long getTicketCountByAssignedStaff(String staffName) {
        try {
            Bson filter = eq("assignedTo", staffName);
            return ticketsCollection.countDocuments(filter);
        } catch (Exception e) {
            System.err.println("Error getting assigned staff ticket count: " + e.getMessage());
            return 0;
        }
    }
    
    public long getTicketCountByStatusAndStaff(String status, String staffName) {
        try {
            List<Bson> filters = new ArrayList<>();
            filters.add(eq("status", status));
            filters.add(eq("assignedTo", staffName));
            Bson combinedFilter = Filters.and(filters);
            return ticketsCollection.countDocuments(combinedFilter);
        } catch (Exception e) {
            System.err.println("Error getting status and staff ticket count: " + e.getMessage());
            return 0;
        }
    }
    
    public List<Document> getTicketsByAssignedStaff(String staffName) {
        List<Document> tickets = new ArrayList<>();
        try {
            FindIterable<Document> result = ticketsCollection.find(eq("assignedTo", staffName))
                .sort(Sorts.descending("createdDate"));
            for (Document ticket : result) {
                tickets.add(ticket);
            }
        } catch (Exception e) {
            System.err.println("Error getting assigned staff tickets: " + e.getMessage());
        }
        return tickets;
    }
    
    public void addNoteToTicket(String ticketId, String note) {
        try {
            Bson filter = eq("ticketId", ticketId);
            Document ticket = ticketsCollection.find(filter).first();
            
            if (ticket != null) {
                @SuppressWarnings("unchecked")
                List<String> notes = (List<String>) ticket.get("notes");
                
                if (notes == null) {
                    notes = new ArrayList<>();
                }
                
                notes.add("[" + new Date() + "] " + note);
                
                // Create update document with proper MongoDB syntax
                Document updateDoc = new Document("$set", new Document()
                    .append("notes", notes)
                    .append("lastUpdated", new Date().toString())
                );
                
                ticketsCollection.updateOne(filter, updateDoc);
            }
        } catch (Exception e) {
            System.err.println("Error adding note to ticket: " + e.getMessage());
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
