/*
 * Decompiled with CFR 0.152.
 */
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
    private boolean active = true;

    public Department() {
    }

    public Department(String departmentId, String name, String description) {
        this();
        this.departmentId = departmentId;
        this.name = name;
        this.description = description;
    }

    public String getDepartmentId() {
        return this.departmentId;
    }

    public void setDepartmentId(String departmentId) {
        this.departmentId = departmentId;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getStaffMembers() {
        return this.staffMembers;
    }

    public void setStaffMembers(List<String> staffMembers) {
        this.staffMembers = staffMembers;
    }

    public String getHeadOfDepartment() {
        return this.headOfDepartment;
    }

    public void setHeadOfDepartment(String headOfDepartment) {
        this.headOfDepartment = headOfDepartment;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLocation() {
        return this.location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public boolean isActive() {
        return this.active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String toString() {
        return "Department{departmentId='" + this.departmentId + "', name='" + this.name + "', headOfDepartment='" + this.headOfDepartment + "'}";
    }
}

