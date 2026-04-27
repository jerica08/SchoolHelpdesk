package schoolhelpdesk.gui;

import schoolhelpdesk.model.User;
import schoolhelpdesk.dao.TicketDAO;
import schoolhelpdesk.dao.UserDAO;
import schoolhelpdesk.dao.DepartmentDAO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import org.bson.Document;
import schoolhelpdesk.model.Department;

public class BeautifulAdminDashboard extends JFrame {
    private User currentUser;
    private TicketDAO ticketDAO;
    private UserDAO userDAO;
    private DepartmentDAO departmentDAO;
    
    // Header components
    private JLabel adminNameLabel;
    private JLabel departmentLabel;
    private JLabel notificationsLabel;
    
    // Sidebar components
    private JPanel sidebarPanel;
    private JButton dashboardButton;
    private JButton ticketsButton;
    private JButton usersButton;
    private JButton departmentsButton;
    private JButton reportsButton;
    private JButton logoutButton;
    
    // Content panels
    private JPanel contentPanel;
    private JPanel overviewPanel;
    private JPanel ticketsPanel;
    private JPanel usersPanel;
    private JPanel departmentsPanel;
    private JPanel reportsPanel;
    
    // Overview components
    private JLabel totalTicketsLabel;
    private JLabel pendingReviewLabel;
    private JLabel assignedTicketsLabel;
    private JLabel resolvedTicketsLabel;
    
    // Tickets components
    private JTable ticketsTable;
    private DefaultTableModel ticketsTableModel;
    private JButton approveButton;
    private JButton rejectButton;
    private JButton redirectButton;
    private JButton clarifyButton;
    private JButton assignButton;
    private JComboBox<String> staffComboBox;
    private JComboBox<String> departmentFilterComboBox;
    
