# Database Setup for School Helpdesk System

## MongoDB Collections

### 1. Users Collection
```javascript
// Create users collection with sample data
db.users.insertMany([
    {
        userId: "U1000000000001",
        username: "admin",
        password: "admin123",
        role: "Admin",
        email: "admin@school.edu",
        fullName: "System Administrator",
        active: true,
        createdDate: new Date(),
        lastLogin: null
    },
    {
        userId: "U1000000000002",
        username: "jstaff",
        password: "staff123",
        role: "Staff",
        email: "john.staff@school.edu",
        fullName: "John Staff",
        active: true,
        createdDate: new Date(),
        lastLogin: null
    },
    {
        userId: "U1000000000003",
        username: "sstaff",
        password: "staff123",
        role: "Staff",
        email: "sarah.staff@school.edu",
        fullName: "Sarah Staff",
        active: true,
        createdDate: new Date(),
        lastLogin: null
    },
    {
        userId: "U1000000000004",
        username: "mstaff",
        password: "staff123",
        role: "Staff",
        email: "mike.staff@school.edu",
        fullName: "Mike Staff",
        active: true,
        createdDate: new Date(),
        lastLogin: null
    },
    {
        userId: "U1000000000005",
        username: "student1",
        password: "student123",
        role: "Student",
        email: "student1@school.edu",
        fullName: "Alice Student",
        active: true,
        createdDate: new Date(),
        lastLogin: null
    },
    {
        userId: "U1000000000006",
        username: "student2",
        password: "student123",
        role: "Student",
        email: "student2@school.edu",
        fullName: "Bob Student",
        active: true,
        createdDate: new Date(),
        lastLogin: null
    }
]);

// Create indexes for users collection
db.users.createIndex({ "username": 1 }, { unique: true });
db.users.createIndex({ "email": 1 }, { unique: true });
db.users.createIndex({ "role": 1 });
db.users.createIndex({ "active": 1 });
```

### 2. Departments Collection
```javascript
// Create departments collection with sample data
db.departments.insertMany([
    {
        departmentId: "DEPT100000000001",
        name: "IT Support",
        description: "Information Technology Support Services",
        staffMembers: ["John Staff", "Sarah Staff"],
        headOfDepartment: "John Staff",
        email: "itsupport@school.edu",
        location: "Building A, Room 101",
        active: true,
        createdDate: new Date()
    },
    {
        departmentId: "DEPT100000000002",
        name: "HR",
        description: "Human Resources Department",
        staffMembers: ["Mike Staff"],
        headOfDepartment: "Mike Staff",
        email: "hr@school.edu",
        location: "Building B, Room 201",
        active: true,
        createdDate: new Date()
    },
    {
        departmentId: "DEPT100000000003",
        name: "Finance",
        description: "Finance and Accounting Department",
        staffMembers: [],
        headOfDepartment: "",
        email: "finance@school.edu",
        location: "Building C, Room 301",
        active: true,
        createdDate: new Date()
    },
    {
        departmentId: "DEPT100000000004",
        name: "Academic Affairs",
        description: "Academic Affairs Department",
        staffMembers: [],
        headOfDepartment: "",
        email: "academic@school.edu",
        location: "Building D, Room 401",
        active: true,
        createdDate: new Date()
    },
    {
        departmentId: "DEPT100000000005",
        name: "Student Services",
        description: "Student Services Department",
        staffMembers: [],
        headOfDepartment: "",
        email: "studentservices@school.edu",
        location: "Building E, Room 501",
        active: true,
        createdDate: new Date()
    }
]);

// Create indexes for departments collection
db.departments.createIndex({ "name": 1 }, { unique: true });
db.departments.createIndex({ "active": 1 });
```

### 3. Tickets Collection
```javascript
// Create tickets collection with sample data
db.tickets.insertMany([
    {
        ticketId: "T1694828800001",
        title: "Cannot login to student portal",
        description: "I am unable to login to the student portal with my credentials. I have tried resetting my password but still cannot access.",
        createdBy: "Alice Student",
        userId: "student1",
        createdDate: "2024-04-16",
        department: "IT Support",
        status: "Pending Review",
        priority: "High",
        assignedTo: null,
        attachments: ["screenshot1.png", "error_log.txt"],
        notes: ["Initial ticket created"],
        lastUpdated: new Date()
    },
    {
        ticketId: "T1694828800002",
        title: "Printer not working in library",
        description: "The main printer in the library is not working. It shows error code 502 and won't print any documents.",
        createdBy: "Bob Student",
        userId: "student2",
        createdDate: "2024-04-16",
        department: "IT Support",
        status: "Assigned",
        priority: "Medium",
        assignedTo: "John Staff",
        attachments: ["printer_error.jpg"],
        notes: ["Initial ticket created", "Assigned to John Staff"],
        lastUpdated: new Date()
    },
    {
        ticketId: "T1694828800003",
        title: "Question about tuition payment",
        description: "I need information about the tuition payment deadline for this semester. The website shows conflicting dates.",
        createdBy: "Alice Student",
        userId: "student1",
        createdDate: "2024-04-15",
        department: "Finance",
        status: "Resolved",
        priority: "Low",
        assignedTo: "Mike Staff",
        attachments: [],
        notes: ["Initial ticket created", "Assigned to Mike Staff", "Responded with payment deadline information", "Marked as resolved"],
        lastUpdated: new Date()
    },
    {
        ticketId: "T1694828800004",
        title: "Network connectivity issues in dorm",
        description: "The Wi-Fi in Dorm Building C has been very slow for the past 3 days. Multiple students are affected.",
        createdBy: "Bob Student",
        userId: "student2",
        createdDate: "2024-04-14",
        department: "IT Support",
        status: "In Progress",
        priority: "Urgent",
        assignedTo: "Sarah Staff",
        attachments: ["speed_test_results.png"],
        notes: ["Initial ticket created", "Assigned to Sarah Staff", "Network team notified", "Investigation ongoing"],
        lastUpdated: new Date()
    },
    {
        ticketId: "T1694828800005",
        title: "Request for transcript",
        description: "I need an official transcript for my job application. How can I request one?",
        createdBy: "Alice Student",
        userId: "student1",
        createdDate: "2024-04-13",
        department: "Student Services",
        status: "Clarification Requested",
        priority: "Medium",
        assignedTo: null,
        attachments: [],
        notes: ["Initial ticket created", "Clarification requested: Need to know which type of transcript is needed"],
        lastUpdated: new Date()
    }
]);

// Create indexes for tickets collection
db.tickets.createIndex({ "ticketId": 1 }, { unique: true });
db.tickets.createIndex({ "createdBy": 1 });
db.tickets.createIndex({ "userId": 1 });
db.tickets.createIndex({ "department": 1 });
db.tickets.createIndex({ "status": 1 });
db.tickets.createIndex({ "priority": 1 });
db.tickets.createIndex({ "assignedTo": 1 });
db.tickets.createIndex({ "createdDate": -1 });
```

