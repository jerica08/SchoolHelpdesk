/*
 * Decompiled with CFR 0.152.
 */
package schoolhelpdesk.model;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class Ticket {
    private String ticketId;
    private String title;
    private String description;
    private String createdBy;
    private String createdDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
    private String department;
    private String status = "Pending Review";
    private String priority = "Medium";
    private String assignedTo;
    private List<String> attachments;
    private List<String> notes;
    private Date lastUpdated = new Date();

    public Ticket() {
    }

    public Ticket(String ticketId, String title, String description, String createdBy, String department) {
        this();
        this.ticketId = ticketId;
        this.title = title;
        this.description = description;
        this.createdBy = createdBy;
        this.department = department;
    }

    public String getTicketId() {
        return this.ticketId;
    }

    public void setTicketId(String ticketId) {
        this.ticketId = ticketId;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCreatedBy() {
        return this.createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getCreatedDate() {
        return this.createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public String getDepartment() {
        return this.department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
        this.lastUpdated = new Date();
    }

    public String getPriority() {
        return this.priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getAssignedTo() {
        return this.assignedTo;
    }

    public void setAssignedTo(String assignedTo) {
        this.assignedTo = assignedTo;
        this.lastUpdated = new Date();
    }

    public List<String> getAttachments() {
        return this.attachments;
    }

    public void setAttachments(List<String> attachments) {
        this.attachments = attachments;
    }

    public List<String> getNotes() {
        return this.notes;
    }

    public void setNotes(List<String> notes) {
        this.notes = notes;
    }

    public Date getLastUpdated() {
        return this.lastUpdated;
    }

    public void setLastUpdated(Date lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public String toString() {
        return "Ticket{ticketId='" + this.ticketId + "', title='" + this.title + "', status='" + this.status + "', department='" + this.department + "'}";
    }
}

