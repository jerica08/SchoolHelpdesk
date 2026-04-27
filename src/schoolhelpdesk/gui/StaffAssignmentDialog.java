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
import org.bson.Document;

public class StaffAssignmentDialog extends JDialog {
    private UserDAO userDAO;
    private DepartmentDAO departmentDAO;
    
    private JComboBox<String> departmentComboBox;
    private JList<String> availableStaffList;
    private JList<String> assignedStaffList;
    private DefaultListModel<String> availableStaffModel;
    private DefaultListModel<String> assignedStaffModel;
    
    public StaffAssignmentDialog(JFrame parent) {
        super(parent, "Assign Staff to Departments", true);
        
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
        setupDialog();
    }
    
    private void initializeComponents() {
        departmentComboBox = new JComboBox<>();
        departmentComboBox.setPreferredSize(new Dimension(200, 30));
        
        availableStaffModel = new DefaultListModel<>();
        assignedStaffModel = new DefaultListModel<>();
        
        availableStaffList = new JList<>(availableStaffModel);
        assignedStaffList = new JList<>(assignedStaffModel);
        
        availableStaffList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        assignedStaffList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        
        JScrollPane availableScrollPane = new JScrollPane(availableStaffList);
        JScrollPane assignedScrollPane = new JScrollPane(assignedStaffList);
        
        availableScrollPane.setPreferredSize(new Dimension(200, 300));
        assignedScrollPane.setPreferredSize(new Dimension(200, 300));
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(new JLabel("Select Department:"));
        topPanel.add(departmentComboBox);
        
        JPanel staffPanel = new JPanel(new GridLayout(1, 3, 10, 10));
        
        JPanel availablePanel = new JPanel(new BorderLayout());
        availablePanel.setBorder(BorderFactory.createTitledBorder("Available Staff"));
        availablePanel.add(new JScrollPane(availableStaffList), BorderLayout.CENTER);
        
        JPanel buttonPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        JButton assignButton = new JButton("Assign >>");
        JButton removeButton = new JButton("<< Remove");
        
        buttonPanel.add(assignButton);
        buttonPanel.add(removeButton);
        
        JPanel assignedPanel = new JPanel(new BorderLayout());
        assignedPanel.setBorder(BorderFactory.createTitledBorder("Assigned Staff"));
        assignedPanel.add(new JScrollPane(assignedStaffList), BorderLayout.CENTER);
        
        staffPanel.add(availablePanel);
        staffPanel.add(buttonPanel);
        staffPanel.add(assignedPanel);
        
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = new JButton("Save Changes");
        JButton cancelButton = new JButton("Cancel");
        
        bottomPanel.add(saveButton);
        bottomPanel.add(cancelButton);
        
        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(staffPanel, BorderLayout.CENTER);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
        
        assignButton.addActionListener(e -> assignSelectedStaff());
        removeButton.addActionListener(e -> removeSelectedStaff());
        saveButton.addActionListener(e -> saveChanges());
        cancelButton.addActionListener(e -> dispose());
    }
    
    private void setupEventHandlers() {
        departmentComboBox.addActionListener(e -> loadStaffForDepartment());
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
    
    private void loadStaffForDepartment() {
        String selectedDept = (String) departmentComboBox.getSelectedItem();
        if (selectedDept == null) return;
        
        try {
            availableStaffModel.clear();
            assignedStaffModel.clear();
            
            Document department = departmentDAO.getDepartmentByName(selectedDept);
            if (department != null) {
                List<String> assignedStaff = (List<String>) department.get("staffMembers");
                
                List<Document> allStaff = userDAO.getUsersByRole("Staff");
                for (Document staff : allStaff) {
                    String fullName = staff.getString("fullName");
                    if (fullName != null && !fullName.trim().isEmpty()) {
                        if (assignedStaff != null && assignedStaff.contains(fullName)) {
                            assignedStaffModel.addElement(fullName);
                        } else {
                            availableStaffModel.addElement(fullName);
                        }
                    }
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading staff: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void assignSelectedStaff() {
        List<String> selected = availableStaffList.getSelectedValuesList();
        for (String staff : selected) {
            availableStaffModel.removeElement(staff);
            assignedStaffModel.addElement(staff);
        }
    }
    
    private void removeSelectedStaff() {
        List<String> selected = assignedStaffList.getSelectedValuesList();
        for (String staff : selected) {
            assignedStaffModel.removeElement(staff);
            availableStaffModel.addElement(staff);
        }
    }
    
    private void saveChanges() {
        String selectedDept = (String) departmentComboBox.getSelectedItem();
        if (selectedDept == null) {
            JOptionPane.showMessageDialog(this, "Please select a department", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try {
            Document department = departmentDAO.getDepartmentByName(selectedDept);
            if (department != null) {
                String departmentId = department.getString("departmentId");
                
                List<String> currentStaff = (List<String>) department.get("staffMembers");
                if (currentStaff == null) currentStaff = new java.util.ArrayList<>();
                
                for (int i = 0; i < assignedStaffModel.getSize(); i++) {
                    String staff = assignedStaffModel.getElementAt(i);
                    if (!currentStaff.contains(staff)) {
                        departmentDAO.addStaffToDepartment(departmentId, staff);
                    }
                }
                
                for (int i = 0; i < availableStaffModel.getSize(); i++) {
                    String staff = availableStaffModel.getElementAt(i);
                    if (currentStaff.contains(staff)) {
                        departmentDAO.removeStaffFromDepartment(departmentId, staff);
                    }
                }
                
                JOptionPane.showMessageDialog(this, "Staff assignments updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                dispose();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error saving changes: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void setupDialog() {
        setSize(600, 500);
        setLocationRelativeTo(getParent());
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }
}