## Database Initialization Script

### Complete Setup Script
```javascript
// Switch to helpdesk database
use helpdesk_db;

// Clear existing collections (for fresh setup)
db.users.drop();
db.departments.drop();
db.tickets.drop();

// Insert sample data as shown above
// (Copy and paste the insertMany statements from above)

// Verify data insertion
print("=== Users Collection ===");
db.users.find().forEach(printdoc);

print("\n=== Departments Collection ===");
db.departments.find().forEach(printdoc);

print("\n=== Tickets Collection ===");
db.tickets.find().forEach(printdoc);

print("\n=== Database Setup Complete ===");
```

## Running the Setup

### Using MongoDB Shell
1. Open MongoDB Shell:
   ```bash
   mongo
   ```

2. Run the setup script:
   ```javascript
   // Copy and paste the complete setup script above
   ```

### Using MongoDB Compass
1. Connect to MongoDB Compass
2. Select `helpdesk_db` database
3. Run the queries in the `_Mongo Shell` tab

## Collection Schemas

### Users Collection Schema
```json
{
    "_id": ObjectId,
    "userId": "String (Unique)",
    "username": "String (Unique)",
    "password": "String",
    "role": "String (Student/Staff/Admin)",
    "email": "String (Unique)",
    "fullName": "String",
    "active": "Boolean",
    "createdDate": "Date",
    "lastLogin": "Date",
    "lastUpdated": "Date"
}
```

### Departments Collection Schema
```json
{
    "_id": ObjectId,
    "departmentId": "String (Unique)",
    "name": "String (Unique)",
    "description": "String",
    "staffMembers": "Array<String>",
    "headOfDepartment": "String",
    "email": "String",
    "location": "String",
    "active": "Boolean",
    "createdDate": "Date",
    "lastUpdated": "Date"
}
```

### Tickets Collection Schema
```json
{
    "_id": ObjectId,
    "ticketId": "String (Unique)",
    "title": "String",
    "description": "String",
    "createdBy": "String",
    "userId": "String",
    "createdDate": "String",
    "department": "String",
    "status": "String",
    "priority": "String",
    "assignedTo": "String",
    "attachments": "Array<String>",
    "notes": "Array<String>",
    "lastUpdated": "Date"
}
```

## Default Credentials

### Admin Account
- **Username**: admin
- **Password**: admin123
- **Role**: Admin

### Sample Staff Accounts
- **Username**: jstaff
- **Password**: staff123
- **Role**: Staff

- **Username**: sstaff
- **Password**: staff123
- **Role**: Staff

### Sample Student Accounts
- **Username**: student1
- **Password**: student123
- **Role**: Student

- **Username**: student2
- **Password**: student123
- **Role**: Student

## Verification Commands

### Check Collection Counts
```javascript
// Verify data was inserted
db.users.countDocuments();
db.departments.countDocuments();
db.tickets.countDocuments();
```

### Check Sample Data
```javascript
// View sample tickets
db.tickets.find().limit(3).pretty();

// View sample users
db.users.find({role: "Staff"}).pretty();

// View sample departments
db.departments.find().pretty();
```

## Production Considerations

1. **Security**: Change default passwords before production deployment
2. **Indexing**: All necessary indexes are created for optimal performance
3. **Backup**: Implement regular backup strategy for production data
4. **Monitoring**: Set up MongoDB monitoring for production environment
5. **Scaling**: Consider sharding for large-scale deployments

## Troubleshooting

### Common Issues
1. **Connection Issues**: Ensure MongoDB is running on localhost:27017
2. **Authentication**: Check MongoDB authentication settings if enabled
3. **Permissions**: Verify user has read/write permissions on helpdesk_db
4. **Port Conflicts**: Ensure port 27017 is not blocked by firewall

### Error Messages
- **"Authentication failed"**: Check username/password in MongoDB connection
- **"Database not found"**: Run the setup script to create helpdesk_db
- **"Collection not found"**: Verify setup script completed successfully
