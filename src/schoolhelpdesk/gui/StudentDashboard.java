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
import javax.swing.Box;
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
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import org.bson.Document;
import schoolhelpdesk.dao.TicketDAO;
import schoolhelpdesk.model.Ticket;
import schoolhelpdesk.model.User;

public class StudentDashboard extends JFrame {
    private static final Color APP_BG = new Color(241, 245, 249);
    private static final Color SURFACE_BG = new Color(255, 255, 255);
    private static final Color BORDER = new Color(226, 232, 240);
    private static final Color BRAND = new Color(37, 99, 235);
    private static final Color BRAND_DARK = new Color(30, 64, 175);
    private static final Color TEXT_PRIMARY = new Color(15, 23, 42);
    private static final Color TEXT_MUTED = new Color(100, 116, 139);
    private static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 24);
    private static final Font BODY_FONT = new Font("Segoe UI", Font.PLAIN, 12);
    private static final Font LABEL_FONT = new Font("Segoe UI", Font.BOLD, 12);

    private final User currentUser;
    private final TicketDAO ticketDAO;
    private List<Document> cachedUserTickets;

    // Header
    private JLabel nameLabel;
    private JLabel dateTimeLabel;
    private JLabel notificationIconLabel;
    private Timer clockTimer;
    private Timer refreshTimer;

    // Sidebar
    private JButton dashboardButton;
    private JButton myTicketsButton;
    private JButton profileButton;
    private JButton logoutButton;

    // Stats cards
    private JLabel totalTicketsValueLabel;
    private JLabel pendingTicketsValueLabel;
    private JLabel inProgressTicketsValueLabel;
    private JLabel resolvedTicketsValueLabel;

    // Tickets table
    private JTable ticketsTable;
    private DefaultTableModel ticketsTableModel;

    // Notifications
    private JTextArea updatesArea;

    // Content
    private JPanel contentPanel;
    private JPanel dashboardPanel;
    private JPanel ticketsPanel;
    private JPanel profilePanel;

    public StudentDashboard(User user) {
        this.currentUser = user;
        this.ticketDAO = new TicketDAO();
        this.cachedUserTickets = new ArrayList<>();

        initializeComponents();
        setupLayout();
        setupEventHandlers();
        setupFrame();
        loadUserDashboardData();
        startLiveUpdates();
    }

    private void initializeComponents() {
        nameLabel = new JLabel("User: " + currentUser.getFullName());
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        nameLabel.setForeground(Color.WHITE);

        dateTimeLabel = new JLabel();
        dateTimeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        dateTimeLabel.setForeground(new Color(220, 235, 255));

        notificationIconLabel = new JLabel("Notifications: 0");
        notificationIconLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        notificationIconLabel.setForeground(new Color(255, 230, 80));

        dashboardButton = createSidebarButton("Dashboard");
        myTicketsButton = createSidebarButton("My Tickets");
        profileButton = createSidebarButton("Profile");
        logoutButton = createSidebarButton("Logout");
        logoutButton.setBackground(new Color(220, 53, 69));

        totalTicketsValueLabel = createValueLabel();
        pendingTicketsValueLabel = createValueLabel();
        inProgressTicketsValueLabel = createValueLabel();
        resolvedTicketsValueLabel = createValueLabel();

        String[] columns = {"Ticket ID", "Issue Title", "Department", "Status", "Date Submitted"};
        ticketsTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        ticketsTable = new JTable(ticketsTableModel);
        ticketsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        styleModernTable(ticketsTable);

        updatesArea = new JTextArea(8, 30);
        updatesArea.setEditable(false);
        updatesArea.setLineWrap(true);
        updatesArea.setWrapStyleWord(true);
        updatesArea.setFont(BODY_FONT);
    }

    private JLabel createValueLabel() {
        JLabel label = new JLabel("0");
        label.setFont(new Font("Segoe UI", Font.BOLD, 30));
        label.setHorizontalAlignment(SwingConstants.CENTER);
        return label;
    }

    private JButton createSidebarButton(String text) {
        JButton button = new JButton(text);
        
        // Match admin sidebar sizing and styling
        boolean isLogout = "Logout".equals(text);
        button.setBackground(isLogout ? new Color(220, 53, 69) : new Color(51, 65, 85));
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(isLogout ? new Color(185, 28, 28) : new Color(71, 85, 105), 1),
            BorderFactory.createEmptyBorder(12, 16, 12, 16)
        ));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setOpaque(true);
        button.setPreferredSize(new Dimension(210, 46));
        button.putClientProperty("active", false);
        button.putClientProperty("logout", isLogout);
        
        // Hover effect that respects active state
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (Boolean.TRUE.equals(button.getClientProperty("logout"))) {
                    button.setBackground(new Color(239, 68, 68));
                } else if (!isSidebarButtonActive(button)) {
                    button.setBackground(new Color(71, 85, 105));
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (Boolean.TRUE.equals(button.getClientProperty("logout"))) {
                    button.setBackground(new Color(220, 53, 69));
                } else if (!isSidebarButtonActive(button)) {
                    button.setBackground(new Color(51, 65, 85));
                }
            }
        });

        return button;
    }

    private void setupLayout() {
        setLayout(new BorderLayout());
        add(createHeaderPanel(), BorderLayout.NORTH);
        add(createSidebarPanel(), BorderLayout.WEST);

        contentPanel = new JPanel(new CardLayout());
        dashboardPanel = createDashboardPanel();
        ticketsPanel = createTicketsPanel();
        profilePanel = createProfilePanel();

        contentPanel.add(dashboardPanel, "Dashboard");
        contentPanel.add(ticketsPanel, "Tickets");
        contentPanel.add(profilePanel, "Profile");
        add(contentPanel, BorderLayout.CENTER);
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BRAND_DARK);
        panel.setBorder(new EmptyBorder(14, 18, 14, 18));

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        left.setOpaque(false);
        left.add(nameLabel);
        left.add(new JLabel("|"));
        left.add(dateTimeLabel);
        left.getComponent(1).setForeground(new Color(180, 210, 240));

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        right.setOpaque(false);
        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> manualRefresh());
        refreshButton.setBackground(BRAND);
        refreshButton.setForeground(Color.WHITE);
        refreshButton.setFont(LABEL_FONT);
        refreshButton.setBorder(BorderFactory.createEmptyBorder(7, 12, 7, 12));
        refreshButton.setFocusPainted(false);
        refreshButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        right.add(refreshButton);
        right.add(notificationIconLabel);

        panel.add(left, BorderLayout.WEST);
        panel.add(right, BorderLayout.EAST);
        return panel;
    }

    private JPanel createSidebarPanel() {
        JPanel panel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Gradient background
                GradientPaint gradient = new GradientPaint(0, 0, new Color(10, 25, 47), 0, getHeight(), new Color(15, 23, 42));
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
                
                g2d.dispose();
            }
        };
        panel.setPreferredSize(new Dimension(260, 0));
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(0, 102, 204)),
            BorderFactory.createEmptyBorder(0, 0, 0, 0)
        ));
        
        // Logo/Title area
        JPanel logoPanel = new JPanel(new BorderLayout());
        logoPanel.setBackground(new Color(0, 0, 0, 0));
        logoPanel.setBorder(BorderFactory.createEmptyBorder(30, 20, 20, 20));
        
        JLabel logoLabel = new JLabel("SH");
        logoLabel.setFont(new Font("Segoe UI", Font.BOLD, 34));
        logoLabel.setForeground(new Color(191, 219, 254));
        logoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        JLabel systemLabel = new JLabel("School Helpdesk", SwingConstants.CENTER);
        systemLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        systemLabel.setForeground(Color.WHITE);
        systemLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        JLabel subtitleLabel = new JLabel("Student Portal", SwingConstants.CENTER);
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        subtitleLabel.setForeground(new Color(148, 163, 184));
        subtitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        JPanel logoTextPanel = new JPanel(new BorderLayout());
        logoTextPanel.setOpaque(false);
        logoTextPanel.add(logoLabel, BorderLayout.NORTH);
        logoTextPanel.add(systemLabel, BorderLayout.CENTER);
        logoTextPanel.add(subtitleLabel, BorderLayout.SOUTH);
        
        logoPanel.add(logoTextPanel, BorderLayout.CENTER);
        
        // Modern Navigation buttons with enhanced layout
        JPanel navPanel = new JPanel(new GridLayout(0, 1, 0, 10));
        navPanel.setBackground(new Color(15, 23, 42));
        navPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(30, 41, 59), 1),
            BorderFactory.createEmptyBorder(20, 15, 20, 15)
        ));
        
        navPanel.add(dashboardButton);
        navPanel.add(myTicketsButton);
        navPanel.add(profileButton);
        
        // Modern User info panel
        JPanel userInfoPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Gradient background
                GradientPaint gradient = new GradientPaint(0, 0, new Color(0, 0, 0, 0), 0, getHeight(), new Color(20, 20, 20));
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
                
                g2d.dispose();
            }
        };
        userInfoPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        
        JLabel userLabel = new JLabel(currentUser.getFullName());
        userLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        userLabel.setForeground(Color.WHITE);
        userLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        JLabel roleLabel = new JLabel("Student");
        roleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        roleLabel.setForeground(new Color(200, 200, 200));
        roleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        JPanel userInfoTextPanel = new JPanel(new BorderLayout());
        userInfoTextPanel.setOpaque(false);
        userInfoTextPanel.add(userLabel, BorderLayout.CENTER);
        userInfoTextPanel.add(roleLabel, BorderLayout.SOUTH);
        
        userInfoPanel.add(userInfoTextPanel, BorderLayout.CENTER);
        userInfoPanel.add(logoutButton, BorderLayout.SOUTH);
        
        panel.add(logoPanel, BorderLayout.NORTH);
        panel.add(navPanel, BorderLayout.CENTER);
        panel.add(userInfoPanel, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createDashboardPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 14));
        panel.setBackground(APP_BG);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel heading = createPageHeader("Student Dashboard");
        JButton submitTicketTopButton = new JButton("Submit New Ticket");
        submitTicketTopButton.setBackground(new Color(34, 139, 34));
        submitTicketTopButton.setForeground(Color.WHITE);
        submitTicketTopButton.setFont(LABEL_FONT);
        submitTicketTopButton.setBorder(BorderFactory.createEmptyBorder(8, 14, 8, 14));
        submitTicketTopButton.setFocusPainted(false);
        submitTicketTopButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        submitTicketTopButton.addActionListener(e -> showCreateTicketDialog());

        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setOpaque(false);
        topBar.add(heading, BorderLayout.WEST);
        topBar.add(submitTicketTopButton, BorderLayout.EAST);
        panel.add(topBar, BorderLayout.NORTH);

        JPanel center = new JPanel(new GridLayout(2, 1, 12, 12));
        center.setOpaque(false);
        center.add(createStatsPanel());
        center.add(createActionAndNotificationPanel());

        panel.add(center, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createStatsPanel() {
        JPanel stats = new JPanel(new GridLayout(1, 4, 12, 12));
        stats.setOpaque(false);
        stats.add(createStatCard("Total Tickets", totalTicketsValueLabel, BRAND));
        stats.add(createStatCard("Pending", pendingTicketsValueLabel, new Color(245, 158, 11)));
        stats.add(createStatCard("In Progress", inProgressTicketsValueLabel, new Color(6, 182, 212)));
        stats.add(createStatCard("Resolved", resolvedTicketsValueLabel, new Color(22, 163, 74)));
        return stats;
    }

    private JPanel createStatCard(String labelText, JLabel valueLabel, Color accent) {
        JPanel card = new JPanel(new BorderLayout(0, 8));
        card.setBackground(SURFACE_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER, 1),
            new EmptyBorder(12, 12, 12, 12)
        ));
        JLabel label = new JLabel(labelText, SwingConstants.CENTER);
        label.setFont(new Font("Segoe UI", Font.BOLD, 13));
        label.setForeground(TEXT_PRIMARY);
        valueLabel.setForeground(accent);
        card.add(label, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        return card;
    }

    private JPanel createActionAndNotificationPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 2, 12, 12));
        panel.setOpaque(false);

        JPanel actionsCard = new JPanel(new BorderLayout(10, 10));
        actionsCard.setBackground(SURFACE_BG);
        actionsCard.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER, 1),
            new EmptyBorder(16, 16, 16, 16)
        ));
        JLabel actionsTitle = new JLabel("Quick Action");
        actionsTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        JLabel actionsDescription = new JLabel("<html>Use the <b>Submit New Ticket</b> button at the top to create a request quickly.</html>");
        actionsDescription.setFont(BODY_FONT);
        actionsDescription.setForeground(TEXT_MUTED);
        actionsCard.add(actionsTitle, BorderLayout.NORTH);
        actionsCard.add(actionsDescription, BorderLayout.CENTER);

        JPanel notifCard = new JPanel(new BorderLayout(6, 8));
        notifCard.setBackground(SURFACE_BG);
        notifCard.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER, 1),
            new EmptyBorder(12, 12, 12, 12)
        ));
        JLabel notifTitle = new JLabel("Notification Center");
        notifTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        notifCard.add(notifTitle, BorderLayout.NORTH);
        notifCard.add(new JScrollPane(updatesArea), BorderLayout.CENTER);

        panel.add(actionsCard);
        panel.add(notifCard);
        return panel;
    }

    private JPanel createTicketsPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 12));
        panel.setBackground(APP_BG);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel title = createPageHeader("My Tickets");

        JButton viewDetailsButton = new JButton("View Details");
        viewDetailsButton.setBackground(BRAND);
        viewDetailsButton.setForeground(Color.WHITE);
        viewDetailsButton.setFont(LABEL_FONT);
        viewDetailsButton.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        viewDetailsButton.setFocusPainted(false);
        viewDetailsButton.addActionListener(e -> showTicketDetailsDialog());

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.add(title, BorderLayout.WEST);
        top.add(viewDetailsButton, BorderLayout.EAST);

        panel.add(top, BorderLayout.NORTH);
        panel.add(wrapAsSectionSurface("Ticket List", new JScrollPane(ticketsTable)), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createProfilePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(APP_BG);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        JLabel title = createPageHeader("Profile");

        JTextArea profileArea = new JTextArea();
        profileArea.setEditable(false);
        profileArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        profileArea.setBackground(SURFACE_BG);
        profileArea.setBorder(new EmptyBorder(12, 12, 12, 12));
        profileArea.setText(
            "Profile Information\n\n" +
            "Full Name: " + currentUser.getFullName() + "\n" +
            "Username: " + currentUser.getUsername() + "\n" +
            "Email: " + currentUser.getEmail() + "\n" +
            "Role: " + currentUser.getRole() + "\n"
        );
        panel.add(title, BorderLayout.NORTH);
        panel.add(wrapAsSectionSurface("Account Details", new JScrollPane(profileArea)), BorderLayout.CENTER);
        return panel;
    }

    private void setupEventHandlers() {
        dashboardButton.addActionListener(e -> showPanel("Dashboard", dashboardButton));
        myTicketsButton.addActionListener(e -> showPanel("Tickets", myTicketsButton));
        profileButton.addActionListener(e -> showPanel("Profile", profileButton));
        logoutButton.addActionListener(e -> logout());

        ticketsTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    showTicketDetailsDialog();
                }
            }
        });
    }

    private void showPanel(String panelName, JButton activeButton) {
        CardLayout cardLayout = (CardLayout) contentPanel.getLayout();
        cardLayout.show(contentPanel, panelName);
        resetSidebarButtons();
        setSidebarButtonActive(activeButton, true);
    }

    private void resetSidebarButtons() {
        setSidebarButtonActive(dashboardButton, false);
        setSidebarButtonActive(myTicketsButton, false);
        setSidebarButtonActive(profileButton, false);
        setSidebarButtonActive(logoutButton, false);
    }

    private void setSidebarButtonActive(JButton button, boolean active) {
        boolean isLogout = Boolean.TRUE.equals(button.getClientProperty("logout"));
        if (isLogout) {
            button.setBackground(new Color(220, 53, 69));
            button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(185, 28, 28), 1),
                BorderFactory.createEmptyBorder(12, 16, 12, 16)
            ));
            return;
        }
        button.putClientProperty("active", active);
        button.setBackground(active ? BRAND : new Color(51, 65, 85));
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(active ? BRAND_DARK : new Color(71, 85, 105), 1),
            BorderFactory.createEmptyBorder(12, 16, 12, 16)
        ));
    }

    private boolean isSidebarButtonActive(JButton button) {
        Object value = button.getClientProperty("active");
        return value instanceof Boolean && (Boolean) value;
    }

    private void setupFrame() {
        setTitle("User Dashboard - School Helpdesk");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1300, 820);
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        showPanel("Dashboard", dashboardButton);
    }

    private void loadUserDashboardData() {
        loadUserTickets();
        updateStatsCards();
        updateTicketsTable();
        updateNotificationsPanel();
    }

    private void loadUserTickets() {
        try {
            cachedUserTickets = ticketDAO.getTicketsByUserId(currentUser.getUsername());
            if (cachedUserTickets.isEmpty()) {
                cachedUserTickets = ticketDAO.getTicketsByUser(currentUser.getUsername(), currentUser.getFullName());
            }
        } catch (Exception e) {
            cachedUserTickets = new ArrayList<>();
            System.err.println("Error loading user tickets: " + e.getMessage());
        }
    }

    private void updateStatsCards() {
        int total = cachedUserTickets.size();
        int pending = 0;
        int inProgress = 0;
        int resolved = 0;

        for (Document ticket : cachedUserTickets) {
            String status = normalizeStatus(ticket.getString("status"));
            if ("pending".equals(status)) {
                pending++;
            } else if ("in progress".equals(status)) {
                inProgress++;
            } else if ("resolved".equals(status)) {
                resolved++;
            }
        }

        totalTicketsValueLabel.setText(String.valueOf(total));
        pendingTicketsValueLabel.setText(String.valueOf(pending));
        inProgressTicketsValueLabel.setText(String.valueOf(inProgress));
        resolvedTicketsValueLabel.setText(String.valueOf(resolved));
    }

    private void updateTicketsTable() {
        ticketsTableModel.setRowCount(0);
        for (Document ticket : cachedUserTickets) {
            String displayStatus = toDisplayStatus(ticket.getString("status"));
            Object[] row = {
                fallback(ticket.getString("ticketId"), "N/A"),
                fallback(ticket.getString("title"), "Untitled"),
                fallback(ticket.getString("department"), "Unassigned"),
                displayStatus,
                resolveCreatedDate(ticket)
            };
            ticketsTableModel.addRow(row);
        }
    }

    private void updateNotificationsPanel() {
        StringBuilder builder = new StringBuilder();
        int updateCount = 0;

        for (Document ticket : cachedUserTickets) {
            String status = normalizeStatus(ticket.getString("status"));
            String title = fallback(ticket.getString("title"), "Ticket");
            if ("resolved".equals(status)) {
                builder.append("- Your ticket has been resolved: ").append(title).append("\n");
                updateCount++;
            } else if ("in progress".equals(status)) {
                builder.append("- Work is in progress for: ").append(title).append("\n");
                updateCount++;
            }

            @SuppressWarnings("unchecked")
            List<String> notes = (List<String>) ticket.get("notes");
            if (notes != null && !notes.isEmpty()) {
                String latestNote = notes.get(notes.size() - 1);
                builder.append("- Update on ").append(title).append(": ").append(latestNote).append("\n");
                updateCount++;
            }
        }

        if (builder.length() == 0) {
            builder.append("No new updates yet.");
        }
        updatesArea.setText(builder.toString());
        notificationIconLabel.setText("Notifications: " + updateCount);
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
        String normalized = normalizeStatus(status);
        switch (normalized) {
            case "pending":
                return "Pending";
            case "in progress":
                return "In Progress";
            case "resolved":
                return "Resolved";
            case "closed":
                return "Closed";
            default:
                return fallback(status, "Pending");
        }
    }

    private String resolveCreatedDate(Document ticket) {
        String createdDate = ticket.getString("createdDate");
        if (createdDate != null && !createdDate.trim().isEmpty()) {
            return createdDate;
        }
        Date createdAt = ticket.getDate("createdAt");
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
        JComboBox<String> departmentCombo = new JComboBox<>(
            new String[]{"IT Support", "Academic Affairs", "Student Services", "Finance"}
        );
        JComboBox<String> priorityCombo = new JComboBox<>(
            new String[]{"Low", "Medium", "High", "Urgent"}
        );
        JTextArea descriptionArea = new JTextArea(5, 20);
        JScrollPane descriptionScroll = new JScrollPane(descriptionArea);
        
        titleField.setFont(BODY_FONT);
        titleField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER, 1),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        departmentCombo.setFont(BODY_FONT);
        priorityCombo.setFont(BODY_FONT);
        descriptionArea.setFont(BODY_FONT);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
        descriptionScroll.setBorder(BorderFactory.createLineBorder(BORDER, 1));
        styleDialogComboBox(departmentCombo);
        styleDialogComboBox(priorityCombo);
        
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(SURFACE_BG);
        form.setBorder(new EmptyBorder(16, 16, 8, 16));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 0, 12, 0);
        gbc.gridx = 0;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        gbc.gridy = 0;
        form.add(createDialogLabel("Issue Title"), gbc);
        gbc.gridy = 1;
        form.add(titleField, gbc);
        
        gbc.gridy = 2;
        form.add(createDialogLabel("Department"), gbc);
        gbc.gridy = 3;
        form.add(departmentCombo, gbc);
        
        gbc.gridy = 4;
        form.add(createDialogLabel("Priority"), gbc);
        gbc.gridy = 5;
        form.add(priorityCombo, gbc);
        
        gbc.gridy = 6;
        form.add(createDialogLabel("Description"), gbc);
        gbc.gridy = 7;
        gbc.weighty = 1;
        gbc.fill = GridBagConstraints.BOTH;
        form.add(descriptionScroll, gbc);

        JButton submitButton = new JButton("Submit Ticket");
        JButton cancelButton = new JButton("Cancel");
        submitButton.setBackground(BRAND);
        submitButton.setForeground(Color.WHITE);
        submitButton.setFont(LABEL_FONT);
        submitButton.setBorder(BorderFactory.createEmptyBorder(8, 14, 8, 14));
        submitButton.setFocusPainted(false);
        cancelButton.setFont(LABEL_FONT);
        cancelButton.setBorder(BorderFactory.createEmptyBorder(8, 14, 8, 14));
        
        JPanel actionButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 12));
        actionButtons.setBackground(SURFACE_BG);
        actionButtons.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER));
        actionButtons.add(submitButton);
        actionButtons.add(cancelButton);

        submitButton.addActionListener(e -> {
            String title = titleField.getText().trim();
            String description = descriptionArea.getText().trim();
            if (title.isEmpty() || description.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Title and description are required.", "Validation", JOptionPane.WARNING_MESSAGE);
                return;
            }

            Ticket ticket = new Ticket();
            ticket.setTitle(title);
            ticket.setDescription(description);
            ticket.setDepartment((String) departmentCombo.getSelectedItem());
            ticket.setPriority((String) priorityCombo.getSelectedItem());
            ticket.setStatus("Pending");
            ticket.setCreatedBy(currentUser.getUsername());
            ticket.setCreatedDate(new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date()));
            ticket.setNotes(new ArrayList<>());

            boolean created = ticketDAO.createTicket(ticket);
            if (created) {
                JOptionPane.showMessageDialog(dialog, "Ticket submitted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
                loadUserDashboardData();
            } else {
                JOptionPane.showMessageDialog(dialog, "Failed to submit ticket.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(SURFACE_BG);
        header.setBorder(new EmptyBorder(14, 16, 8, 16));
        JLabel title = new JLabel("Submit New Ticket");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(TEXT_PRIMARY);
        JLabel subtitle = new JLabel("Provide complete details so staff can assist faster.");
        subtitle.setFont(BODY_FONT);
        subtitle.setForeground(TEXT_MUTED);
        JPanel titleWrap = new JPanel(new GridLayout(0, 1));
        titleWrap.setOpaque(false);
        titleWrap.add(title);
        titleWrap.add(subtitle);
        header.add(titleWrap, BorderLayout.WEST);

        JPanel container = new JPanel(new BorderLayout());
        container.setBackground(SURFACE_BG);
        container.setBorder(BorderFactory.createLineBorder(BORDER, 1));
        container.add(header, BorderLayout.NORTH);
        container.add(form, BorderLayout.CENTER);
        container.add(actionButtons, BorderLayout.SOUTH);
        
        dialog.setLayout(new BorderLayout());
        dialog.getContentPane().setBackground(APP_BG);
        dialog.add(container, BorderLayout.CENTER);
        dialog.setVisible(true);
    }

    private void showTicketDetailsDialog() {
        int selectedRow = ticketsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Select a ticket first.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String ticketId = (String) ticketsTableModel.getValueAt(selectedRow, 0);
        Document ticket = ticketDAO.getTicketById(ticketId);
        if (ticket == null) {
            JOptionPane.showMessageDialog(this, "Ticket details not found.", "Not Found", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JDialog dialog = new JDialog(this, "Ticket Details - " + ticketId, true);
        dialog.setSize(720, 540);
        dialog.setLocationRelativeTo(this);

        JTextArea detailsArea = new JTextArea();
        detailsArea.setEditable(false);
        detailsArea.setFont(new Font("Monospaced", Font.PLAIN, 12));

        StringBuilder details = new StringBuilder();
        details.append("Ticket ID: ").append(fallback(ticket.getString("ticketId"), "N/A")).append("\n");
        details.append("Title: ").append(fallback(ticket.getString("title"), "N/A")).append("\n");
        details.append("Department: ").append(fallback(ticket.getString("department"), "N/A")).append("\n");
        details.append("Status: ").append(toDisplayStatus(ticket.getString("status"))).append("\n");
        details.append("Date Submitted: ").append(resolveCreatedDate(ticket)).append("\n\n");
        details.append("Full Description:\n");
        details.append(fallback(ticket.getString("description"), "No description provided")).append("\n\n");
        details.append("Admin/Staff Updates:\n");

        @SuppressWarnings("unchecked")
        List<String> notes = (List<String>) ticket.get("notes");
        if (notes != null && !notes.isEmpty()) {
            for (String note : notes) {
                details.append("- ").append(note).append("\n");
            }
        } else {
            details.append("- No staff updates yet.\n");
        }

        details.append("\nStatus History:\n");
        details.append("- Current Status: ").append(toDisplayStatus(ticket.getString("status"))).append("\n");
        Date updatedDate = ticket.getDate("lastUpdated");
        if (updatedDate != null) {
            details.append("- Last Updated: ").append(new SimpleDateFormat("yyyy-MM-dd HH:mm").format(updatedDate)).append("\n");
        }

        detailsArea.setText(details.toString());
        dialog.add(new JScrollPane(detailsArea));
        dialog.setVisible(true);
    }

    private void manualRefresh() {
        loadUserDashboardData();
        JOptionPane.showMessageDialog(this, "Dashboard refreshed.", "Refresh", JOptionPane.INFORMATION_MESSAGE);
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
        surface.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER, 1),
            BorderFactory.createEmptyBorder(12, 12, 12, 12)
        ));
        JLabel title = new JLabel(titleText);
        title.setFont(new Font("Segoe UI", Font.BOLD, 13));
        title.setForeground(TEXT_PRIMARY);
        title.setBorder(new EmptyBorder(0, 2, 4, 2));
        surface.add(title, BorderLayout.NORTH);
        surface.add(content, BorderLayout.CENTER);
        return surface;
    }
    
    private JLabel createDialogLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 12));
        label.setForeground(TEXT_PRIMARY);
        return label;
    }
    
    private void styleDialogComboBox(JComboBox<String> comboBox) {
        comboBox.setBackground(Color.WHITE);
        comboBox.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER, 1),
            BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
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
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                           boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(248, 250, 252));
                    c.setForeground(TEXT_PRIMARY);
                }
                setBorder(new EmptyBorder(0, 8, 0, 8));
                return c;
            }
        });
    }

    private void startLiveUpdates() {
        clockTimer = new Timer(1000, e -> dateTimeLabel.setText(new SimpleDateFormat("EEE, MMM dd yyyy HH:mm:ss").format(new Date())));
        clockTimer.start();

        refreshTimer = new Timer(30000, e -> loadUserDashboardData());
        refreshTimer.start();
    }

    private void logout() {
        int option = JOptionPane.showConfirmDialog(this, "Are you sure you want to logout?", "Confirm Logout", JOptionPane.YES_NO_OPTION);
        if (option == JOptionPane.YES_OPTION) {
            dispose();
            SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
        }
    }

    @Override
    public void dispose() {
        if (clockTimer != null) {
            clockTimer.stop();
        }
        if (refreshTimer != null) {
            refreshTimer.stop();
        }
        if (ticketDAO != null) {
            ticketDAO.close();
        }
        super.dispose();
    }
}
