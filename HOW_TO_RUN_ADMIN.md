# How to Run the Admin Dashboard

## Prerequisites

1. **Java Development Kit (JDK)** - Version 17 or higher
2. **Apache Maven** - For dependency management
3. **MongoDB** - Local MongoDB instance running on port 27017
4. **IDE** - NetBeans, IntelliJ IDEA, or Eclipse

## Step-by-Step Instructions

### Step 1: Install and Start MongoDB

#### Windows:
1. Download MongoDB Community Server from [mongodb.com](https://www.mongodb.com/try/download/community)
2. Run the installer with default settings
3. Start MongoDB service:
   ```bash
   # Open Command Prompt as Administrator
   net start MongoDB
   ```

#### macOS:
```bash
# Using Homebrew
brew install mongodb-community
brew services start mongodb-community
```

#### Linux (Ubuntu/Debian):
```bash
sudo apt-get install mongodb
sudo systemctl start mongodb
```

### Step 2: Setup the Database

1. Open MongoDB Shell:
   ```bash
   mongo
   ```

2. Run the database setup script:
   ```javascript
   use helpdesk_db;

   // Insert sample users
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
           username: "student1",
           password: "student123",
           role: "Student",
           email: "student1@school.edu",
           fullName: "Alice Student",
           active: true,
           createdDate: new Date(),
           lastLogin: null
       }
   ]);

   // Insert sample departments
   db.departments.insertMany([
       {
           departmentId: "DEPT100000000001",
           name: "IT Support",
           description: "Information Technology Support Services",
           staffMembers: ["John Staff"],
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
           staffMembers: [],
           headOfDepartment: "",
           email: "hr@school.edu",
           location: "Building B, Room 201",
           active: true,
           createdDate: new Date()
       }
   ]);

   // Insert sample tickets
   db.tickets.insertMany([
       {
           ticketId: "T1694828800001",
           title: "Cannot login to student portal",
           description: "I am unable to login to the student portal with my credentials.",
           createdBy: "Alice Student",
           userId: "student1",
           createdDate: "2024-04-16",
           department: "IT Support",
           status: "Pending Review",
           priority: "High",
           assignedTo: null,
           attachments: [],
           notes: ["Initial ticket created"],
           lastUpdated: new Date()
       }
   ]);

   print("Database setup complete!");
   ```

### Step 3: Build the Project

#### Using Command Line:
1. Navigate to project directory:
   ```bash
   cd c:\Users\User\OneDrive\Documents\NetBeansProjects\SchoolHelpdesk-main
   ```

2. Clean and compile:
   ```bash
   mvn clean compile
   ```

3. Package the application:
   ```bash
   mvn package
   ```

#### Using NetBeans:
1. Open NetBeans IDE
2. File > Open Project
3. Navigate to `SchoolHelpdesk-main` directory
4. Right-click project > Clean and Build

#### Using IntelliJ IDEA:
1. Open IntelliJ IDEA
2. File > Open > Navigate to project directory
3. Wait for Maven to import dependencies
4. Build > Build Project

### Step 4: Run the Application

#### Method 1: Using Maven
```bash
mvn exec:java -Dexec.mainClass="schoolhelpdesk.SchoolHelpdesk"
```

#### Method 2: Using Java JAR
```bash
java -cp target/classes;target/dependency/* schoolhelpdesk.SchoolHelpdesk
```

#### Method 3: Using IDE
- **NetBeans**: Right-click `SchoolHelpdesk.java` > Run File
- **IntelliJ**: Right-click `SchoolHelpdesk.java` > Run 'SchoolHelpdesk.main()'

### Step 5: Login to Admin Dashboard

1. The application will start with a login screen
2. Enter the admin credentials:
   - **Username**: `admin`
   - **Password**: `admin123`
3. Click "Login"

You will be redirected to the Complete Admin Dashboard!

## Default Login Credentials

| Role | Username | Password |
|------|----------|----------|
| Admin | admin | admin123 |
| Staff | jstaff | staff123 |
| Student | student1 | student123 |

## Troubleshooting

### Common Issues and Solutions

#### 1. MongoDB Connection Error
**Problem**: `Error connecting to database: Connection refused`
**Solution**:
- Ensure MongoDB is running: `net start MongoDB` (Windows) or `brew services start mongodb-community` (macOS)
- Check if MongoDB is listening on port 27017
- Verify firewall is not blocking port 27017

#### 2. Java Compilation Error
**Problem**: `package org.jfree.chart does not exist`
**Solution**:
- Make sure JFreeChart dependency is in `pom.xml`
- Run `mvn clean install` to download dependencies
- Check your internet connection for dependency download

#### 3. Main Class Not Found
**Problem**: `Could not find or load main class`
**Solution**:
- Verify the main class path in `pom.xml`:
  ```xml
  <exec.mainClass>schoolhelpdesk.SchoolHelpdesk</exec.mainClass>
  ```
- Ensure project is compiled: `mvn clean compile`

#### 4. Login Authentication Failed
**Problem**: Invalid username or password
**Solution**:
- Check database setup is complete
- Verify admin user exists in database:
  ```javascript
  use helpdesk_db;
  db.users.find({username: "admin"}).pretty();
  ```

#### 5. UI Display Issues
**Problem**: Dashboard appears blank or distorted
**Solution**:
- Update Java to latest version
- Ensure system has sufficient memory
- Try running with increased heap size:
  ```bash
  java -Xmx2g -cp target/classes schoolhelpdesk.SchoolHelpdesk
  ```

### Verification Steps

To verify everything is working correctly:

1. **Check MongoDB Connection**:
   ```javascript
   mongo
   use helpdesk_db
   show collections
   ```

2. **Verify Sample Data**:
   ```javascript
   db.users.countDocuments();  // Should return > 0
   db.departments.countDocuments();  // Should return > 0
   db.tickets.countDocuments();  // Should return > 0
   ```

3. **Test Application**:
   - Application starts without errors
   - Login screen appears
   - Admin login works
   - Dashboard loads with data

## Quick Start Commands

For experienced users, here are the essential commands:

```bash
# 1. Start MongoDB
net start MongoDB

# 2. Setup database (one-time)
mongo helpdesk_db --eval "
db.users.insertOne({
    userId: 'U1000000000001',
    username: 'admin',
    password: 'admin123',
    role: 'Admin',
    email: 'admin@school.edu',
    fullName: 'System Administrator',
    active: true,
    createdDate: new Date()
});
"

# 3. Build and run
cd c:\Users\User\OneDrive\Documents\NetBeansProjects\SchoolHelpdesk-main
mvn clean compile
java -cp target/classes schoolhelpdesk.SchoolHelpdesk

# 4. Login with: admin / admin123
```

## Next Steps

Once the admin dashboard is running:

1. **Explore Features**: Navigate through Dashboard, Tickets, Users, Departments, and Reports
2. **Manage Tickets**: Test approve, reject, assign, and redirect functionality
3. **User Management**: Add/edit/delete users
4. **Department Management**: Create and manage departments
5. **View Analytics**: Check reports and charts

## Support

If you encounter issues:

1. Check the **DATABASE_SETUP.md** for detailed database instructions
2. Review **ENHANCED_ADMIN_DASHBOARD.md** for feature documentation
3. Ensure all prerequisites are installed correctly
4. Check MongoDB service status

The admin dashboard is now ready to use!
