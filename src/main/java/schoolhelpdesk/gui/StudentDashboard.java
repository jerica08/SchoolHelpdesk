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
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import org.bson.Document;
import schoolhelpdesk.dao.TicketDAO;
import schoolhelpdesk.gui.LoginFrame;
import schoolhelpdesk.model.Ticket;
import schoolhelpdesk.model.User;

public class StudentDashboard
extends JFrame {
    private static final Color APP_BG = new Color(241, 245, 249);
    private static final Color SURFACE_BG = new Color(255, 255, 255);
    private static final Color BORDER = new Color(226, 232, 240);
    private static final Color BRAND = new Color(37, 99, 235);
    private static final Color BRAND_DARK = new Color(30, 64, 175);
    private static final Color TEXT_PRIMARY = new Color(15, 23, 42);
    private static final Color TEXT_MUTED = new Color(100, 116, 139);
    private static final Font TITLE_FONT = new Font("Segoe UI", 1, 24);
    private static final Font BODY_FONT = new Font("Segoe UI", 0, 12);
    private static final Font LABEL_FONT = new Font("Segoe UI", 1, 12);
    private static final Color GREEN_SUBMIT = new Color(34, 139, 34);
    private static final Color GREEN_SUBMIT_HOVER = new Color(22, 163, 74);
    private final User currentUser;
    private final TicketDAO ticketDAO;
    private List<Document> cachedUserTickets;
    private JLabel nameLabel;
    private JLabel dateTimeLabel;
    private JLabel notificationIconLabel;
    private Timer clockTimer;
    private Timer refreshTimer;
    private JButton dashboardButton;
    private JButton myTicketsButton;
    private JButton profileButton;
    private JButton logoutButton;
    private JLabel totalTicketsValueLabel;
    private JLabel pendingTicketsValueLabel;
    private JLabel inProgressTicketsValueLabel;
    private JLabel resolvedTicketsValueLabel;
    private JTable ticketsTable;
    private DefaultTableModel ticketsTableModel;
    private JTextArea updatesArea;
    private JPanel contentPanel;
    private JPanel dashboardPanel;
    private JPanel ticketsPanel;
    private JPanel profilePanel;

    public StudentDashboard(User user) {
        this.currentUser = user;
        this.ticketDAO = new TicketDAO();
        this.cachedUserTickets = new ArrayList<Document>();
        this.initializeComponents();
        this.setupLayout();
        this.setupEventHandlers();
        this.setupFrame();
        this.loadUserDashboardData();
        this.startLiveUpdates();
    }

    private void initializeComponents() {
        this.nameLabel = new JLabel("User: " + this.currentUser.getFullName());
        this.nameLabel.setFont(new Font("Segoe UI", 1, 16));
        this.nameLabel.setForeground(Color.WHITE);
        this.dateTimeLabel = new JLabel();
        this.dateTimeLabel.setFont(new Font("Segoe UI", 0, 13));
        this.dateTimeLabel.setForeground(new Color(220, 235, 255));
        this.notificationIconLabel = new JLabel("Notifications: 0");
        this.notificationIconLabel.setFont(new Font("Segoe UI", 0, 12));
        this.notificationIconLabel.setForeground(new Color(203, 213, 225));
        this.dashboardButton = this.createSidebarButton("Dashboard");
        this.myTicketsButton = this.createSidebarButton("My Tickets");
        this.profileButton = this.createSidebarButton("Profile");
        this.logoutButton = this.createSidebarButton("Logout");
        this.logoutButton.setBackground(new Color(220, 53, 69));
        this.totalTicketsValueLabel = this.createValueLabel();
        this.pendingTicketsValueLabel = this.createValueLabel();
        this.inProgressTicketsValueLabel = this.createValueLabel();
        this.resolvedTicketsValueLabel = this.createValueLabel();
        Object[] columns = new String[]{"Ticket ID", "Issue Title", "Department", "Status", "Date Submitted"};
        this.ticketsTableModel = new DefaultTableModel(columns, 0){

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        this.ticketsTable = new JTable(this.ticketsTableModel);
        this.ticketsTable.setSelectionMode(0);
        this.styleModernTable(this.ticketsTable);
        this.updatesArea = new JTextArea(8, 30);
        this.updatesArea.setEditable(false);
        this.updatesArea.setLineWrap(true);
        this.updatesArea.setWrapStyleWord(true);
        this.updatesArea.setFont(BODY_FONT);
    }

    private JLabel createValueLabel() {
        JLabel label = new JLabel("0");
        label.setFont(new Font("Segoe UI", 1, 30));
        label.setHorizontalAlignment(0);
        return label;
    }

    private JButton createSidebarButton(String text) {
        final JButton button = new JButton(text);
        boolean isLogout = "Logout".equals(text);
        button.setBackground(isLogout ? new Color(220, 53, 69) : new Color(51, 65, 85));
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", 1, 13));
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setHorizontalAlignment(2);
        button.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(isLogout ? new Color(185, 28, 28) : new Color(71, 85, 105), 1), BorderFactory.createEmptyBorder(12, 16, 12, 16)));
        button.setCursor(new Cursor(12));
        button.setOpaque(true);
        button.setPreferredSize(new Dimension(210, 46));
        button.putClientProperty("active", false);
        button.putClientProperty("logout", isLogout);
        button.addMouseListener(new MouseAdapter(){

            @Override
            public void mouseEntered(MouseEvent e) {
                if (Boolean.TRUE.equals(button.getClientProperty("logout"))) {
                    button.setBackground(new Color(239, 68, 68));
                } else if (!StudentDashboard.this.isSidebarButtonActive(button)) {
                    button.setBackground(new Color(71, 85, 105));
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (Boolean.TRUE.equals(button.getClientProperty("logout"))) {
                    button.setBackground(new Color(220, 53, 69));
                } else if (!StudentDashboard.this.isSidebarButtonActive(button)) {
                    button.setBackground(new Color(51, 65, 85));
                }
            }
        });
        return button;
    }

    private void setupLayout() {
        this.setLayout(new BorderLayout());
        this.add((Component)this.createHeaderPanel(), "North");
        this.add((Component)this.createSidebarPanel(), "West");
        this.contentPanel = new JPanel(new CardLayout());
        this.dashboardPanel = this.createDashboardPanel();
        this.ticketsPanel = this.createTicketsPanel();
        this.profilePanel = this.createProfilePanel();
        this.contentPanel.add((Component)this.dashboardPanel, "Dashboard");
        this.contentPanel.add((Component)this.ticketsPanel, "Tickets");
        this.contentPanel.add((Component)this.profilePanel, "Profile");
        this.add((Component)this.contentPanel, "Center");
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BRAND_DARK);
        panel.setBorder(new EmptyBorder(14, 18, 14, 18));
        JPanel left = new JPanel(new FlowLayout(0, 12, 0));
        left.setOpaque(false);
        left.add(this.nameLabel);
        JLabel dot = new JLabel(" \u00b7 ");
        dot.setFont(new Font("Segoe UI", 0, 13));
        dot.setForeground(new Color(148, 163, 184));
        left.add(dot);
        left.add(this.dateTimeLabel);
        JPanel right = new JPanel(new FlowLayout(2, 10, 0));
        right.setOpaque(false);
        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> this.manualRefresh());
        refreshButton.setBackground(BRAND);
        refreshButton.setForeground(Color.WHITE);
        refreshButton.setFont(LABEL_FONT);
        refreshButton.setBorder(BorderFactory.createEmptyBorder(7, 12, 7, 12));
        refreshButton.setFocusPainted(false);
        refreshButton.setCursor(new Cursor(12));
        right.add(refreshButton);
        right.add(this.notificationIconLabel);
        panel.add((Component)left, "West");
        panel.add((Component)right, "East");
        return panel;
    }

    private JPanel createSidebarPanel() {
        JPanel panel = new JPanel(new BorderLayout()){

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D)g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gradient = new GradientPaint(0.0f, 0.0f, new Color(10, 25, 47), 0.0f, this.getHeight(), new Color(15, 23, 42));
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, this.getWidth(), this.getHeight());
                g2d.dispose();
            }
        };
        panel.setPreferredSize(new Dimension(260, 0));
        panel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(0, 102, 204)), BorderFactory.createEmptyBorder(0, 0, 0, 0)));
        JPanel logoPanel = new JPanel(new BorderLayout());
        logoPanel.setBackground(new Color(0, 0, 0, 0));
        logoPanel.setBorder(BorderFactory.createEmptyBorder(30, 20, 20, 20));
        JLabel logoLabel = new JLabel("SH");
        logoLabel.setFont(new Font("Segoe UI", 1, 34));
        logoLabel.setForeground(new Color(191, 219, 254));
        logoLabel.setHorizontalAlignment(0);
        JLabel systemLabel = new JLabel("School Helpdesk", 0);
        systemLabel.setFont(new Font("Segoe UI", 1, 16));
        systemLabel.setForeground(Color.WHITE);
        systemLabel.setHorizontalAlignment(0);
        JLabel subtitleLabel = new JLabel("Student Portal", 0);
        subtitleLabel.setFont(new Font("Segoe UI", 0, 10));
        subtitleLabel.setForeground(new Color(148, 163, 184));
        subtitleLabel.setHorizontalAlignment(0);
        JPanel logoTextPanel = new JPanel(new BorderLayout());
        logoTextPanel.setOpaque(false);
        logoTextPanel.add((Component)logoLabel, "North");
        logoTextPanel.add((Component)systemLabel, "Center");
        logoTextPanel.add((Component)subtitleLabel, "South");
        logoPanel.add((Component)logoTextPanel, "Center");
        JPanel navPanel = new JPanel(new GridLayout(0, 1, 0, 10));
        navPanel.setBackground(new Color(15, 23, 42));
        navPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(30, 41, 59), 1), BorderFactory.createEmptyBorder(20, 15, 20, 15)));
        navPanel.add(this.dashboardButton);
        navPanel.add(this.myTicketsButton);
        navPanel.add(this.profileButton);
        JPanel userInfoPanel = new JPanel(new BorderLayout()){

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D)g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gradient = new GradientPaint(0.0f, 0.0f, new Color(0, 0, 0, 0), 0.0f, this.getHeight(), new Color(20, 20, 20));
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, this.getWidth(), this.getHeight());
                g2d.dispose();
            }
        };
        userInfoPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        JLabel userLabel = new JLabel(this.currentUser.getFullName());
        userLabel.setFont(new Font("Segoe UI", 1, 12));
        userLabel.setForeground(Color.WHITE);
        userLabel.setHorizontalAlignment(0);
        JLabel roleLabel = new JLabel("Student");
        roleLabel.setFont(new Font("Segoe UI", 0, 10));
        roleLabel.setForeground(new Color(200, 200, 200));
        roleLabel.setHorizontalAlignment(0);
        JPanel userInfoTextPanel = new JPanel(new BorderLayout());
        userInfoTextPanel.setOpaque(false);
        userInfoTextPanel.add((Component)userLabel, "Center");
        userInfoTextPanel.add((Component)roleLabel, "South");
        userInfoPanel.add((Component)userInfoTextPanel, "Center");
        userInfoPanel.add((Component)this.logoutButton, "South");
        panel.add((Component)logoPanel, "North");
        panel.add((Component)navPanel, "Center");
        panel.add((Component)userInfoPanel, "South");
        return panel;
    }

    private JPanel createDashboardPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 14));
        panel.setBackground(APP_BG);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        JLabel heading = this.createPageHeader("Student Dashboard");
        JButton submitTicketTopButton = new JButton("Submit New Ticket");
        this.styleSolidGreenButton(submitTicketTopButton);
        submitTicketTopButton.addActionListener(e -> this.showCreateTicketDialog());
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setOpaque(false);
        topBar.add((Component)heading, "West");
        topBar.add((Component)submitTicketTopButton, "East");
        panel.add((Component)topBar, "North");
        JPanel center = new JPanel(new GridLayout(2, 1, 12, 12));
        center.setOpaque(false);
        center.add(this.createStatsPanel());
        center.add(this.createNotificationPanel());
        panel.add((Component)center, "Center");
        return panel;
    }

    private JPanel createStatsPanel() {
        JPanel stats = new JPanel(new GridLayout(1, 4, 12, 12));
        stats.setOpaque(false);
        stats.add(this.createStatCard("Total Tickets", this.totalTicketsValueLabel, BRAND));
        stats.add(this.createStatCard("Pending", this.pendingTicketsValueLabel, new Color(245, 158, 11)));
        stats.add(this.createStatCard("In Progress", this.inProgressTicketsValueLabel, new Color(6, 182, 212)));
        stats.add(this.createStatCard("Resolved", this.resolvedTicketsValueLabel, new Color(22, 163, 74)));
        return stats;
    }

    private JPanel createStatCard(String labelText, JLabel valueLabel, Color accent) {
        JPanel card = new JPanel(new BorderLayout(0, 8));
        card.setBackground(SURFACE_BG);
        card.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(BORDER, 1), new EmptyBorder(12, 12, 12, 12)));
        JLabel label = new JLabel(labelText, 0);
        label.setFont(new Font("Segoe UI", 1, 13));
        label.setForeground(TEXT_PRIMARY);
        valueLabel.setForeground(accent);
        card.add((Component)label, "North");
        card.add((Component)valueLabel, "Center");
        return card;
    }

    private JPanel createNotificationPanel() {
        JPanel notifCard = new JPanel(new BorderLayout(6, 8));
        notifCard.setBackground(SURFACE_BG);
        notifCard.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(BORDER, 1), new EmptyBorder(12, 12, 12, 12)));
        JLabel notifTitle = new JLabel("Notification Center");
        notifTitle.setFont(new Font("Segoe UI", 1, 16));
        notifCard.add((Component)notifTitle, "North");
        JScrollPane scroll = new JScrollPane(this.updatesArea);
        scroll.setBorder(BorderFactory.createLineBorder(BORDER, 1));
        notifCard.add((Component)scroll, "Center");
        return notifCard;
    }

    private JPanel createTicketsPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 12));
        panel.setBackground(APP_BG);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        JLabel title = this.createPageHeader("My Tickets");
        JButton viewDetailsButton = new JButton("View Details");
        viewDetailsButton.setBackground(BRAND);
        viewDetailsButton.setForeground(Color.WHITE);
        viewDetailsButton.setFont(LABEL_FONT);
        viewDetailsButton.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        viewDetailsButton.setFocusPainted(false);
        viewDetailsButton.addActionListener(e -> this.showTicketDetailsDialog());
        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.add((Component)title, "West");
        top.add((Component)viewDetailsButton, "East");
        panel.add((Component)top, "North");
        panel.add((Component)this.wrapAsSectionSurface("Ticket List", new JScrollPane(this.ticketsTable)), "Center");
        return panel;
    }

    private JPanel createProfilePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(APP_BG);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        JLabel title = this.createPageHeader("Profile");
        JTextArea profileArea = new JTextArea();
        profileArea.setEditable(false);
        profileArea.setFont(new Font("Segoe UI", 0, 14));
        profileArea.setBackground(SURFACE_BG);
        profileArea.setBorder(new EmptyBorder(12, 12, 12, 12));
        profileArea.setText("Profile Information\n\nFull Name: " + this.currentUser.getFullName() + "\nUsername: " + this.currentUser.getUsername() + "\nEmail: " + this.currentUser.getEmail() + "\nRole: " + this.currentUser.getRole() + "\n");
        panel.add((Component)title, "North");
        panel.add((Component)this.wrapAsSectionSurface("Account Details", new JScrollPane(profileArea)), "Center");
        return panel;
    }

    private void setupEventHandlers() {
        this.dashboardButton.addActionListener(e -> this.showPanel("Dashboard", this.dashboardButton));
        this.myTicketsButton.addActionListener(e -> this.showPanel("Tickets", this.myTicketsButton));
        this.profileButton.addActionListener(e -> this.showPanel("Profile", this.profileButton));
        this.logoutButton.addActionListener(e -> this.logout());
        this.ticketsTable.addMouseListener(new MouseAdapter(){

            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    StudentDashboard.this.showTicketDetailsDialog();
                }
            }
        });
    }

    private void showPanel(String panelName, JButton activeButton) {
        CardLayout cardLayout = (CardLayout)this.contentPanel.getLayout();
        cardLayout.show(this.contentPanel, panelName);
        this.resetSidebarButtons();
        this.setSidebarButtonActive(activeButton, true);
    }

    private void resetSidebarButtons() {
        this.setSidebarButtonActive(this.dashboardButton, false);
        this.setSidebarButtonActive(this.myTicketsButton, false);
        this.setSidebarButtonActive(this.profileButton, false);
        this.setSidebarButtonActive(this.logoutButton, false);
    }

    private void setSidebarButtonActive(JButton button, boolean active) {
        boolean isLogout = Boolean.TRUE.equals(button.getClientProperty("logout"));
        if (isLogout) {
            button.setBackground(new Color(220, 53, 69));
            button.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(185, 28, 28), 1), BorderFactory.createEmptyBorder(12, 16, 12, 16)));
            return;
        }
        button.putClientProperty("active", active);
        button.setBackground(active ? BRAND : new Color(51, 65, 85));
        button.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(active ? BRAND_DARK : new Color(71, 85, 105), 1), BorderFactory.createEmptyBorder(12, 16, 12, 16)));
    }

    private boolean isSidebarButtonActive(JButton button) {
        Object value = button.getClientProperty("active");
        return value instanceof Boolean && (Boolean)value != false;
    }

    private void setupFrame() {
        this.setTitle("User Dashboard - School Helpdesk");
        this.setDefaultCloseOperation(3);
        this.setSize(1300, 820);
        this.setLocationRelativeTo(null);
        this.setExtendedState(6);
        this.showPanel("Dashboard", this.dashboardButton);
    }

    private void loadUserDashboardData() {
        this.loadUserTickets();
        this.updateStatsCards();
        this.updateTicketsTable();
        this.updateNotificationsPanel();
    }

    private void loadUserTickets() {
        try {
            this.cachedUserTickets = this.ticketDAO.getTicketsByUserId(this.currentUser.getUsername());
            if (this.cachedUserTickets.isEmpty()) {
                this.cachedUserTickets = this.ticketDAO.getTicketsByUser(this.currentUser.getUsername(), this.currentUser.getFullName());
            }
        }
        catch (Exception e) {
            this.cachedUserTickets = new ArrayList<Document>();
            System.err.println("Error loading user tickets: " + e.getMessage());
        }
    }

    private void updateStatsCards() {
        int total = this.cachedUserTickets.size();
        int pending = 0;
        int inProgress = 0;
        int resolved = 0;
        for (Document ticket : this.cachedUserTickets) {
            String status = this.normalizeStatus(ticket.getString((Object)"status"));
            if ("pending".equals(status)) {
                ++pending;
                continue;
            }
            if ("in progress".equals(status)) {
                ++inProgress;
                continue;
            }
            if (!"resolved".equals(status)) continue;
            ++resolved;
        }
        this.totalTicketsValueLabel.setText(String.valueOf(total));
        this.pendingTicketsValueLabel.setText(String.valueOf(pending));
        this.inProgressTicketsValueLabel.setText(String.valueOf(inProgress));
        this.resolvedTicketsValueLabel.setText(String.valueOf(resolved));
    }

    private void updateTicketsTable() {
        this.ticketsTableModel.setRowCount(0);
        for (Document ticket : this.cachedUserTickets) {
            String displayStatus = this.toDisplayStatus(ticket.getString((Object)"status"));
            Object[] row = new Object[]{this.fallback(ticket.getString((Object)"ticketId"), "N/A"), this.fallback(ticket.getString((Object)"title"), "Untitled"), this.fallback(ticket.getString((Object)"department"), "Unassigned"), displayStatus, this.resolveCreatedDate(ticket)};
            this.ticketsTableModel.addRow(row);
        }
    }

    private void updateNotificationsPanel() {
        StringBuilder builder = new StringBuilder();
        int updateCount = 0;
        for (Document ticket : this.cachedUserTickets) {
            List notes;
            String status = this.normalizeStatus(ticket.getString((Object)"status"));
            String title = this.fallback(ticket.getString((Object)"title"), "Ticket");
            if ("resolved".equals(status)) {
                builder.append("- Your ticket has been resolved: ").append(title).append("\n");
                ++updateCount;
            } else if ("in progress".equals(status)) {
                builder.append("- Work is in progress for: ").append(title).append("\n");
                ++updateCount;
            }
            if ((notes = (List)ticket.get((Object)"notes")) == null || notes.isEmpty()) continue;
            String latestNote = (String)notes.get(notes.size() - 1);
            builder.append("- Update on ").append(title).append(": ").append(latestNote).append("\n");
            ++updateCount;
        }
        if (builder.length() == 0) {
            builder.append("No new updates yet.");
        }
        this.updatesArea.setText(builder.toString());
        this.notificationIconLabel.setText("Notifications: " + updateCount);
    }

    private String normalizeStatus(String status) {
        if (status == null) {
            return "pending";
        }
        String value = status.trim().toLowerCase();
        if ("open".equals(value) || "pending review".equals(value) || "pending clarification".equals(value)) {
            return "pending";
        }
        if ("assigned".equals(value) || "approved".equals(value)) {
            return "in progress";
        }
        return value;
    }

    private String toDisplayStatus(String status) {
        String normalized;
        switch (normalized = this.normalizeStatus(status)) {
            case "pending": {
                return "Pending";
            }
            case "in progress": {
                return "In Progress";
            }
            case "resolved": {
                return "Resolved";
            }
            case "closed": {
                return "Closed";
            }
        }
        return this.fallback(status, "Pending");
    }

    private String resolveCreatedDate(Document ticket) {
        String createdDate = ticket.getString((Object)"createdDate");
        if (createdDate != null && !createdDate.trim().isEmpty()) {
            return createdDate;
        }
        Date createdAt = ticket.getDate((Object)"createdAt");
        if (createdAt != null) {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm").format(createdAt);
        }
        return "N/A";
    }

    private String fallback(String value, String defaultValue) {
        return value == null || value.trim().isEmpty() ? defaultValue : value;
    }

    private void showCreateTicketDialog() {
        JDialog dialog = new JDialog(this, "Submit New Ticket", true);
        dialog.setSize(680, 500);
        dialog.setLocationRelativeTo(this);
        JTextField titleField = new JTextField();
        JComboBox<String> departmentCombo = new JComboBox<String>(new String[]{"IT Support", "Academic Affairs", "Student Services", "Finance"});
        JComboBox<String> priorityCombo = new JComboBox<String>(new String[]{"Low", "Medium", "High", "Urgent"});
        JTextArea descriptionArea = new JTextArea(5, 20);
        JScrollPane descriptionScroll = new JScrollPane(descriptionArea);
        titleField.setFont(BODY_FONT);
        titleField.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(BORDER, 1), BorderFactory.createEmptyBorder(8, 10, 8, 10)));
        departmentCombo.setFont(BODY_FONT);
        priorityCombo.setFont(BODY_FONT);
        descriptionArea.setFont(BODY_FONT);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
        descriptionScroll.setBorder(BorderFactory.createLineBorder(BORDER, 1));
        this.styleDialogComboBox(departmentCombo);
        this.styleDialogComboBox(priorityCombo);
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(SURFACE_BG);
        form.setBorder(new EmptyBorder(16, 16, 8, 16));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 0, 12, 0);
        gbc.gridx = 0;
        gbc.weightx = 1.0;
        gbc.fill = 2;
        gbc.gridy = 0;
        form.add((Component)this.createDialogLabel("Issue Title"), gbc);
        gbc.gridy = 1;
        form.add((Component)titleField, gbc);
        gbc.gridy = 2;
        form.add((Component)this.createDialogLabel("Department"), gbc);
        gbc.gridy = 3;
        form.add(departmentCombo, gbc);
        gbc.gridy = 4;
        form.add((Component)this.createDialogLabel("Priority"), gbc);
        gbc.gridy = 5;
        form.add(priorityCombo, gbc);
        gbc.gridy = 6;
        form.add((Component)this.createDialogLabel("Description"), gbc);
        gbc.gridy = 7;
        gbc.weighty = 1.0;
        gbc.fill = 1;
        form.add((Component)descriptionScroll, gbc);
        JButton submitButton = new JButton("Submit Ticket");
        JButton cancelButton = new JButton("Cancel");
        submitButton.setBackground(BRAND);
        submitButton.setForeground(Color.WHITE);
        submitButton.setFont(LABEL_FONT);
        submitButton.setBorder(BorderFactory.createEmptyBorder(8, 14, 8, 14));
        submitButton.setFocusPainted(false);
        cancelButton.setFont(LABEL_FONT);
        cancelButton.setBorder(BorderFactory.createEmptyBorder(8, 14, 8, 14));
        JPanel actionButtons = new JPanel(new FlowLayout(2, 8, 12));
        actionButtons.setBackground(SURFACE_BG);
        actionButtons.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER));
        actionButtons.add(submitButton);
        actionButtons.add(cancelButton);
        submitButton.addActionListener(e -> {
            String title = titleField.getText().trim();
            String description = descriptionArea.getText().trim();
            if (title.isEmpty() || description.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Title and description are required.", "Validation", 2);
                return;
            }
            Ticket ticket = new Ticket();
            ticket.setTitle(title);
            ticket.setDescription(description);
            ticket.setDepartment((String)departmentCombo.getSelectedItem());
            ticket.setPriority((String)priorityCombo.getSelectedItem());
            ticket.setStatus("Pending");
            ticket.setCreatedBy(this.currentUser.getUsername());
            ticket.setCreatedDate(new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date()));
            ticket.setNotes(new ArrayList<String>());
            boolean created = this.ticketDAO.createTicket(ticket);
            if (created) {
                JOptionPane.showMessageDialog(dialog, "Ticket submitted successfully.", "Success", 1);
                dialog.dispose();
                this.loadUserDashboardData();
            } else {
                JOptionPane.showMessageDialog(dialog, "Failed to submit ticket.", "Error", 0);
            }
        });
        cancelButton.addActionListener(e -> dialog.dispose());
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(SURFACE_BG);
        header.setBorder(new EmptyBorder(14, 16, 8, 16));
        JLabel title = new JLabel("Submit New Ticket");
        title.setFont(new Font("Segoe UI", 1, 18));
        title.setForeground(TEXT_PRIMARY);
        JLabel subtitle = new JLabel("Provide complete details so staff can assist faster.");
        subtitle.setFont(BODY_FONT);
        subtitle.setForeground(TEXT_MUTED);
        JPanel titleWrap = new JPanel(new GridLayout(0, 1));
        titleWrap.setOpaque(false);
        titleWrap.add(title);
        titleWrap.add(subtitle);
        header.add((Component)titleWrap, "West");
        JPanel container = new JPanel(new BorderLayout());
        container.setBackground(SURFACE_BG);
        container.setBorder(BorderFactory.createLineBorder(BORDER, 1));
        container.add((Component)header, "North");
        container.add((Component)form, "Center");
        container.add((Component)actionButtons, "South");
        dialog.setLayout(new BorderLayout());
        dialog.getContentPane().setBackground(APP_BG);
        dialog.add((Component)container, "Center");
        dialog.setVisible(true);
    }

    private void showTicketDetailsDialog() {
        int selectedRow = this.ticketsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Select a ticket first.", "No Selection", 2);
            return;
        }
        String ticketId = (String)this.ticketsTableModel.getValueAt(selectedRow, 0);
        Document ticket = this.ticketDAO.getTicketById(ticketId);
        if (ticket == null) {
            JOptionPane.showMessageDialog(this, "Ticket details not found.", "Not Found", 2);
            return;
        }
        JDialog dialog = new JDialog(this, "Ticket Details - " + ticketId, true);
        dialog.setSize(720, 540);
        dialog.setLocationRelativeTo(this);
        JTextArea detailsArea = new JTextArea();
        detailsArea.setEditable(false);
        detailsArea.setFont(new Font("Monospaced", 0, 12));
        StringBuilder details = new StringBuilder();
        details.append("Ticket ID: ").append(this.fallback(ticket.getString((Object)"ticketId"), "N/A")).append("\n");
        details.append("Title: ").append(this.fallback(ticket.getString((Object)"title"), "N/A")).append("\n");
        details.append("Department: ").append(this.fallback(ticket.getString((Object)"department"), "N/A")).append("\n");
        details.append("Status: ").append(this.toDisplayStatus(ticket.getString((Object)"status"))).append("\n");
        details.append("Date Submitted: ").append(this.resolveCreatedDate(ticket)).append("\n\n");
        details.append("Full Description:\n");
        details.append(this.fallback(ticket.getString((Object)"description"), "No description provided")).append("\n\n");
        details.append("Admin/Staff Updates:\n");
        List notes = (List)ticket.get((Object)"notes");
        if (notes != null && !notes.isEmpty()) {
            for (Object note : notes) {
                details.append("- ").append(String.valueOf(note)).append("\n");
            }
        } else {
            details.append("- No staff updates yet.\n");
        }
        details.append("\nStatus History:\n");
        details.append("- Current Status: ").append(this.toDisplayStatus(ticket.getString((Object)"status"))).append("\n");
        Date updatedDate = ticket.getDate((Object)"lastUpdated");
        if (updatedDate != null) {
            details.append("- Last Updated: ").append(new SimpleDateFormat("yyyy-MM-dd HH:mm").format(updatedDate)).append("\n");
        }
        detailsArea.setText(details.toString());
        dialog.add(new JScrollPane(detailsArea));
        dialog.setVisible(true);
    }

    private void manualRefresh() {
        this.loadUserDashboardData();
        JOptionPane.showMessageDialog(this, "Dashboard refreshed.", "Refresh", 1);
    }

    /**
     * Ensures a filled green background on common Swing look-and-feels (Metal/Nimbus/Windows).
     */
    private void styleSolidGreenButton(JButton button) {
        button.setBackground(GREEN_SUBMIT);
        button.setForeground(Color.WHITE);
        button.setFont(LABEL_FONT);
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createEmptyBorder(8, 14, 8, 14));
        button.addMouseListener(new MouseAdapter(){

            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(GREEN_SUBMIT_HOVER);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(GREEN_SUBMIT);
            }
        });
    }

    private JLabel createPageHeader(String text) {
        JLabel title = new JLabel(text);
        title.setFont(TITLE_FONT);
        title.setForeground(TEXT_PRIMARY);
        return title;
    }

    private JPanel wrapAsSectionSurface(String titleText, JComponent content) {
        JPanel surface = new JPanel(new BorderLayout(0, 8));
        surface.setBackground(SURFACE_BG);
        surface.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(BORDER, 1), BorderFactory.createEmptyBorder(12, 12, 12, 12)));
        JLabel title = new JLabel(titleText);
        title.setFont(new Font("Segoe UI", 1, 13));
        title.setForeground(TEXT_PRIMARY);
        title.setBorder(new EmptyBorder(0, 2, 4, 2));
        surface.add((Component)title, "North");
        surface.add((Component)content, "Center");
        return surface;
    }

    private JLabel createDialogLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", 1, 12));
        label.setForeground(TEXT_PRIMARY);
        return label;
    }

    private void styleDialogComboBox(JComboBox<String> comboBox) {
        comboBox.setBackground(Color.WHITE);
        comboBox.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(BORDER, 1), BorderFactory.createEmptyBorder(5, 8, 5, 8)));
        comboBox.setPreferredSize(new Dimension(0, 36));
    }

    private void styleModernTable(JTable table) {
        table.setRowHeight(32);
        table.setFont(BODY_FONT);
        table.getTableHeader().setFont(LABEL_FONT);
        table.getTableHeader().setBackground(BRAND_DARK);
        table.getTableHeader().setForeground(Color.WHITE);
        table.getTableHeader().setPreferredSize(new Dimension(0, 38));
        table.getTableHeader().setReorderingAllowed(false);
        table.setSelectionBackground(new Color(219, 234, 254));
        table.setSelectionForeground(TEXT_PRIMARY);
        table.setGridColor(BORDER);
        table.setIntercellSpacing(new Dimension(0, 1));
        table.setRowMargin(0);
        table.setFillsViewportHeight(true);
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer(){

            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(248, 250, 252));
                    c.setForeground(TEXT_PRIMARY);
                }
                this.setBorder(new EmptyBorder(0, 8, 0, 8));
                return c;
            }
        });
    }

    private void startLiveUpdates() {
        this.clockTimer = new Timer(1000, e -> this.dateTimeLabel.setText(new SimpleDateFormat("EEE, MMM dd yyyy HH:mm:ss").format(new Date())));
        this.clockTimer.start();
        this.refreshTimer = new Timer(30000, e -> this.loadUserDashboardData());
        this.refreshTimer.start();
    }

    private void logout() {
        int option = JOptionPane.showConfirmDialog(this, "Are you sure you want to logout?", "Confirm Logout", 0);
        if (option == 0) {
            this.dispose();
            SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
        }
    }

    @Override
    public void dispose() {
        if (this.clockTimer != null) {
            this.clockTimer.stop();
        }
        if (this.refreshTimer != null) {
            this.refreshTimer.stop();
        }
        if (this.ticketDAO != null) {
            this.ticketDAO.close();
        }
        super.dispose();
    }
}

