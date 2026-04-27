package schoolhelpdesk.gui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
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
import org.bson.Document;
import schoolhelpdesk.dao.TicketDAO;
import schoolhelpdesk.model.Ticket;
import schoolhelpdesk.model.User;

public class StudentDashboard extends JFrame {
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
        nameLabel.setFont(new Font("Arial", Font.BOLD, 16));
        nameLabel.setForeground(Color.WHITE);

        dateTimeLabel = new JLabel();
        dateTimeLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        dateTimeLabel.setForeground(new Color(220, 235, 255));

        notificationIconLabel = new JLabel("🔔 0 updates");
        notificationIconLabel.setFont(new Font("Arial", Font.BOLD, 12));
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
        ticketsTable.setRowHeight(28);
        ticketsTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        ticketsTable.getTableHeader().setBackground(new Color(0, 81, 153));
        ticketsTable.getTableHeader().setForeground(Color.WHITE);
        ticketsTable.setSelectionBackground(new Color(198, 227, 255));

        updatesArea = new JTextArea(8, 30);
        updatesArea.setEditable(false);
        updatesArea.setLineWrap(true);
        updatesArea.setWrapStyleWord(true);
        updatesArea.setFont(new Font("Arial", Font.PLAIN, 12));
    }

    private JLabel createValueLabel() {
        JLabel label = new JLabel("0");
        label.setFont(new Font("Arial", Font.BOLD, 30));
        label.setHorizontalAlignment(SwingConstants.CENTER);
        return label;
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
            @Override
            public void mouseEntered(MouseEvent e) {
                if (button != dashboardButton || !button.getBackground().equals(new Color(0, 102, 204))) {
                    button.setBackground(new Color(58, 58, 76));
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (!button.getBackground().equals(new Color(0, 102, 204)) && button != logoutButton) {
                    button.setBackground(new Color(40, 40, 52));
                }
                if (button == logoutButton) {
                    button.setBackground(new Color(160, 35, 45));
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
        panel.setBackground(new Color(0, 51, 102));
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
        
        JLabel logoLabel = new JLabel("🏫");
        logoLabel.setFont(new Font("Arial", Font.PLAIN, 32));
        logoLabel.setForeground(new Color(0, 102, 204));
        logoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        JLabel systemLabel = new JLabel("School Helpdesk", SwingConstants.CENTER);
        systemLabel.setFont(new Font("Arial", Font.BOLD, 16));
        systemLabel.setForeground(Color.WHITE);
        systemLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        JLabel subtitleLabel = new JLabel("Student Portal", SwingConstants.CENTER);
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 10));
        subtitleLabel.setForeground(new Color(200, 200, 200));
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
        panel.setBackground(new Color(240, 248, 255));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel heading = new JLabel("User Dashboard");
        heading.setFont(new Font("Arial", Font.BOLD, 24));
        heading.setForeground(new Color(0, 61, 122));
        panel.add(heading, BorderLayout.NORTH);

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
        stats.add(createStatCard("Total Tickets Submitted", totalTicketsValueLabel, new Color(0, 102, 204)));
        stats.add(createStatCard("Tickets Pending", pendingTicketsValueLabel, new Color(255, 140, 0)));
        stats.add(createStatCard("Tickets In Progress", inProgressTicketsValueLabel, new Color(65, 105, 225)));
        stats.add(createStatCard("Tickets Resolved", resolvedTicketsValueLabel, new Color(34, 139, 34)));
        return stats;
    }

    private JPanel createStatCard(String labelText, JLabel valueLabel, Color accent) {
        JPanel card = new JPanel(new BorderLayout(0, 8));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(accent, 2),
            new EmptyBorder(12, 12, 12, 12)
        ));
        JLabel label = new JLabel(labelText, SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 13));
        label.setForeground(accent);
        valueLabel.setForeground(accent);
        card.add(label, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        return card;
    }

    private JPanel createActionAndNotificationPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 2, 12, 12));
        panel.setOpaque(false);

        JPanel actionsCard = new JPanel(new BorderLayout(10, 10));
        actionsCard.setBackground(Color.WHITE);
        actionsCard.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0, 102, 204), 2),
            new EmptyBorder(16, 16, 16, 16)
        ));
        JLabel actionsTitle = new JLabel("Quick Action");
        actionsTitle.setFont(new Font("Arial", Font.BOLD, 16));
        JButton submitTicketButton = new JButton("Submit New Ticket");
        submitTicketButton.setFont(new Font("Arial", Font.BOLD, 14));
        submitTicketButton.setFocusPainted(false);
        submitTicketButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        submitTicketButton.addActionListener(e -> showCreateTicketDialog());
        actionsCard.add(actionsTitle, BorderLayout.NORTH);
        actionsCard.add(submitTicketButton, BorderLayout.CENTER);

        JPanel notifCard = new JPanel(new BorderLayout(6, 8));
        notifCard.setBackground(Color.WHITE);
        notifCard.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(255, 166, 0), 2),
            new EmptyBorder(12, 12, 12, 12)
        ));
        JLabel notifTitle = new JLabel("Notification Panel");
        notifTitle.setFont(new Font("Arial", Font.BOLD, 16));
        notifCard.add(notifTitle, BorderLayout.NORTH);
        notifCard.add(new JScrollPane(updatesArea), BorderLayout.CENTER);

        panel.add(actionsCard);
        panel.add(notifCard);
        return panel;
    }

    private JPanel createTicketsPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 12));
        panel.setBackground(new Color(240, 248, 255));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel title = new JLabel("My Tickets");
        title.setFont(new Font("Arial", Font.BOLD, 24));
        title.setForeground(new Color(0, 61, 122));

        JButton viewDetailsButton = new JButton("View Details");
        viewDetailsButton.setFocusPainted(false);
        viewDetailsButton.addActionListener(e -> showTicketDetailsDialog());

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.add(title, BorderLayout.WEST);
        top.add(viewDetailsButton, BorderLayout.EAST);

        panel.add(top, BorderLayout.NORTH);
        panel.add(new JScrollPane(ticketsTable), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createProfilePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(240, 248, 255));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JTextArea profileArea = new JTextArea();
        profileArea.setEditable(false);
        profileArea.setFont(new Font("Arial", Font.PLAIN, 14));
        profileArea.setText(
            "Profile Information\n\n" +
            "Full Name: " + currentUser.getFullName() + "\n" +
            "Username: " + currentUser.getUsername() + "\n" +
            "Email: " + currentUser.getEmail() + "\n" +
            "Role: " + currentUser.getRole() + "\n"
        );
        panel.add(new JScrollPane(profileArea), BorderLayout.CENTER);
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
        activeButton.setBackground(new Color(0, 102, 204));
        if (activeButton == logoutButton) {
            activeButton.setBackground(new Color(160, 35, 45));
        }
    }

    private void resetSidebarButtons() {
        dashboardButton.setBackground(new Color(40, 40, 52));
        myTicketsButton.setBackground(new Color(40, 40, 52));
        profileButton.setBackground(new Color(40, 40, 52));
        logoutButton.setBackground(new Color(160, 35, 45));
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
        notificationIconLabel.setText("🔔 " + updateCount + " updates");
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
        dialog.setSize(620, 430);
        dialog.setLocationRelativeTo(this);

        JPanel form = new JPanel(new GridLayout(0, 2, 10, 10));
        form.setBorder(new EmptyBorder(16, 16, 16, 16));

        JTextField titleField = new JTextField();
        JComboBox<String> departmentCombo = new JComboBox<>(
            new String[]{"IT Support", "Academic Affairs", "Student Services", "Finance"}
        );
        JComboBox<String> priorityCombo = new JComboBox<>(
            new String[]{"Low", "Medium", "High", "Urgent"}
        );
        JTextArea descriptionArea = new JTextArea(5, 20);
        JScrollPane descriptionScroll = new JScrollPane(descriptionArea);

        form.add(new JLabel("Issue Title:"));
        form.add(titleField);
        form.add(new JLabel("Department:"));
        form.add(departmentCombo);
        form.add(new JLabel("Priority:"));
        form.add(priorityCombo);
        form.add(new JLabel("Description:"));
        form.add(descriptionScroll);

        JButton submitButton = new JButton("Submit Ticket");
        JButton cancelButton = new JButton("Cancel");
        JPanel actionButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
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

        dialog.setLayout(new BorderLayout());
        dialog.add(form, BorderLayout.CENTER);
        dialog.add(actionButtons, BorderLayout.SOUTH);
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
