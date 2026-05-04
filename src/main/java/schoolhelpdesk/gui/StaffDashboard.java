/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bson.Document
 */
package schoolhelpdesk.gui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import org.bson.Document;
import schoolhelpdesk.dao.TicketDAO;
import schoolhelpdesk.gui.LoginFrame;
import schoolhelpdesk.model.User;

public class StaffDashboard
extends JFrame {
    private static final Color APP_BG = new Color(248, 250, 252);
    private static final Color SURFACE = Color.WHITE;
    private static final Color BORDER = new Color(226, 232, 240);
    private static final Color BRAND = new Color(37, 99, 235);
    private static final Color BRAND_DARK = new Color(30, 64, 175);
    private static final Color SIDEBAR_TOP = new Color(15, 23, 42);
    private static final Color SIDEBAR_NAV = new Color(30, 41, 59);
    private static final Color SIDEBAR_BTN = new Color(51, 65, 85);
    private static final Color TEXT_PRIMARY = new Color(15, 23, 42);
    private static final Color TEXT_MUTED = new Color(100, 116, 139);
    private static final Color ACCENT_GREEN = new Color(22, 163, 74);
    private static final Color ACCENT_AMBER = new Color(217, 119, 6);
    private static final Color ACCENT_SKY = new Color(2, 132, 199);
    private static final Color ACCENT_SLATE = new Color(71, 85, 105);
    private User currentUser;
    private TicketDAO ticketDAO;
    private JLabel staffNameLabel;
    private JLabel departmentLabel;
    private JLabel notificationsLabel;
    private JLabel totalAssignedLabel;
    private JLabel pendingTasksLabel;
    private JLabel inProgressLabel;
    private JLabel completedTasksLabel;
    private JTable ticketsTable;
    private DefaultTableModel ticketsTableModel;
    private TableRowSorter<DefaultTableModel> tableSorter;
    private JButton startTaskButton;
    private JButton markResolvedButton;
    private JButton requestInfoButton;
    private JButton viewDetailsButton;
    private JButton refreshButton;
    private JTextArea notesArea;
    private JButton saveNotesButton;
    private JButton dashboardButton;
    private JButton assignedTasksButton;
    private JButton completedTasksButton;
    private JButton profileButton;
    private JButton logoutButton;
    private JPanel headerPanel;
    private JPanel sidebarPanel;
    private JPanel summaryPanel;
    private JPanel ticketsPanel;
    private JPanel notesPanel;
    private JPanel contentPanel;
    private List<String> notifications;

    public StaffDashboard(User user) {
        this.currentUser = user;
        this.ticketDAO = new TicketDAO();
        this.notifications = new ArrayList<String>();
        this.initializeComponents();
        this.setupLayout();
        this.setupEventHandlers();
        this.loadInitialData();
        this.setupFrame();
    }

    private void initializeComponents() {
        this.staffNameLabel = new JLabel("Staff: " + this.currentUser.getFullName());
        this.staffNameLabel.setFont(new Font("Segoe UI", 1, 16));
        this.staffNameLabel.setForeground(Color.WHITE);
        this.departmentLabel = new JLabel("Department: IT Support");
        this.departmentLabel.setFont(new Font("Segoe UI", 0, 14));
        this.departmentLabel.setForeground(Color.WHITE);
        this.notificationsLabel = new JLabel("Notifications: 0");
        this.notificationsLabel.setFont(new Font("Segoe UI", 0, 12));
        this.notificationsLabel.setForeground(new Color(203, 213, 225));
        this.notificationsLabel.setCursor(new Cursor(12));
        this.totalAssignedLabel = new JLabel("0");
        this.totalAssignedLabel.setFont(new Font("Segoe UI", 1, 30));
        this.totalAssignedLabel.setForeground(BRAND);
        this.pendingTasksLabel = new JLabel("0");
        this.pendingTasksLabel.setFont(new Font("Segoe UI", 1, 30));
        this.pendingTasksLabel.setForeground(ACCENT_AMBER);
        this.inProgressLabel = new JLabel("0");
        this.inProgressLabel.setFont(new Font("Segoe UI", 1, 30));
        this.inProgressLabel.setForeground(ACCENT_SKY);
        this.completedTasksLabel = new JLabel("0");
        this.completedTasksLabel.setFont(new Font("Segoe UI", 1, 30));
        this.completedTasksLabel.setForeground(ACCENT_GREEN);
        Object[] ticketColumns = new String[]{"Ticket ID", "Issue Title", "Priority", "Status", "Assigned Date"};
        this.ticketsTableModel = new DefaultTableModel(ticketColumns, 0){

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        this.ticketsTable = new JTable(this.ticketsTableModel);
        this.ticketsTable.setSelectionMode(0);
        this.ticketsTable.getTableHeader().setFont(new Font("Segoe UI", 1, 12));
        this.ticketsTable.setRowHeight(28);
        this.tableSorter = new TableRowSorter<DefaultTableModel>(this.ticketsTableModel);
        this.ticketsTable.setRowSorter(this.tableSorter);
        this.startTaskButton = new JButton("Start task");
        this.markResolvedButton = new JButton("Mark resolved");
        this.requestInfoButton = new JButton("Request info");
        this.viewDetailsButton = new JButton("View details");
        this.refreshButton = new JButton("Refresh");
        this.notesArea = new JTextArea(5, 30);
        this.notesArea.setFont(new Font("Segoe UI", 0, 12));
        this.notesArea.setLineWrap(true);
        this.notesArea.setWrapStyleWord(true);
        this.saveNotesButton = new JButton("Save notes");
        this.dashboardButton = this.createSidebarButton("Dashboard");
        this.assignedTasksButton = this.createSidebarButton("Assigned Tasks");
        this.completedTasksButton = this.createSidebarButton("Completed");
        this.profileButton = this.createSidebarButton("Profile");
        this.logoutButton = this.createSidebarButton("Logout");
    }

    private void setupLayout() {
        this.setLayout(new BorderLayout());
        this.headerPanel = new JPanel(new BorderLayout());
        this.headerPanel.setBackground(BRAND_DARK);
        this.headerPanel.setBorder(new EmptyBorder(15, 20, 15, 20));
        this.headerPanel.setPreferredSize(new Dimension(0, 80));
        JPanel headerLeft = new JPanel(new FlowLayout(0));
        headerLeft.setOpaque(false);
        headerLeft.add(this.staffNameLabel);
        headerLeft.add(Box.createHorizontalStrut(30));
        headerLeft.add(this.departmentLabel);
        this.headerPanel.add((Component)headerLeft, "West");
        this.headerPanel.add((Component)this.notificationsLabel, "East");
        this.createSidebarPanel();
        this.createSummaryPanel();
        this.createTicketsPanel();
        this.createNotesPanel();
        this.contentPanel = new JPanel(new CardLayout());
        this.contentPanel.add((Component)this.summaryPanel, "Dashboard");
        this.contentPanel.add((Component)this.ticketsPanel, "Assigned Tasks");
        this.contentPanel.add((Component)this.notesPanel, "Work Notes");
        this.add((Component)this.headerPanel, "North");
        this.add((Component)this.sidebarPanel, "West");
        this.add((Component)this.contentPanel, "Center");
    }

    private void createSidebarPanel() {
        this.sidebarPanel = new JPanel(new BorderLayout());
        this.sidebarPanel.setBackground(SIDEBAR_TOP);
        this.sidebarPanel.setPreferredSize(new Dimension(232, 0));
        this.sidebarPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, BORDER), BorderFactory.createEmptyBorder(0, 0, 0, 0)));
        JPanel logoPanel = new JPanel(new BorderLayout());
        logoPanel.setBackground(SIDEBAR_TOP);
        logoPanel.setBorder(BorderFactory.createEmptyBorder(22, 18, 18, 18));
        JLabel logoLabel = new JLabel("SH");
        logoLabel.setFont(new Font("Segoe UI", 1, 26));
        logoLabel.setForeground(Color.WHITE);
        logoLabel.setHorizontalAlignment(0);
        JLabel systemLabel = new JLabel("Helpdesk");
        systemLabel.setFont(new Font("Segoe UI", 1, 13));
        systemLabel.setForeground(new Color(203, 213, 225));
        systemLabel.setHorizontalAlignment(0);
        logoPanel.add((Component)logoLabel, "North");
        logoPanel.add((Component)systemLabel, "Center");
        JPanel navPanel = new JPanel(new GridLayout(0, 1, 6, 6));
        navPanel.setBackground(SIDEBAR_NAV);
        navPanel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        navPanel.add(this.dashboardButton);
        navPanel.add(this.assignedTasksButton);
        navPanel.add(this.completedTasksButton);
        navPanel.add(this.profileButton);
        JPanel userInfoPanel = new JPanel(new BorderLayout());
        userInfoPanel.setBackground(SIDEBAR_TOP);
        userInfoPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER), BorderFactory.createEmptyBorder(14, 14, 14, 14)));
        JLabel userLabel = new JLabel(this.currentUser.getFullName());
        userLabel.setFont(new Font("Segoe UI", 1, 12));
        userLabel.setForeground(Color.WHITE);
        userLabel.setHorizontalAlignment(0);
        JLabel roleLabel = new JLabel("Support staff");
        roleLabel.setFont(new Font("Segoe UI", 0, 11));
        roleLabel.setForeground(TEXT_MUTED);
        roleLabel.setHorizontalAlignment(0);
        userInfoPanel.add((Component)userLabel, "North");
        userInfoPanel.add((Component)roleLabel, "Center");
        userInfoPanel.add((Component)this.logoutButton, "South");
        this.sidebarPanel.add((Component)logoPanel, "North");
        this.sidebarPanel.add((Component)navPanel, "Center");
        this.sidebarPanel.add((Component)userInfoPanel, "South");
        this.logoutButton.setBackground(new Color(220, 53, 69));
        this.logoutButton.setForeground(Color.WHITE);
        this.logoutButton.setFont(new Font("Segoe UI", 1, 12));
        this.logoutButton.setBorderPainted(false);
        this.logoutButton.setFocusPainted(false);
        this.logoutButton.setCursor(new Cursor(12));
    }

    private void createSummaryPanel() {
        this.summaryPanel = new JPanel(new BorderLayout());
        this.summaryPanel.setBackground(APP_BG);
        this.summaryPanel.setBorder(new EmptyBorder(28, 28, 28, 28));
        JLabel summaryTitle = new JLabel("Task summary");
        summaryTitle.setFont(new Font("Segoe UI", 1, 22));
        summaryTitle.setForeground(TEXT_PRIMARY);
        summaryTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 16, 0));
        JPanel cardsPanel = new JPanel(new GridLayout(2, 2, 16, 16));
        cardsPanel.setBackground(APP_BG);
        cardsPanel.add(this.createSummaryCard("Total assigned", this.totalAssignedLabel, BRAND, "All tickets assigned to you"));
        cardsPanel.add(this.createSummaryCard("Pending", this.pendingTasksLabel, ACCENT_AMBER, "Awaiting your action"));
        cardsPanel.add(this.createSummaryCard("In progress", this.inProgressLabel, ACCENT_SKY, "Active work items"));
        cardsPanel.add(this.createSummaryCard("Completed", this.completedTasksLabel, ACCENT_GREEN, "Resolved tickets"));
        this.summaryPanel.add((Component)summaryTitle, "North");
        this.summaryPanel.add((Component)cardsPanel, "Center");
    }

    private JPanel createSummaryCard(String title, JLabel valueLabel, Color accent, String description) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(SURFACE);
        card.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(0, 0, 0, 3, accent), BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(BORDER, 1), BorderFactory.createEmptyBorder(16, 16, 16, 16))));
        JPanel titlePanel = new JPanel(new FlowLayout(0));
        titlePanel.setOpaque(false);
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", 1, 12));
        titleLabel.setForeground(TEXT_PRIMARY);
        titlePanel.add(titleLabel);
        valueLabel.setHorizontalAlignment(0);
        JLabel descLabel = new JLabel(description);
        descLabel.setFont(new Font("Segoe UI", 0, 11));
        descLabel.setForeground(TEXT_MUTED);
        descLabel.setHorizontalAlignment(0);
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setOpaque(false);
        centerPanel.add((Component)valueLabel, "Center");
        centerPanel.add((Component)descLabel, "South");
        centerPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 5, 0));
        card.add((Component)titlePanel, "North");
        card.add((Component)centerPanel, "Center");
        return card;
    }

    private void createTicketsPanel() {
        this.ticketsPanel = new JPanel(new BorderLayout());
        this.ticketsPanel.setBackground(APP_BG);
        this.ticketsPanel.setBorder(new EmptyBorder(28, 28, 28, 28));
        JLabel ticketsTitle = new JLabel("Assigned tasks");
        ticketsTitle.setFont(new Font("Segoe UI", 1, 22));
        ticketsTitle.setForeground(TEXT_PRIMARY);
        ticketsTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 12, 0));
        JPanel actionsPanel = new JPanel(new FlowLayout(0, 10, 8));
        actionsPanel.setBackground(SURFACE);
        actionsPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(BORDER, 1), BorderFactory.createEmptyBorder(14, 14, 14, 14)));
        this.styleButton(this.startTaskButton, ACCENT_GREEN, "Start task");
        this.styleButton(this.markResolvedButton, BRAND, "Mark resolved");
        this.styleButton(this.requestInfoButton, ACCENT_AMBER, "Request info");
        this.styleButton(this.viewDetailsButton, ACCENT_SLATE, "View details");
        this.styleButton(this.refreshButton, new Color(100, 116, 139), "Refresh");
        actionsPanel.add(this.startTaskButton);
        actionsPanel.add(this.markResolvedButton);
        actionsPanel.add(this.requestInfoButton);
        actionsPanel.add(this.viewDetailsButton);
        actionsPanel.add(Box.createHorizontalStrut(12));
        actionsPanel.add(this.refreshButton);
        JScrollPane ticketsScrollPane = this.createTicketsScrollPane();
        JPanel northStack = new JPanel(new BorderLayout(0, 12));
        northStack.setOpaque(false);
        northStack.add((Component)ticketsTitle, "North");
        northStack.add((Component)actionsPanel, "Center");
        this.ticketsPanel.add((Component)northStack, "North");
        this.ticketsPanel.add((Component)ticketsScrollPane, "Center");
    }

    private void createNotesPanel() {
        this.notesPanel = new JPanel(new BorderLayout());
        this.notesPanel.setBackground(APP_BG);
        this.notesPanel.setBorder(new EmptyBorder(28, 28, 28, 28));
        JLabel notesTitle = new JLabel("Work notes");
        notesTitle.setFont(new Font("Segoe UI", 1, 22));
        notesTitle.setForeground(TEXT_PRIMARY);
        notesTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 12, 0));
        JScrollPane notesScrollPane = new JScrollPane(this.notesArea);
        notesScrollPane.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(BORDER, 1), "Task notes and actions", 2, 0, new Font("Segoe UI", 1, 12), TEXT_PRIMARY));
        notesScrollPane.setPreferredSize(new Dimension(0, 200));
        JPanel savePanel = new JPanel(new FlowLayout(2));
        savePanel.setOpaque(false);
        savePanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        this.saveNotesButton.setBackground(BRAND);
        this.saveNotesButton.setForeground(Color.WHITE);
        this.saveNotesButton.setFont(new Font("Segoe UI", 1, 12));
        this.saveNotesButton.setBorderPainted(false);
        this.saveNotesButton.setFocusPainted(false);
        this.saveNotesButton.setCursor(new Cursor(12));
        savePanel.add(this.saveNotesButton);
        this.notesPanel.add((Component)notesTitle, "North");
        this.notesPanel.add((Component)notesScrollPane, "Center");
        this.notesPanel.add((Component)savePanel, "South");
    }

    private void styleButton(JButton button, Color color, String text) {
        button.setText(text);
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", 1, 12));
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(12));
        button.setPreferredSize(new Dimension(128, 34));
    }

    private JScrollPane createTicketsScrollPane() {
        this.ticketsTable.setRowHeight(28);
        this.ticketsTable.setFont(new Font("Segoe UI", 0, 11));
        this.ticketsTable.getTableHeader().setFont(new Font("Segoe UI", 1, 11));
        this.ticketsTable.getTableHeader().setBackground(BRAND_DARK);
        this.ticketsTable.getTableHeader().setForeground(Color.WHITE);
        this.ticketsTable.getTableHeader().setPreferredSize(new Dimension(0, 36));
        this.ticketsTable.setSelectionBackground(new Color(224, 242, 254));
        this.ticketsTable.setSelectionForeground(TEXT_PRIMARY);
        this.ticketsTable.setGridColor(BORDER);
        this.ticketsTable.setShowGrid(true);
        this.ticketsTable.setFillsViewportHeight(true);
        JScrollPane scrollPane = new JScrollPane(this.ticketsTable);
        scrollPane.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(BORDER, 1), BorderFactory.createEmptyBorder(0, 0, 0, 0)));
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.setPreferredSize(new Dimension(0, 400));
        return scrollPane;
    }

    private JButton createSidebarButton(String text) {
        final JButton button = new JButton(text);
        button.setBackground(SIDEBAR_BTN);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", 1, 12));
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setHorizontalAlignment(2);
        button.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(71, 85, 105), 1), BorderFactory.createEmptyBorder(10, 14, 10, 14)));
        button.setCursor(new Cursor(12));
        button.setOpaque(true);
        button.setPreferredSize(new Dimension(200, 42));
        return button;
    }

    private void setupEventHandlers() {
        this.dashboardButton.addActionListener(e -> this.showPanel("Dashboard"));
        this.assignedTasksButton.addActionListener(e -> this.showPanel("Assigned Tasks"));
        this.completedTasksButton.addActionListener(e -> this.showPanel("Completed"));
        this.profileButton.addActionListener(e -> this.showProfile());
        this.logoutButton.addActionListener(e -> this.logout());
        this.startTaskButton.addActionListener(e -> this.startTask());
        this.markResolvedButton.addActionListener(e -> this.markAsResolved());
        this.requestInfoButton.addActionListener(e -> this.requestMoreInfo());
        this.viewDetailsButton.addActionListener(e -> this.showDetailedTicketView());
        this.refreshButton.addActionListener(e -> {
            this.loadInitialData();
            this.addNotification("Dashboard refreshed successfully!");
        });
        this.saveNotesButton.addActionListener(e -> this.saveWorkNotes());
        this.ticketsTable.addMouseListener(new MouseAdapter(){

            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    StaffDashboard.this.showDetailedTicketView();
                }
            }
        });
        this.notificationsLabel.addMouseListener(new MouseAdapter(){

            @Override
            public void mouseClicked(MouseEvent e) {
                StaffDashboard.this.showNotifications();
            }
        });
    }

    private void showPanel(String panelName) {
        CardLayout cl = (CardLayout)this.contentPanel.getLayout();
        cl.show(this.contentPanel, panelName);
        this.resetSidebarButtonColors();
        switch (panelName) {
            case "Dashboard": {
                this.dashboardButton.setBackground(BRAND);
                break;
            }
            case "Assigned Tasks": {
                this.assignedTasksButton.setBackground(BRAND);
                break;
            }
            case "Completed": {
                this.completedTasksButton.setBackground(BRAND);
                break;
            }
        }
    }

    private void resetSidebarButtonColors() {
        this.dashboardButton.setBackground(SIDEBAR_BTN);
        this.assignedTasksButton.setBackground(SIDEBAR_BTN);
        this.completedTasksButton.setBackground(SIDEBAR_BTN);
        this.profileButton.setBackground(SIDEBAR_BTN);
    }

    private void startTask() {
        int selectedRow = this.ticketsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a ticket to start working on.", "No Selection", 2);
            return;
        }
        String ticketId = (String)this.ticketsTableModel.getValueAt(selectedRow, 0);
        try {
            this.ticketDAO.updateTicketStatus(ticketId, "In Progress");
            this.ticketDAO.addNoteToTicket(ticketId, "Task started by " + this.currentUser.getFullName());
            this.loadInitialData();
            this.addNotification("Task " + ticketId + " started.");
            JOptionPane.showMessageDialog(this, "Task started successfully!", "Success", 1);
        }
        catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error starting task: " + e.getMessage(), "Error", 0);
        }
    }

    private void markAsResolved() {
        int selectedRow = this.ticketsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a ticket to mark as resolved.", "No Selection", 2);
            return;
        }
        String ticketId = (String)this.ticketsTableModel.getValueAt(selectedRow, 0);
        String resolution = JOptionPane.showInputDialog(this, "Enter resolution details:", "Mark as Resolved", 3);
        if (resolution != null && !resolution.trim().isEmpty()) {
            try {
                this.ticketDAO.updateTicketStatus(ticketId, "Resolved");
                this.ticketDAO.addNoteToTicket(ticketId, "Resolved by " + this.currentUser.getFullName() + ": " + resolution);
                this.loadInitialData();
                this.addNotification("Ticket " + ticketId + " resolved.");
                JOptionPane.showMessageDialog(this, "Ticket marked as resolved!", "Success", 1);
            }
            catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error resolving ticket: " + e.getMessage(), "Error", 0);
            }
        }
    }

    private void requestMoreInfo() {
        int selectedRow = this.ticketsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a ticket to request more information.", "No Selection", 2);
            return;
        }
        String ticketId = (String)this.ticketsTableModel.getValueAt(selectedRow, 0);
        String question = JOptionPane.showInputDialog(this, "Enter your question for the user:", "Request More Info", 3);
        if (question != null && !question.trim().isEmpty()) {
            try {
                this.ticketDAO.updateTicketStatus(ticketId, "Clarification Requested");
                this.ticketDAO.addNoteToTicket(ticketId, "Staff requested clarification: " + question);
                this.loadInitialData();
                this.addNotification("Clarification requested for ticket " + ticketId);
                JOptionPane.showMessageDialog(this, "Clarification request sent to user!", "Success", 1);
            }
            catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error requesting clarification: " + e.getMessage(), "Error", 0);
            }
        }
    }

    private void showDetailedTicketView() {
        int selectedRow = this.ticketsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a ticket to view details.", "No Selection", 2);
            return;
        }
        String ticketId = (String)this.ticketsTableModel.getValueAt(selectedRow, 0);
        JDialog detailsDialog = new JDialog(this, "Ticket details — " + ticketId, true);
        detailsDialog.setSize(800, 700);
        detailsDialog.setLocationRelativeTo(this);
        JPanel mainPanel = new JPanel(new BorderLayout());
        JPanel detailsPanel = new JPanel(new GridBagLayout());
        detailsPanel.setBackground(Color.WHITE);
        detailsPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(BORDER, 1), "Ticket information", 2, 0, new Font("Segoe UI", 1, 13), TEXT_PRIMARY));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = 17;
        gbc.fill = 2;
        try {
            Document ticket = this.ticketDAO.getTicketById(ticketId);
            if (ticket != null) {
                this.addDetailField(detailsPanel, gbc, "Ticket ID:", ticket.getString((Object)"ticketId"), 0);
                this.addDetailField(detailsPanel, gbc, "Title:", ticket.getString((Object)"title"), 1);
                this.addDetailField(detailsPanel, gbc, "Created By:", ticket.getString((Object)"createdBy"), 2);
                this.addDetailField(detailsPanel, gbc, "Department:", ticket.getString((Object)"department"), 3);
                this.addDetailField(detailsPanel, gbc, "Status:", ticket.getString((Object)"status"), 4);
                this.addDetailField(detailsPanel, gbc, "Priority:", ticket.getString((Object)"priority"), 5);
                this.addDetailField(detailsPanel, gbc, "Created Date:", ticket.getString((Object)"createdDate"), 6);
                this.addDetailField(detailsPanel, gbc, "Assigned Date:", ticket.getString((Object)"assignedDate") != null ? ticket.getString((Object)"assignedDate") : "Not assigned", 7);
                gbc.gridx = 0;
                gbc.gridy = 8;
                gbc.gridwidth = 2;
                detailsPanel.add((Component)new JLabel("Description:"), gbc);
                gbc.gridx = 0;
                gbc.gridy = 9;
                gbc.gridwidth = 2;
                gbc.fill = 1;
                JTextArea descArea = new JTextArea(ticket.getString((Object)"description"));
                descArea.setEditable(false);
                descArea.setLineWrap(true);
                descArea.setWrapStyleWord(true);
                descArea.setFont(new Font("Segoe UI", 0, 12));
                descArea.setBackground(Color.WHITE);
                descArea.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
                JScrollPane descScrollPane = new JScrollPane(descArea);
                descScrollPane.setPreferredSize(new Dimension(600, 100));
                detailsPanel.add((Component)descScrollPane, gbc);
                gbc.gridx = 0;
                gbc.gridy = 10;
                gbc.gridwidth = 2;
                detailsPanel.add((Component)new JLabel("Notes/History:"), gbc);
                gbc.gridx = 0;
                gbc.gridy = 11;
                gbc.fill = 1;
                List notes = (List)ticket.get((Object)"notes");
                if (notes != null && !notes.isEmpty()) {
                    JTextArea notesArea = new JTextArea();
                    for (Object note : notes) {
                        notesArea.append("- " + String.valueOf(note) + "\n");
                    }
                    notesArea.setEditable(false);
                    notesArea.setFont(new Font("Segoe UI", 0, 11));
                    notesArea.setBackground(Color.WHITE);
                    notesArea.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
                    JScrollPane notesScrollPane = new JScrollPane(notesArea);
                    notesScrollPane.setPreferredSize(new Dimension(600, 120));
                    detailsPanel.add((Component)notesScrollPane, gbc);
                } else {
                    detailsPanel.add((Component)new JLabel("No notes"), gbc);
                }
            }
        }
        catch (Exception e2) {
            detailsPanel.add(new JLabel("Error loading ticket details: " + e2.getMessage()));
        }
        JPanel actionPanel = new JPanel(new FlowLayout(2));
        actionPanel.setBackground(Color.WHITE);
        JButton closeButton = new JButton("Close");
        closeButton.setBackground(BRAND);
        closeButton.setForeground(Color.WHITE);
        closeButton.setFont(new Font("Segoe UI", 1, 12));
        closeButton.setBorderPainted(false);
        closeButton.setFocusPainted(false);
        closeButton.addActionListener(e -> detailsDialog.dispose());
        actionPanel.add(closeButton);
        mainPanel.add((Component)detailsPanel, "Center");
        mainPanel.add((Component)actionPanel, "South");
        detailsDialog.add(new JScrollPane(mainPanel));
        detailsDialog.setVisible(true);
    }

    private void addDetailField(JPanel panel, GridBagConstraints gbc, String label, String value, int row) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        gbc.fill = 2;
        panel.add((Component)new JLabel(label), gbc);
        gbc.gridx = 1;
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", 0, 12));
        valueLabel.setForeground(TEXT_PRIMARY);
        panel.add((Component)valueLabel, gbc);
    }

    private void saveWorkNotes() {
        String notes = this.notesArea.getText().trim();
        if (notes.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter some notes before saving.", "Empty Notes", 2);
            return;
        }
        try {
            this.addNotification("Work notes saved.");
            JOptionPane.showMessageDialog(this, "Work notes saved successfully!", "Success", 1);
        }
        catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error saving notes: " + e.getMessage(), "Error", 0);
        }
    }

    private void showProfile() {
        JDialog profileDialog = new JDialog(this, "Staff profile", true);
        profileDialog.setSize(500, 400);
        profileDialog.setLocationRelativeTo(this);
        JPanel profilePanel = new JPanel(new GridLayout(0, 2, 10, 10));
        profilePanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        profilePanel.add(new JLabel("Username:"));
        profilePanel.add(new JLabel(this.currentUser.getUsername()));
        profilePanel.add(new JLabel("Full Name:"));
        profilePanel.add(new JLabel(this.currentUser.getFullName()));
        profilePanel.add(new JLabel("Email:"));
        profilePanel.add(new JLabel(this.currentUser.getEmail()));
        profilePanel.add(new JLabel("Role:"));
        profilePanel.add(new JLabel("Support Staff"));
        profilePanel.add(new JLabel("Department:"));
        profilePanel.add(new JLabel("IT Support"));
        profilePanel.add(new JLabel("Employee ID:"));
        profilePanel.add(new JLabel("STF001"));
        profilePanel.add(new JLabel("Status:"));
        profilePanel.add(new JLabel("Active"));
        profileDialog.add(profilePanel);
        profileDialog.setVisible(true);
    }

    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to logout?", "Confirm Logout", 0);
        if (confirm == 0) {
            this.dispose();
            new LoginFrame().setVisible(true);
        }
    }

    private void showNotifications() {
        if (this.notifications.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No new notifications.", "Notifications", 1);
        } else {
            StringBuilder sb = new StringBuilder("Recent Notifications:\n\n");
            for (int i = this.notifications.size() - 1; i >= Math.max(0, this.notifications.size() - 5); --i) {
                sb.append("\u2022 ").append(this.notifications.get(i)).append("\n");
            }
            JOptionPane.showMessageDialog(this, sb.toString(), "Notifications", 1);
        }
    }

    private void addNotification(String message) {
        String timestamp = new SimpleDateFormat("HH:mm:ss").format(new Date());
        String notification = "[" + timestamp + "] " + message;
        this.notifications.add(notification);
        this.updateNotificationsLabel();
    }

    private void updateNotificationsLabel() {
        int count = this.notifications.size();
        this.notificationsLabel.setText("Notifications: " + count);
    }

    private void loadInitialData() {
        this.loadSummaryStatistics();
        this.loadAssignedTickets();
    }

    private void loadSummaryStatistics() {
        try {
            long totalAssigned = this.ticketDAO.getTicketCountByAssignedStaff(this.currentUser.getUsername());
            long pendingTasks = this.ticketDAO.getTicketCountByStatusAndStaff("Pending Review", this.currentUser.getUsername());
            long inProgress = this.ticketDAO.getTicketCountByStatusAndStaff("In Progress", this.currentUser.getUsername());
            long completed = this.ticketDAO.getTicketCountByStatusAndStaff("Resolved", this.currentUser.getUsername());
            this.totalAssignedLabel.setText(String.valueOf(totalAssigned));
            this.pendingTasksLabel.setText(String.valueOf(pendingTasks));
            this.inProgressLabel.setText(String.valueOf(inProgress));
            this.completedTasksLabel.setText(String.valueOf(completed));
        }
        catch (Exception e) {
            System.err.println("Error loading summary statistics: " + e.getMessage());
        }
    }

    private void loadAssignedTickets() {
        try {
            this.ticketsTableModel.setRowCount(0);
            List<Document> tickets = this.ticketDAO.getTicketsByAssignedStaff(this.currentUser.getUsername());
            for (Document ticket : tickets) {
                String priority = ticket.getString((Object)"priority");
                boolean isHighPriority = "Urgent".equals(priority) || "High".equals(priority);
                Object[] row = new Object[]{ticket.getString((Object)"ticketId"), ticket.getString((Object)"title"), priority, ticket.getString((Object)"status"), ticket.getString((Object)"assignedDate")};
                this.ticketsTableModel.addRow(row);
            }
        }
        catch (Exception e) {
            System.err.println("Error loading assigned tickets: " + e.getMessage());
        }
    }

    private void setupFrame() {
        this.setTitle("Staff dashboard — School Helpdesk");
        this.setDefaultCloseOperation(3);
        this.setSize(1280, 800);
        this.setLocationRelativeTo(null);
        try {
            this.setIconImage(new ImageIcon(this.getClass().getResource("/schoolhelpdesk/resources/images/rmmc.logo.png")).getImage());
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    @Override
    public void dispose() {
        try {
            if (this.ticketDAO != null) {
                this.ticketDAO.close();
            }
        }
        catch (Exception e) {
            System.err.println("Error closing resources: " + e.getMessage());
        }
        super.dispose();
    }
}

