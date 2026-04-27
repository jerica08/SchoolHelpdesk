package schoolhelpdesk.model;

import java.util.Date;
import java.util.List;
import java.text.SimpleDateFormat;

public class Ticket {
    private String ticketId;
    private String title;
    private String description;
    private String createdBy;
    private String createdDate;
    private String department;
    private String status;
    private String priority;
    private String assignedTo;
    private List<String> attachments;
    private List<String> notes;
    private Date lastUpdated;
    
    public Ticket() {
        this.createdDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        this.status = "Pending Review";
        this.priority = "Medium";
        this.lastUpdated = new Date();
    }
    
    public Ticket(String ticketId, String title, String description, String createdBy, String department) {
        this();
        this.ticketId = ticketId;
        this.title = title;
        this.description = description;
        this.createdBy = createdBy;
        this.department = department;
    }
    
    // Getters and Setters
    public String getTicketId() {
        return ticketId;
    }
    
    public void setTicketId(String ticketId) {
        this.ticketId = ticketId;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getCreatedBy() {
        return createdBy;
    }
    
    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }
    
    public String getCreatedDate() {
        return createdDate;
    }
    
    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }
    
    public String getDepartment() {
        return department;
    }
    
    public void setDepartment(String department) {
        this.department = department;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
        this.lastUpdated = new Date();
    }
    
    public String getPriority() {
        return priority;
    }
    
    public void setPriority(String priority) {
        this.priority = priority;
    }
    
    public String getAssignedTo() {
        return assignedTo;
    }
    
    public void setAssignedTo(String assignedTo) {
        this.assignedTo = assignedTo;
        this.lastUpdated = new Date();
    }
    
    public List<String> getAttachments() {
        return attachments;
    }
    
    public void setAttachments(List<String> attachments) {
        this.attachments = attachments;
    }
    
    public List<String> getNotes() {
        return notes;
    }
    
    public void setNotes(List<String> notes) {
        this.notes = notes;
    }
    
    public Date getLastUpdated() {
        return lastUpdated;
    }
    
    public void setLastUpdated(Date lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
    
    @Override
    public String toString() {
        return "Ticket{" +
                "ticketId='" + ticketId + '\'' +
                ", title='" + title + '\'' +
                ", status='" + status + '\'' +
                ", department='" + department + '\'' +
                '}';
    }
}
