/*
 * Decompiled with CFR 0.152.
 */
package schoolhelpdesk.model;

public class User {
    private String username;
    private String password;
    private String role;
    private String email;
    private String fullName;
    private String phone;
    private String department;
    private String notes;
    private boolean active = true;

    public User() {
    }

    public User(String username, String password, String role, String email, String fullName) {
        this();
        this.username = username;
        this.password = password;
        this.role = role;
        this.email = email;
        this.fullName = fullName;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return this.role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullName() {
        return this.fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhone() {
        return this.phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getDepartment() {
        return this.department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getNotes() {
        return this.notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public boolean isActive() {
        return this.active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String toString() {
        return "User{username='" + this.username + "', role='" + this.role + "', email='" + this.email + "', fullName='" + this.fullName + "', phone='" + this.phone + "', department='" + this.department + "', active=" + this.active + "}";
    }
}

