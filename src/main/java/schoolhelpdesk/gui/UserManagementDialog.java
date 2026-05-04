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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import org.bson.Document;
import schoolhelpdesk.dao.DepartmentDAO;
import schoolhelpdesk.dao.UserDAO;
import schoolhelpdesk.gui.UserEditDialog;

public class UserManagementDialog
extends JDialog {
    private UserDAO userDAO;
    private DepartmentDAO departmentDAO;
    private JFrame parentFrame;
    private JTable userTable;
    private DefaultTableModel userTableModel;
    private JTextField searchField;
    private JComboBox<String> roleFilterComboBox;
    private JComboBox<String> statusFilterComboBox;

    public UserManagementDialog(JFrame parent) {
        super(parent, "User Management", true);
        this.parentFrame = parent;
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
        this.loadUserTable();
        this.loadFilters();
        this.setupDialog();
    }

    private void initializeComponents() {
        this.searchField = new JTextField(20);
        this.searchField.setToolTipText("Search by username, name, or email");
        this.roleFilterComboBox = new JComboBox();
        this.roleFilterComboBox.setPreferredSize(new Dimension(120, 30));
        this.statusFilterComboBox = new JComboBox();
        this.statusFilterComboBox.setPreferredSize(new Dimension(120, 30));
        Object[] userColumns = new String[]{"Username", "Full Name", "Email", "Role", "Department", "Status", "Created Date"};
        this.userTableModel = new DefaultTableModel(userColumns, 0){

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        this.userTable = new JTable(this.userTableModel);
        this.userTable.setRowHeight(25);
        this.userTable.setFont(new Font("Arial", 0, 11));
        this.userTable.getTableHeader().setFont(new Font("Arial", 1, 12));
        this.userTable.getTableHeader().setBackground(new Color(0, 51, 102));
        this.userTable.getTableHeader().setForeground(Color.WHITE);
        this.userTable.setSelectionBackground(new Color(173, 216, 230));
        this.userTable.setGridColor(new Color(200, 200, 200));
        this.userTable.setShowGrid(true);
    }

    private void setupLayout() {
        this.setLayout(new BorderLayout());
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBorder(BorderFactory.createTitledBorder("Search and Filter"));
        JPanel searchPanel = new JPanel(new FlowLayout(0));
        searchPanel.add(new JLabel("Search:"));
        searchPanel.add(this.searchField);
        JButton searchButton = new JButton("Search");
        this.styleButton(searchButton, new Color(70, 130, 180), "Search");
        searchPanel.add(searchButton);
        JPanel filterPanel = new JPanel(new FlowLayout(0));
        filterPanel.add(new JLabel("Role:"));
        filterPanel.add(this.roleFilterComboBox);
        filterPanel.add(Box.createHorizontalStrut(20));
        filterPanel.add(new JLabel("Status:"));
        filterPanel.add(this.statusFilterComboBox);
        JButton clearFiltersButton = new JButton("Clear Filters");
        this.styleButton(clearFiltersButton, new Color(108, 117, 125), "Clear Filters");
        filterPanel.add(clearFiltersButton);
        topPanel.add((Component)searchPanel, "North");
        topPanel.add((Component)filterPanel, "Center");
        JScrollPane tableScrollPane = new JScrollPane(this.userTable);
        tableScrollPane.setBorder(BorderFactory.createTitledBorder("Users"));
        tableScrollPane.setPreferredSize(new Dimension(800, 400));
        JPanel bottomPanel = new JPanel(new FlowLayout());
        JButton addUserButton = new JButton("Add User");
        this.styleButton(addUserButton, new Color(34, 139, 34), "Add User");
        JButton editUserButton = new JButton("Edit User");
        this.styleButton(editUserButton, new Color(255, 140, 0), "Edit User");
        JButton deleteUserButton = new JButton("Delete User");
        this.styleButton(deleteUserButton, new Color(220, 53, 69), "Delete User");
        JButton resetPasswordButton = new JButton("Reset Password");
        this.styleButton(resetPasswordButton, new Color(70, 130, 180), "Reset Password");
        JButton toggleStatusButton = new JButton("Toggle Status");
        this.styleButton(toggleStatusButton, new Color(108, 117, 125), "Toggle Status");
        JButton refreshButton = new JButton("Refresh");
        this.styleButton(refreshButton, new Color(70, 130, 180), "Refresh");
        JButton closeButton = new JButton("Close");
        this.styleButton(closeButton, new Color(108, 117, 125), "Close");
        bottomPanel.add(addUserButton);
        bottomPanel.add(editUserButton);
        bottomPanel.add(deleteUserButton);
        bottomPanel.add(resetPasswordButton);
        bottomPanel.add(toggleStatusButton);
        bottomPanel.add(Box.createHorizontalStrut(20));
        bottomPanel.add(refreshButton);
        bottomPanel.add(closeButton);
        mainPanel.add((Component)topPanel, "North");
        mainPanel.add((Component)tableScrollPane, "Center");
        mainPanel.add((Component)bottomPanel, "South");
        this.add(mainPanel);
        searchButton.addActionListener(e -> this.searchUsers());
        clearFiltersButton.addActionListener(e -> this.clearFilters());
        addUserButton.addActionListener(e -> this.addNewUser());
        editUserButton.addActionListener(e -> this.editSelectedUser());
        deleteUserButton.addActionListener(e -> this.deleteSelectedUser());
        resetPasswordButton.addActionListener(e -> this.resetSelectedUserPassword());
        toggleStatusButton.addActionListener(e -> this.toggleSelectedUserStatus());
        refreshButton.addActionListener(e -> this.loadUserTable());
        closeButton.addActionListener(e -> this.dispose());
    }

    private void setupEventHandlers() {
        this.roleFilterComboBox.addActionListener(e -> this.filterUsers());
        this.statusFilterComboBox.addActionListener(e -> this.filterUsers());
    }

    private void loadUserTable() {
        try {
            if (this.userTableModel == null) {
                System.err.println("userTableModel is null in loadUserTable");
                return;
            }
            this.userTableModel.setRowCount(0);
            List<Document> users = this.userDAO.getAllUsers();
            if (users == null) {
                System.err.println("users list is null in loadUserTable");
                return;
            }
            for (Document user : users) {
                Object[] row = new Object[]{user.getString((Object)"username"), user.getString((Object)"fullName"), user.getString((Object)"email"), user.getString((Object)"role"), user.getString((Object)"department") != null ? user.getString((Object)"department") : "Not assigned", user.getBoolean((Object)"active") != null && user.getBoolean((Object)"active") != false ? "Active" : "Inactive", this.formatDate(user.get((Object)"createdDate"))};
                this.userTableModel.addRow(row);
            }
        }
        catch (Exception e) {
            System.err.println("Error in loadUserTable: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading users: " + e.getMessage(), "Error", 0);
        }
    }

    private void loadFilters() {
        try {
            if (this.roleFilterComboBox == null || this.statusFilterComboBox == null) {
                System.err.println("Filter combo boxes are null in loadFilters");
                return;
            }
            this.roleFilterComboBox.removeAllItems();
            this.roleFilterComboBox.addItem("All Roles");
            this.roleFilterComboBox.addItem("Admin");
            this.roleFilterComboBox.addItem("Staff");
            this.roleFilterComboBox.addItem("Student");
            this.statusFilterComboBox.removeAllItems();
            this.statusFilterComboBox.addItem("All Status");
            this.statusFilterComboBox.addItem("Active");
            this.statusFilterComboBox.addItem("Inactive");
        }
        catch (Exception e) {
            System.err.println("Error in loadFilters: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void searchUsers() {
        String searchTerm = this.searchField.getText().trim();
        if (searchTerm.isEmpty()) {
            this.loadUserTable();
            return;
        }
        try {
            this.userTableModel.setRowCount(0);
            List<Document> users = this.userDAO.searchUsers(searchTerm);
            for (Document user : users) {
                Object[] row = new Object[]{user.getString((Object)"username"), user.getString((Object)"fullName"), user.getString((Object)"email"), user.getString((Object)"role"), user.getString((Object)"department") != null ? user.getString((Object)"department") : "Not assigned", user.getBoolean((Object)"active") != null && user.getBoolean((Object)"active") != false ? "Active" : "Inactive", this.formatDate(user.get((Object)"createdDate"))};
                this.userTableModel.addRow(row);
            }
        }
        catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error searching users: " + e.getMessage(), "Error", 0);
        }
    }

    private void filterUsers() {
        String roleFilter = (String)this.roleFilterComboBox.getSelectedItem();
        String statusFilter = (String)this.statusFilterComboBox.getSelectedItem();
        try {
            this.userTableModel.setRowCount(0);
            List<Document> users = this.userDAO.getAllUsers();
            for (Document user : users) {
                boolean matchesStatus;
                boolean matchesRole = roleFilter.equals("All Roles") || user.getString((Object)"role").equals(roleFilter);
                boolean bl = matchesStatus = statusFilter.equals("All Status") || statusFilter.equals("Active") && user.getBoolean((Object)"active") != false || statusFilter.equals("Inactive") && user.getBoolean((Object)"active") == false;
                if (!matchesRole || !matchesStatus) continue;
                Object[] row = new Object[]{user.getString((Object)"username"), user.getString((Object)"fullName"), user.getString((Object)"email"), user.getString((Object)"role"), user.getString((Object)"department") != null ? user.getString((Object)"department") : "Not assigned", user.getBoolean((Object)"active") != null && user.getBoolean((Object)"active") != false ? "Active" : "Inactive", this.formatDate(user.get((Object)"createdDate"))};
                this.userTableModel.addRow(row);
            }
        }
        catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error filtering users: " + e.getMessage(), "Error", 0);
        }
    }

    private void clearFilters() {
        this.searchField.setText("");
        this.roleFilterComboBox.setSelectedIndex(0);
        this.statusFilterComboBox.setSelectedIndex(0);
        this.loadUserTable();
    }

    private void addNewUser() {
        UserEditDialog dialog = new UserEditDialog(this.parentFrame, "Add New User", null, this.userDAO, this.departmentDAO);
        dialog.setVisible(true);
        this.loadUserTable();
    }

    private void editSelectedUser() {
        int selectedRow = this.userTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a user to edit", "No Selection", 2);
            return;
        }
        String username = (String)this.userTableModel.getValueAt(selectedRow, 0);
        Document user = this.userDAO.getUserByUsername(username);
        UserEditDialog dialog = new UserEditDialog(this.parentFrame, "Edit User", user, this.userDAO, this.departmentDAO);
        dialog.setVisible(true);
        this.loadUserTable();
    }

    private void deleteSelectedUser() {
        int selectedRow = this.userTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a user to delete", "No Selection", 2);
            return;
        }
        String username = (String)this.userTableModel.getValueAt(selectedRow, 0);
        String fullName = (String)this.userTableModel.getValueAt(selectedRow, 1);
        int confirm = JOptionPane.showConfirmDialog(this, "Delete user " + fullName + " (" + username + ")?\nThis action cannot be undone!", "Confirm Delete", 0);
        if (confirm == 0) {
            try {
                if (this.userDAO.deleteUserByUsername(username)) {
                    JOptionPane.showMessageDialog(this, "User deleted successfully!", "Success", 1);
                    this.loadUserTable();
                } else {
                    JOptionPane.showMessageDialog(this, "Error deleting user!", "Error", 0);
                }
            }
            catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error deleting user: " + e.getMessage(), "Error", 0);
            }
        }
    }

    private void resetSelectedUserPassword() {
        int selectedRow = this.userTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a user to reset password", "No Selection", 2);
            return;
        }
        String username = (String)this.userTableModel.getValueAt(selectedRow, 0);
        String fullName = (String)this.userTableModel.getValueAt(selectedRow, 1);
        String newPassword = JOptionPane.showInputDialog(this, "Enter new password for " + fullName + " (" + username + "):", "Reset Password", 3);
        if (newPassword != null && !newPassword.trim().isEmpty()) {
            try {
                if (this.userDAO.updateUserPassword(username, newPassword)) {
                    JOptionPane.showMessageDialog(this, "Password reset successfully!", "Success", 1);
                } else {
                    JOptionPane.showMessageDialog(this, "Error resetting password!", "Error", 0);
                }
            }
            catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error resetting password: " + e.getMessage(), "Error", 0);
            }
        }
    }

    private void toggleSelectedUserStatus() {
        int selectedRow = this.userTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a user to toggle status", "No Selection", 2);
            return;
        }
        String username = (String)this.userTableModel.getValueAt(selectedRow, 0);
        String fullName = (String)this.userTableModel.getValueAt(selectedRow, 1);
        String currentStatus = (String)this.userTableModel.getValueAt(selectedRow, 5);
        boolean newStatus = currentStatus.equals("Inactive");
        String statusText = newStatus ? "activate" : "deactivate";
        int confirm = JOptionPane.showConfirmDialog(this, statusText + " user " + fullName + " (" + username + ")?", "Confirm Status Change", 0);
        if (confirm == 0) {
            try {
                if (this.userDAO.updateUserStatus(username, newStatus)) {
                    JOptionPane.showMessageDialog(this, "User status updated successfully!", "Success", 1);
                    this.loadUserTable();
                } else {
                    JOptionPane.showMessageDialog(this, "Error updating user status!", "Error", 0);
                }
            }
            catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error updating user status: " + e.getMessage(), "Error", 0);
            }
        }
    }

    private void styleButton(final JButton button, final Color color, String text) {
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", 1, 12));
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setHorizontalAlignment(2);
        button.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(color.darker(), 1), BorderFactory.createEmptyBorder(10, 15, 10, 15)));
        button.setCursor(new Cursor(12));
        button.setPreferredSize(new Dimension(180, 40));
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

    private String formatDate(Object dateObj) {
        if (dateObj == null) {
            return "Unknown";
        }
        if (dateObj instanceof String) {
            String dateStr = (String)dateObj;
            return dateStr.length() >= 10 ? dateStr.substring(0, 10) : dateStr;
        }
        if (dateObj instanceof Date) {
            return new SimpleDateFormat("yyyy-MM-dd").format((Date)dateObj);
        }
        return dateObj.toString();
    }

    private void setupDialog() {
        this.setSize(900, 600);
        this.setLocationRelativeTo(this.getParent());
        this.setDefaultCloseOperation(2);
    }
}