    public BeautifulAdminDashboard(User user) {
        this.currentUser = user;
        
        // Initialize DAOs
        try {
            this.ticketDAO = new TicketDAO();
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
        loadInitialData();
        setupFrame();
    }
    
    private void initializeComponents() {
        // Header components
        adminNameLabel = new JLabel("👤 " + currentUser.getFullName());
        adminNameLabel.setFont(new Font("Arial", Font.BOLD, 16));
        adminNameLabel.setForeground(Color.WHITE);
        
        departmentLabel = new JLabel("🏢 " + currentUser.getRole());
        departmentLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        departmentLabel.setForeground(new Color(173, 216, 230));
        
        notificationsLabel = new JLabel("🔔 3 new notifications");
        notificationsLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        notificationsLabel.setForeground(Color.YELLOW);
        
        // Sidebar buttons
        dashboardButton = new JButton("📊 Dashboard");
        ticketsButton = new JButton("🎫 Tickets");
        usersButton = new JButton("👥 Users");
        departmentsButton = new JButton("🏢 Departments");
        reportsButton = new JButton("📈 Reports");
        logoutButton = new JButton("🚪 Logout");
        
        // Overview labels
        totalTicketsLabel = new JLabel("0");
        pendingReviewLabel = new JLabel("0");
        assignedTicketsLabel = new JLabel("0");
        resolvedTicketsLabel = new JLabel("0");
        
        // Tickets table
        String[] columns = {"Ticket ID", "User", "Title", "Department", "Status", "Date"};
        ticketsTableModel = new DefaultTableModel(columns, 0);
        ticketsTable = new JTable(ticketsTableModel);
        
        // Action buttons
        approveButton = new JButton("Approve");
        rejectButton = new JButton("Reject");
        redirectButton = new JButton("Redirect");
        clarifyButton = new JButton("Request Info");
        assignButton = new JButton("Assign");
        
        // Staff combo box
        staffComboBox = new JComboBox<>();
        staffComboBox.setPreferredSize(new Dimension(200, 30));
        
        // Department filter combo box
        departmentFilterComboBox = new JComboBox<>();
        departmentFilterComboBox.setPreferredSize(new Dimension(150, 30));
    }
    
    private JButton createSidebarButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(new Color(30, 30, 30));
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Arial", Font.BOLD, 13));
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setPreferredSize(new Dimension(220, 45));
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(60, 60, 60)),
            BorderFactory.createEmptyBorder(0, 15, 0, 0)
        ));
        
        // Add hover effect
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) {
                button.setBackground(new Color(0, 102, 204));
                button.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }
            public void mouseExited(MouseEvent evt) {
                if (!button.getText().contains("Dashboard")) {
                    button.setBackground(new Color(30, 30, 30));
                }
                button.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
        });
        
        return button;
    }
    
    private void createSidebarPanel() {
        sidebarPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                
                // Create gradient background
                GradientPaint gradient = new GradientPaint(
                    0, 0, new Color(25, 25, 35),
                    0, getHeight(), new Color(45, 45, 65)
                );
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
                
                g2d.dispose();
            }
        };
        sidebarPanel.setPreferredSize(new Dimension(260, 0));
        sidebarPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(0, 102, 204)),
            BorderFactory.createEmptyBorder(0, 0, 0, 0)
        ));
        
        // Modern Logo/Title area
        JPanel logoPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                
                // Create gradient for logo area
                GradientPaint gradient = new GradientPaint(
                    0, 0, new Color(0, 102, 204),
                    0, getHeight(), new Color(0, 51, 102)
                );
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
                
                g2d.dispose();
            }
        };
        logoPanel.setBorder(BorderFactory.createEmptyBorder(30, 20, 30, 20));
        logoPanel.setOpaque(false);
        
        // Modern logo with shadow effect
        JLabel logoLabel = new JLabel("🏫");
        logoLabel.setFont(new Font("Segoe UI", Font.BOLD, 42));
        logoLabel.setForeground(Color.WHITE);
        logoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        logoLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        
        // Modern system label
        JLabel systemLabel = new JLabel("HELPDESK");
        systemLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        systemLabel.setForeground(Color.WHITE);
        systemLabel.setHorizontalAlignment(SwingConstants.CENTER);
        systemLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
        
        // Subtitle
        JLabel subtitleLabel = new JLabel("Administration Portal");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        subtitleLabel.setForeground(new Color(173, 216, 230));
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
        
        // Create modern sidebar buttons with clean text
        dashboardButton = createModernSidebarButton("Dashboard", true);
        ticketsButton = createModernSidebarButton("Tickets", false);
        usersButton = createModernSidebarButton("Users", false);
        departmentsButton = createModernSidebarButton("Departments", false);
        reportsButton = createModernSidebarButton("Reports", false);
        
        navPanel.add(dashboardButton);
        navPanel.add(ticketsButton);
        navPanel.add(usersButton);
        navPanel.add(departmentsButton);
        navPanel.add(reportsButton);
        
        // Modern User info panel
        JPanel userInfoPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                
                // Create gradient for user area
                GradientPaint gradient = new GradientPaint(
                    0, 0, new Color(20, 20, 30),
                    0, getHeight(), new Color(35, 35, 50)
                );
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
                
                g2d.dispose();
            }
        };
        userInfoPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(60, 60, 80)),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        userInfoPanel.setOpaque(false);
        
        // User avatar placeholder
        JPanel avatarPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Draw circular avatar background
                int size = Math.min(getWidth(), getHeight()) - 10;
                int x = (getWidth() - size) / 2;
                int y = (getHeight() - size) / 2;
                
                g2d.setColor(new Color(0, 102, 204));
                g2d.fillOval(x, y, size, size);
                
                // Draw user icon
                g2d.setColor(Color.WHITE);
                g2d.setFont(new Font("Segoe UI", Font.BOLD, size / 2));
                FontMetrics fm = g2d.getFontMetrics();
                String text = "👤";
                int textX = x + (size - fm.stringWidth(text)) / 2;
                int textY = y + (size - fm.getHeight()) / 2 + fm.getAscent();
                g2d.drawString(text, textX, textY);
                
                g2d.dispose();
            }
        };
        avatarPanel.setPreferredSize(new Dimension(60, 60));
        avatarPanel.setOpaque(false);
        
        // User info labels
        JLabel userLabel = new JLabel(currentUser.getFullName());
        userLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        userLabel.setForeground(Color.WHITE);
        userLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        JLabel roleLabel = new JLabel("Administrator");
        roleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        roleLabel.setForeground(new Color(173, 216, 230));
        roleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        JPanel userInfoTextPanel = new JPanel(new BorderLayout());
        userInfoTextPanel.setOpaque(false);
        userInfoTextPanel.add(userLabel, BorderLayout.NORTH);
        userInfoTextPanel.add(roleLabel, BorderLayout.CENTER);
        
        // Modern logout button
        logoutButton = createModernSidebarButton("Logout", false);
        logoutButton.setBackground(new Color(220, 53, 69));
        logoutButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        
        // Add hover effect for logout
        logoutButton.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) {
                logoutButton.setBackground(new Color(255, 69, 58));
            }
            public void mouseExited(MouseEvent evt) {
                logoutButton.setBackground(new Color(220, 53, 69));
            }
        });
        
        // Assemble user info
        JPanel userMainPanel = new JPanel(new BorderLayout());
        userMainPanel.setOpaque(false);
        userMainPanel.add(avatarPanel, BorderLayout.WEST);
        userMainPanel.add(userInfoTextPanel, BorderLayout.CENTER);
        userMainPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 0));
        
        userInfoPanel.add(userMainPanel, BorderLayout.CENTER);
        userInfoPanel.add(logoutButton, BorderLayout.SOUTH);
        
        sidebarPanel.add(logoPanel, BorderLayout.NORTH);
        sidebarPanel.add(navPanel, BorderLayout.CENTER);
        sidebarPanel.add(userInfoPanel, BorderLayout.SOUTH);
    }
    
    private JButton createModernSidebarButton(String text, boolean isActive) {
        JButton button = new JButton(text);
        
        // Enhanced button styling with gradient-like appearance
        button.setBackground(isActive ? new Color(25, 118, 210) : new Color(52, 58, 64));
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(isActive ? new Color(13, 71, 161) : new Color(38, 42, 52), 1),
            BorderFactory.createEmptyBorder(12, 18, 12, 18)
        ));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setOpaque(true);
        button.setPreferredSize(new Dimension(200, 45));
        
        // Enhanced hover effect with smooth transition
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) {
                if (!isActive) {
                    button.setBackground(new Color(62, 68, 73));
                    button.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(46, 51, 61), 1),
                        BorderFactory.createEmptyBorder(12, 18, 12, 18)
                    ));
                }
            }
            public void mouseExited(MouseEvent evt) {
                if (!isActive) {
                    button.setBackground(new Color(52, 58, 64));
                    button.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(38, 42, 52), 1),
                        BorderFactory.createEmptyBorder(12, 18, 12, 18)
                    ));
                }
            }
            public void mousePressed(MouseEvent evt) {
                if (!isActive) {
                    button.setBackground(new Color(38, 42, 52));
                }
            }
            public void mouseReleased(MouseEvent evt) {
                if (!isActive) {
                    button.setBackground(new Color(52, 58, 64));
                }
            }
        });
        
        return button;
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // Header Panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(0, 51, 102));
        headerPanel.setBorder(new EmptyBorder(15, 20, 15, 20));
        headerPanel.setPreferredSize(new Dimension(0, 80));
        
        JPanel headerLeft = new JPanel(new FlowLayout(FlowLayout.LEFT));
        headerLeft.setOpaque(false);
        headerLeft.add(adminNameLabel);
        headerLeft.add(Box.createHorizontalStrut(30));
        headerLeft.add(departmentLabel);
        
        headerPanel.add(headerLeft, BorderLayout.WEST);
        headerPanel.add(notificationsLabel, BorderLayout.EAST);
        
        // Create content panels
        createSidebarPanel();
        createOverviewPanel();
        createTicketsPanel();
        createUsersPanel();
        createDepartmentsPanel();
        createReportsPanel();
        
        // Content Panel (card layout)
        contentPanel = new JPanel(new CardLayout());
        contentPanel.add(overviewPanel, "Dashboard");
        contentPanel.add(ticketsPanel, "Tickets");
        contentPanel.add(usersPanel, "Users");
        contentPanel.add(departmentsPanel, "Departments");
        contentPanel.add(reportsPanel, "Reports");
        
        // Add main components
        add(headerPanel, BorderLayout.NORTH);
        add(sidebarPanel, BorderLayout.WEST);
        add(contentPanel, BorderLayout.CENTER);
    }
    
    private void createOverviewPanel() {
        overviewPanel = new JPanel(new BorderLayout());
        overviewPanel.setBackground(new Color(240, 248, 255));
        overviewPanel.setBorder(new EmptyBorder(25, 25, 25, 25));
        
        // Title for overview
        JLabel overviewTitle = new JLabel("📊 System Overview");
        overviewTitle.setFont(new Font("Arial", Font.BOLD, 24));
        overviewTitle.setForeground(new Color(0, 51, 102));
        overviewTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        
        // Overview cards panel with improved spacing
        JPanel cardsPanel = new JPanel(new GridLayout(2, 2, 25, 25));
        cardsPanel.setBackground(new Color(240, 248, 255));
        
        // Enhanced Total Tickets Card
        JPanel totalCard = createEnhancedOverviewCard(
            "📋 Total Tickets", 
            totalTicketsLabel, 
            new Color(0, 102, 204),
            "All tickets in system"
        );
        cardsPanel.add(totalCard);
        
        // Enhanced Pending Review Card
        JPanel pendingCard = createEnhancedOverviewCard(
            "⏳ Pending Review", 
            pendingReviewLabel, 
            new Color(255, 140, 0),
            "Awaiting approval"
        );
        cardsPanel.add(pendingCard);
        
        // Enhanced Assigned Tickets Card
        JPanel assignedCard = createEnhancedOverviewCard(
            "👤 Assigned Tickets", 
            assignedTicketsLabel, 
            new Color(0, 128, 128),
            "Assigned to staff"
        );
        cardsPanel.add(assignedCard);
        
        // Enhanced Resolved Tickets Card
        JPanel resolvedCard = createEnhancedOverviewCard(
            "✅ Resolved Tickets", 
            resolvedTicketsLabel, 
            new Color(34, 139, 34),
            "Completed tickets"
        );
        cardsPanel.add(resolvedCard);
        
        overviewPanel.add(overviewTitle, BorderLayout.NORTH);
        overviewPanel.add(cardsPanel, BorderLayout.CENTER);
        
        // Enhanced Recent tickets preview
        JPanel recentPanel = createEnhancedTicketsPanel();
        overviewPanel.add(recentPanel, BorderLayout.SOUTH);
    }
    
    private JPanel createEnhancedOverviewCard(String title, JLabel valueLabel, Color color, String description) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(color, 3),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        card.setPreferredSize(new Dimension(250, 150));
        
        // Title panel
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        titlePanel.setOpaque(false);
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setForeground(color);
        titlePanel.add(titleLabel);
        
        // Value label with larger font
        valueLabel.setFont(new Font("Arial", Font.BOLD, 36));
        valueLabel.setForeground(color);
        valueLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        // Description label
        JLabel descLabel = new JLabel(description);
        descLabel.setFont(new Font("Arial", Font.PLAIN, 11));
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
        
        // Add hover effect
        card.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) {
                card.setBackground(new Color(248, 252, 255));
                card.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }
            public void mouseExited(MouseEvent evt) {
                card.setBackground(Color.WHITE);
                card.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
        });
        
        return card;
    }
    
    private JPanel createEnhancedTicketsPanel() {
        JPanel recentPanel = new JPanel(new BorderLayout());
        recentPanel.setBackground(new Color(240, 248, 255));
        recentPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(0, 102, 204), 2),
            "🎫 Recent Tickets",
            javax.swing.border.TitledBorder.LEFT,
            javax.swing.border.TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 14),
            new Color(0, 51, 102)
        ));
        recentPanel.setPreferredSize(new Dimension(0, 300));
        
        // Enhanced table styling
        ticketsTable.setRowHeight(30);
        ticketsTable.setFont(new Font("Arial", Font.PLAIN, 12));
        ticketsTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        ticketsTable.getTableHeader().setBackground(new Color(0, 102, 204));
        ticketsTable.getTableHeader().setForeground(Color.WHITE);
        ticketsTable.setSelectionBackground(new Color(173, 216, 230));
        ticketsTable.setGridColor(new Color(200, 200, 200));
        ticketsTable.setShowGrid(true);
        
        JScrollPane recentScrollPane = new JScrollPane(ticketsTable);
        recentScrollPane.setBorder(BorderFactory.createLineBorder(new Color(0, 102, 204), 1));
        recentScrollPane.getViewport().setBackground(Color.WHITE);
        
        recentPanel.add(recentScrollPane, BorderLayout.CENTER);
        
        // Add refresh button
        JButton refreshButton = new JButton("🔄 Refresh");
        refreshButton.setBackground(new Color(0, 102, 204));
        refreshButton.setForeground(Color.WHITE);
        refreshButton.setFont(new Font("Arial", Font.BOLD, 12));
        refreshButton.setBorderPainted(false);
        refreshButton.setFocusPainted(false);
        refreshButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        refreshButton.addActionListener(e -> {
            loadInitialData();
            JOptionPane.showMessageDialog(this, "Dashboard refreshed!", "Success", JOptionPane.INFORMATION_MESSAGE);
        });
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setOpaque(false);
        buttonPanel.add(refreshButton);
        recentPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        return recentPanel;
    }
    
    private void createTicketsPanel() {
        ticketsPanel = new JPanel(new BorderLayout());
        ticketsPanel.setBackground(new Color(240, 248, 255));
        ticketsPanel.setBorder(new EmptyBorder(25, 25, 25, 25));
        
        // Title for tickets panel
        JLabel ticketsTitle = new JLabel("🎫 Ticket Management");
        ticketsTitle.setFont(new Font("Arial", Font.BOLD, 24));
        ticketsTitle.setForeground(new Color(0, 51, 102));
        ticketsTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        
        // Enhanced action buttons panel
        JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        actionsPanel.setBackground(new Color(240, 248, 255));
        actionsPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0, 102, 204), 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        // Style action buttons
        styleButton(approveButton, new Color(34, 139, 34), "✅ Approve");
        styleButton(rejectButton, new Color(220, 20, 60), "❌ Reject");
        styleButton(redirectButton, new Color(255, 140, 0), "🔄 Redirect");
        styleButton(clarifyButton, new Color(70, 130, 180), "❓ Request Info");
        
        actionsPanel.add(approveButton);
        actionsPanel.add(rejectButton);
        actionsPanel.add(redirectButton);
        actionsPanel.add(clarifyButton);
        actionsPanel.add(Box.createHorizontalStrut(20));
        
        // Assignment section
        JLabel deptFilterLabel = new JLabel("Department:");
        deptFilterLabel.setFont(new Font("Arial", Font.BOLD, 12));
        deptFilterLabel.setForeground(new Color(0, 51, 102));
        actionsPanel.add(deptFilterLabel);
        
        departmentFilterComboBox.setBackground(Color.WHITE);
        departmentFilterComboBox.setBorder(BorderFactory.createLineBorder(new Color(0, 102, 204), 1));
        departmentFilterComboBox.setFont(new Font("Arial", Font.PLAIN, 12));
        actionsPanel.add(departmentFilterComboBox);
        
        JLabel assignLabel = new JLabel("Assign to:");
        assignLabel.setFont(new Font("Arial", Font.BOLD, 12));
        assignLabel.setForeground(new Color(0, 51, 102));
        actionsPanel.add(assignLabel);
        
        staffComboBox.setBackground(Color.WHITE);
        staffComboBox.setBorder(BorderFactory.createLineBorder(new Color(0, 102, 204), 1));
        staffComboBox.setFont(new Font("Arial", Font.PLAIN, 12));
        actionsPanel.add(staffComboBox);
        
        styleButton(assignButton, new Color(0, 102, 204), "👤 Assign Staff");
        actionsPanel.add(assignButton);
        
        // Add details button
        JButton detailsButton = new JButton("📋 View Details");
        styleButton(detailsButton, new Color(70, 130, 180), "📋 View Details");
        actionsPanel.add(detailsButton);
        
        // Enhanced tickets table
        JScrollPane ticketsScrollPane = createEnhancedTicketsScrollPane();
        
        ticketsPanel.add(ticketsTitle, BorderLayout.NORTH);
        ticketsPanel.add(actionsPanel, BorderLayout.NORTH);
        ticketsPanel.add(ticketsScrollPane, BorderLayout.CENTER);
        
        // Add event handlers for ticket actions
        setupTicketActionHandlers(detailsButton);
    }
    
    private void styleButton(JButton button, Color color, String text) {
        button.setText(text);
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(120, 35));
        
        // Add hover effect
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) {
                button.setBackground(color.brighter());
            }
            public void mouseExited(MouseEvent evt) {
                button.setBackground(color);
            }
        });
    }
    
    private JScrollPane createEnhancedTicketsScrollPane() {
        // Enhanced table styling
        ticketsTable.setRowHeight(30);
        ticketsTable.setFont(new Font("Arial", Font.PLAIN, 12));
        ticketsTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        ticketsTable.getTableHeader().setBackground(new Color(0, 51, 102));
        ticketsTable.getTableHeader().setForeground(Color.WHITE);
        ticketsTable.getTableHeader().setPreferredSize(new Dimension(0, 40));
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
    
    private void setupTicketActionHandlers(JButton detailsButton) {
        // Approve ticket
        approveButton.addActionListener(e -> approveTicket());
        
        // Reject ticket
        rejectButton.addActionListener(e -> rejectTicket());
        
        // Redirect ticket
        redirectButton.addActionListener(e -> redirectTicket());
        
        // Request clarification
        clarifyButton.addActionListener(e -> requestClarification());
        
        // Assign ticket
        assignButton.addActionListener(e -> assignTicket());
        
        // View ticket details
        detailsButton.addActionListener(e -> showTicketDetails());
    }
    
    private void approveTicket() {
        int selectedRow = ticketsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a ticket to approve.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String ticketId = (String) ticketsTableModel.getValueAt(selectedRow, 0);
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Approve ticket " + ticketId + "?", 
            "Confirm Approval", 
            JOptionPane.YES_NO_OPTION);
            
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                ticketDAO.updateTicketStatus(ticketId, "Approved");
                loadTickets();
                loadStatistics();
                JOptionPane.showMessageDialog(this, "Ticket approved successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error approving ticket: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void rejectTicket() {
        int selectedRow = ticketsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a ticket to reject.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String ticketId = (String) ticketsTableModel.getValueAt(selectedRow, 0);
        String reason = JOptionPane.showInputDialog(this, "Reason for rejection:", "Reject Ticket", JOptionPane.QUESTION_MESSAGE);
        
        if (reason != null && !reason.trim().isEmpty()) {
            try {
                ticketDAO.updateTicketStatus(ticketId, "Rejected");
                ticketDAO.addNoteToTicket(ticketId, "Rejected: " + reason);
                loadTickets();
                loadStatistics();
                JOptionPane.showMessageDialog(this, "Ticket rejected successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error rejecting ticket: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void redirectTicket() {
        int selectedRow = ticketsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a ticket to redirect.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String ticketId = (String) ticketsTableModel.getValueAt(selectedRow, 0);
        String[] departments = {"IT Support", "Academic Affairs", "Student Services", "Finance"};
        String selectedDept = (String) JOptionPane.showInputDialog(
            this,
            "Select department to redirect to:",
            "Redirect Ticket",
            JOptionPane.QUESTION_MESSAGE,
            null,
            departments,
            departments[0]
        );
        
        if (selectedDept != null) {
            try {
                ticketDAO.updateTicketDepartment(ticketId, selectedDept);
                ticketDAO.updateTicketStatus(ticketId, "Pending Review");
                loadTickets();
                JOptionPane.showMessageDialog(this, "Ticket redirected to " + selectedDept + "!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error redirecting ticket: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void requestClarification() {
        int selectedRow = ticketsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a ticket to request clarification.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String ticketId = (String) ticketsTableModel.getValueAt(selectedRow, 0);
        String clarification = JOptionPane.showInputDialog(this, "Clarification needed:", "Request Clarification", JOptionPane.QUESTION_MESSAGE);
        
        if (clarification != null && !clarification.trim().isEmpty()) {
            try {
                ticketDAO.addNoteToTicket(ticketId, "Clarification needed: " + clarification);
                ticketDAO.updateTicketStatus(ticketId, "Pending Clarification");
                loadTickets();
                JOptionPane.showMessageDialog(this, "Clarification request sent!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error requesting clarification: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void assignTicket() {
        int selectedRow = ticketsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a ticket to assign.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String selectedStaff = (String) staffComboBox.getSelectedItem();
        if (selectedStaff == null || selectedStaff.equals("Select Staff Member...")) {
            JOptionPane.showMessageDialog(this, "Please select a staff member.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String ticketId = (String) ticketsTableModel.getValueAt(selectedRow, 0);
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Assign ticket " + ticketId + " to " + selectedStaff + "?", 
            "Confirm Assignment", 
            JOptionPane.YES_NO_OPTION);
            
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                ticketDAO.assignTicket(ticketId, selectedStaff);
                ticketDAO.updateTicketStatus(ticketId, "Assigned");
                loadTickets();
                loadStatistics();
                JOptionPane.showMessageDialog(this, "Ticket assigned to " + selectedStaff + "!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error assigning ticket: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void showTicketDetails() {
        int selectedRow = ticketsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a ticket to view details.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String ticketId = (String) ticketsTableModel.getValueAt(selectedRow, 0);
        
        JDialog detailsDialog = new JDialog(this, "Ticket Details - " + ticketId, true);
        detailsDialog.setSize(600, 500);
        detailsDialog.setLocationRelativeTo(this);
        
        JTextArea detailsArea = new JTextArea();
        detailsArea.setEditable(false);
        detailsArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        
        try {
            Document ticket = ticketDAO.getTicketById(ticketId);
            if (ticket != null) {
                StringBuilder details = new StringBuilder();
                details.append("Ticket ID: ").append(ticket.getString("ticketId") != null ? ticket.getString("ticketId") : "N/A").append("\n");
                details.append("Title: ").append(ticket.getString("title") != null ? ticket.getString("title") : "N/A").append("\n");
                details.append("Description: ").append(ticket.getString("description") != null ? ticket.getString("description") : "N/A").append("\n");
                details.append("Created By: ").append(ticket.getString("createdBy") != null ? ticket.getString("createdBy") : "N/A").append("\n");
                details.append("Department: ").append(ticket.getString("department") != null ? ticket.getString("department") : "N/A").append("\n");
                details.append("Status: ").append(ticket.getString("status") != null ? ticket.getString("status") : "N/A").append("\n");
                details.append("Priority: ").append(ticket.getString("priority") != null ? ticket.getString("priority") : "N/A").append("\n");
                details.append("Created Date: ").append(ticket.getString("createdDate") != null ? ticket.getString("createdDate") : "N/A").append("\n");
                details.append("Assigned To: ").append(ticket.getString("assignedTo") != null ? ticket.getString("assignedTo") : "Not assigned").append("\n\n");
                
                details.append("Notes:\n");
                List<String> notes = (List<String>) ticket.get("notes");
                if (notes != null) {
                    for (String note : notes) {
                        details.append("- ").append(note).append("\n");
                    }
                }
                
                detailsArea.setText(details.toString());
            }
        } catch (Exception e) {
            detailsArea.setText("Error loading ticket details: " + e.getMessage());
        }
        
        detailsDialog.add(new JScrollPane(detailsArea));
        detailsDialog.setVisible(true);
    }
    
    private void createUsersPanel() {
        usersPanel = new JPanel(new BorderLayout());
        usersPanel.setBackground(new Color(240, 248, 255));
        usersPanel.setBorder(new EmptyBorder(25, 25, 25, 25));
        
        JLabel usersTitle = new JLabel("👥 User Management");
        usersTitle.setFont(new Font("Arial", Font.BOLD, 24));
        usersTitle.setForeground(new Color(0, 51, 102));
        usersTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        
        // Create user table
        String[] userColumns = {"Username", "Full Name", "Email", "Role", "Department", "Status"};
        DefaultTableModel userTableModel = new DefaultTableModel(userColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table read-only
            }
        };
        JTable userTable = new JTable(userTableModel);
        
        // Style user table
        userTable.setRowHeight(30);
        userTable.setFont(new Font("Arial", Font.PLAIN, 12));
        userTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        userTable.getTableHeader().setBackground(new Color(0, 51, 102));
        userTable.getTableHeader().setForeground(Color.WHITE);
        userTable.setSelectionBackground(new Color(173, 216, 230));
        userTable.setGridColor(new Color(200, 200, 200));
        
        JScrollPane userScrollPane = new JScrollPane(userTable);
        userScrollPane.setBorder(BorderFactory.createLineBorder(new Color(0, 102, 204), 2));
        userScrollPane.getViewport().setBackground(Color.WHITE);
        
        // Action buttons panel
        JPanel userActionsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        userActionsPanel.setBackground(new Color(240, 248, 255));
        userActionsPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0, 102, 204), 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        JButton manageUsersButton = new JButton("👥 Manage Users");
        styleButton(manageUsersButton, new Color(0, 102, 204), "👥 Manage Users");
        
        JButton refreshUsersButton = new JButton("🔄 Refresh");
        styleButton(refreshUsersButton, new Color(70, 130, 180), "🔄 Refresh");
        
        userActionsPanel.add(manageUsersButton);
        userActionsPanel.add(refreshUsersButton);
        
        usersPanel.add(usersTitle, BorderLayout.NORTH);
        usersPanel.add(userActionsPanel, BorderLayout.NORTH);
        usersPanel.add(userScrollPane, BorderLayout.CENTER);
        
        // Store table reference for later use
        usersPanel.putClientProperty("userTable", userTable);
        usersPanel.putClientProperty("userTableModel", userTableModel);
        
        // Setup event handlers
        manageUsersButton.addActionListener(e -> openUserManagementDialog());
        refreshUsersButton.addActionListener(e -> loadUsers());
        
        // Load initial data
        loadUsers();
    }
    
    private void createDepartmentsPanel() {
        departmentsPanel = new JPanel(new BorderLayout());
        departmentsPanel.setBackground(new Color(240, 248, 255));
        departmentsPanel.setBorder(new EmptyBorder(25, 25, 25, 25));
        
        JLabel deptTitle = new JLabel("🏢 Department Management");
        deptTitle.setFont(new Font("Arial", Font.BOLD, 24));
        deptTitle.setForeground(new Color(0, 51, 102));
        deptTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        
        // Create department table
        String[] deptColumns = {"Department ID", "Name", "Staff Count", "Head of Department", "Status"};
        DefaultTableModel deptTableModel = new DefaultTableModel(deptColumns, 0);
        JTable deptTable = new JTable(deptTableModel);
        
        // Style department table
        deptTable.setRowHeight(30);
        deptTable.setFont(new Font("Arial", Font.PLAIN, 12));
        deptTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        deptTable.getTableHeader().setBackground(new Color(0, 51, 102));
        deptTable.getTableHeader().setForeground(Color.WHITE);
        deptTable.setSelectionBackground(new Color(173, 216, 230));
        deptTable.setGridColor(new Color(200, 200, 200));
        
        JScrollPane deptScrollPane = new JScrollPane(deptTable);
        deptScrollPane.setBorder(BorderFactory.createLineBorder(new Color(0, 102, 204), 2));
        deptScrollPane.getViewport().setBackground(Color.WHITE);
        
        // Action buttons panel
        JPanel deptActionsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        deptActionsPanel.setBackground(new Color(240, 248, 255));
        deptActionsPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0, 102, 204), 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        JButton addDeptButton = new JButton("➕ Add Department");
        styleButton(addDeptButton, new Color(34, 139, 34), "➕ Add Department");
        
        JButton refreshDeptButton = new JButton("🔄 Refresh");
        styleButton(refreshDeptButton, new Color(70, 130, 180), "🔄 Refresh");
        
        deptActionsPanel.add(addDeptButton);
        deptActionsPanel.add(refreshDeptButton);
        
        departmentsPanel.add(deptTitle, BorderLayout.NORTH);
        departmentsPanel.add(deptActionsPanel, BorderLayout.NORTH);
        departmentsPanel.add(deptScrollPane, BorderLayout.CENTER);
        
        // Store table reference for later use
        departmentsPanel.putClientProperty("deptTable", deptTable);
        departmentsPanel.putClientProperty("deptTableModel", deptTableModel);
        
        // Setup event handlers
        addDeptButton.addActionListener(e -> addNewDepartment());
        refreshDeptButton.addActionListener(e -> loadDepartments());
        
        // Load initial data
        loadDepartments();
    }
    
    private void createReportsPanel() {
        reportsPanel = new JPanel(new BorderLayout());
        reportsPanel.setBackground(new Color(240, 248, 255));
        reportsPanel.setBorder(new EmptyBorder(25, 25, 25, 25));
        
        JLabel reportsTitle = new JLabel("📈 Analytics & Reports");
        reportsTitle.setFont(new Font("Arial", Font.BOLD, 24));
        reportsTitle.setForeground(new Color(0, 51, 102));
        
        reportsPanel.add(reportsTitle, BorderLayout.NORTH);
        reportsPanel.add(new JLabel("Reports and analytics coming soon..."), BorderLayout.CENTER);
    }
    
    private void setupEventHandlers() {
        // Sidebar navigation
        dashboardButton.addActionListener(e -> showPanel("Dashboard"));
        ticketsButton.addActionListener(e -> showPanel("Tickets"));
        usersButton.addActionListener(e -> showPanel("Users"));
        departmentsButton.addActionListener(e -> showPanel("Departments"));
        reportsButton.addActionListener(e -> showPanel("Reports"));
        logoutButton.addActionListener(e -> logout());
    }
    
    private void showPanel(String panelName) {
        CardLayout cardLayout = (CardLayout) contentPanel.getLayout();
        cardLayout.show(contentPanel, panelName);
        
        // Update sidebar button colors
        resetSidebarButtonColors();
        switch (panelName) {
            case "Dashboard":
                dashboardButton.setBackground(new Color(0, 102, 204));
                break;
            case "Tickets":
                ticketsButton.setBackground(new Color(0, 102, 204));
                break;
            case "Users":
                usersButton.setBackground(new Color(0, 102, 204));
                break;
            case "Departments":
                departmentsButton.setBackground(new Color(0, 102, 204));
                break;
            case "Reports":
                reportsButton.setBackground(new Color(0, 102, 204));
                break;
        }
    }
    
    private void resetSidebarButtonColors() {
        dashboardButton.setBackground(new Color(30, 30, 30));
        ticketsButton.setBackground(new Color(30, 30, 30));
        usersButton.setBackground(new Color(30, 30, 30));
        departmentsButton.setBackground(new Color(30, 30, 30));
        reportsButton.setBackground(new Color(30, 30, 30));
    }
    
    private void loadInitialData() {
        loadStatistics();
        loadTickets();
        loadStaffMembers();
        loadDepartments();
        loadDepartmentFilter();
        loadUsers();
    }
    
    private void loadStatistics() {
        try {
            long totalTickets = ticketDAO.getTicketCount();
            long pendingTickets = ticketDAO.getTicketCountByStatus("Pending Review");
            long assignedTickets = ticketDAO.getTicketCountByStatus("Assigned");
            long resolvedTickets = ticketDAO.getTicketCountByStatus("Resolved");
            
            totalTicketsLabel.setText(String.valueOf(totalTickets));
            pendingReviewLabel.setText(String.valueOf(pendingTickets));
            assignedTicketsLabel.setText(String.valueOf(assignedTickets));
            resolvedTicketsLabel.setText(String.valueOf(resolvedTickets));
        } catch (Exception e) {
            System.err.println("Error loading statistics: " + e.getMessage());
        }
    }
    
    private void loadTickets() {
        try {
            ticketsTableModel.setRowCount(0);
            List<Document> tickets = ticketDAO.getAllTickets();
            
            for (Document ticket : tickets) {
                Object[] row = {
                    ticket.getString("ticketId") != null ? ticket.getString("ticketId") : "",
                    ticket.getString("createdBy") != null ? ticket.getString("createdBy") : "",
                    ticket.getString("title") != null ? ticket.getString("title") : "",
                    ticket.getString("department") != null ? ticket.getString("department") : "",
                    ticket.getString("status") != null ? ticket.getString("status") : "",
                    ticket.getString("createdDate") != null ? ticket.getString("createdDate") : ""
                };
                ticketsTableModel.addRow(row);
            }
        } catch (Exception e) {
            System.err.println("Error loading tickets: " + e.getMessage());
        }
    }
    
    private void loadStaffMembers() {
        try {
            staffComboBox.removeAllItems();
            staffComboBox.addItem("Select Staff Member...");
            
            // Load staff users from database
            List<Document> staffUsers = userDAO.getUsersByRole("Staff");
            for (Document staff : staffUsers) {
                String fullName = staff.getString("fullName");
                if (fullName != null && !fullName.trim().isEmpty()) {
                    staffComboBox.addItem(fullName);
                }
            }
        } catch (Exception e) {
            System.err.println("Error loading staff members: " + e.getMessage());
        }
    }
    
    private void loadDepartmentFilter() {
        try {
            departmentFilterComboBox.removeAllItems();
            departmentFilterComboBox.addItem("All Departments");
            
            List<Document> departments = departmentDAO.getActiveDepartments();
            for (Document dept : departments) {
                String deptName = dept.getString("name");
                if (deptName != null && !deptName.trim().isEmpty()) {
                    departmentFilterComboBox.addItem(deptName);
                }
            }
            
            // Add event listener for department filtering
            departmentFilterComboBox.addActionListener(e -> filterStaffByDepartment());
        } catch (Exception e) {
            System.err.println("Error loading department filter: " + e.getMessage());
        }
    }
    
    private void filterStaffByDepartment() {
        String selectedDept = (String) departmentFilterComboBox.getSelectedItem();
        if (selectedDept == null || selectedDept.equals("All Departments")) {
            loadStaffMembers();
            return;
        }
        
        try {
            staffComboBox.removeAllItems();
            staffComboBox.addItem("Select Staff Member...");
            
            Document department = departmentDAO.getDepartmentByName(selectedDept);
            if (department != null) {
                List<String> assignedStaff = (List<String>) department.get("staffMembers");
                if (assignedStaff != null) {
                    for (String staffName : assignedStaff) {
                        staffComboBox.addItem(staffName);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error filtering staff by department: " + e.getMessage());
        }
    }
    
    private void loadDepartments() {
        try {
            JTable deptTable = (JTable) departmentsPanel.getClientProperty("deptTable");
            DefaultTableModel deptTableModel = (DefaultTableModel) departmentsPanel.getClientProperty("deptTableModel");
            
            if (deptTable != null && deptTableModel != null) {
                deptTableModel.setRowCount(0);
                List<Document> departments = departmentDAO.getAllDepartments();
                
                for (Document dept : departments) {
                    List<String> staffMembers = (List<String>) dept.get("staffMembers");
                    int staffCount = staffMembers != null ? staffMembers.size() : 0;
                    
                    Object[] row = {
                        dept.getString("departmentId") != null ? dept.getString("departmentId") : "",
                        dept.getString("name") != null ? dept.getString("name") : "",
                        staffCount,
                        dept.getString("headOfDepartment") != null ? dept.getString("headOfDepartment") : "Not assigned",
                        dept.getBoolean("active") != null && dept.getBoolean("active") ? "Active" : "Inactive"
                    };
                    deptTableModel.addRow(row);
                }
            }
        } catch (Exception e) {
            System.err.println("Error loading departments: " + e.getMessage());
        }
    }
    
    private void openStaffAssignmentDialog() {
        StaffAssignmentDialog dialog = new StaffAssignmentDialog(this);
        dialog.setVisible(true);
        // Refresh departments after dialog closes
        loadDepartments();
    }
    
    private void openStaffManagementDialog() {
        StaffManagementDialog dialog = new StaffManagementDialog(this);
        dialog.setVisible(true);
        // Refresh departments after dialog closes
        loadDepartments();
        loadStaffMembers();
    }
    
    private void addNewDepartment() {
        JTextField nameField = new JTextField(20);
        JTextField descField = new JTextField(20);
        JTextField emailField = new JTextField(20);
        JTextField locationField = new JTextField(20);
        
        JPanel panel = new JPanel(new GridLayout(0, 2, 5, 5));
        panel.add(new JLabel("Department Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Description:"));
        panel.add(descField);
        panel.add(new JLabel("Email:"));
        panel.add(emailField);
        panel.add(new JLabel("Location:"));
        panel.add(locationField);
        
        int result = JOptionPane.showConfirmDialog(this, panel, "Add New Department", 
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            
        if (result == JOptionPane.OK_OPTION) {
            String name = nameField.getText().trim();
            String description = descField.getText().trim();
            String email = emailField.getText().trim();
            String location = locationField.getText().trim();
            
            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Department name is required!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            try {
                Department department = new Department();
                department.setName(name);
                department.setDescription(description);
                department.setEmail(email);
                department.setLocation(location);
                
                if (departmentDAO.createDepartment(department)) {
                    JOptionPane.showMessageDialog(this, "Department created successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    loadDepartments();
                } else {
                    JOptionPane.showMessageDialog(this, "Error creating department!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error creating department: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void loadUsers() {
        try {
            JTable userTable = (JTable) usersPanel.getClientProperty("userTable");
            DefaultTableModel userTableModel = (DefaultTableModel) usersPanel.getClientProperty("userTableModel");
            
            if (userTable != null && userTableModel != null) {
                userTableModel.setRowCount(0);
                List<Document> users = userDAO.getAllUsers();
                
                for (Document user : users) {
                    Object[] row = {
                        user.getString("username"),
                        user.getString("fullName"),
                        user.getString("email"),
                        user.getString("role"),
                        user.getString("department") != null ? user.getString("department") : "Not assigned",
                        user.getBoolean("active") != null && user.getBoolean("active") ? "Active" : "Inactive"
                    };
                    userTableModel.addRow(row);
                }
            }
        } catch (Exception e) {
            System.err.println("Error loading users: " + e.getMessage());
        }
    }
    
    private void openUserManagementDialog() {
        UserManagementDialog dialog = new UserManagementDialog(this);
        dialog.setVisible(true);
        // Refresh users after dialog closes
        loadUsers();
    }
    
    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to logout?", 
            "Confirm Logout", 
            JOptionPane.YES_NO_OPTION);
            
        if (confirm == JOptionPane.YES_OPTION) {
            this.dispose();
            SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
        }
    }
    
    private void setupFrame() {
        setTitle("Admin Dashboard - School Helpdesk System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1400, 900);
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
            if (userDAO != null) userDAO.close();
            if (departmentDAO != null) departmentDAO.close();
        } catch (Exception e) {
            System.err.println("Error closing resources: " + e.getMessage());
        }
        super.dispose();
    }
}
