package schoolhelpdesk.gui;

import schoolhelpdesk.model.User;
import schoolhelpdesk.dao.TicketDAO;
import schoolhelpdesk.model.Ticket;
import org.bson.Document;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.regex.Pattern;

public class StaffDashboard extends JFrame {
    private User currentUser;
    private TicketDAO ticketDAO;
    
    // Header components
    private JLabel staffNameLabel;
    private JLabel departmentLabel;
    private JLabel notificationsLabel;
    
    // Summary panel components
    private JLabel totalAssignedLabel;
    private JLabel pendingTasksLabel;
    private JLabel inProgressLabel;
    private JLabel completedTasksLabel;
    
    // Tickets table
    private JTable ticketsTable;
    private DefaultTableModel ticketsTableModel;
    private TableRowSorter<DefaultTableModel> tableSorter;
    
    // Action buttons
    private JButton startTaskButton;
    private JButton markResolvedButton;
    private JButton requestInfoButton;
    private JButton viewDetailsButton;
    private JButton refreshButton;
    
    // Work notes
    private JTextArea notesArea;
    private JButton saveNotesButton;
    
    // Sidebar buttons
    private JButton dashboardButton;
    private JButton assignedTasksButton;
    private JButton completedTasksButton;
    private JButton profileButton;
    private JButton logoutButton;
    
    // Main panels
    private JPanel headerPanel;
    private JPanel sidebarPanel;
    private JPanel summaryPanel;
    private JPanel ticketsPanel;
    private JPanel notesPanel;
    private JPanel contentPanel;
    
    // Notifications system
    private List<String> notifications;
    
    public StaffDashboard(User user) {
        this.currentUser = user;
        this.ticketDAO = new TicketDAO();
        this.notifications = new ArrayList<>();
        
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        loadInitialData();
        setupFrame();
    }
    
    private void initializeComponents() {
        // Header components
        staffNameLabel = new JLabel("Staff: " + currentUser.getFullName());
        staffNameLabel.setFont(new Font("Times New Roman", Font.BOLD, 16));
        staffNameLabel.setForeground(Color.WHITE);
        
        departmentLabel = new JLabel("Department: IT Support");
        departmentLabel.setFont(new Font("Times New Roman", Font.PLAIN, 14));
        departmentLabel.setForeground(Color.WHITE);
        
        notificationsLabel = new JLabel("🔔 0 new notifications");
        notificationsLabel.setFont(new Font("Times New Roman", Font.PLAIN, 12));
        notificationsLabel.setForeground(Color.YELLOW);
        notificationsLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Summary panel components
        totalAssignedLabel = new JLabel("0");
        totalAssignedLabel.setFont(new Font("Times New Roman", Font.BOLD, 32));
        totalAssignedLabel.setForeground(new Color(0, 102, 204));
        
        pendingTasksLabel = new JLabel("0");
        pendingTasksLabel.setFont(new Font("Times New Roman", Font.BOLD, 32));
        pendingTasksLabel.setForeground(Color.ORANGE);
        
        inProgressLabel = new JLabel("0");
        inProgressLabel.setFont(new Font("Times New Roman", Font.BOLD, 32));
        inProgressLabel.setForeground(Color.BLUE);
        
        completedTasksLabel = new JLabel("0");
        completedTasksLabel.setFont(new Font("Times New Roman", Font.BOLD, 32));
        completedTasksLabel.setForeground(Color.GREEN);
        
        // Enhanced tickets table
        String[] ticketColumns = {"Ticket ID", "Issue Title", "Priority", "Status", "Assigned Date"};
        ticketsTableModel = new DefaultTableModel(ticketColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        ticketsTable = new JTable(ticketsTableModel);
        ticketsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        ticketsTable.getTableHeader().setFont(new Font("Times New Roman", Font.BOLD, 12));
        ticketsTable.setRowHeight(28);
        tableSorter = new TableRowSorter<>(ticketsTableModel);
        ticketsTable.setRowSorter(tableSorter);
        
        // Action buttons
        startTaskButton = new JButton("🚀 Start Task");
        markResolvedButton = new JButton("✅ Mark as Resolved");
        requestInfoButton = new JButton("❓ Request More Info");
        viewDetailsButton = new JButton("👁️ View Details");
        refreshButton = new JButton("🔄 Refresh");
        
        // Work notes
        notesArea = new JTextArea(5, 30);
        notesArea.setFont(new Font("Times New Roman", Font.PLAIN, 12));
        notesArea.setLineWrap(true);
        notesArea.setWrapStyleWord(true);
        saveNotesButton = new JButton("💾 Save Notes");
        
        // Modern sidebar buttons with clean design
        dashboardButton = createSidebarButton("Dashboard");
        assignedTasksButton = createSidebarButton("Assigned Tasks");
        completedTasksButton = createSidebarButton("Completed");
        profileButton = createSidebarButton("Profile");
        logoutButton = createSidebarButton("Logout");
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // Header Panel
        headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(0, 51, 102));
        headerPanel.setBorder(new EmptyBorder(15, 20, 15, 20));
        headerPanel.setPreferredSize(new Dimension(0, 80));
        
        JPanel headerLeft = new JPanel(new FlowLayout(FlowLayout.LEFT));
        headerLeft.setOpaque(false);
        headerLeft.add(staffNameLabel);
        headerLeft.add(Box.createHorizontalStrut(30));
        headerLeft.add(departmentLabel);
        
        headerPanel.add(headerLeft, BorderLayout.WEST);
        headerPanel.add(notificationsLabel, BorderLayout.EAST);
        
        // Create content panels
        createSidebarPanel();
        createSummaryPanel();
        createTicketsPanel();
        createNotesPanel();
        
        // Content Panel (card layout)
        contentPanel = new JPanel(new CardLayout());
        contentPanel.add(summaryPanel, "Dashboard");
        contentPanel.add(ticketsPanel, "Assigned Tasks");
        contentPanel.add(notesPanel, "Work Notes");
        
        // Add main components
        add(headerPanel, BorderLayout.NORTH);
        add(sidebarPanel, BorderLayout.WEST);
        add(contentPanel, BorderLayout.CENTER);
    }
    
