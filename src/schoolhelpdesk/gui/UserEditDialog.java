package schoolhelpdesk.gui;

import schoolhelpdesk.dao.UserDAO;
import schoolhelpdesk.dao.DepartmentDAO;
import schoolhelpdesk.model.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import org.bson.Document;

public class UserEditDialog extends JDialog {
    private UserDAO userDAO;
    private DepartmentDAO departmentDAO;
    private boolean isEditMode;
    private String originalUsername;
    
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JTextField fullNameField;
    private JTextField emailField;
    private JTextField phoneField;
    private JComboBox<String> roleComboBox;
    private JComboBox<String> departmentComboBox;
    private JTextArea notesArea;
    private JCheckBox activeCheckBox;
    
    public UserEditDialog(JFrame parent, String title, Document existingUser, UserDAO userDAO, DepartmentDAO departmentDAO) {
        super(parent, title, true);
        this.userDAO = userDAO;
        this.departmentDAO = departmentDAO;
        this.isEditMode = (existingUser != null);
        
        if (isEditMode) {
            this.originalUsername = existingUser.getString("username");
        }
        
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        
        if (isEditMode) {
            populateFields(existingUser);
        }
        
        loadDepartments();
        setupDialog();
    }
    
    private void initializeComponents() {
        usernameField = new JTextField(20);
        passwordField = new JPasswordField(20);
        fullNameField = new JTextField(20);
        emailField = new JTextField(20);
        phoneField = new JTextField(20);
        
        roleComboBox = new JComboBox<>();
        roleComboBox.setPreferredSize(new Dimension(200, 30));
        
        departmentComboBox = new JComboBox<>();
        departmentComboBox.setPreferredSize(new Dimension(200, 30));
        
        notesArea = new JTextArea(3, 20);
        notesArea.setLineWrap(true);
        notesArea.setWrapStyleWord(true);
        
        activeCheckBox = new JCheckBox("Active");
        activeCheckBox.setSelected(true);
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Username
        gbc.gridx = 0; gbc.gridy = 0;
        mainPanel.add(new JLabel("Username:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(usernameField, gbc);
        
        // Password
        gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE;
        mainPanel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(passwordField, gbc);
        
        // Full Name
        gbc.gridx = 0; gbc.gridy = 2; gbc.fill = GridBagConstraints.NONE;
        mainPanel.add(new JLabel("Full Name:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(fullNameField, gbc);
        
        // Email
        gbc.gridx = 0; gbc.gridy = 3; gbc.fill = GridBagConstraints.NONE;
        mainPanel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(emailField, gbc);
        
        // Phone
        gbc.gridx = 0; gbc.gridy = 4; gbc.fill = GridBagConstraints.NONE;
        mainPanel.add(new JLabel("Phone:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(phoneField, gbc);
        
        // Role
        gbc.gridx = 0; gbc.gridy = 5; gbc.fill = GridBagConstraints.NONE;
        mainPanel.add(new JLabel("Role:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(roleComboBox, gbc);
        
        // Department
        gbc.gridx = 0; gbc.gridy = 6; gbc.fill = GridBagConstraints.NONE;
        mainPanel.add(new JLabel("Department:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(departmentComboBox, gbc);
        
        // Notes
        gbc.gridx = 0; gbc.gridy = 7; gbc.fill = GridBagConstraints.NONE;
        mainPanel.add(new JLabel("Notes:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0; gbc.weighty = 1.0;
        mainPanel.add(new JScrollPane(notesArea), gbc);
        
        // Active Status
        gbc.gridx = 0; gbc.gridy = 8; gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0; gbc.weighty = 0;
        mainPanel.add(activeCheckBox, gbc);
        
        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout());
        
        JButton saveButton = new JButton("Save");
        styleButton(saveButton, new Color(34, 139, 34), "Save");
        
        JButton cancelButton = new JButton("Cancel");
        styleButton(cancelButton, new Color(108, 117, 125), "Cancel");
        
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        
        gbc.gridx = 0; gbc.gridy = 9; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        mainPanel.add(buttonPanel, gbc);
        
        add(mainPanel, BorderLayout.CENTER);
        
        // Event handlers
        saveButton.addActionListener(e -> saveUser());
        cancelButton.addActionListener(e -> dispose());
    }
    
    private void setupEventHandlers() {
        // Enable/disable password field based on edit mode
        if (isEditMode) {
            passwordField.setToolTipText("Leave blank to keep current password");
        }
    }
    
    private void loadDepartments() {
        try {
            departmentComboBox.removeAllItems();
            departmentComboBox.addItem("Not assigned");
            
            List<Document> departments = departmentDAO.getActiveDepartments();
            for (Document dept : departments) {
                departmentComboBox.addItem(dept.getString("name"));
            }
        } catch (Exception e) {
            System.err.println("Error loading departments: " + e.getMessage());
        }
        
        // Load roles
        roleComboBox.removeAllItems();
        roleComboBox.addItem("Admin");
        roleComboBox.addItem("Staff");
        roleComboBox.addItem("Student");
    }
    
    private void populateFields(Document user) {
        usernameField.setText(user.getString("username"));
        fullNameField.setText(user.getString("fullName"));
        emailField.setText(user.getString("email"));
        phoneField.setText(user.getString("phone"));
        roleComboBox.setSelectedItem(user.getString("role"));
        
        String dept = user.getString("department");
        if (dept != null && !dept.isEmpty()) {
            departmentComboBox.setSelectedItem(dept);
        } else {
            departmentComboBox.setSelectedItem("Not assigned");
        }
        
        notesArea.setText(user.getString("notes"));
        activeCheckBox.setSelected(user.getBoolean("active"));
        
        // In edit mode, make username non-editable
        usernameField.setEditable(false);
        usernameField.setBackground(Color.LIGHT_GRAY);
    }
    
    private void saveUser() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();
        String fullName = fullNameField.getText().trim();
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();
        String role = (String) roleComboBox.getSelectedItem();
        String department = (String) departmentComboBox.getSelectedItem();
        String notes = notesArea.getText().trim();
        boolean active = activeCheckBox.isSelected();
        
        // Validation
        if (username.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Username is required", "Error", JOptionPane.ERROR_MESSAGE);
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
        
        if (!isEditMode && password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Password is required for new users", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (department.equals("Not assigned")) {
            department = null;
        }
        
        try {
            User user = new User();
            user.setUsername(username);
            user.setFullName(fullName);
            user.setEmail(email);
            user.setPhone(phone);
            user.setRole(role);
            user.setDepartment(department);
            user.setNotes(notes);
            user.setActive(active);
            
            boolean success;
            
            if (isEditMode) {
                // Update existing user
                if (!password.isEmpty()) {
                    user.setPassword(password);
                }
                success = userDAO.updateUser(user);
            } else {
                // Check if username already exists
                if (userDAO.getUserByUsername(username) != null) {
                    JOptionPane.showMessageDialog(this, "Username already exists!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                user.setPassword(password);
                success = userDAO.createUser(user);
            }
            
            if (success) {
                JOptionPane.showMessageDialog(this, 
                    isEditMode ? "User updated successfully!" : "User created successfully!", 
                    "Success", JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, 
                    isEditMode ? "Error updating user!" : "Error creating user!", 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error saving user: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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
        button.setPreferredSize(new Dimension(80, 30));
        
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
        setSize(400, 500);
        setLocationRelativeTo(getParent());
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }
}
