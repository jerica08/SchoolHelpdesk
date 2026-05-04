/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bson.Document
 */
package schoolhelpdesk.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import org.bson.Document;
import schoolhelpdesk.dao.DepartmentDAO;
import schoolhelpdesk.dao.UserDAO;
import schoolhelpdesk.model.User;

public class UserEditDialog
extends JDialog {
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
        boolean bl = this.isEditMode = existingUser != null;
        if (this.isEditMode) {
            this.originalUsername = existingUser.getString((Object)"username");
        }
        this.initializeComponents();
        this.setupLayout();
        this.setupEventHandlers();
        if (this.isEditMode) {
            this.populateFields(existingUser);
        }
        this.loadDepartments();
        this.setupDialog();
    }

    private void initializeComponents() {
        this.usernameField = new JTextField(20);
        this.passwordField = new JPasswordField(20);
        this.fullNameField = new JTextField(20);
        this.emailField = new JTextField(20);
        this.phoneField = new JTextField(20);
        this.roleComboBox = new JComboBox();
        this.roleComboBox.setPreferredSize(new Dimension(200, 30));
        this.departmentComboBox = new JComboBox();
        this.departmentComboBox.setPreferredSize(new Dimension(200, 30));
        this.notesArea = new JTextArea(3, 20);
        this.notesArea.setLineWrap(true);
        this.notesArea.setWrapStyleWord(true);
        this.activeCheckBox = new JCheckBox("Active");
        this.activeCheckBox.setSelected(true);
    }

    private void setupLayout() {
        this.setLayout(new BorderLayout());
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = 17;
        gbc.gridx = 0;
        gbc.gridy = 0;
        mainPanel.add((Component)new JLabel("Username:"), gbc);
        gbc.gridx = 1;
        gbc.fill = 2;
        mainPanel.add((Component)this.usernameField, gbc);
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = 0;
        mainPanel.add((Component)new JLabel("Password:"), gbc);
        gbc.gridx = 1;
        gbc.fill = 2;
        mainPanel.add((Component)this.passwordField, gbc);
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.fill = 0;
        mainPanel.add((Component)new JLabel("Full Name:"), gbc);
        gbc.gridx = 1;
        gbc.fill = 2;
        mainPanel.add((Component)this.fullNameField, gbc);
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.fill = 0;
        mainPanel.add((Component)new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        gbc.fill = 2;
        mainPanel.add((Component)this.emailField, gbc);
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.fill = 0;
        mainPanel.add((Component)new JLabel("Phone:"), gbc);
        gbc.gridx = 1;
        gbc.fill = 2;
        mainPanel.add((Component)this.phoneField, gbc);
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.fill = 0;
        mainPanel.add((Component)new JLabel("Role:"), gbc);
        gbc.gridx = 1;
        gbc.fill = 2;
        mainPanel.add(this.roleComboBox, gbc);
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.fill = 0;
        mainPanel.add((Component)new JLabel("Department:"), gbc);
        gbc.gridx = 1;
        gbc.fill = 2;
        mainPanel.add(this.departmentComboBox, gbc);
        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.fill = 0;
        mainPanel.add((Component)new JLabel("Notes:"), gbc);
        gbc.gridx = 1;
        gbc.fill = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        mainPanel.add((Component)new JScrollPane(this.notesArea), gbc);
        gbc.gridx = 0;
        gbc.gridy = 8;
        gbc.fill = 0;
        gbc.weightx = 0.0;
        gbc.weighty = 0.0;
        mainPanel.add((Component)this.activeCheckBox, gbc);
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton saveButton = new JButton("Save");
        this.styleButton(saveButton, new Color(34, 139, 34), "Save");
        JButton cancelButton = new JButton("Cancel");
        this.styleButton(cancelButton, new Color(108, 117, 125), "Cancel");
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        gbc.gridx = 0;
        gbc.gridy = 9;
        gbc.gridwidth = 2;
        gbc.fill = 0;
        gbc.anchor = 10;
        mainPanel.add((Component)buttonPanel, gbc);
        this.add((Component)mainPanel, "Center");
        saveButton.addActionListener(e -> this.saveUser());
        cancelButton.addActionListener(e -> this.dispose());
    }

    private void setupEventHandlers() {
        if (this.isEditMode) {
            this.passwordField.setToolTipText("Leave blank to keep current password");
        }
    }

    private void loadDepartments() {
        try {
            this.departmentComboBox.removeAllItems();
            this.departmentComboBox.addItem("Not assigned");
            List<Document> departments = this.departmentDAO.getActiveDepartments();
            for (Document dept : departments) {
                this.departmentComboBox.addItem(dept.getString((Object)"name"));
            }
        }
        catch (Exception e) {
            System.err.println("Error loading departments: " + e.getMessage());
        }
        this.roleComboBox.removeAllItems();
        this.roleComboBox.addItem("Admin");
        this.roleComboBox.addItem("Staff");
        this.roleComboBox.addItem("Student");
    }

    private void populateFields(Document user) {
        this.usernameField.setText(user.getString((Object)"username"));
        this.fullNameField.setText(user.getString((Object)"fullName"));
        this.emailField.setText(user.getString((Object)"email"));
        this.phoneField.setText(user.getString((Object)"phone"));
        this.roleComboBox.setSelectedItem(user.getString((Object)"role"));
        String dept = user.getString((Object)"department");
        if (dept != null && !dept.isEmpty()) {
            this.departmentComboBox.setSelectedItem(dept);
        } else {
            this.departmentComboBox.setSelectedItem("Not assigned");
        }
        this.notesArea.setText(user.getString((Object)"notes"));
        this.activeCheckBox.setSelected(user.getBoolean((Object)"active"));
        this.usernameField.setEditable(false);
        this.usernameField.setBackground(Color.LIGHT_GRAY);
    }

    private void saveUser() {
        String username = this.usernameField.getText().trim();
        String password = new String(this.passwordField.getPassword()).trim();
        String fullName = this.fullNameField.getText().trim();
        String email = this.emailField.getText().trim();
        String phone = this.phoneField.getText().trim();
        String role = (String)this.roleComboBox.getSelectedItem();
        String department = (String)this.departmentComboBox.getSelectedItem();
        String notes = this.notesArea.getText().trim();
        boolean active = this.activeCheckBox.isSelected();
        if (username.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Username is required", "Error", 0);
            return;
        }
        if (fullName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Full name is required", "Error", 0);
            return;
        }
        if (email.isEmpty() || !email.contains("@")) {
            JOptionPane.showMessageDialog(this, "Valid email is required", "Error", 0);
            return;
        }
        if (!this.isEditMode && password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Password is required for new users", "Error", 0);
            return;
        }
        if (department.equals("Not assigned")) {
            department = null;
        }
        try {
            boolean success;
            User user = new User();
            user.setUsername(username);
            user.setFullName(fullName);
            user.setEmail(email);
            user.setPhone(phone);
            user.setRole(role);
            user.setDepartment(department);
            user.setNotes(notes);
            user.setActive(active);
            if (this.isEditMode) {
                if (!password.isEmpty()) {
                    user.setPassword(password);
                }
                success = this.userDAO.updateUser(user);
            } else {
                if (this.userDAO.getUserByUsername(username) != null) {
                    JOptionPane.showMessageDialog(this, "Username already exists!", "Error", 0);
                    return;
                }
                user.setPassword(password);
                success = this.userDAO.createUser(user);
            }
            if (success) {
                JOptionPane.showMessageDialog(this, this.isEditMode ? "User updated successfully!" : "User created successfully!", "Success", 1);
                this.dispose();
            } else {
                JOptionPane.showMessageDialog(this, this.isEditMode ? "Error updating user!" : "Error creating user!", "Error", 0);
            }
        }
        catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error saving user: " + e.getMessage(), "Error", 0);
        }
    }

    private void styleButton(final JButton button, final Color color, String text) {
        button.setText(text);
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Arial", 1, 11));
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(12));
        button.setPreferredSize(new Dimension(80, 30));
        button.addMouseListener(new MouseAdapter(){

            @Override
            public void mouseEntered(MouseEvent evt) {
                button.setBackground(color.brighter());
            }

            @Override
            public void mouseExited(MouseEvent evt) {
                button.setBackground(color);
            }
        });
    }

    private void setupDialog() {
        this.setSize(400, 500);
        this.setLocationRelativeTo(this.getParent());
        this.setDefaultCloseOperation(2);
    }
}

