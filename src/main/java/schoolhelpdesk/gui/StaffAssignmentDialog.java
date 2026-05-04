/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bson.Document
 */
package schoolhelpdesk.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import org.bson.Document;
import schoolhelpdesk.dao.DepartmentDAO;
import schoolhelpdesk.dao.UserDAO;

public class StaffAssignmentDialog
extends JDialog {
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
        this.setupDialog();
    }

    private void initializeComponents() {
        this.departmentComboBox = new JComboBox();
        this.departmentComboBox.setPreferredSize(new Dimension(200, 30));
        this.availableStaffModel = new DefaultListModel();
        this.assignedStaffModel = new DefaultListModel();
        this.availableStaffList = new JList<String>(this.availableStaffModel);
        this.assignedStaffList = new JList<String>(this.assignedStaffModel);
        this.availableStaffList.setSelectionMode(2);
        this.assignedStaffList.setSelectionMode(2);
        JScrollPane availableScrollPane = new JScrollPane(this.availableStaffList);
        JScrollPane assignedScrollPane = new JScrollPane(this.assignedStaffList);
        availableScrollPane.setPreferredSize(new Dimension(200, 300));
        assignedScrollPane.setPreferredSize(new Dimension(200, 300));
    }

    private void setupLayout() {
        this.setLayout(new BorderLayout());
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        JPanel topPanel = new JPanel(new FlowLayout(0));
        topPanel.add(new JLabel("Select Department:"));
        topPanel.add(this.departmentComboBox);
        JPanel staffPanel = new JPanel(new GridLayout(1, 3, 10, 10));
        JPanel availablePanel = new JPanel(new BorderLayout());
        availablePanel.setBorder(BorderFactory.createTitledBorder("Available Staff"));
        availablePanel.add((Component)new JScrollPane(this.availableStaffList), "Center");
        JPanel buttonPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        JButton assignButton = new JButton("Assign >>");
        JButton removeButton = new JButton("<< Remove");
        buttonPanel.add(assignButton);
        buttonPanel.add(removeButton);
        JPanel assignedPanel = new JPanel(new BorderLayout());
        assignedPanel.setBorder(BorderFactory.createTitledBorder("Assigned Staff"));
        assignedPanel.add((Component)new JScrollPane(this.assignedStaffList), "Center");
        staffPanel.add(availablePanel);
        staffPanel.add(buttonPanel);
        staffPanel.add(assignedPanel);
        JPanel bottomPanel = new JPanel(new FlowLayout(2));
        JButton saveButton = new JButton("Save Changes");
        JButton cancelButton = new JButton("Cancel");
        bottomPanel.add(saveButton);
        bottomPanel.add(cancelButton);
        mainPanel.add((Component)topPanel, "North");
        mainPanel.add((Component)staffPanel, "Center");
        mainPanel.add((Component)bottomPanel, "South");
        this.add(mainPanel);
        assignButton.addActionListener(e -> this.assignSelectedStaff());
        removeButton.addActionListener(e -> this.removeSelectedStaff());
        saveButton.addActionListener(e -> this.saveChanges());
        cancelButton.addActionListener(e -> this.dispose());
    }

    private void setupEventHandlers() {
        this.departmentComboBox.addActionListener(e -> this.loadStaffForDepartment());
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

    private void loadStaffForDepartment() {
        String selectedDept = (String)this.departmentComboBox.getSelectedItem();
        if (selectedDept == null) {
            return;
        }
        try {
            this.availableStaffModel.clear();
            this.assignedStaffModel.clear();
            Document department = this.departmentDAO.getDepartmentByName(selectedDept);
            if (department != null) {
                List assignedStaff = (List)department.get((Object)"staffMembers");
                List<Document> allStaff = this.userDAO.getUsersByRole("Staff");
                for (Document staff : allStaff) {
                    String fullName = staff.getString((Object)"fullName");
                    if (fullName == null || fullName.trim().isEmpty()) continue;
                    if (assignedStaff != null && assignedStaff.contains(fullName)) {
                        this.assignedStaffModel.addElement(fullName);
                        continue;
                    }
                    this.availableStaffModel.addElement(fullName);
                }
            }
        }
        catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading staff: " + e.getMessage(), "Error", 0);
        }
    }

    private void assignSelectedStaff() {
        List<String> selected = this.availableStaffList.getSelectedValuesList();
        for (String staff : selected) {
            this.availableStaffModel.removeElement(staff);
            this.assignedStaffModel.addElement(staff);
        }
    }

    private void removeSelectedStaff() {
        List<String> selected = this.assignedStaffList.getSelectedValuesList();
        for (String staff : selected) {
            this.assignedStaffModel.removeElement(staff);
            this.availableStaffModel.addElement(staff);
        }
    }

    private void saveChanges() {
        String selectedDept = (String)this.departmentComboBox.getSelectedItem();
        if (selectedDept == null) {
            JOptionPane.showMessageDialog(this, "Please select a department", "Error", 0);
            return;
        }
        try {
            Document department = this.departmentDAO.getDepartmentByName(selectedDept);
            if (department != null) {
                String staff;
                int i;
                String departmentId = department.getString((Object)"departmentId");
                ArrayList currentStaff = (ArrayList)department.get((Object)"staffMembers");
                if (currentStaff == null) {
                    currentStaff = new ArrayList();
                }
                for (i = 0; i < this.assignedStaffModel.getSize(); ++i) {
                    staff = this.assignedStaffModel.getElementAt(i);
                    if (currentStaff.contains(staff)) continue;
                    this.departmentDAO.addStaffToDepartment(departmentId, staff);
                }
                for (i = 0; i < this.availableStaffModel.getSize(); ++i) {
                    staff = this.availableStaffModel.getElementAt(i);
                    if (!currentStaff.contains(staff)) continue;
                    this.departmentDAO.removeStaffFromDepartment(departmentId, staff);
                }
                JOptionPane.showMessageDialog(this, "Staff assignments updated successfully!", "Success", 1);
                this.dispose();
            }
        }
        catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error saving changes: " + e.getMessage(), "Error", 0);
        }
    }

    private void setupDialog() {
        this.setSize(600, 500);
        this.setLocationRelativeTo(this.getParent());
        this.setDefaultCloseOperation(2);
    }
}