    private void createSidebarPanel() {
        sidebarPanel = new JPanel(new BorderLayout());
        sidebarPanel.setBackground(new Color(15, 23, 42));
        sidebarPanel.setPreferredSize(new Dimension(220, 0));
        sidebarPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(30, 41, 59), 1),
            BorderFactory.createEmptyBorder(0, 0, 0, 0)
        ));
        
        // Logo/Title area
        JPanel logoPanel = new JPanel(new BorderLayout());
        logoPanel.setBackground(new Color(20, 20, 20));
        logoPanel.setBorder(BorderFactory.createEmptyBorder(20, 15, 20, 15));
        
        JLabel logoLabel = new JLabel("🏫");
        logoLabel.setFont(new Font("Times New Roman", Font.PLAIN, 32));
        logoLabel.setForeground(new Color(0, 102, 204));
        logoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        JLabel systemLabel = new JLabel("HELPDESK");
        systemLabel.setFont(new Font("Times New Roman", Font.BOLD, 16));
        systemLabel.setForeground(Color.WHITE);
        systemLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        logoPanel.add(logoLabel, BorderLayout.NORTH);
        logoPanel.add(systemLabel, BorderLayout.CENTER);
        
        // Navigation buttons
        JPanel navPanel = new JPanel(new GridLayout(0, 1, 0, 0));
        navPanel.setBackground(new Color(30, 30, 30));
        
        navPanel.add(dashboardButton);
        navPanel.add(assignedTasksButton);
        navPanel.add(completedTasksButton);
        navPanel.add(profileButton);
        
        // User info at bottom
        JPanel userInfoPanel = new JPanel(new BorderLayout());
        userInfoPanel.setBackground(new Color(20, 20, 20));
        userInfoPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(60, 60, 60)),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        JLabel userLabel = new JLabel("👤 " + currentUser.getFullName());
        userLabel.setFont(new Font("Times New Roman", Font.PLAIN, 11));
        userLabel.setForeground(new Color(173, 216, 230));
        userLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        JLabel roleLabel = new JLabel("Support Staff");
        roleLabel.setFont(new Font("Times New Roman", Font.BOLD, 10));
        roleLabel.setForeground(new Color(0, 102, 204));
        roleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        userInfoPanel.add(userLabel, BorderLayout.NORTH);
        userInfoPanel.add(roleLabel, BorderLayout.CENTER);
        userInfoPanel.add(logoutButton, BorderLayout.SOUTH);
        
        sidebarPanel.add(logoPanel, BorderLayout.NORTH);
        sidebarPanel.add(navPanel, BorderLayout.CENTER);
        sidebarPanel.add(userInfoPanel, BorderLayout.SOUTH);
        
        // Style logout button
        logoutButton.setBackground(new Color(139, 0, 0));
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setFont(new Font("Times New Roman", Font.BOLD, 11));
        logoutButton.setBorderPainted(false);
        logoutButton.setFocusPainted(false);
        logoutButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }
    
    private void createSummaryPanel() {
        summaryPanel = new JPanel(new BorderLayout());
        summaryPanel.setBackground(new Color(240, 248, 255));
        summaryPanel.setBorder(new EmptyBorder(25, 25, 25, 25));
        
        // Title for summary
        JLabel summaryTitle = new JLabel("📊 Task Summary");
        summaryTitle.setFont(new Font("Times New Roman", Font.BOLD, 28));
        summaryTitle.setForeground(new Color(0, 51, 102));
        summaryTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        
        // Summary cards panel
        JPanel cardsPanel = new JPanel(new GridLayout(2, 2, 15, 15));
        cardsPanel.setBackground(new Color(240, 248, 255));
        
        cardsPanel.add(createSummaryCard("📋 Total Assigned", totalAssignedLabel, new Color(0, 102, 204), "All assigned tickets"));
        cardsPanel.add(createSummaryCard("⏳ Pending Tasks", pendingTasksLabel, new Color(255, 140, 0), "Awaiting action"));
        cardsPanel.add(createSummaryCard("🔄 In Progress", inProgressLabel, new Color(0, 128, 128), "Currently working on"));
        cardsPanel.add(createSummaryCard("✅ Completed", completedTasksLabel, new Color(34, 139, 34), "Finished tasks"));
        
        summaryPanel.add(summaryTitle, BorderLayout.NORTH);
        summaryPanel.add(cardsPanel, BorderLayout.CENTER);
    }
    
    private JPanel createSummaryCard(String title, JLabel valueLabel, Color color, String description) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(color, 3),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        // Title panel
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        titlePanel.setOpaque(false);
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Times New Roman", Font.BOLD, 12));
        titleLabel.setForeground(color);
        titlePanel.add(titleLabel);
        
        // Value label
        valueLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        // Description label
        JLabel descLabel = new JLabel(description);
        descLabel.setFont(new Font("Times New Roman", Font.PLAIN, 10));
        descLabel.setForeground(Color.GRAY);
        descLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        // Add components with spacing
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setOpaque(false);
        centerPanel.add(valueLabel, BorderLayout.CENTER);
        centerPanel.add(descLabel, BorderLayout.SOUTH);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 5, 0));
        
        card.add(titlePanel, BorderLayout.NORTH);
        card.add(centerPanel, BorderLayout.CENTER);
        
        return card;
    }
    
    private void createTicketsPanel() {
        ticketsPanel = new JPanel(new BorderLayout());
        ticketsPanel.setBackground(new Color(240, 248, 255));
        ticketsPanel.setBorder(new EmptyBorder(25, 25, 25, 25));
        
        // Title for tickets panel
        JLabel ticketsTitle = new JLabel("📋 Assigned Tasks");
        ticketsTitle.setFont(new Font("Times New Roman", Font.BOLD, 24));
        ticketsTitle.setForeground(new Color(0, 51, 102));
        ticketsTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        
        // Action buttons panel
        JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        actionsPanel.setBackground(new Color(240, 248, 255));
        actionsPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0, 102, 204), 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        styleButton(startTaskButton, new Color(34, 139, 34), "🚀 Start Task");
        styleButton(markResolvedButton, new Color(0, 102, 204), "✅ Mark as Resolved");
        styleButton(requestInfoButton, new Color(255, 140, 0), "❓ Request More Info");
        styleButton(viewDetailsButton, new Color(70, 130, 180), "👁️ View Details");
        styleButton(refreshButton, new Color(128, 0, 128), "🔄 Refresh");
        
        actionsPanel.add(startTaskButton);
        actionsPanel.add(markResolvedButton);
        actionsPanel.add(requestInfoButton);
        actionsPanel.add(viewDetailsButton);
        actionsPanel.add(Box.createHorizontalStrut(20));
        actionsPanel.add(refreshButton);
        
        // Enhanced tickets table
        JScrollPane ticketsScrollPane = createTicketsScrollPane();
        
        ticketsPanel.add(ticketsTitle, BorderLayout.NORTH);
        ticketsPanel.add(actionsPanel, BorderLayout.NORTH);
        ticketsPanel.add(ticketsScrollPane, BorderLayout.CENTER);
    }
    
    private void createNotesPanel() {
        notesPanel = new JPanel(new BorderLayout());
        notesPanel.setBackground(new Color(240, 248, 255));
        notesPanel.setBorder(new EmptyBorder(25, 25, 25, 25));
        
        // Title for notes panel
        JLabel notesTitle = new JLabel("📝 Work Notes");
        notesTitle.setFont(new Font("Times New Roman", Font.BOLD, 24));
        notesTitle.setForeground(new Color(0, 51, 102));
        notesTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        
        // Notes area with scroll
        JScrollPane notesScrollPane = new JScrollPane(notesArea);
        notesScrollPane.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(0, 102, 204), 2),
            "Task Notes & Actions Taken",
            javax.swing.border.TitledBorder.LEFT,
            javax.swing.border.TitledBorder.TOP,
            new Font("Times New Roman", Font.BOLD, 12),
            new Color(0, 51, 102)
        ));
        notesScrollPane.setPreferredSize(new Dimension(0, 200));
        
        // Save button panel
        JPanel savePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        savePanel.setOpaque(false);
        savePanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        
        saveNotesButton.setBackground(new Color(0, 102, 204));
        saveNotesButton.setForeground(Color.WHITE);
        saveNotesButton.setFont(new Font("Times New Roman", Font.BOLD, 12));
        saveNotesButton.setBorderPainted(false);
        saveNotesButton.setFocusPainted(false);
        saveNotesButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        savePanel.add(saveNotesButton);
        
        notesPanel.add(notesTitle, BorderLayout.NORTH);
        notesPanel.add(notesScrollPane, BorderLayout.CENTER);
        notesPanel.add(savePanel, BorderLayout.SOUTH);
    }
    
    private void styleButton(JButton button, Color color, String text) {
        button.setText(text);
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Times New Roman", Font.BOLD, 11));
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(120, 32));
    }
    
    private JScrollPane createTicketsScrollPane() {
        // Enhanced table styling
        ticketsTable.setRowHeight(28);
        ticketsTable.setFont(new Font("Times New Roman", Font.PLAIN, 11));
        ticketsTable.getTableHeader().setFont(new Font("Times New Roman", Font.BOLD, 11));
        ticketsTable.getTableHeader().setBackground(new Color(0, 51, 102));
        ticketsTable.getTableHeader().setForeground(Color.WHITE);
        ticketsTable.getTableHeader().setPreferredSize(new Dimension(0, 35));
        ticketsTable.setSelectionBackground(new Color(173, 216, 230));
        ticketsTable.setSelectionForeground(Color.BLACK);
        ticketsTable.setGridColor(new Color(200, 200, 200));
        ticketsTable.setShowGrid(true);
        ticketsTable.setFillsViewportHeight(true);
        
        JScrollPane scrollPane = new JScrollPane(ticketsTable);
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0, 102, 204), 2),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.setPreferredSize(new Dimension(0, 400));
        
        return scrollPane;
    }
    
    private JButton createSidebarButton(String text) {
        JButton button = new JButton(text);
        
        // Modern button styling
        button.setBackground(new Color(52, 58, 64));
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(38, 42, 52), 1),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setOpaque(true);
        button.setPreferredSize(new Dimension(180, 40));
        
        // Simple hover effect
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) {
                button.setBackground(new Color(62, 68, 73));
            }
            public void mouseExited(MouseEvent evt) {
                if (!button.getText().contains("Dashboard")) {
                    button.setBackground(new Color(52, 58, 64));
                } else {
                    button.setBackground(new Color(0, 102, 204));
                }
                button.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
        });
        
        return button;
    }
    
    private void setupEventHandlers() {
        // Sidebar navigation
        dashboardButton.addActionListener(e -> showPanel("Dashboard"));
        assignedTasksButton.addActionListener(e -> showPanel("Assigned Tasks"));
        completedTasksButton.addActionListener(e -> showPanel("Completed"));
        profileButton.addActionListener(e -> showProfile());
        logoutButton.addActionListener(e -> logout());
        
        // Action buttons
        startTaskButton.addActionListener(e -> startTask());
        markResolvedButton.addActionListener(e -> markAsResolved());
        requestInfoButton.addActionListener(e -> requestMoreInfo());
        viewDetailsButton.addActionListener(e -> showDetailedTicketView());
        refreshButton.addActionListener(e -> {
            loadInitialData();
            addNotification("Dashboard refreshed successfully!");
        });
        
        // Save notes
        saveNotesButton.addActionListener(e -> saveWorkNotes());
        
        // Table selection
        ticketsTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    showDetailedTicketView();
                }
            }
        });
        
        // Notifications
        notificationsLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                showNotifications();
            }
        });
    }
    
    private void showPanel(String panelName) {
        CardLayout cl = (CardLayout) contentPanel.getLayout();
        cl.show(contentPanel, panelName);
        
        // Reset button colors
        resetSidebarButtonColors();
        
        // Highlight active button
        switch (panelName) {
            case "Dashboard":
                dashboardButton.setBackground(new Color(0, 102, 204));
                break;
            case "Assigned Tasks":
                assignedTasksButton.setBackground(new Color(0, 102, 204));
                break;
            case "Completed":
                completedTasksButton.setBackground(new Color(0, 102, 204));
                break;
        }
    }
    
    private void resetSidebarButtonColors() {
        dashboardButton.setBackground(new Color(50, 50, 50));
        assignedTasksButton.setBackground(new Color(50, 50, 50));
        completedTasksButton.setBackground(new Color(50, 50, 50));
        profileButton.setBackground(new Color(50, 50, 50));
    }
    
    private void startTask() {
        int selectedRow = ticketsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a ticket to start working on.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String ticketId = (String) ticketsTableModel.getValueAt(selectedRow, 0);
        try {
            ticketDAO.updateTicketStatus(ticketId, "In Progress");
            ticketDAO.addNoteToTicket(ticketId, "Task started by " + currentUser.getFullName());
            loadInitialData();
            addNotification("🚀 Task " + ticketId + " started successfully!");
            JOptionPane.showMessageDialog(this, "Task started successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error starting task: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void markAsResolved() {
        int selectedRow = ticketsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a ticket to mark as resolved.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String ticketId = (String) ticketsTableModel.getValueAt(selectedRow, 0);
        String resolution = JOptionPane.showInputDialog(this, "Enter resolution details:", "Mark as Resolved", JOptionPane.QUESTION_MESSAGE);
        
        if (resolution != null && !resolution.trim().isEmpty()) {
            try {
                ticketDAO.updateTicketStatus(ticketId, "Resolved");
                ticketDAO.addNoteToTicket(ticketId, "Resolved by " + currentUser.getFullName() + ": " + resolution);
                loadInitialData();
                addNotification("✅ Ticket " + ticketId + " resolved successfully!");
                JOptionPane.showMessageDialog(this, "Ticket marked as resolved!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error resolving ticket: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void requestMoreInfo() {
        int selectedRow = ticketsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a ticket to request more information.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String ticketId = (String) ticketsTableModel.getValueAt(selectedRow, 0);
        String question = JOptionPane.showInputDialog(this, "Enter your question for the user:", "Request More Info", JOptionPane.QUESTION_MESSAGE);
        
        if (question != null && !question.trim().isEmpty()) {
            try {
                ticketDAO.updateTicketStatus(ticketId, "Clarification Requested");
                ticketDAO.addNoteToTicket(ticketId, "Staff requested clarification: " + question);
                loadInitialData();
                addNotification("❓ Clarification requested for ticket " + ticketId);
                JOptionPane.showMessageDialog(this, "Clarification request sent to user!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error requesting clarification: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void showDetailedTicketView() {
        int selectedRow = ticketsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a ticket to view details.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String ticketId = (String) ticketsTableModel.getValueAt(selectedRow, 0);
        
        JDialog detailsDialog = new JDialog(this, "📋 Ticket Details - " + ticketId, true);
        detailsDialog.setSize(800, 700);
        detailsDialog.setLocationRelativeTo(this);
        
        JPanel mainPanel = new JPanel(new BorderLayout());
        
        // Ticket details panel
        JPanel detailsPanel = new JPanel(new GridBagLayout());
        detailsPanel.setBackground(Color.WHITE);
        detailsPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(0, 102, 204), 2),
            "Ticket Information",
            javax.swing.border.TitledBorder.LEFT,
            javax.swing.border.TitledBorder.TOP,
            new Font("Times New Roman", Font.BOLD, 14),
            new Color(0, 51, 102)
        ));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        try {
            Document ticket = ticketDAO.getTicketById(ticketId);
            if (ticket != null) {
                // Add ticket details
                addDetailField(detailsPanel, gbc, "Ticket ID:", ticket.getString("ticketId"), 0);
                addDetailField(detailsPanel, gbc, "Title:", ticket.getString("title"), 1);
                addDetailField(detailsPanel, gbc, "Created By:", ticket.getString("createdBy"), 2);
                addDetailField(detailsPanel, gbc, "Department:", ticket.getString("department"), 3);
                addDetailField(detailsPanel, gbc, "Status:", ticket.getString("status"), 4);
                addDetailField(detailsPanel, gbc, "Priority:", ticket.getString("priority"), 5);
                addDetailField(detailsPanel, gbc, "Created Date:", ticket.getString("createdDate"), 6);
                addDetailField(detailsPanel, gbc, "Assigned Date:", 
                    ticket.getString("assignedDate") != null ? ticket.getString("assignedDate") : "Not assigned", 7);
                
                // Description
                gbc.gridx = 0; gbc.gridy = 8;
                gbc.gridwidth = 2;
                detailsPanel.add(new JLabel("Description:"), gbc);
                gbc.gridx = 0; gbc.gridy = 9;
                gbc.gridwidth = 2;
                gbc.fill = GridBagConstraints.BOTH;
                JTextArea descArea = new JTextArea(ticket.getString("description"));
                descArea.setEditable(false);
                descArea.setLineWrap(true);
                descArea.setWrapStyleWord(true);
                descArea.setFont(new Font("Times New Roman", Font.PLAIN, 12));
                descArea.setBackground(Color.WHITE);
                descArea.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
                JScrollPane descScrollPane = new JScrollPane(descArea);
                descScrollPane.setPreferredSize(new Dimension(600, 100));
                detailsPanel.add(descScrollPane, gbc);
                
                // Notes
                gbc.gridx = 0; gbc.gridy = 10;
                gbc.gridwidth = 2;
                detailsPanel.add(new JLabel("Notes/History:"), gbc);
                gbc.gridx = 0; gbc.gridy = 11;
                gbc.fill = GridBagConstraints.BOTH;
                @SuppressWarnings("unchecked")
                List<String> notes = (List<String>) ticket.get("notes");
                if (notes != null && !notes.isEmpty()) {
                    JTextArea notesArea = new JTextArea();
                    for (String note : notes) {
                        notesArea.append("- " + note + "\n");
                    }
                    notesArea.setEditable(false);
                    notesArea.setFont(new Font("Times New Roman", Font.PLAIN, 11));
                    notesArea.setBackground(Color.WHITE);
                    notesArea.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
                    JScrollPane notesScrollPane = new JScrollPane(notesArea);
                    notesScrollPane.setPreferredSize(new Dimension(600, 120));
                    detailsPanel.add(notesScrollPane, gbc);
                } else {
                    detailsPanel.add(new JLabel("No notes"), gbc);
                }
            }
        } catch (Exception e) {
            detailsPanel.add(new JLabel("Error loading ticket details: " + e.getMessage()));
        }
        
        // Action buttons
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actionPanel.setBackground(Color.WHITE);
        
        JButton closeButton = new JButton("Close");
        closeButton.setBackground(new Color(0, 102, 204));
        closeButton.setForeground(Color.WHITE);
        closeButton.setFont(new Font("Times New Roman", Font.BOLD, 12));
        closeButton.setBorderPainted(false);
        closeButton.setFocusPainted(false);
        closeButton.addActionListener(e -> detailsDialog.dispose());
        
        actionPanel.add(closeButton);
        
        mainPanel.add(detailsPanel, BorderLayout.CENTER);
        mainPanel.add(actionPanel, BorderLayout.SOUTH);
        
        detailsDialog.add(new JScrollPane(mainPanel));
        detailsDialog.setVisible(true);
    }
    
    private void addDetailField(JPanel panel, GridBagConstraints gbc, String label, String value, int row) {
        gbc.gridx = 0; gbc.gridy = row;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(new JLabel(label), gbc);
        gbc.gridx = 1;
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Times New Roman", Font.BOLD, 12));
        panel.add(valueLabel, gbc);
    }
    
    private void saveWorkNotes() {
        String notes = notesArea.getText().trim();
        if (notes.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter some notes before saving.", "Empty Notes", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            // Save notes to database (this would be implemented based on your requirements)
            addNotification("💾 Work notes saved successfully!");
            JOptionPane.showMessageDialog(this, "Work notes saved successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error saving notes: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void showProfile() {
        JDialog profileDialog = new JDialog(this, "👤 Staff Profile", true);
        profileDialog.setSize(500, 400);
        profileDialog.setLocationRelativeTo(this);
        
        JPanel profilePanel = new JPanel(new GridLayout(0, 2, 10, 10));
        profilePanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        profilePanel.add(new JLabel("Username:"));
        profilePanel.add(new JLabel(currentUser.getUsername()));
        
        profilePanel.add(new JLabel("Full Name:"));
        profilePanel.add(new JLabel(currentUser.getFullName()));
        
        profilePanel.add(new JLabel("Email:"));
        profilePanel.add(new JLabel(currentUser.getEmail()));
        
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
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to logout?", 
            "Confirm Logout", 
            JOptionPane.YES_NO_OPTION);
            
        if (confirm == JOptionPane.YES_OPTION) {
            this.dispose();
            new LoginFrame().setVisible(true);
        }
    }
    
    private void showNotifications() {
        if (notifications.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No new notifications.", "Notifications", JOptionPane.INFORMATION_MESSAGE);
        } else {
            StringBuilder sb = new StringBuilder("Recent Notifications:\n\n");
            for (int i = notifications.size() - 1; i >= Math.max(0, notifications.size() - 5); i--) {
                sb.append("• ").append(notifications.get(i)).append("\n");
            }
            JOptionPane.showMessageDialog(this, sb.toString(), "Notifications", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    private void addNotification(String message) {
        String timestamp = new SimpleDateFormat("HH:mm:ss").format(new Date());
        String notification = "[" + timestamp + "] " + message;
        notifications.add(notification);
        updateNotificationsLabel();
    }
    
    private void updateNotificationsLabel() {
        int count = notifications.size();
        notificationsLabel.setText("🔔 " + count + " new notification" + (count != 1 ? "s" : ""));
    }
    
    private void loadInitialData() {
        loadSummaryStatistics();
        loadAssignedTickets();
    }
    
    private void loadSummaryStatistics() {
        try {
            // Load statistics for the current staff member
            long totalAssigned = ticketDAO.getTicketCountByAssignedStaff(currentUser.getUsername());
            long pendingTasks = ticketDAO.getTicketCountByStatusAndStaff("Pending Review", currentUser.getUsername());
            long inProgress = ticketDAO.getTicketCountByStatusAndStaff("In Progress", currentUser.getUsername());
            long completed = ticketDAO.getTicketCountByStatusAndStaff("Resolved", currentUser.getUsername());
            
            totalAssignedLabel.setText(String.valueOf(totalAssigned));
            pendingTasksLabel.setText(String.valueOf(pendingTasks));
            inProgressLabel.setText(String.valueOf(inProgress));
            completedTasksLabel.setText(String.valueOf(completed));
        } catch (Exception e) {
            System.err.println("Error loading summary statistics: " + e.getMessage());
        }
    }
    
    private void loadAssignedTickets() {
        try {
            ticketsTableModel.setRowCount(0);
            List<Document> tickets = ticketDAO.getTicketsByAssignedStaff(currentUser.getUsername());
            
            for (Document ticket : tickets) {
                // Highlight high-priority tickets
                String priority = ticket.getString("priority");
                boolean isHighPriority = "Urgent".equals(priority) || "High".equals(priority);
                
                Object[] row = {
                    ticket.getString("ticketId"),
                    ticket.getString("title"),
                    priority,
                    ticket.getString("status"),
                    ticket.getString("assignedDate")
                };
                ticketsTableModel.addRow(row);
            }
        } catch (Exception e) {
            System.err.println("Error loading assigned tickets: " + e.getMessage());
        }
    }
    
    private void setupFrame() {
        setTitle("Staff Dashboard - School Helpdesk System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1600, 1000);
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        
        // Set window icon
        try {
            setIconImage(new ImageIcon(getClass().getResource("/schoolhelpdesk/resources/images/rmmc.logo.png")).getImage());
        } catch (Exception e) {
            // Icon not found, continue without it
        }
    }
    
    @Override
    public void dispose() {
        try {
            if (ticketDAO != null) ticketDAO.close();
        } catch (Exception e) {
            System.err.println("Error closing resources: " + e.getMessage());
        }
        super.dispose();
    }
}
