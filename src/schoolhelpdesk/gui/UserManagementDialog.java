package schoolhelpdesk.gui;

import schoolhelpdesk.dao.UserDAO;
import schoolhelpdesk.dao.DepartmentDAO;
import schoolhelpdesk.model.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import org.bson.Document;

public class UserManagementDialog extends JDialog {
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
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error connecting to database: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            dispose();
            return;
        }
        
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        loadUserTable();
        loadFilters();
        setupDialog();
    }
    
    private void initializeComponents() {
        searchField = new JTextField(20);
        searchField.setToolTipText("Search by username, name, or email");
        
        roleFilterComboBox = new JComboBox<>();
        roleFilterComboBox.setPreferredSize(new Dimension(120, 30));
        
        statusFilterComboBox = new JComboBox<>();
        statusFilterComboBox.setPreferredSize(new Dimension(120, 30));
        
        String[] userColumns = {"Username", "Full Name", "Email", "Role", "Department", "Status", "Created Date"};
        userTableModel = new DefaultTableModel(userColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table read-only
            }
        };
        userTable = new JTable(userTableModel);
        
        // Style user table
        userTable.setRowHeight(25);
        userTable.setFont(new Font("Arial", Font.PLAIN, 11));
        userTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        userTable.getTableHeader().setBackground(new Color(0, 51, 102));
        userTable.getTableHeader().setForeground(Color.WHITE);
        userTable.setSelectionBackground(new Color(173, 216, 230));
        userTable.setGridColor(new Color(200, 200, 200));
        userTable.setShowGrid(true);
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Top panel - Search and filters
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBorder(BorderFactory.createTitledBorder("Search and Filter"));
        
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.add(new JLabel("Search:"));
        searchPanel.add(searchField);
        
        JButton searchButton = new JButton("Search");
        styleButton(searchButton, new Color(70, 130, 180), "Search");
        searchPanel.add(searchButton);
        
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterPanel.add(new JLabel("Role:"));
        filterPanel.add(roleFilterComboBox);
        filterPanel.add(Box.createHorizontalStrut(20));
        filterPanel.add(new JLabel("Status:"));
        filterPanel.add(statusFilterComboBox);
        
        JButton clearFiltersButton = new JButton("Clear Filters");
        styleButton(clearFiltersButton, new Color(108, 117, 125), "Clear Filters");
        filterPanel.add(clearFiltersButton);
        
        topPanel.add(searchPanel, BorderLayout.NORTH);
        topPanel.add(filterPanel, BorderLayout.CENTER);
        
        // Middle panel - User table
        JScrollPane tableScrollPane = new JScrollPane(userTable);
        tableScrollPane.setBorder(BorderFactory.createTitledBorder("Users"));
        tableScrollPane.setPreferredSize(new Dimension(800, 400));
        
        // Bottom panel - Action buttons
        JPanel bottomPanel = new JPanel(new FlowLayout());
        
        JButton addUserButton = new JButton("Add User");
        styleButton(addUserButton, new Color(34, 139, 34), "Add User");
        
        JButton editUserButton = new JButton("Edit User");
        styleButton(editUserButton, new Color(255, 140, 0), "Edit User");
        
        JButton deleteUserButton = new JButton("Delete User");
        styleButton(deleteUserButton, new Color(220, 53, 69), "Delete User");
        
        JButton resetPasswordButton = new JButton("Reset Password");
        styleButton(resetPasswordButton, new Color(70, 130, 180), "Reset Password");
        
        JButton toggleStatusButton = new JButton("Toggle Status");
        styleButton(toggleStatusButton, new Color(108, 117, 125), "Toggle Status");
        
        JButton refreshButton = new JButton("Refresh");
        styleButton(refreshButton, new Color(70, 130, 180), "Refresh");
        
        JButton closeButton = new JButton("Close");
        styleButton(closeButton, new Color(108, 117, 125), "Close");
        
        bottomPanel.add(addUserButton);
        bottomPanel.add(editUserButton);
        bottomPanel.add(deleteUserButton);
        bottomPanel.add(resetPasswordButton);
        bottomPanel.add(toggleStatusButton);
        bottomPanel.add(Box.createHorizontalStrut(20));
        bottomPanel.add(refreshButton);
        bottomPanel.add(closeButton);
        
        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(tableScrollPane, BorderLayout.CENTER);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
        
        // Setup event handlers
        searchButton.addActionListener(e -> searchUsers());
        clearFiltersButton.addActionListener(e -> clearFilters());
        addUserButton.addActionListener(e -> addNewUser());
        editUserButton.addActionListener(e -> editSelectedUser());
        deleteUserButton.addActionListener(e -> deleteSelectedUser());
        resetPasswordButton.addActionListener(e -> resetSelectedUserPassword());
        toggleStatusButton.addActionListener(e -> toggleSelectedUserStatus());
        refreshButton.addActionListener(e -> loadUserTable());
        closeButton.addActionListener(e -> dispose());
    }
    
    private void setupEventHandlers() {
        // Additional event handlers if needed
        roleFilterComboBox.addActionListener(e -> filterUsers());
        statusFilterComboBox.addActionListener(e -> filterUsers());
    }
    
    private void loadUserTable() {
        try {
            if (userTableModel == null) {
                System.err.println("userTableModel is null in loadUserTable");
                return;
            }
            
            userTableModel.setRowCount(0);
            List<Document> users = userDAO.getAllUsers();
            
            if (users == null) {
                System.err.println("users list is null in loadUserTable");
                return;
            }
            
            for (Document user : users) {
                Object[] row = {
                    user.getString("username"),
                    user.getString("fullName"),
                    user.getString("email"),
                    user.getString("role"),
                    user.getString("department") != null ? user.getString("department") : "Not assigned",
                    user.getBoolean("active") != null && user.getBoolean("active") ? "Active" : "Inactive",
                    formatDate(user.get("createdDate"))
                };
                userTableModel.addRow(row);
            }
        } catch (Exception e) {
            System.err.println("Error in loadUserTable: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading users: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void loadFilters() {
        try {
            if (roleFilterComboBox == null || statusFilterComboBox == null) {
                System.err.println("Filter combo boxes are null in loadFilters");
                return;
            }
            
            roleFilterComboBox.removeAllItems();
            roleFilterComboBox.addItem("All Roles");
            roleFilterComboBox.addItem("Admin");
            roleFilterComboBox.addItem("Staff");
            roleFilterComboBox.addItem("Student");
            
            statusFilterComboBox.removeAllItems();
            statusFilterComboBox.addItem("All Status");
            statusFilterComboBox.addItem("Active");
            statusFilterComboBox.addItem("Inactive");
        } catch (Exception e) {
            System.err.println("Error in loadFilters: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void searchUsers() {
        String searchTerm = searchField.getText().trim();
        if (searchTerm.isEmpty()) {
            loadUserTable();
            return;
        }
        
        try {
            userTableModel.setRowCount(0);
            List<Document> users = userDAO.searchUsers(searchTerm);
            
            for (Document user : users) {
                Object[] row = {
                    user.getString("username"),
                    user.getString("fullName"),
                    user.getString("email"),
                    user.getString("role"),
                    user.getString("department") != null ? user.getString("department") : "Not assigned",
                    user.getBoolean("active") != null && user.getBoolean("active") ? "Active" : "Inactive",
                    formatDate(user.get("createdDate"))
                };
                userTableModel.addRow(row);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error searching users: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void filterUsers() {
        String roleFilter = (String) roleFilterComboBox.getSelectedItem();
        String statusFilter = (String) statusFilterComboBox.getSelectedItem();
        
        try {
            userTableModel.setRowCount(0);
            List<Document> users = userDAO.getAllUsers();
            
            for (Document user : users) {
                boolean matchesRole = roleFilter.equals("All Roles") || 
                    user.getString("role").equals(roleFilter);
                boolean matchesStatus = statusFilter.equals("All Status") || 
                    (statusFilter.equals("Active") && user.getBoolean("active")) ||
                    (statusFilter.equals("Inactive") && !user.getBoolean("active"));
                
                if (matchesRole && matchesStatus) {
                    Object[] row = {
                        user.getString("username"),
                        user.getString("fullName"),
                        user.getString("email"),
                        user.getString("role"),
                        user.getString("department") != null ? user.getString("department") : "Not assigned",
                        user.getBoolean("active") != null && user.getBoolean("active") ? "Active" : "Inactive",
                        formatDate(user.get("createdDate"))
                    };
                    userTableModel.addRow(row);
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error filtering users: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void clearFilters() {
        searchField.setText("");
        roleFilterComboBox.setSelectedIndex(0);
        statusFilterComboBox.setSelectedIndex(0);
        loadUserTable();
    }
    
    private void addNewUser() {
        UserEditDialog dialog = new UserEditDialog(parentFrame, "Add New User", null, userDAO, departmentDAO);
        dialog.setVisible(true);
        loadUserTable();
    }
    
    private void editSelectedUser() {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a user to edit", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String username = (String) userTableModel.getValueAt(selectedRow, 0);
        Document user = userDAO.getUserByUsername(username);
        
        UserEditDialog dialog = new UserEditDialog(parentFrame, "Edit User", user, userDAO, departmentDAO);
        dialog.setVisible(true);
        loadUserTable();
    }
    
    private void deleteSelectedUser() {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a user to delete", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String username = (String) userTableModel.getValueAt(selectedRow, 0);
        String fullName = (String) userTableModel.getValueAt(selectedRow, 1);
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Delete user " + fullName + " (" + username + ")?\nThis action cannot be undone!", 
            "Confirm Delete", 
            JOptionPane.YES_NO_OPTION);
            
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                if (userDAO.deleteUserByUsername(username)) {
                    JOptionPane.showMessageDialog(this, "User deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    loadUserTable();
                } else {
                    JOptionPane.showMessageDialog(this, "Error deleting user!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error deleting user: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void resetSelectedUserPassword() {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a user to reset password", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String username = (String) userTableModel.getValueAt(selectedRow, 0);
        String fullName = (String) userTableModel.getValueAt(selectedRow, 1);
        
        String newPassword = JOptionPane.showInputDialog(this, 
            "Enter new password for " + fullName + " (" + username + "):", 
            "Reset Password", 
            JOptionPane.QUESTION_MESSAGE);
            
        if (newPassword != null && !newPassword.trim().isEmpty()) {
            try {
                if (userDAO.updateUserPassword(username, newPassword)) {
                    JOptionPane.showMessageDialog(this, "Password reset successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Error resetting password!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error resetting password: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void toggleSelectedUserStatus() {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a user to toggle status", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String username = (String) userTableModel.getValueAt(selectedRow, 0);
        String fullName = (String) userTableModel.getValueAt(selectedRow, 1);
        String currentStatus = (String) userTableModel.getValueAt(selectedRow, 5);
        
        boolean newStatus = currentStatus.equals("Inactive");
        String statusText = newStatus ? "activate" : "deactivate";
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            statusText + " user " + fullName + " (" + username + ")?", 
            "Confirm Status Change", 
            JOptionPane.YES_NO_OPTION);
            
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                if (userDAO.updateUserStatus(username, newStatus)) {
                    JOptionPane.showMessageDialog(this, "User status updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    loadUserTable();
                } else {
                    JOptionPane.showMessageDialog(this, "Error updating user status!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error updating user status: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void styleButton(JButton button, Color color, String text) {
        // Modern button styling to match admin dashboard
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(color.darker(), 1),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(180, 40));
        
        // Enhanced hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(color.brighter());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(color);
            }
        });
    }
    
    private String formatDate(Object dateObj) {
        if (dateObj == null) {
            return "Unknown";
        }
        if (dateObj instanceof String) {
            String dateStr = (String) dateObj;
            return dateStr.length() >= 10 ? dateStr.substring(0, 10) : dateStr;
        }
        if (dateObj instanceof java.util.Date) {
            return new java.text.SimpleDateFormat("yyyy-MM-dd").format((java.util.Date) dateObj);
        }
        return dateObj.toString();
    }
    
    private void setupDialog() {
        setSize(900, 600);
        setLocationRelativeTo(getParent());
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }
}
