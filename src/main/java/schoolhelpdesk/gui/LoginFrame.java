/*
 * Decompiled with CFR 0.152.
 */
package schoolhelpdesk.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import schoolhelpdesk.dao.UserDAO;
import schoolhelpdesk.gui.BeautifulAdminDashboard;
import schoolhelpdesk.gui.StaffDashboard;
import schoolhelpdesk.gui.StudentDashboard;
import schoolhelpdesk.model.User;

public class LoginFrame
extends JFrame {
    private JTextField usernameField = new JTextField(15);
    private JPasswordField passwordField = new JPasswordField(15);
    private JButton loginButton = new JButton("Login");
    private JButton exitButton = new JButton("Exit");
    private UserDAO userDAO = new UserDAO();
    private static final Color CLR_BG = new Color(241, 245, 249);
    private static final Color CLR_HEADER = new Color(15, 23, 42);
    private static final Color CLR_BRAND = new Color(37, 99, 235);
    private static final Color CLR_TEXT = new Color(15, 23, 42);
    private static final Color CLR_MUTED = new Color(100, 116, 139);
    private static final Color CLR_BORDER = new Color(226, 232, 240);
    private static final Color CLR_PLACEHOLDER = new Color(148, 163, 184);
    private static final Font FT_BRAND = new Font("Segoe UI", 1, 17);
    private static final Font FT_TITLE = new Font("Segoe UI", 1, 22);
    private static final Font FT_SUB = new Font("Segoe UI", 0, 13);
    private static final Font FT_FIELD = new Font("Segoe UI", 0, 14);
    private static final Font FT_BTN = new Font("Segoe UI", 1, 14);
    private static final Font FT_FOOT = new Font("Segoe UI", 0, 11);

    public LoginFrame() {
        this.initializeComponents();
        this.setupLayout();
        this.setupEventHandlers();
        this.setupFrame();
    }

    private void initializeComponents() {
        this.loginButton.setMnemonic(76);
        this.exitButton.setMnemonic(88);
        this.usernameField.setToolTipText("Enter your username");
        this.passwordField.setToolTipText("Enter your password");
        this.loginButton.setToolTipText("Click to login or press Enter");
        this.exitButton.setToolTipText("Exit the application");
    }

    private void setupLayout() {
        this.setLayout(new BorderLayout());
        this.getContentPane().setBackground(CLR_BG);
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(CLR_HEADER);
        headerPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, CLR_BORDER), BorderFactory.createEmptyBorder(16, 28, 16, 28)));
        JLabel titleLabel = new JLabel("School Helpdesk");
        titleLabel.setFont(FT_BRAND);
        titleLabel.setForeground(Color.WHITE);
        JLabel headerSub = new JLabel("Sign in to continue");
        headerSub.setFont(FT_FOOT);
        headerSub.setForeground(new Color(148, 163, 184));
        JPanel headerText = new JPanel(new GridBagLayout());
        headerText.setOpaque(false);
        GridBagConstraints hg = new GridBagConstraints();
        hg.gridx = 0;
        hg.gridy = 0;
        hg.anchor = 17;
        headerText.add(titleLabel, hg);
        hg.gridy = 1;
        hg.insets = new Insets(4, 0, 0, 0);
        headerText.add(headerSub, hg);
        headerPanel.add(headerText, "West");
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setOpaque(false);
        mainPanel.setBackground(CLR_BG);
        JPanel logoStrip = new JPanel(new FlowLayout(1, 0, 0));
        logoStrip.setOpaque(false);
        logoStrip.setBorder(BorderFactory.createEmptyBorder(24, 24, 8, 24));
        JLabel logoLabel = new JLabel();
        try {
            ImageIcon logoIcon = new ImageIcon(this.getClass().getResource("/schoolhelpdesk/resources/images/rmmc.logo.png"));
            if (logoIcon.getIconWidth() > 0) {
                int maxW = 220;
                int w = logoIcon.getIconWidth();
                int h = logoIcon.getIconHeight();
                if (w > maxW) {
                    Image scaledImage = logoIcon.getImage().getScaledInstance(maxW, -1, 4);
                    logoLabel.setIcon(new ImageIcon(scaledImage));
                } else {
                    logoLabel.setIcon(logoIcon);
                }
            }
        }
        catch (Exception e) {
            logoLabel.setText("");
        }
        logoStrip.add(logoLabel);
        mainPanel.add(logoStrip, "North");
        JPanel centerWrap = new JPanel(new GridBagLayout());
        centerWrap.setOpaque(true);
        centerWrap.setBackground(CLR_BG);
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 1.0;
        c.weighty = 1.0;
        c.anchor = GridBagConstraints.NORTH;
        c.insets = new Insets(8, 32, 24, 32);
        JPanel loginFormPanel = new JPanel(new GridBagLayout());
        loginFormPanel.setBackground(Color.WHITE);
        loginFormPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(CLR_BORDER, 1), BorderFactory.createEmptyBorder(28, 32, 28, 32)));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = 2;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(0, 0, 6, 0);
        JLabel loginTitle = new JLabel("Sign in", 0);
        loginTitle.setFont(FT_TITLE);
        loginTitle.setForeground(CLR_TEXT);
        loginFormPanel.add(loginTitle, gbc);
        gbc.insets = new Insets(0, 0, 20, 0);
        JLabel loginHint = new JLabel("Use your school username and password.", 0);
        loginHint.setFont(FT_SUB);
        loginHint.setForeground(CLR_MUTED);
        loginFormPanel.add(loginHint, gbc);
        gbc.insets = new Insets(0, 0, 10, 0);
        this.usernameField = new JTextField(20);
        this.usernameField.setFont(FT_FIELD);
        this.usernameField.setBackground(Color.WHITE);
        this.usernameField.setForeground(CLR_TEXT);
        this.usernameField.setCaretColor(CLR_BRAND);
        this.usernameField.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(CLR_BORDER, 1), BorderFactory.createEmptyBorder(10, 12, 10, 12)));
        loginFormPanel.add(this.usernameField, gbc);
        final String[] usernamePlaceholder = new String[]{"Username or email"};
        this.usernameField.setText(usernamePlaceholder[0]);
        this.usernameField.setForeground(CLR_PLACEHOLDER);
        this.usernameField.addFocusListener(new FocusAdapter(){

            @Override
            public void focusGained(FocusEvent evt) {
                if (LoginFrame.this.usernameField.getText().equals(usernamePlaceholder[0])) {
                    LoginFrame.this.usernameField.setText("");
                    LoginFrame.this.usernameField.setForeground(CLR_TEXT);
                }
            }

            @Override
            public void focusLost(FocusEvent evt) {
                if (LoginFrame.this.usernameField.getText().isEmpty()) {
                    LoginFrame.this.usernameField.setText(usernamePlaceholder[0]);
                    LoginFrame.this.usernameField.setForeground(CLR_PLACEHOLDER);
                }
            }
        });
        gbc.insets = new Insets(0, 0, 10, 0);
        this.passwordField = new JPasswordField(20);
        this.passwordField.setFont(FT_FIELD);
        this.passwordField.setBackground(Color.WHITE);
        this.passwordField.setForeground(CLR_TEXT);
        this.passwordField.setCaretColor(CLR_BRAND);
        this.passwordField.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(CLR_BORDER, 1), BorderFactory.createEmptyBorder(10, 12, 10, 12)));
        loginFormPanel.add(this.passwordField, gbc);
        final String[] passwordPlaceholder = new String[]{"Password"};
        this.passwordField.setEchoChar('\u0000');
        this.passwordField.setText(passwordPlaceholder[0]);
        this.passwordField.setForeground(CLR_PLACEHOLDER);
        this.passwordField.addFocusListener(new FocusAdapter(){

            @Override
            public void focusGained(FocusEvent evt) {
                if (new String(LoginFrame.this.passwordField.getPassword()).equals(passwordPlaceholder[0])) {
                    LoginFrame.this.passwordField.setText("");
                    LoginFrame.this.passwordField.setEchoChar('\u2022');
                    LoginFrame.this.passwordField.setForeground(CLR_TEXT);
                }
            }

            @Override
            public void focusLost(FocusEvent evt) {
                if (LoginFrame.this.passwordField.getPassword().length == 0) {
                    LoginFrame.this.passwordField.setEchoChar('\u0000');
                    LoginFrame.this.passwordField.setText(passwordPlaceholder[0]);
                    LoginFrame.this.passwordField.setForeground(CLR_PLACEHOLDER);
                }
            }
        });
        gbc.insets = new Insets(16, 0, 0, 0);
        JPanel buttonRow = new JPanel(new GridLayout(1, 2, 12, 0));
        buttonRow.setOpaque(false);
        this.loginButton = new JButton("Sign in"){

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D)g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color fill = this.getModel().isPressed() ? new Color(29, 78, 216) : (this.getModel().isRollover() ? new Color(59, 130, 246) : CLR_BRAND);
                g2d.setColor(fill);
                g2d.fillRoundRect(0, 0, this.getWidth(), this.getHeight(), 8, 8);
                super.paintComponent(g);
            }

            @Override
            protected void paintBorder(Graphics g) {
            }
        };
        this.loginButton.setFont(FT_BTN);
        this.loginButton.setForeground(Color.WHITE);
        this.loginButton.setContentAreaFilled(false);
        this.loginButton.setBorderPainted(false);
        this.loginButton.setFocusPainted(false);
        this.loginButton.setOpaque(false);
        this.loginButton.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));
        this.loginButton.setCursor(new Cursor(12));
        this.loginButton.addMouseListener(new MouseAdapter(){

            @Override
            public void mouseEntered(MouseEvent evt) {
                LoginFrame.this.loginButton.repaint();
            }

            @Override
            public void mouseExited(MouseEvent evt) {
                LoginFrame.this.loginButton.repaint();
            }

            @Override
            public void mousePressed(MouseEvent evt) {
                LoginFrame.this.loginButton.repaint();
            }

            @Override
            public void mouseReleased(MouseEvent evt) {
                LoginFrame.this.loginButton.repaint();
            }
        });
        this.exitButton.setFont(FT_FIELD);
        this.exitButton.setForeground(CLR_MUTED);
        this.exitButton.setBackground(new Color(248, 250, 252));
        this.exitButton.setFocusPainted(false);
        this.exitButton.setCursor(new Cursor(12));
        this.exitButton.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(CLR_BORDER, 1), BorderFactory.createEmptyBorder(12, 16, 12, 16)));
        buttonRow.add(this.loginButton);
        buttonRow.add(this.exitButton);
        loginFormPanel.add(buttonRow, gbc);
        centerWrap.add(loginFormPanel, c);
        mainPanel.add(centerWrap, "Center");
        JPanel footerPanel = new JPanel(new FlowLayout(1));
        footerPanel.setOpaque(false);
        footerPanel.setBorder(BorderFactory.createEmptyBorder(12, 12, 20, 12));
        JLabel footerLabel = new JLabel("School Helpdesk \u00b7 Secure access for students, staff, and administrators");
        footerLabel.setFont(FT_FOOT);
        footerLabel.setForeground(CLR_MUTED);
        footerPanel.add(footerLabel);
        mainPanel.add(footerPanel, "South");
        this.add(headerPanel, "North");
        this.add(mainPanel, "Center");
    }

    private void setupEventHandlers() {
        this.loginButton.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                LoginFrame.this.performLogin();
            }
        });
        this.exitButton.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        KeyListener enterKeyListener = new KeyListener(){

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == 10) {
                    LoginFrame.this.performLogin();
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
            }

            @Override
            public void keyTyped(KeyEvent e) {
            }
        };
        this.usernameField.addKeyListener(enterKeyListener);
        this.passwordField.addKeyListener(enterKeyListener);
    }

    private void performLogin() {
        String username = this.usernameField.getText().trim();
        String password = new String(this.passwordField.getPassword());
        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter both username and password", "Login Error", 0);
            return;
        }
        User user = this.userDAO.authenticateUser(username, password);
        if (user != null) {
            JOptionPane.showMessageDialog(this, "Login successful! Welcome " + user.getFullName(), "Success", 1);
            this.openDashboard(user);
            this.dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Invalid username or password", "Login Failed", 0);
            this.passwordField.setText("");
            this.usernameField.requestFocus();
        }
    }

    private void openDashboard(final User user) {
        SwingUtilities.invokeLater(new Runnable(){

            @Override
            public void run() {
                try {
                    switch (user.getRole().toLowerCase()) {
                        case "admin": {
                            new BeautifulAdminDashboard(user).setVisible(true);
                            break;
                        }
                        case "staff": {
                            new StaffDashboard(user).setVisible(true);
                            break;
                        }
                        case "student": {
                            new StudentDashboard(user).setVisible(true);
                            break;
                        }
                        default: {
                            JOptionPane.showMessageDialog(null, "Unknown user role: " + user.getRole(), "Error", 0);
                            break;
                        }
                    }
                }
                catch (Exception e) {
                    JOptionPane.showMessageDialog(null, "Error opening dashboard: " + e.getMessage(), "Error", 0);
                    e.printStackTrace();
                }
            }
        });
    }

    private void setupFrame() {
        this.setTitle("School Helpdesk - Sign in");
        this.setDefaultCloseOperation(3);
        this.setSize(520, 640);
        this.setMinimumSize(new Dimension(480, 560));
        this.setLocationRelativeTo(null);
        try {
            this.setIconImage(new ImageIcon(this.getClass().getResource("/images/helpdesk_icon.png")).getImage());
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    @Override
    public void dispose() {
        if (this.userDAO != null) {
            this.userDAO.close();
        }
        super.dispose();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable(){

            @Override
            public void run() {
                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
                new LoginFrame().setVisible(true);
            }
        });
    }
}

