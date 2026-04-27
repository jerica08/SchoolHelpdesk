# How to Run Admin Dashboard in NetBeans

## Step-by-Step NetBeans Instructions

### 1. Open Project in NetBeans

1. **Launch NetBeans IDE**
2. **Open Project**:
   - Click `File` > `Open Project`
   - Navigate to: `c:\Users\User\OneDrive\Documents\NetBeansProjects\SchoolHelpdesk-main`
   - Select the project folder and click `Open Project`

### 2. Verify Project Configuration

1. **Check Project Properties**:
   - Right-click the project `SchoolHelpdeskSystem`
   - Select `Properties`
   - Verify:
     - **Sources**: Should show `src/schoolhelpdesk` as source package
     - **Libraries**: Should contain MongoDB dependencies
     - **Run**: Main class should be `schoolhelpdesk.SchoolHelpdesk`

2. **Verify Maven Dependencies**:
   - Expand `Dependencies` node in project tree
   - Should show:
     - `mongodb-driver-sync`
     - `mongodb-driver-core`
     - `bson`
     - `jfreechart`

### 3. Setup Database Before Running

1. **Start MongoDB**:
   ```bash
   # Open Command Prompt as Administrator
   net start MongoDB
   ```

2. **Setup Sample Data**:
   - Open MongoDB Shell: `mongo`
   - Run the setup commands from `DATABASE_SETUP.md`

### 4. Compile and Build

1. **Clean and Build**:
   - Right-click project `SchoolHelpdeskSystem`
   - Select `Clean and Build`
   - Wait for build to complete (check Output window)

2. **Check for Errors**:
   - Look at Output window for any compilation errors
   - If errors appear, fix them before proceeding

### 5. Run the Application

#### Method 1: Run Main Class
1. Navigate to `src/schoolhelpdesk/SchoolHelpdesk.java`
2. Right-click on `SchoolHelpdesk.java`
3. Select `Run File`

#### Method 2: Run Project
1. Right-click project `SchoolHelpdeskSystem`
2. Select `Run`

#### Method 3: Run Configuration
1. Click the green "Run" button in toolbar
2. Or press `F6` key

### 6. Login to Admin Dashboard

When the application starts:

1. **Login Screen** will appear
2. **Enter Credentials**:
   - Username: `admin`
   - Password: `admin123`
3. **Click "Login" button**

### 7. Verify Dashboard Loading

After successful login, you should see:

- **Header**: Admin name, department, notifications
- **Sidebar**: Navigation menu (Dashboard, Tickets, Users, Departments, Reports)
- **Main Content**: Dashboard with statistics cards and recent tickets

## NetBeans-Specific Troubleshooting

### Common NetBeans Issues

#### 1. Project Won't Open
**Problem**: "Project folder is not a NetBeans project"
**Solution**:
- Ensure you're selecting the folder containing `pom.xml` and `nbproject` folder
- Look for the NetBeans project icon next to the folder

#### 2. Maven Dependencies Not Loading
**Problem**: Missing dependencies in Libraries node
**Solution**:
- Right-click project > `Properties` > `Categories` > `Libraries`
- Click `Add Dependency` and manually add:
  - GroupId: `org.mongodb`
  - ArtifactId: `mongodb-driver-sync`
  - Version: `4.5.0`
  - GroupId: `org.jfree`
  - ArtifactId: `jfreechart`
  - Version: `1.5.3`

#### 3. Main Class Not Found
**Problem**: "Could not find or load main class"
**Solution**:
- Right-click project > `Properties` > `Categories` > `Run`
- Set `Main Class` to: `schoolhelpdesk.SchoolHelpdesk`
- Click `Browse` to locate the main class if needed

#### 4. Compilation Errors
**Problem**: Red squiggly lines under code
**Solution**:
- Check Output window for specific error messages
- Common issues:
  - Missing imports (add import statements)
  - Syntax errors (fix code)
  - Missing dependencies (verify Maven setup)

#### 5. MongoDB Connection Error
**Problem**: Runtime error connecting to database
**Solution**:
- Ensure MongoDB service is running
- Check MongoDB is on port 27017
- Verify database exists: `mongo helpdesk_db`

### NetBeans Project Structure

Your project should look like this in NetBeans:

```
SchoolHelpdeskSystem
|-- Source Packages
|   |-- schoolhelpdesk
|       |-- dao
|       |   |-- DepartmentDAO.java
|       |   |-- TicketDAO.java
|       |   |-- UserDAO.java
|       |-- gui
|       |   |-- CompleteAdminDashboard.java
|       |   |-- EnhancedAdminDashboard.java
|       |   |-- LoginFrame.java
|       |   |-- StaffDashboard.java
|       |   |-- StudentDashboard.java
|       |-- model
|       |   |-- Department.java
|       |   |-- Ticket.java
|       |   |-- User.java
|       |-- SchoolHelpdesk.java
|-- Dependencies
|   |-- mongodb-driver-sync-4.5.0.jar
|   |-- mongodb-driver-core-4.5.0.jar
|   |-- bson-4.5.0.jar
|   |-- jfreechart-1.5.3.jar
|-- Project Files
|   |-- pom.xml
|   |-- nbproject
|   |-- build.xml
```

### NetBeans Run Configuration

1. **Set Working Directory**:
   - Right-click project > `Properties` > `Categories` > `Run`
   - Set `Working Directory` to project root

2. **VM Options** (if needed):
   - Add VM options for more memory: `-Xmx2g`
   - Add system properties if required

3. **Arguments**:
   - No arguments needed for this application

### Debugging in NetBeans

1. **Set Breakpoints**:
   - Double-click in the margin next to code line
   - Red dot indicates breakpoint

2. **Debug Mode**:
   - Right-click project > `Debug`
   - Or press `Ctrl+F5`

3. **Step Through Code**:
   - Use Debug toolbar buttons to step, continue, etc.

### Performance Tips in NetBeans

1. **Increase Memory**:
   - Edit `netbeans.conf` file in NetBeans installation
   - Increase `-Xmx` value to 2048m or higher

2. **Disable Unused Plugins**:
   - Tools > Plugins > Installed
   - Disable plugins you don't use

3. **Project Optimization**:
   - Right-click project > `Properties` > `Categories` > `Build`
   - Disable "Compile on Save" if not needed

## Quick NetBeans Start

For experienced NetBeans users:

1. **Open**: File > Open Project > Select `SchoolHelpdesk-main`
2. **Build**: Right-click > Clean and Build
3. **Run**: Right-click `SchoolHelpdesk.java` > Run File
4. **Login**: admin / admin123

## Success Indicators

You'll know it's working when:

- [ ] Project opens without errors in NetBeans
- [ ] Build completes successfully (green checkmark)
- [ ] Application window appears
- [ ] Login screen loads
- [ ] Admin dashboard displays with data
- [ ] No error messages in console

## Next Steps After Running

Once running successfully:

1. **Explore Dashboard**: Click through different sections
2. **Test Features**: Try ticket management, user management
3. **Check Analytics**: View reports and charts
4. **Modify Data**: Add users, departments, tickets

The admin dashboard is now ready to use in NetBeans!
