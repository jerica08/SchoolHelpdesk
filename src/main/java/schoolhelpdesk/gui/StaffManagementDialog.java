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
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import org.bson.Document;
import schoolhelpdesk.dao.DepartmentDAO;
import schoolhelpdesk.dao.UserDAO;
import schoolhelpdesk.model.User;

public class StaffManagementDialog
extends JDialog {
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
        }
        catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error connecting to database: " + e.getMessage(), "Database Error", 0);
            this.dispose();
            return;
        }
        this.initializeComponents();
        this.setupLayout();
        this.setupEventHandlers();
        this.loadDepartments();
        this.loadStaffTable();
        this.setupDialog();
    }

    private void initializeComponents() {
        this.departmentComboBox = new JComboBox();
        this.departmentComboBox.setPreferredSize(new Dimension(200, 30));
        this.usernameField = new JTextField(20);
        this.passwordField = new JTextField(20);
        this.fullNameField = new JTextField(20);
        this.emailField = new JTextField(20);
        this.phoneField = new JTextField(20);
        this.notesArea = new JTextArea(3, 20);
        this.notesArea.setLineWrap(true);
        this.notesArea.setWrapStyleWord(true);
        Object[] staffColumns = new String[]{"Username", "Full Name", "Email", "Department", "Status"};
        this.staffTableModel = new DefaultTableModel(staffColumns, 0);
        this.staffTable = new JTable(this.staffTableModel);
        this.staffTable.setRowHeight(25);
        this.staffTable.setFont(new Font("Arial", 0, 11));
        this.staffTable.getTableHeader().setFont(new Font("Arial", 1, 12));
        this.staffTable.getTableHeader().setBackground(new Color(0, 51, 102));
        this.staffTable.getTableHeader().setForeground(Color.WHITE);
        this.staffTable.setSelectionBackground(new Color(173, 216, 230));
        this.staffTable.setGridColor(new Color(200, 200, 200));
    }

    private void setupLayout() {
        this.setLayout(new BorderLayout());
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Add New Staff Member"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = 17;
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add((Component)new JLabel("Department:"), gbc);
        gbc.gridx = 1;
        gbc.fill = 2;
        formPanel.add(this.departmentComboBox, gbc);
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = 0;
        formPanel.add((Component)new JLabel("Username:"), gbc);
        gbc.gridx = 1;
        gbc.fill = 2;
        formPanel.add((Component)this.usernameField, gbc);
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.fill = 0;
        formPanel.add((Component)new JLabel("Password:"), gbc);
        gbc.gridx = 1;
        gbc.fill = 2;
        formPanel.add((Component)this.passwordField, gbc);
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.fill = 0;
        formPanel.add((Component)new JLabel("Full Name:"), gbc);
        gbc.gridx = 1;
        gbc.fill = 2;
        formPanel.add((Component)this.fullNameField, gbc);
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.fill = 0;
        formPanel.add((Component)new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        gbc.fill = 2;
        formPanel.add((Component)this.emailField, gbc);
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.fill = 0;
        formPanel.add((Component)new JLabel("Phone:"), gbc);
        gbc.gridx = 1;
        gbc.fill = 2;
        formPanel.add((Component)this.phoneField, gbc);
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.fill = 0;
        formPanel.add((Component)new JLabel("Notes:"), gbc);
        gbc.gridx = 1;
        gbc.fill = 1;
        formPanel.add((Component)new JScrollPane(this.notesArea), gbc);
        JPanel formButtonPanel = new JPanel(new FlowLayout());
        JButton addButton = new JButton("Add Staff");
        JButton clearFormButton = new JButton("Clear Form");
        this.styleButton(addButton, new Color(34, 139, 34), "Add Staff");
        this.styleButton(clearFormButton, new Color(70, 130, 180), "Clear Form");
        formButtonPanel.add(addButton);
        formButtonPanel.add(clearFormButton);
        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.gridwidth = 2;
        gbc.fill = 0;
        formPanel.add((Component)formButtonPanel, gbc);
        JScrollPane tableScrollPane = new JScrollPane(this.staffTable);
        tableScrollPane.setBorder(BorderFactory.createTitledBorder("Current Staff Members"));
        tableScrollPane.setPreferredSize(new Dimension(600, 300));
        JPanel bottomPanel = new JPanel(new FlowLayout(2));
        JButton deleteButton = new JButton("Delete Selected");
        JButton refreshButton = new JButton("Refresh");
        JButton closeButton = new JButton("Close");
        this.styleButton(deleteButton, new Color(220, 53, 69), "Delete Selected");
        this.styleButton(refreshButton, new Color(70, 130, 180), "Refresh");
        this.styleButton(closeButton, new Color(108, 117, 125), "Close");
        bottomPanel.add(deleteButton);
        bottomPanel.add(refreshButton);
        bottomPanel.add(closeButton);
        mainPanel.add((Component)formPanel, "North");
        mainPanel.add((Component)tableScrollPane, "Center");
        mainPanel.add((Component)bottomPanel, "South");
        this.add(mainPanel);
        addButton.addActionListener(e -> this.addNewStaff());
        clearFormButton.addActionListener(e -> this.clearForm());
        deleteButton.addActionListener(e -> this.deleteSelectedStaff());
        refreshButton.addActionListener(e -> this.loadStaffTable());
        closeButton.addActionListener(e -> this.dispose());
    }

    private void setupEventHandlers() {
    }

    private void loadDepartments() {
        try {
            this.departmentComboBox.removeAllItems();
            List<Document> departments = this.departmentDAO.getActiveDepartments();
            for (Document dept : departments) {
                this.departmentComboBox.addItem(dept.getString((Object)"name"));
            }
        }
        catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading departments: " + e.getMessage(), "Error", 0);
        }
    }

    private void loadStaffTable() {
        try {
            this.staffTableModel.setRowCount(0);
            List<Document> staffUsers = this.userDAO.getUsersByRole("Staff");
            for (Document staff : staffUsers) {
                Object[] row = new Object[]{staff.getString((Object)"username"), staff.getString((Object)"fullName"), staff.getString((Object)"email"), staff.getString((Object)"department") != null ? staff.getString((Object)"department") : "Not assigned", staff.getBoolean((Object)"active") != null && staff.getBoolean((Object)"active") != false ? "Active" : "Inactive"};
                this.staffTableModel.addRow(row);
            }
        }
        catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading staff: " + e.getMessage(), "Error", 0);
        }
    }

    private void addNewStaff() {
        String department = (String)this.departmentComboBox.getSelectedItem();
        String username = this.usernameField.getText().trim();
        String password = this.passwordField.getText().trim();
        String fullName = this.fullNameField.getText().trim();
        String email = this.emailField.getText().trim();
        String phone = this.phoneField.getText().trim();
        String notes = this.notesArea.getText().trim();
        if (department == null) {
            JOptionPane.showMessageDialog(this, "Please select a department", "Error", 0);
            return;
        }
        if (username.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Username is required", "Error", 0);
            return;
        }
        if (password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Password is required", "Error", 0);
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
        try {
            if (this.userDAO.getUserByUsername(username) != null) {
                JOptionPane.showMessageDialog(this, "Username already exists!", "Error", 0);
                return;
            }
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
            if (this.userDAO.createUser(newStaff)) {
                Document deptDoc = this.departmentDAO.getDepartmentByName(department);
                if (deptDoc != null) {
                    String departmentId = deptDoc.getString((Object)"departmentId");
                    this.departmentDAO.addStaffToDepartment(departmentId, fullName);
                }
                JOptionPane.showMessageDialog(this, "Staff member added successfully!", "Success", 1);
                this.clearForm();
                this.loadStaffTable();
            } else {
                JOptionPane.showMessageDialog(this, "Error adding staff member!", "Error", 0);
            }
        }
        catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error adding staff: " + e.getMessage(), "Error", 0);
        }
    }

    private void clearForm() {
        this.usernameField.setText("");
        this.passwordField.setText("");
        this.fullNameField.setText("");
        this.emailField.setText("");
        this.phoneField.setText("");
        this.notesArea.setText("");
        this.departmentComboBox.setSelectedIndex(0);
    }

    private void deleteSelectedStaff() {
        int selectedRow = this.staffTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a staff member to delete", "No Selection", 2);
            return;
        }
        String username = (String)this.staffTableModel.getValueAt(selectedRow, 0);
        String fullName = (String)this.staffTableModel.getValueAt(selectedRow, 1);
        int confirm = JOptionPane.showConfirmDialog(this, "Delete staff member " + fullName + " (" + username + ")?\nThis action cannot be undone!", "Confirm Delete", 0);
        if (confirm == 0) {
            try {
                if (this.userDAO.deleteUserByUsername(username)) {
                    JOptionPane.showMessageDialog(this, "Staff member deleted successfully!", "Success", 1);
                    this.loadStaffTable();
                } else {
                    JOptionPane.showMessageDialog(this, "Error deleting staff member!", "Error", 0);
                }
            }
            catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error deleting staff: " + e.getMessage(), "Error", 0);
            }
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
        button.setPreferredSize(new Dimension(120, 30));
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
        this.setSize(800, 700);
        this.setLocationRelativeTo(this.getParent());
        this.setDefaultCloseOperation(2);
    }
}

