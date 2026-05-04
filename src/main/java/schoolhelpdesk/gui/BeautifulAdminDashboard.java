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
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ImageIcon;
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
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import org.bson.Document;
import schoolhelpdesk.dao.DepartmentDAO;
import schoolhelpdesk.dao.TicketDAO;
import schoolhelpdesk.dao.UserDAO;
import schoolhelpdesk.gui.LoginFrame;
import schoolhelpdesk.gui.StaffAssignmentDialog;
import schoolhelpdesk.gui.StaffManagementDialog;
import schoolhelpdesk.gui.UserManagementDialog;
import schoolhelpdesk.model.Department;
import schoolhelpdesk.model.User;

public class BeautifulAdminDashboard
extends JFrame {
    private static final Color APP_BG = new Color(241, 245, 249);
    private static final Color SURFACE_BG = new Color(255, 255, 255);
    private static final Color BRAND = new Color(37, 99, 235);
    private static final Color BRAND_DARK = new Color(30, 64, 175);
    private static final Color SUCCESS = new Color(22, 163, 74);
    private static final Color WARNING = new Color(245, 158, 11);
    private static final Color TEXT_PRIMARY = new Color(15, 23, 42);
    private static final Color TEXT_MUTED = new Color(100, 116, 139);
    private static final Color BORDER = new Color(226, 232, 240);
    private static final Font TITLE_FONT = new Font("Segoe UI", 1, 24);
    private static final Font BODY_FONT = new Font("Segoe UI", 0, 12);
    private static final Font LABEL_FONT = new Font("Segoe UI", 1, 12);
    private User currentUser;
    private TicketDAO ticketDAO;
    private UserDAO userDAO;
    private DepartmentDAO departmentDAO;
    private JLabel adminNameLabel;
    private JLabel departmentLabel;
    private JLabel notificationsLabel;
    private JPanel sidebarPanel;
    private JButton dashboardButton;
    private JButton ticketsButton;
    private JButton usersButton;
    private JButton departmentsButton;
    private JButton reportsButton;
    private JButton logoutButton;
    private JPanel contentPanel;
    private JPanel overviewPanel;
    private JPanel ticketsPanel;
    private JPanel usersPanel;
    private JPanel departmentsPanel;
    private JPanel reportsPanel;
    private JLabel totalTicketsLabel;
    private JLabel pendingReviewLabel;
    private JLabel assignedTicketsLabel;
    private JLabel resolvedTicketsLabel;
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
        try {
            this.ticketDAO = new TicketDAO();
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
        this.loadInitialData();
        this.setupFrame();
    }

    private void initializeComponents() {
        this.adminNameLabel = new JLabel("Admin: " + this.currentUser.getFullName());
        this.adminNameLabel.setFont(new Font("Segoe UI", 1, 16));
        this.adminNameLabel.setForeground(Color.WHITE);
        String today = new SimpleDateFormat("EEE, MMM dd yyyy").format(new Date());
        this.departmentLabel = new JLabel(this.currentUser.getRole() + "  \u2022  " + today);
        this.departmentLabel.setFont(new Font("Segoe UI", 0, 13));
        this.departmentLabel.setForeground(new Color(191, 219, 254));
        this.notificationsLabel = new JLabel("Notifications: 0");
        this.notificationsLabel.setFont(new Font("Segoe UI", 1, 12));
        this.notificationsLabel.setForeground(new Color(254, 240, 138));
        this.dashboardButton = new JButton("Dashboard");
        this.ticketsButton = new JButton("Tickets");
        this.usersButton = new JButton("Users");
        this.departmentsButton = new JButton("Departments");
        this.reportsButton = new JButton("Reports");
        this.logoutButton = new JButton("Logout");
        this.totalTicketsLabel = new JLabel("0");
        this.pendingReviewLabel = new JLabel("0");
        this.assignedTicketsLabel = new JLabel("0");
        this.resolvedTicketsLabel = new JLabel("0");
        Object[] columns = new String[]{"Ticket ID", "User", "Title", "Department", "Status", "Date"};
        this.ticketsTableModel = new DefaultTableModel(columns, 0);
        this.ticketsTable = new JTable(this.ticketsTableModel);
        this.approveButton = new JButton("Approve");
        this.rejectButton = new JButton("Reject");
        this.redirectButton = new JButton("Redirect");
        this.clarifyButton = new JButton("Request Info");
        this.assignButton = new JButton("Assign");
        this.staffComboBox = new JComboBox();
        this.staffComboBox.setPreferredSize(new Dimension(200, 30));
        this.departmentFilterComboBox = new JComboBox();
        this.departmentFilterComboBox.setPreferredSize(new Dimension(150, 30));
    }

    private JButton createSidebarButton(String text) {
        final JButton button = new JButton(text);
        button.setBackground(new Color(30, 30, 30));
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", 1, 13));
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setHorizontalAlignment(2);
        button.setPreferredSize(new Dimension(220, 45));
        button.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(60, 60, 60)), BorderFactory.createEmptyBorder(0, 15, 0, 0)));
        button.addMouseListener(new MouseAdapter(){

            @Override
            public void mouseEntered(MouseEvent evt) {
                button.setBackground(new Color(0, 102, 204));
                button.setCursor(new Cursor(12));
            }

            @Override
            public void mouseExited(MouseEvent evt) {
                if (!button.getText().contains("Dashboard")) {
                    button.setBackground(new Color(30, 30, 30));
                }
                button.setCursor(new Cursor(0));
            }
        });
        return button;
    }

    private void createSidebarPanel() {
        this.sidebarPanel = new JPanel(new BorderLayout()){

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D)g.create();
                GradientPaint gradient = new GradientPaint(0.0f, 0.0f, new Color(25, 25, 35), 0.0f, this.getHeight(), new Color(45, 45, 65));
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, this.getWidth(), this.getHeight());
                g2d.dispose();
            }
        };
        this.sidebarPanel.setPreferredSize(new Dimension(260, 0));
        this.sidebarPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(0, 102, 204)), BorderFactory.createEmptyBorder(0, 0, 0, 0)));
        JPanel logoPanel = new JPanel(new BorderLayout()){

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D)g.create();
                GradientPaint gradient = new GradientPaint(0.0f, 0.0f, new Color(0, 102, 204), 0.0f, this.getHeight(), new Color(0, 51, 102));
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, this.getWidth(), this.getHeight());
                g2d.dispose();
            }
        };
        logoPanel.setBorder(BorderFactory.createEmptyBorder(30, 20, 30, 20));
        logoPanel.setOpaque(false);
        JLabel logoLabel = new JLabel("SH");
        logoLabel.setFont(new Font("Segoe UI", 1, 42));
        logoLabel.setForeground(Color.WHITE);
        logoLabel.setHorizontalAlignment(0);
        logoLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        JLabel systemLabel = new JLabel("School Helpdesk");
        systemLabel.setFont(new Font("Segoe UI", 1, 18));
        systemLabel.setForeground(Color.WHITE);
        systemLabel.setHorizontalAlignment(0);
        systemLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
        JLabel subtitleLabel = new JLabel("Admin Control Center");
        subtitleLabel.setFont(new Font("Segoe UI", 0, 11));
        subtitleLabel.setForeground(new Color(173, 216, 230));
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
        this.dashboardButton = this.createModernSidebarButton("Dashboard", true);
        this.ticketsButton = this.createModernSidebarButton("Tickets", false);
        this.usersButton = this.createModernSidebarButton("Users", false);
        this.departmentsButton = this.createModernSidebarButton("Departments", false);
        this.reportsButton = this.createModernSidebarButton("Reports", false);
        navPanel.add(this.dashboardButton);
        navPanel.add(this.ticketsButton);
        navPanel.add(this.usersButton);
        navPanel.add(this.departmentsButton);
        navPanel.add(this.reportsButton);
        this.styleSidebarButtonsUniform();
        JPanel userInfoPanel = new JPanel(new BorderLayout()){

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D)g.create();
                GradientPaint gradient = new GradientPaint(0.0f, 0.0f, new Color(20, 20, 30), 0.0f, this.getHeight(), new Color(35, 35, 50));
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, this.getWidth(), this.getHeight());
                g2d.dispose();
            }
        };
        userInfoPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(60, 60, 80)), BorderFactory.createEmptyBorder(20, 20, 20, 20)));
        userInfoPanel.setOpaque(false);
        JPanel avatarPanel = new JPanel(new BorderLayout()){

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D)g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int size = Math.min(this.getWidth(), this.getHeight()) - 10;
                int x = (this.getWidth() - size) / 2;
                int y = (this.getHeight() - size) / 2;
                g2d.setColor(new Color(0, 102, 204));
                g2d.fillOval(x, y, size, size);
                g2d.setColor(Color.WHITE);
                String text = BeautifulAdminDashboard.initialsFor(BeautifulAdminDashboard.this.currentUser.getFullName());
                int fontSize = Math.max(14, size / 3);
                g2d.setFont(new Font("Segoe UI", 1, fontSize));
                FontMetrics fm = g2d.getFontMetrics();
                int textX = x + (size - fm.stringWidth(text)) / 2;
                int textY = y + (size - fm.getHeight()) / 2 + fm.getAscent();
                g2d.drawString(text, textX, textY);
                g2d.dispose();
            }
        };
        avatarPanel.setPreferredSize(new Dimension(60, 60));
        avatarPanel.setOpaque(false);
        JLabel userLabel = new JLabel(this.currentUser.getFullName());
        userLabel.setFont(new Font("Segoe UI", 1, 14));
        userLabel.setForeground(Color.WHITE);
        userLabel.setHorizontalAlignment(0);
        JLabel roleLabel = new JLabel("System Administrator");
        roleLabel.setFont(new Font("Segoe UI", 0, 11));
        roleLabel.setForeground(new Color(173, 216, 230));
        roleLabel.setHorizontalAlignment(0);
        JPanel userInfoTextPanel = new JPanel(new BorderLayout());
        userInfoTextPanel.setOpaque(false);
        userInfoTextPanel.add((Component)userLabel, "North");
        userInfoTextPanel.add((Component)roleLabel, "Center");
        this.logoutButton = this.createModernSidebarButton("Logout", false);
        this.logoutButton.setBackground(new Color(220, 53, 69));
        this.logoutButton.setFont(new Font("Segoe UI", 1, 12));
        this.logoutButton.addMouseListener(new MouseAdapter(){

            @Override
            public void mouseEntered(MouseEvent evt) {
                BeautifulAdminDashboard.this.logoutButton.setBackground(new Color(255, 69, 58));
            }

            @Override
            public void mouseExited(MouseEvent evt) {
                BeautifulAdminDashboard.this.logoutButton.setBackground(new Color(220, 53, 69));
            }
        });
        JPanel userMainPanel = new JPanel(new BorderLayout());
        userMainPanel.setOpaque(false);
        userMainPanel.add((Component)avatarPanel, "West");
        userMainPanel.add((Component)userInfoTextPanel, "Center");
        userMainPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 0));
        userInfoPanel.add((Component)userMainPanel, "Center");
        userInfoPanel.add((Component)this.logoutButton, "South");
        this.sidebarPanel.add((Component)logoPanel, "North");
        this.sidebarPanel.add((Component)navPanel, "Center");
        this.sidebarPanel.add((Component)userInfoPanel, "South");
    }

    private JButton createModernSidebarButton(String text, boolean isActive) {
        final JButton button = new JButton(text);
        button.setBackground(isActive ? BRAND : new Color(51, 65, 85));
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", 1, 12));
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setHorizontalAlignment(2);
        button.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(isActive ? BRAND_DARK : new Color(71, 85, 105), 1), BorderFactory.createEmptyBorder(12, 18, 12, 18)));
        button.setCursor(new Cursor(12));
        button.setOpaque(true);
        button.setPreferredSize(new Dimension(200, 45));
        button.putClientProperty("active", isActive);
        button.addMouseListener(new MouseAdapter(){

            @Override
            public void mouseEntered(MouseEvent evt) {
                if (!BeautifulAdminDashboard.this.isButtonActive(button)) {
                    button.setBackground(new Color(71, 85, 105));
                    button.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(100, 116, 139), 1), BorderFactory.createEmptyBorder(12, 18, 12, 18)));
                }
            }

            @Override
            public void mouseExited(MouseEvent evt) {
                if (!BeautifulAdminDashboard.this.isButtonActive(button)) {
                    button.setBackground(new Color(51, 65, 85));
                    button.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(71, 85, 105), 1), BorderFactory.createEmptyBorder(12, 18, 12, 18)));
                }
            }

            @Override
            public void mousePressed(MouseEvent evt) {
                if (!BeautifulAdminDashboard.this.isButtonActive(button)) {
                    button.setBackground(new Color(30, 41, 59));
                }
            }

            @Override
            public void mouseReleased(MouseEvent evt) {
                if (!BeautifulAdminDashboard.this.isButtonActive(button)) {
                    button.setBackground(new Color(51, 65, 85));
                }
            }
        });
        return button;
    }

    private void setupLayout() {
        this.setLayout(new BorderLayout());
        JPanel headerPanel = new JPanel(new BorderLayout(0, 0));
        headerPanel.setBackground(BRAND_DARK);
        headerPanel.setBorder(new EmptyBorder(15, 20, 15, 20));
        headerPanel.setPreferredSize(new Dimension(0, 80));
        JPanel headerLeft = new JPanel(new FlowLayout(0, 14, 0));
        headerLeft.setOpaque(false);
        headerLeft.add(this.adminNameLabel);
        headerLeft.add(Box.createHorizontalStrut(12));
        headerLeft.add(this.departmentLabel);
        headerPanel.add((Component)headerLeft, "West");
        headerPanel.add((Component)this.notificationsLabel, "East");
        this.createSidebarPanel();
        this.createOverviewPanel();
        this.createTicketsPanel();
        this.createUsersPanel();
        this.createDepartmentsPanel();
        this.createReportsPanel();
        this.contentPanel = new JPanel(new CardLayout());
        this.contentPanel.add((Component)this.overviewPanel, "Dashboard");
        this.contentPanel.add((Component)this.ticketsPanel, "Tickets");
        this.contentPanel.add((Component)this.usersPanel, "Users");
        this.contentPanel.add((Component)this.departmentsPanel, "Departments");
        this.contentPanel.add((Component)this.reportsPanel, "Reports");
        this.add((Component)headerPanel, "North");
        this.add((Component)this.sidebarPanel, "West");
        this.add((Component)this.contentPanel, "Center");
    }

    private void createOverviewPanel() {
        this.overviewPanel = new JPanel(new BorderLayout());
        this.overviewPanel.setBackground(APP_BG);
        this.overviewPanel.setBorder(new EmptyBorder(25, 25, 25, 25));
        JLabel overviewTitle = this.createPageHeader("System Overview");
        JPanel cardsPanel = new JPanel(new GridLayout(2, 2, 18, 18));
        cardsPanel.setBackground(APP_BG);
        JPanel totalCard = this.createEnhancedOverviewCard("Total Tickets", this.totalTicketsLabel, BRAND, "All tickets in system");
        cardsPanel.add(totalCard);
        JPanel pendingCard = this.createEnhancedOverviewCard("Pending Review", this.pendingReviewLabel, WARNING, "Awaiting approval");
        cardsPanel.add(pendingCard);
        JPanel assignedCard = this.createEnhancedOverviewCard("Assigned Tickets", this.assignedTicketsLabel, new Color(6, 182, 212), "Assigned to staff");
        cardsPanel.add(assignedCard);
        JPanel resolvedCard = this.createEnhancedOverviewCard("Resolved Tickets", this.resolvedTicketsLabel, SUCCESS, "Completed tickets");
        cardsPanel.add(resolvedCard);
        this.overviewPanel.add((Component)overviewTitle, "North");
        this.overviewPanel.add((Component)cardsPanel, "Center");
        JPanel recentPanel = this.createEnhancedTicketsPanel();
        this.overviewPanel.add((Component)recentPanel, "South");
    }

    private JPanel createEnhancedOverviewCard(String title, JLabel valueLabel, Color color, String description) {
        final JPanel card = new JPanel(new BorderLayout());
        card.setBackground(SURFACE_BG);
        card.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(BORDER, 1), BorderFactory.createEmptyBorder(18, 18, 18, 18)));
        card.setPreferredSize(new Dimension(250, 150));
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", 1, 14));
        titleLabel.setForeground(TEXT_PRIMARY);
        JLabel accent = new JLabel("\u25cf");
        accent.setForeground(color);
        accent.setFont(new Font("Segoe UI", 1, 14));
        titlePanel.add((Component)titleLabel, "West");
        titlePanel.add((Component)accent, "East");
        valueLabel.setFont(new Font("Segoe UI", 1, 34));
        valueLabel.setForeground(color);
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
        card.addMouseListener(new MouseAdapter(){

            @Override
            public void mouseEntered(MouseEvent evt) {
                card.setBackground(new Color(248, 250, 252));
                card.setCursor(new Cursor(12));
            }

            @Override
            public void mouseExited(MouseEvent evt) {
                card.setBackground(SURFACE_BG);
                card.setCursor(new Cursor(0));
            }
        });
        return card;
    }

    private JPanel createEnhancedTicketsPanel() {
        JPanel recentPanel = new JPanel(new BorderLayout());
        recentPanel.setBackground(APP_BG);
        recentPanel.setBorder(BorderFactory.createLineBorder(BORDER, 1));
        recentPanel.setPreferredSize(new Dimension(0, 300));
        JLabel sectionTitle = new JLabel("Recent Tickets");
        sectionTitle.setFont(new Font("Segoe UI", 1, 14));
        sectionTitle.setForeground(TEXT_PRIMARY);
        sectionTitle.setBorder(new EmptyBorder(10, 12, 10, 12));
        this.ticketsTable.setRowHeight(30);
        this.ticketsTable.setFont(BODY_FONT);
        this.ticketsTable.getTableHeader().setFont(LABEL_FONT);
        this.ticketsTable.getTableHeader().setBackground(BRAND);
        this.ticketsTable.getTableHeader().setForeground(Color.WHITE);
        this.ticketsTable.setSelectionBackground(new Color(173, 216, 230));
        this.ticketsTable.setGridColor(new Color(200, 200, 200));
        this.ticketsTable.setShowGrid(true);
        JScrollPane recentScrollPane = new JScrollPane(this.ticketsTable);
        recentScrollPane.setBorder(BorderFactory.createLineBorder(BORDER, 1));
        recentScrollPane.getViewport().setBackground(Color.WHITE);
        recentPanel.add((Component)sectionTitle, "North");
        recentPanel.add((Component)recentScrollPane, "Center");
        JButton refreshButton = new JButton("Refresh");
        refreshButton.setBackground(BRAND);
        refreshButton.setForeground(Color.WHITE);
        refreshButton.setFont(LABEL_FONT);
        refreshButton.setBorderPainted(false);
        refreshButton.setFocusPainted(false);
        refreshButton.setCursor(new Cursor(12));
        refreshButton.addActionListener(e -> {
            this.loadInitialData();
            JOptionPane.showMessageDialog(this, "Dashboard refreshed!", "Success", 1);
        });
        JPanel buttonPanel = new JPanel(new FlowLayout(2));
        buttonPanel.setOpaque(false);
        buttonPanel.add(refreshButton);
        recentPanel.add((Component)buttonPanel, "South");
        return recentPanel;
    }

    private void createTicketsPanel() {
        this.ticketsPanel = new JPanel(new BorderLayout());
        this.ticketsPanel.setBackground(APP_BG);
        this.ticketsPanel.setBorder(new EmptyBorder(25, 25, 25, 25));
        JLabel ticketsTitle = this.createPageHeader("Ticket Management");
        JPanel actionsPanel = new JPanel(new BorderLayout(0, 10));
        actionsPanel.setBackground(SURFACE_BG);
        actionsPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(BORDER, 1), BorderFactory.createEmptyBorder(15, 15, 15, 15)));
        JPanel actionButtonsRow = new JPanel(new FlowLayout(0, 8, 0));
        actionButtonsRow.setOpaque(false);
        this.styleButton(this.approveButton, SUCCESS, "Approve");
        this.styleButton(this.rejectButton, new Color(220, 38, 38), "Reject");
        this.styleButton(this.redirectButton, WARNING, "Redirect");
        this.styleButton(this.clarifyButton, new Color(14, 116, 144), "Request Info");
        actionButtonsRow.add(this.approveButton);
        actionButtonsRow.add(this.rejectButton);
        actionButtonsRow.add(this.redirectButton);
        actionButtonsRow.add(this.clarifyButton);
        JPanel assignmentRow = new JPanel(new FlowLayout(0, 8, 0));
        assignmentRow.setOpaque(false);
        JLabel deptFilterLabel = new JLabel("Department");
        deptFilterLabel.setFont(LABEL_FONT);
        deptFilterLabel.setForeground(TEXT_PRIMARY);
        assignmentRow.add(deptFilterLabel);
        this.styleComboBox(this.departmentFilterComboBox, new Dimension(170, 34));
        assignmentRow.add(this.departmentFilterComboBox);
        JLabel assignLabel = new JLabel("Assign to");
        assignLabel.setFont(LABEL_FONT);
        assignLabel.setForeground(TEXT_PRIMARY);
        assignmentRow.add(assignLabel);
        this.styleComboBox(this.staffComboBox, new Dimension(190, 34));
        assignmentRow.add(this.staffComboBox);
        this.styleButton(this.assignButton, BRAND, "Assign Staff");
        assignmentRow.add(this.assignButton);
        JButton detailsButton = new JButton("View Details");
        this.styleButton(detailsButton, new Color(59, 130, 246), "View Details");
        assignmentRow.add(detailsButton);
        actionsPanel.add((Component)actionButtonsRow, "North");
        actionsPanel.add((Component)assignmentRow, "Center");
        JScrollPane ticketsScrollPane = this.createEnhancedTicketsScrollPane();
        JPanel topPanel = new JPanel(new BorderLayout(0, 12));
        topPanel.setOpaque(false);
        topPanel.add((Component)ticketsTitle, "North");
        topPanel.add((Component)actionsPanel, "Center");
        this.ticketsPanel.add((Component)topPanel, "North");
        this.ticketsPanel.add((Component)this.wrapAsSectionSurface("Ticket Queue", ticketsScrollPane), "Center");
        this.setupTicketActionHandlers(detailsButton);
    }

    private void styleButton(final JButton button, final Color color, String text) {
        button.setText(text);
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFont(LABEL_FONT);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(12));
        button.setPreferredSize(new Dimension(132, 36));
        button.setBorder(BorderFactory.createEmptyBorder(6, 14, 6, 14));
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

    private JScrollPane createEnhancedTicketsScrollPane() {
        this.styleModernTable(this.ticketsTable);
        JScrollPane scrollPane = new JScrollPane(this.ticketsTable);
        scrollPane.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(BORDER, 1), BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.setPreferredSize(new Dimension(0, 400));
        return scrollPane;
    }

    private void setupTicketActionHandlers(JButton detailsButton) {
        this.approveButton.addActionListener(e -> this.approveTicket());
        this.rejectButton.addActionListener(e -> this.rejectTicket());
        this.redirectButton.addActionListener(e -> this.redirectTicket());
        this.clarifyButton.addActionListener(e -> this.requestClarification());
        this.assignButton.addActionListener(e -> this.assignTicket());
        detailsButton.addActionListener(e -> this.showTicketDetails());
    }

    private void approveTicket() {
        int selectedRow = this.ticketsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a ticket to approve.", "No Selection", 2);
            return;
        }
        String ticketId = (String)this.ticketsTableModel.getValueAt(selectedRow, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Approve ticket " + ticketId + "?", "Confirm Approval", 0);
        if (confirm == 0) {
            try {
                this.ticketDAO.updateTicketStatus(ticketId, "Approved");
                this.loadTickets();
                this.loadStatistics();
                JOptionPane.showMessageDialog(this, "Ticket approved successfully!", "Success", 1);
            }
            catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error approving ticket: " + e.getMessage(), "Error", 0);
            }
        }
    }

    private void rejectTicket() {
        int selectedRow = this.ticketsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a ticket to reject.", "No Selection", 2);
            return;
        }
        String ticketId = (String)this.ticketsTableModel.getValueAt(selectedRow, 0);
        String reason = JOptionPane.showInputDialog(this, "Reason for rejection:", "Reject Ticket", 3);
        if (reason != null && !reason.trim().isEmpty()) {
            try {
                this.ticketDAO.updateTicketStatus(ticketId, "Rejected");
                this.ticketDAO.addNoteToTicket(ticketId, "Rejected: " + reason);
                this.loadTickets();
                this.loadStatistics();
                JOptionPane.showMessageDialog(this, "Ticket rejected successfully!", "Success", 1);
            }
            catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error rejecting ticket: " + e.getMessage(), "Error", 0);
            }
        }
    }

    private void redirectTicket() {
        int selectedRow = this.ticketsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a ticket to redirect.", "No Selection", 2);
            return;
        }
        String ticketId = (String)this.ticketsTableModel.getValueAt(selectedRow, 0);
        Object[] departments = new String[]{"IT Support", "Academic Affairs", "Student Services", "Finance"};
        String selectedDept = (String)JOptionPane.showInputDialog(this, "Select department to redirect to:", "Redirect Ticket", 3, null, departments, departments[0]);
        if (selectedDept != null) {
            try {
                this.ticketDAO.updateTicketDepartment(ticketId, selectedDept);
                this.ticketDAO.updateTicketStatus(ticketId, "Pending Review");
                this.loadTickets();
                JOptionPane.showMessageDialog(this, "Ticket redirected to " + selectedDept + "!", "Success", 1);
            }
            catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error redirecting ticket: " + e.getMessage(), "Error", 0);
            }
        }
    }

    private void requestClarification() {
        int selectedRow = this.ticketsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a ticket to request clarification.", "No Selection", 2);
            return;
        }
        String ticketId = (String)this.ticketsTableModel.getValueAt(selectedRow, 0);
        String clarification = JOptionPane.showInputDialog(this, "Clarification needed:", "Request Clarification", 3);
        if (clarification != null && !clarification.trim().isEmpty()) {
            try {
                this.ticketDAO.addNoteToTicket(ticketId, "Clarification needed: " + clarification);
                this.ticketDAO.updateTicketStatus(ticketId, "Pending Clarification");
                this.loadTickets();
                JOptionPane.showMessageDialog(this, "Clarification request sent!", "Success", 1);
            }
            catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error requesting clarification: " + e.getMessage(), "Error", 0);
            }
        }
    }

    private void assignTicket() {
        int selectedRow = this.ticketsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a ticket to assign.", "No Selection", 2);
            return;
        }
        String selectedStaff = (String)this.staffComboBox.getSelectedItem();
        if (selectedStaff == null || selectedStaff.equals("Select Staff Member...")) {
            JOptionPane.showMessageDialog(this, "Please select a staff member.", "No Selection", 2);
            return;
        }
        String ticketId = (String)this.ticketsTableModel.getValueAt(selectedRow, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Assign ticket " + ticketId + " to " + selectedStaff + "?", "Confirm Assignment", 0);
        if (confirm == 0) {
            try {
                this.ticketDAO.assignTicket(ticketId, selectedStaff);
                this.ticketDAO.updateTicketStatus(ticketId, "Assigned");
                this.loadTickets();
                this.loadStatistics();
                JOptionPane.showMessageDialog(this, "Ticket assigned to " + selectedStaff + "!", "Success", 1);
            }
            catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error assigning ticket: " + e.getMessage(), "Error", 0);
            }
        }
    }

    private void showTicketDetails() {
        int selectedRow = this.ticketsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a ticket to view details.", "No Selection", 2);
            return;
        }
        String ticketId = (String)this.ticketsTableModel.getValueAt(selectedRow, 0);
        JDialog detailsDialog = new JDialog(this, "Ticket Details - " + ticketId, true);
        detailsDialog.setSize(600, 500);
        detailsDialog.setLocationRelativeTo(this);
        JTextArea detailsArea = new JTextArea();
        detailsArea.setEditable(false);
        detailsArea.setFont(new Font("Monospaced", 0, 12));
        try {
            Document ticket = this.ticketDAO.getTicketById(ticketId);
            if (ticket != null) {
                StringBuilder details = new StringBuilder();
                details.append("Ticket ID: ").append(ticket.getString((Object)"ticketId") != null ? ticket.getString((Object)"ticketId") : "N/A").append("\n");
                details.append("Title: ").append(ticket.getString((Object)"title") != null ? ticket.getString((Object)"title") : "N/A").append("\n");
                details.append("Description: ").append(ticket.getString((Object)"description") != null ? ticket.getString((Object)"description") : "N/A").append("\n");
                details.append("Created By: ").append(ticket.getString((Object)"createdBy") != null ? ticket.getString((Object)"createdBy") : "N/A").append("\n");
                details.append("Department: ").append(ticket.getString((Object)"department") != null ? ticket.getString((Object)"department") : "N/A").append("\n");
                details.append("Status: ").append(ticket.getString((Object)"status") != null ? ticket.getString((Object)"status") : "N/A").append("\n");
                details.append("Priority: ").append(ticket.getString((Object)"priority") != null ? ticket.getString((Object)"priority") : "N/A").append("\n");
                details.append("Created Date: ").append(ticket.getString((Object)"createdDate") != null ? ticket.getString((Object)"createdDate") : "N/A").append("\n");
                details.append("Assigned To: ").append(ticket.getString((Object)"assignedTo") != null ? ticket.getString((Object)"assignedTo") : "Not assigned").append("\n\n");
                details.append("Notes:\n");
                List notes = (List)ticket.get((Object)"notes");
                if (notes != null) {
                    for (Object note : notes) {
                        details.append("- ").append(String.valueOf(note)).append("\n");
                    }
                }
                detailsArea.setText(details.toString());
            }
        }
        catch (Exception e) {
            detailsArea.setText("Error loading ticket details: " + e.getMessage());
        }
        detailsDialog.add(new JScrollPane(detailsArea));
        detailsDialog.setVisible(true);
    }

    private void createUsersPanel() {
        this.usersPanel = new JPanel(new BorderLayout());
        this.usersPanel.setBackground(APP_BG);
        this.usersPanel.setBorder(new EmptyBorder(25, 25, 25, 25));
        JLabel usersTitle = this.createPageHeader("User Management");
        Object[] userColumns = new String[]{"Username", "Full Name", "Email", "Role", "Department", "Status"};
        DefaultTableModel userTableModel = new DefaultTableModel(userColumns, 0){

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable userTable = new JTable(userTableModel);
        this.styleModernTable(userTable);
        userTable.getColumnModel().getColumn(0).setPreferredWidth(120);
        userTable.getColumnModel().getColumn(1).setPreferredWidth(180);
        userTable.getColumnModel().getColumn(2).setPreferredWidth(210);
        userTable.getColumnModel().getColumn(3).setPreferredWidth(110);
        userTable.getColumnModel().getColumn(4).setPreferredWidth(150);
        userTable.getColumnModel().getColumn(5).setPreferredWidth(100);
        JScrollPane userScrollPane = new JScrollPane(userTable);
        userScrollPane.setBorder(BorderFactory.createLineBorder(BORDER, 1));
        userScrollPane.getViewport().setBackground(Color.WHITE);
        JPanel userActionsPanel = new JPanel(new BorderLayout(0, 10));
        userActionsPanel.setBackground(SURFACE_BG);
        userActionsPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(BORDER, 1), BorderFactory.createEmptyBorder(15, 15, 15, 15)));
        JPanel quickActionsRow = new JPanel(new FlowLayout(0, 8, 0));
        quickActionsRow.setOpaque(false);
        JButton manageUsersButton = new JButton("Manage Users");
        this.styleButton(manageUsersButton, BRAND, "Manage Users");
        JButton refreshUsersButton = new JButton("Refresh");
        this.styleButton(refreshUsersButton, new Color(59, 130, 246), "Refresh");
        quickActionsRow.add(manageUsersButton);
        quickActionsRow.add(refreshUsersButton);
        JPanel helperRow = new JPanel(new FlowLayout(0, 8, 0));
        helperRow.setOpaque(false);
        JLabel helperLabel = new JLabel("Manage account access, roles, and activation status");
        helperLabel.setFont(BODY_FONT);
        helperLabel.setForeground(TEXT_MUTED);
        helperRow.add(helperLabel);
        userActionsPanel.add((Component)quickActionsRow, "North");
        userActionsPanel.add((Component)helperRow, "Center");
        JPanel topPanel = new JPanel(new BorderLayout(0, 12));
        topPanel.setOpaque(false);
        topPanel.add((Component)usersTitle, "North");
        topPanel.add((Component)userActionsPanel, "Center");
        this.usersPanel.add((Component)topPanel, "North");
        this.usersPanel.add((Component)this.wrapAsSectionSurface("Registered Users", userScrollPane), "Center");
        this.usersPanel.putClientProperty("userTable", userTable);
        this.usersPanel.putClientProperty("userTableModel", userTableModel);
        manageUsersButton.addActionListener(e -> this.openUserManagementDialog());
        refreshUsersButton.addActionListener(e -> this.loadUsers());
        this.loadUsers();
    }

    private void createDepartmentsPanel() {
        this.departmentsPanel = new JPanel(new BorderLayout());
        this.departmentsPanel.setBackground(APP_BG);
        this.departmentsPanel.setBorder(new EmptyBorder(25, 25, 25, 25));
        JLabel deptTitle = this.createPageHeader("Department Management");
        Object[] deptColumns = new String[]{"Department ID", "Name", "Staff Count", "Head of Department", "Status"};
        DefaultTableModel deptTableModel = new DefaultTableModel(deptColumns, 0);
        JTable deptTable = new JTable(deptTableModel);
        this.styleModernTable(deptTable);
        JScrollPane deptScrollPane = new JScrollPane(deptTable);
        deptScrollPane.setBorder(BorderFactory.createLineBorder(BORDER, 1));
        deptScrollPane.getViewport().setBackground(Color.WHITE);
        JPanel deptActionsPanel = new JPanel(new FlowLayout(0));
        deptActionsPanel.setBackground(SURFACE_BG);
        deptActionsPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(BORDER, 1), BorderFactory.createEmptyBorder(15, 15, 15, 15)));
        JButton addDeptButton = new JButton("Add Department");
        this.styleButton(addDeptButton, SUCCESS, "Add Department");
        JButton refreshDeptButton = new JButton("Refresh");
        this.styleButton(refreshDeptButton, new Color(59, 130, 246), "Refresh");
        deptActionsPanel.add(addDeptButton);
        deptActionsPanel.add(refreshDeptButton);
        JPanel topPanel = new JPanel(new BorderLayout(0, 12));
        topPanel.setOpaque(false);
        topPanel.add((Component)deptTitle, "North");
        topPanel.add((Component)deptActionsPanel, "Center");
        this.departmentsPanel.add((Component)topPanel, "North");
        this.departmentsPanel.add((Component)this.wrapAsSectionSurface("Department Directory", deptScrollPane), "Center");
        this.departmentsPanel.putClientProperty("deptTable", deptTable);
        this.departmentsPanel.putClientProperty("deptTableModel", deptTableModel);
        addDeptButton.addActionListener(e -> this.addNewDepartment());
        refreshDeptButton.addActionListener(e -> this.loadDepartments());
        this.loadDepartments();
    }

    private void createReportsPanel() {
        this.reportsPanel = new JPanel(new BorderLayout());
        this.reportsPanel.setBackground(APP_BG);
        this.reportsPanel.setBorder(new EmptyBorder(25, 25, 25, 25));
        JLabel reportsTitle = this.createPageHeader("Analytics & Reports");
        JPanel analyticsGrid = new JPanel(new GridLayout(2, 2, 16, 16));
        analyticsGrid.setOpaque(false);
        analyticsGrid.add(this.createInfoCard("Service Health", "Operational", "All core services are stable", SUCCESS));
        analyticsGrid.add(this.createInfoCard("Queue Trend", "Moderate", "Pending review count is within target", WARNING));
        analyticsGrid.add(this.createInfoCard("User Activity", "Healthy", "User and staff interactions are active", BRAND));
        analyticsGrid.add(this.createInfoCard("SLA Compliance", "On Track", "Average response time within SLA", new Color(6, 182, 212)));
        JPanel footer = new JPanel(new FlowLayout(0, 0, 8));
        footer.setOpaque(false);
        JLabel note = new JLabel("Tip: Use Dashboard and Ticket Management for real-time operations.");
        note.setFont(BODY_FONT);
        note.setForeground(TEXT_MUTED);
        footer.add(note);
        JPanel reportsCenter = new JPanel(new BorderLayout(0, 12));
        reportsCenter.setOpaque(false);
        reportsCenter.add((Component)this.wrapAsSectionSurface("Platform Insights", analyticsGrid), "Center");
        this.reportsPanel.add((Component)reportsTitle, "North");
        this.reportsPanel.add((Component)reportsCenter, "Center");
        this.reportsPanel.add((Component)footer, "South");
    }

    private void setupEventHandlers() {
        this.dashboardButton.addActionListener(e -> this.showPanel("Dashboard"));
        this.ticketsButton.addActionListener(e -> this.showPanel("Tickets"));
        this.usersButton.addActionListener(e -> this.showPanel("Users"));
        this.departmentsButton.addActionListener(e -> this.showPanel("Departments"));
        this.reportsButton.addActionListener(e -> this.showPanel("Reports"));
        this.logoutButton.addActionListener(e -> this.logout());
    }

    private void showPanel(String panelName) {
        CardLayout cardLayout = (CardLayout)this.contentPanel.getLayout();
        cardLayout.show(this.contentPanel, panelName);
        this.resetSidebarButtonStates();
        switch (panelName) {
            case "Dashboard": {
                this.setButtonActive(this.dashboardButton, true);
                break;
            }
            case "Tickets": {
                this.setButtonActive(this.ticketsButton, true);
                break;
            }
            case "Users": {
                this.setButtonActive(this.usersButton, true);
                break;
            }
            case "Departments": {
                this.setButtonActive(this.departmentsButton, true);
                break;
            }
            case "Reports": {
                this.setButtonActive(this.reportsButton, true);
            }
        }
    }

    private void resetSidebarButtonStates() {
        this.setButtonActive(this.dashboardButton, false);
        this.setButtonActive(this.ticketsButton, false);
        this.setButtonActive(this.usersButton, false);
        this.setButtonActive(this.departmentsButton, false);
        this.setButtonActive(this.reportsButton, false);
    }

    private void styleSidebarButtonsUniform() {
        JButton[] buttons;
        for (JButton button : buttons = new JButton[]{this.dashboardButton, this.ticketsButton, this.usersButton, this.departmentsButton, this.reportsButton}) {
            button.setFont(new Font("Segoe UI", 1, 13));
            button.setPreferredSize(new Dimension(210, 46));
            button.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(this.isButtonActive(button) ? BRAND_DARK : new Color(71, 85, 105), 1), BorderFactory.createEmptyBorder(12, 16, 12, 16)));
        }
    }

    private void setButtonActive(JButton button, boolean active) {
        button.putClientProperty("active", active);
        button.setBackground(active ? BRAND : new Color(51, 65, 85));
        button.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(active ? BRAND_DARK : new Color(71, 85, 105), 1), BorderFactory.createEmptyBorder(12, 18, 12, 18)));
    }

    private boolean isButtonActive(JButton button) {
        Object value = button.getClientProperty("active");
        return value instanceof Boolean && (Boolean)value != false;
    }

    private JLabel createPageHeader(String text) {
        JLabel title = new JLabel(text);
        title.setFont(TITLE_FONT);
        title.setForeground(TEXT_PRIMARY);
        title.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));
        return title;
    }

    private JPanel wrapAsSectionSurface(String sectionTitle, JComponent content) {
        JPanel surface = new JPanel(new BorderLayout(0, 8));
        surface.setBackground(SURFACE_BG);
        surface.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(BORDER, 1), BorderFactory.createEmptyBorder(12, 12, 12, 12)));
        JLabel title = new JLabel(sectionTitle);
        title.setFont(new Font("Segoe UI", 1, 13));
        title.setForeground(TEXT_PRIMARY);
        title.setBorder(new EmptyBorder(0, 2, 4, 2));
        surface.add((Component)title, "North");
        surface.add((Component)content, "Center");
        return surface;
    }

    private void styleComboBox(JComboBox<String> comboBox, Dimension size) {
        comboBox.setBackground(Color.WHITE);
        comboBox.setFont(BODY_FONT);
        comboBox.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(BORDER, 1), BorderFactory.createEmptyBorder(4, 6, 4, 6)));
        comboBox.setPreferredSize(size);
    }

    private JPanel createInfoCard(String title, String value, String subtitle, Color accent) {
        JPanel card = new JPanel(new BorderLayout(0, 6));
        card.setBackground(SURFACE_BG);
        card.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(BORDER, 1), BorderFactory.createEmptyBorder(16, 16, 16, 16)));
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", 1, 13));
        titleLabel.setForeground(TEXT_PRIMARY);
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", 1, 22));
        valueLabel.setForeground(accent);
        JLabel subtitleLabel = new JLabel(subtitle);
        subtitleLabel.setFont(new Font("Segoe UI", 0, 11));
        subtitleLabel.setForeground(TEXT_MUTED);
        card.add((Component)titleLabel, "North");
        card.add((Component)valueLabel, "Center");
        card.add((Component)subtitleLabel, "South");
        return card;
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
        table.setShowGrid(true);
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

    private void loadInitialData() {
        this.loadStatistics();
        this.loadTickets();
        this.loadStaffMembers();
        this.loadDepartments();
        this.loadDepartmentFilter();
        this.loadUsers();
    }

    private void loadStatistics() {
        try {
            long totalTickets = this.ticketDAO.getTicketCount();
            long pendingTickets = this.ticketDAO.getTicketCountByStatus("Pending Review");
            long assignedTickets = this.ticketDAO.getTicketCountByStatus("Assigned");
            long resolvedTickets = this.ticketDAO.getTicketCountByStatus("Resolved");
            this.totalTicketsLabel.setText(String.valueOf(totalTickets));
            this.pendingReviewLabel.setText(String.valueOf(pendingTickets));
            this.assignedTicketsLabel.setText(String.valueOf(assignedTickets));
            this.resolvedTicketsLabel.setText(String.valueOf(resolvedTickets));
            this.notificationsLabel.setText("Notifications: " + pendingTickets + " pending review");
        }
        catch (Exception e) {
            System.err.println("Error loading statistics: " + e.getMessage());
        }
    }

    private void loadTickets() {
        try {
            this.ticketsTableModel.setRowCount(0);
            List<Document> tickets = this.ticketDAO.getAllTickets();
            for (Document ticket : tickets) {
                Object[] row = new Object[]{ticket.getString((Object)"ticketId") != null ? ticket.getString((Object)"ticketId") : "", ticket.getString((Object)"createdBy") != null ? ticket.getString((Object)"createdBy") : "", ticket.getString((Object)"title") != null ? ticket.getString((Object)"title") : "", ticket.getString((Object)"department") != null ? ticket.getString((Object)"department") : "", ticket.getString((Object)"status") != null ? ticket.getString((Object)"status") : "", ticket.getString((Object)"createdDate") != null ? ticket.getString((Object)"createdDate") : ""};
                this.ticketsTableModel.addRow(row);
            }
        }
        catch (Exception e) {
            System.err.println("Error loading tickets: " + e.getMessage());
        }
    }

    private void loadStaffMembers() {
        try {
            this.staffComboBox.removeAllItems();
            this.staffComboBox.addItem("Select Staff Member...");
            List<Document> staffUsers = this.userDAO.getUsersByRole("Staff");
            for (Document staff : staffUsers) {
                String fullName = staff.getString((Object)"fullName");
                if (fullName == null || fullName.trim().isEmpty()) continue;
                this.staffComboBox.addItem(fullName);
            }
        }
        catch (Exception e) {
            System.err.println("Error loading staff members: " + e.getMessage());
        }
    }

    private void loadDepartmentFilter() {
        try {
            this.departmentFilterComboBox.removeAllItems();
            this.departmentFilterComboBox.addItem("All Departments");
            List<Document> departments = this.departmentDAO.getActiveDepartments();
            for (Document dept : departments) {
                String deptName = dept.getString((Object)"name");
                if (deptName == null || deptName.trim().isEmpty()) continue;
                this.departmentFilterComboBox.addItem(deptName);
            }
            this.departmentFilterComboBox.addActionListener(e -> this.filterStaffByDepartment());
        }
        catch (Exception e2) {
            System.err.println("Error loading department filter: " + e2.getMessage());
        }
    }

    private void filterStaffByDepartment() {
        String selectedDept = (String)this.departmentFilterComboBox.getSelectedItem();
        if (selectedDept == null || selectedDept.equals("All Departments")) {
            this.loadStaffMembers();
            return;
        }
        try {
            List assignedStaff;
            this.staffComboBox.removeAllItems();
            this.staffComboBox.addItem("Select Staff Member...");
            Document department = this.departmentDAO.getDepartmentByName(selectedDept);
            if (department != null && (assignedStaff = (List)department.get((Object)"staffMembers")) != null) {
                for (Object staffName : assignedStaff) {
                    this.staffComboBox.addItem(String.valueOf(staffName));
                }
            }
        }
        catch (Exception e) {
            System.err.println("Error filtering staff by department: " + e.getMessage());
        }
    }

    private void loadDepartments() {
        try {
            JTable deptTable = (JTable)this.departmentsPanel.getClientProperty("deptTable");
            DefaultTableModel deptTableModel = (DefaultTableModel)this.departmentsPanel.getClientProperty("deptTableModel");
            if (deptTable != null && deptTableModel != null) {
                deptTableModel.setRowCount(0);
                List<Document> departments = this.departmentDAO.getAllDepartments();
                for (Document dept : departments) {
                    List staffMembers = (List)dept.get((Object)"staffMembers");
                    int staffCount = staffMembers != null ? staffMembers.size() : 0;
                    Object[] row = new Object[]{dept.getString((Object)"departmentId") != null ? dept.getString((Object)"departmentId") : "", dept.getString((Object)"name") != null ? dept.getString((Object)"name") : "", staffCount, dept.getString((Object)"headOfDepartment") != null ? dept.getString((Object)"headOfDepartment") : "Not assigned", dept.getBoolean((Object)"active") != null && dept.getBoolean((Object)"active") != false ? "Active" : "Inactive"};
                    deptTableModel.addRow(row);
                }
            }
        }
        catch (Exception e) {
            System.err.println("Error loading departments: " + e.getMessage());
        }
    }

    private void openStaffAssignmentDialog() {
        StaffAssignmentDialog dialog = new StaffAssignmentDialog(this);
        dialog.setVisible(true);
        this.loadDepartments();
    }

    private void openStaffManagementDialog() {
        StaffManagementDialog dialog = new StaffManagementDialog(this);
        dialog.setVisible(true);
        this.loadDepartments();
        this.loadStaffMembers();
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
        int result = JOptionPane.showConfirmDialog(this, panel, "Add New Department", 2, -1);
        if (result == 0) {
            String name = nameField.getText().trim();
            String description = descField.getText().trim();
            String email = emailField.getText().trim();
            String location = locationField.getText().trim();
            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Department name is required!", "Error", 0);
                return;
            }
            try {
                Department department = new Department();
                department.setName(name);
                department.setDescription(description);
                department.setEmail(email);
                department.setLocation(location);
                if (this.departmentDAO.createDepartment(department)) {
                    JOptionPane.showMessageDialog(this, "Department created successfully!", "Success", 1);
                    this.loadDepartments();
                } else {
                    JOptionPane.showMessageDialog(this, "Error creating department!", "Error", 0);
                }
            }
            catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error creating department: " + e.getMessage(), "Error", 0);
            }
        }
    }

    private void loadUsers() {
        try {
            JTable userTable = (JTable)this.usersPanel.getClientProperty("userTable");
            DefaultTableModel userTableModel = (DefaultTableModel)this.usersPanel.getClientProperty("userTableModel");
            if (userTable != null && userTableModel != null) {
                userTableModel.setRowCount(0);
                List<Document> users = this.userDAO.getAllUsers();
                for (Document user : users) {
                    Object[] row = new Object[]{user.getString((Object)"username"), user.getString((Object)"fullName"), user.getString((Object)"email"), user.getString((Object)"role"), user.getString((Object)"department") != null ? user.getString((Object)"department") : "Not assigned", user.getBoolean((Object)"active") != null && user.getBoolean((Object)"active") != false ? "Active" : "Inactive"};
                    userTableModel.addRow(row);
                }
            }
        }
        catch (Exception e) {
            System.err.println("Error loading users: " + e.getMessage());
        }
    }

    private void openUserManagementDialog() {
        UserManagementDialog dialog = new UserManagementDialog(this);
        dialog.setVisible(true);
        this.loadUsers();
    }

    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to logout?", "Confirm Logout", 0);
        if (confirm == 0) {
            this.dispose();
            SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
        }
    }

    private static String initialsFor(String fullName) {
        if (fullName == null || fullName.isBlank()) {
            return "?";
        }
        String t = fullName.trim();
        String[] parts = t.split("\\s+");
        if (parts.length >= 2 && !parts[0].isEmpty() && !parts[parts.length - 1].isEmpty()) {
            return (parts[0].substring(0, 1) + parts[parts.length - 1].substring(0, 1)).toUpperCase();
        }
        if (t.length() >= 2) {
            return t.substring(0, 2).toUpperCase();
        }
        return t.substring(0, 1).toUpperCase();
    }

    private void setupFrame() {
        this.setTitle("Admin Dashboard - School Helpdesk System");
        this.setDefaultCloseOperation(3);
        this.setSize(1400, 900);
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
            if (this.userDAO != null) {
                this.userDAO.close();
            }
            if (this.departmentDAO != null) {
                this.departmentDAO.close();
            }
        }
        catch (Exception e) {
            System.err.println("Error closing resources: " + e.getMessage());
        }
        super.dispose();
    }
}

