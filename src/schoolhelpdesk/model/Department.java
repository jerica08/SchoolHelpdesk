package schoolhelpdesk.model;

import java.util.List;

public class Department {
    private String departmentId;
    private String name;
    private String description;
    private List<String> staffMembers;
    private String headOfDepartment;
    private String email;
    private String location;
    private boolean active;
    
    public Department() {
        this.active = true;
    }
    
    public Department(String departmentId, String name, String description) {
        this();
        this.departmentId = departmentId;
        this.name = name;
        this.description = description;
    }
    
    // Getters and Setters
    public String getDepartmentId() {
        return departmentId;
    }
    
    public void setDepartmentId(String departmentId) {
        this.departmentId = departmentId;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public List<String> getStaffMembers() {
        return staffMembers;
    }
    
    public void setStaffMembers(List<String> staffMembers) {
        this.staffMembers = staffMembers;
    }
    
    public String getHeadOfDepartment() {
        return headOfDepartment;
    }
    
    public void setHeadOfDepartment(String headOfDepartment) {
        this.headOfDepartment = headOfDepartment;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getLocation() {
        return location;
    }
    
    public void setLocation(String location) {
        this.location = location;
    }
    
    public boolean isActive() {
        return active;
    }
    
    public void setActive(boolean active) {
        this.active = active;
    }
    
    @Override
    public String toString() {
        return "Department{" +
                "departmentId='" + departmentId + '\'' +
                ", name='" + name + '\'' +
                ", headOfDepartment='" + headOfDepartment + '\'' +
                '}';
    }
}
