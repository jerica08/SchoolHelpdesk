package schoolhelpdesk.gui;

import schoolhelpdesk.dao.UserDAO;
import schoolhelpdesk.dao.DepartmentDAO;
import schoolhelpdesk.model.User;
import schoolhelpdesk.model.Department;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.table.DefaultTableModel;
import org.bson.Document;

public class StaffManagementDialog extends JDialog {
    private UserDAO userDAO;
    private DepartmentDAO departmentDAO;
    
    private JComboBox<String> departmentComboBox;
    private JTextField usernameField;
    private JTextField passwordField;
    private JTextField fullNameField;
    private JTextField emailField;
    private JTextField phoneField;
    private JTextArea notesArea;
    
    private JTable staffTable;
    private DefaultTableModel staffTableModel;
    
    public StaffManagementDialog(JFrame parent) {
        super(parent, "Add Staff to Departments", true);
        
        try {
            this.userDAO = new UserDAO();
            this.departmentDAO = new DepartmentDAO();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error connecting to database: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            dispose();
            return;
        }
        
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        loadDepartments();
        loadStaffTable();
        setupDialog();
    }
    
    private void initializeComponents() {
        departmentComboBox = new JComboBox<>();
        departmentComboBox.setPreferredSize(new Dimension(200, 30));
        
        usernameField = new JTextField(20);
        passwordField = new JTextField(20);
        fullNameField = new JTextField(20);
        emailField = new JTextField(20);
        phoneField = new JTextField(20);
        
        notesArea = new JTextArea(3, 20);
        notesArea.setLineWrap(true);
        notesArea.setWrapStyleWord(true);
        
        String[] staffColumns = {"Username", "Full Name", "Email", "Department", "Status"};
        staffTableModel = new DefaultTableModel(staffColumns, 0);
        staffTable = new JTable(staffTableModel);
        
        staffTable.setRowHeight(25);
        staffTable.setFont(new Font("Arial", Font.PLAIN, 11));
        staffTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        staffTable.getTableHeader().setBackground(new Color(0, 51, 102));
        staffTable.getTableHeader().setForeground(Color.WHITE);
        staffTable.setSelectionBackground(new Color(173, 216, 230));
        staffTable.setGridColor(new Color(200, 200, 200));
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Top panel - Add new staff form
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Add New Staff Member"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Department selection
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Department:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(departmentComboBox, gbc);
        
        // Username
        gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE;
        formPanel.add(new JLabel("Username:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(usernameField, gbc);
        
        // Password
        gbc.gridx = 0; gbc.gridy = 2; gbc.fill = GridBagConstraints.NONE;
        formPanel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(passwordField, gbc);
        
        // Full Name
        gbc.gridx = 0; gbc.gridy = 3; gbc.fill = GridBagConstraints.NONE;
        formPanel.add(new JLabel("Full Name:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(fullNameField, gbc);
        
        // Email
        gbc.gridx = 0; gbc.gridy = 4; gbc.fill = GridBagConstraints.NONE;
        formPanel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(emailField, gbc);
        
        // Phone
        gbc.gridx = 0; gbc.gridy = 5; gbc.fill = GridBagConstraints.NONE;
        formPanel.add(new JLabel("Phone:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(phoneField, gbc);
        
        // Notes
        gbc.gridx = 0; gbc.gridy = 6; gbc.fill = GridBagConstraints.NONE;
        formPanel.add(new JLabel("Notes:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.BOTH;
        formPanel.add(new JScrollPane(notesArea), gbc);
        
        // Buttons for form
        JPanel formButtonPanel = new JPanel(new FlowLayout());
        JButton addButton = new JButton("Add Staff");
        JButton clearFormButton = new JButton("Clear Form");
        
        styleButton(addButton, new Color(34, 139, 34), "Add Staff");
        styleButton(clearFormButton, new Color(70, 130, 180), "Clear Form");
        
        formButtonPanel.add(addButton);
        formButtonPanel.add(clearFormButton);
        
        gbc.gridx = 0; gbc.gridy = 7; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.NONE;
        formPanel.add(formButtonPanel, gbc);
        
        // Middle panel - Staff table
        JScrollPane tableScrollPane = new JScrollPane(staffTable);
        tableScrollPane.setBorder(BorderFactory.createTitledBorder("Current Staff Members"));
        tableScrollPane.setPreferredSize(new Dimension(600, 300));
        
        // Bottom panel - Action buttons
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton deleteButton = new JButton("Delete Selected");
        JButton refreshButton = new JButton("Refresh");
        JButton closeButton = new JButton("Close");
        
        styleButton(deleteButton, new Color(220, 53, 69), "Delete Selected");
        styleButton(refreshButton, new Color(70, 130, 180), "Refresh");
        styleButton(closeButton, new Color(108, 117, 125), "Close");
        
        bottomPanel.add(deleteButton);
        bottomPanel.add(refreshButton);
        bottomPanel.add(closeButton);
        
        mainPanel.add(formPanel, BorderLayout.NORTH);
        mainPanel.add(tableScrollPane, BorderLayout.CENTER);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
        
        // Setup event handlers
        addButton.addActionListener(e -> addNewStaff());
        clearFormButton.addActionListener(e -> clearForm());
        deleteButton.addActionListener(e -> deleteSelectedStaff());
        refreshButton.addActionListener(e -> loadStaffTable());
        closeButton.addActionListener(e -> dispose());
    }
    
    private void setupEventHandlers() {
        // Additional event handlers if needed
    }
    
    private void loadDepartments() {
        try {
            departmentComboBox.removeAllItems();
            List<Document> departments = departmentDAO.getActiveDepartments();
            for (Document dept : departments) {
                departmentComboBox.addItem(dept.getString("name"));
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading departments: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void loadStaffTable() {
        try {
            staffTableModel.setRowCount(0);
            List<Document> staffUsers = userDAO.getUsersByRole("Staff");
            
            for (Document staff : staffUsers) {
                Object[] row = {
                    staff.getString("username"),
                    staff.getString("fullName"),
                    staff.getString("email"),
                    staff.getString("department") != null ? staff.getString("department") : "Not assigned",
                    staff.getBoolean("active") != null && staff.getBoolean("active") ? "Active" : "Inactive"
                };
                staffTableModel.addRow(row);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading staff: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void addNewStaff() {
        String department = (String) departmentComboBox.getSelectedItem();
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();
        String fullName = fullNameField.getText().trim();
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();
        String notes = notesArea.getText().trim();
        
        // Validation
        if (department == null) {
            JOptionPane.showMessageDialog(this, "Please select a department", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (username.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Username is required", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Password is required", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (fullName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Full name is required", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (email.isEmpty() || !email.contains("@")) {
            JOptionPane.showMessageDialog(this, "Valid email is required", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try {
            // Check if username already exists
            if (userDAO.getUserByUsername(username) != null) {
                JOptionPane.showMessageDialog(this, "Username already exists!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Create new staff user
            User newStaff = new User();
            newStaff.setUsername(username);
            newStaff.setPassword(password);
            newStaff.setFullName(fullName);
            newStaff.setEmail(email);
            newStaff.setPhone(phone);
            newStaff.setRole("Staff");
            newStaff.setDepartment(department);
            newStaff.setNotes(notes);
            newStaff.setActive(true);
            
            if (userDAO.createUser(newStaff)) {
                // Also add staff to department
                Document deptDoc = departmentDAO.getDepartmentByName(department);
                if (deptDoc != null) {
                    String departmentId = deptDoc.getString("departmentId");
                    departmentDAO.addStaffToDepartment(departmentId, fullName);
                }
                
                JOptionPane.showMessageDialog(this, "Staff member added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                clearForm();
                loadStaffTable();
            } else {
                JOptionPane.showMessageDialog(this, "Error adding staff member!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error adding staff: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void clearForm() {
        usernameField.setText("");
        passwordField.setText("");
        fullNameField.setText("");
        emailField.setText("");
        phoneField.setText("");
        notesArea.setText("");
        departmentComboBox.setSelectedIndex(0);
    }
    
    private void deleteSelectedStaff() {
        int selectedRow = staffTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a staff member to delete", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String username = (String) staffTableModel.getValueAt(selectedRow, 0);
        String fullName = (String) staffTableModel.getValueAt(selectedRow, 1);
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Delete staff member " + fullName + " (" + username + ")?\nThis action cannot be undone!", 
            "Confirm Delete", 
            JOptionPane.YES_NO_OPTION);
            
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                if (userDAO.deleteUserByUsername(username)) {
                    JOptionPane.showMessageDialog(this, "Staff member deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    loadStaffTable();
                } else {
                    JOptionPane.showMessageDialog(this, "Error deleting staff member!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error deleting staff: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void styleButton(JButton button, Color color, String text) {
        button.setText(text);
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Arial", Font.BOLD, 11));
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(120, 30));
        
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(color.brighter());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(color);
            }
        });
    }
    
    private void setupDialog() {
        setSize(800, 700);
        setLocationRelativeTo(getParent());
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }
}
