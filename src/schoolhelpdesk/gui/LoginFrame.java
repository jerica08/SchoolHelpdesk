package schoolhelpdesk.gui;

import schoolhelpdesk.dao.UserDAO;
import schoolhelpdesk.model.User;
import schoolhelpdesk.gui.BeautifulAdminDashboard;
import schoolhelpdesk.gui.StaffDashboard;
import schoolhelpdesk.gui.StudentDashboard;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.Image;
import java.awt.GraphicsEnvironment;
import java.awt.GraphicsDevice;

public class LoginFrame extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton exitButton;
    private UserDAO userDAO;
    
    public LoginFrame() {
        // Initialize components first
        usernameField = new JTextField(15);
        passwordField = new JPasswordField(15);
        loginButton = new JButton("Login");
        exitButton = new JButton("Exit");
        
        userDAO = new UserDAO();
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        setupFrame();
    }
    
    private void initializeComponents() {
        // Set mnemonics
        loginButton.setMnemonic(KeyEvent.VK_L);
        exitButton.setMnemonic(KeyEvent.VK_X);
        
        // Set tooltips
        usernameField.setToolTipText("Enter your username");
        passwordField.setToolTipText("Enter your password");
        loginButton.setToolTipText("Click to login or press Enter");
        exitButton.setToolTipText("Exit the application");
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // Header Panel (keep original)
        JPanel headerPanel = new JPanel(new FlowLayout());
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 10, 10, 10));
        headerPanel.setBackground(new Color(0, 51, 102)); // Dark blue
        JLabel titleLabel = new JLabel("School Helpdesk System");
        titleLabel.setFont(new Font("Times New Roman", Font.BOLD, 32));
        titleLabel.setForeground(Color.WHITE); // White text
        headerPanel.add(titleLabel);
        
        // Logo Panel (keep original)
        JPanel logoPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        logoPanel.setBackground(new Color(240, 240, 240));
        JLabel logoLabel = new JLabel();
        try {
            ImageIcon logoIcon = new ImageIcon(getClass().getResource("/schoolhelpdesk/resources/images/rmmc.logo.png"));
            // Scale the logo if it's too large
            if (logoIcon.getIconWidth() > 300 || logoIcon.getIconHeight() > 150) {
                Image scaledImage = logoIcon.getImage().getScaledInstance(300, -1, Image.SCALE_SMOOTH);
                logoLabel.setIcon(new ImageIcon(scaledImage));
            } else {
                logoLabel.setIcon(logoIcon);
            }
        } catch (Exception e) {
            // If image not found, show placeholder
            logoLabel.setText("[LOGO]");
            logoLabel.setFont(new Font("Times New Roman", Font.PLAIN, 32));
            logoLabel.setForeground(Color.GRAY);
        }
        logoPanel.add(logoLabel);
        
        // Create a panel to hold logo and login form vertically
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(240, 240, 240));
        mainPanel.add(logoPanel, BorderLayout.NORTH);
        
        // Center Panel for Login Form (modern design)
        JPanel centerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        centerPanel.setBackground(new Color(240, 240, 240));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 50, 50));
        
        // Modern Login Form Panel (matching the image exactly)
        JPanel loginFormPanel = new JPanel(new GridBagLayout());
        loginFormPanel.setBackground(new Color(240, 240, 240, 240)); // Light gray background
        loginFormPanel.setPreferredSize(new Dimension(350, 250)); // Compact size
        loginFormPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 0, 20, 0);
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // "Login Now" title (matching the image - dark blue, serif font)
        JLabel loginTitle = new JLabel("Login Now", SwingConstants.CENTER);
        loginTitle.setFont(new Font("Times New Roman", Font.BOLD, 28));
        loginTitle.setForeground(new Color(0, 51, 102)); // Dark blue
        loginTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 30, 0));
        loginFormPanel.add(loginTitle, gbc);
        
        // Username field with rounded corners (matching the image)
        gbc.insets = new Insets(0, 0, 15, 0);
        usernameField = new JTextField(20);
        usernameField.setFont(new Font("Times New Roman", Font.PLAIN, 14));
        usernameField.setBackground(Color.WHITE);
        usernameField.setForeground(Color.BLACK);
        usernameField.setCaretColor(new Color(0, 51, 102));
        usernameField.setBorder(new javax.swing.border.AbstractBorder() {
            @Override
            public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(new Color(200, 200, 200)); // Light grey border
                g2d.setStroke(new BasicStroke(1));
                g2d.drawRoundRect(x, y, width - 1, height - 1, 15, 15);
            }
            @Override
            public Insets getBorderInsets(Component c) {
                return new Insets(12, 15, 12, 15);
            }
        });
        loginFormPanel.add(usernameField, gbc);
        
        // Add placeholder text for username
        final String[] usernamePlaceholder = {"Email or Username"};
        usernameField.setText(usernamePlaceholder[0]);
        usernameField.setForeground(new Color(170, 170, 170));
        usernameField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                if (usernameField.getText().equals(usernamePlaceholder[0])) {
                    usernameField.setText("");
                    usernameField.setForeground(Color.BLACK);
                }
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                if (usernameField.getText().isEmpty()) {
                    usernameField.setText(usernamePlaceholder[0]);
                    usernameField.setForeground(new Color(170, 170, 170));
                }
            }
        });
        
        // Password field with rounded corners (matching the image)
        gbc.insets = new Insets(0, 0, 25, 0);
        passwordField = new JPasswordField(20);
        passwordField.setFont(new Font("Times New Roman", Font.PLAIN, 14));
        passwordField.setBackground(Color.WHITE);
        passwordField.setForeground(Color.BLACK);
        passwordField.setCaretColor(new Color(0, 51, 102));
        passwordField.setBorder(new javax.swing.border.AbstractBorder() {
            @Override
            public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(new Color(200, 200, 200)); // Light grey border
                g2d.setStroke(new BasicStroke(1));
                g2d.drawRoundRect(x, y, width - 1, height - 1, 15, 15);
            }
            @Override
            public Insets getBorderInsets(Component c) {
                return new Insets(12, 15, 12, 15);
            }
        });
        loginFormPanel.add(passwordField, gbc);
        
        // Add placeholder text for password
        final String[] passwordPlaceholder = {"Password"};
        passwordField.setEchoChar((char)0); // Show placeholder text
        passwordField.setText(passwordPlaceholder[0]);
        passwordField.setForeground(new Color(170, 170, 170));
        passwordField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                if (new String(passwordField.getPassword()).equals(passwordPlaceholder[0])) {
                    passwordField.setText("");
                    passwordField.setEchoChar('●');
                    passwordField.setForeground(Color.BLACK);
                }
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                if (passwordField.getPassword().length == 0) {
                    passwordField.setEchoChar((char)0);
                    passwordField.setText(passwordPlaceholder[0]);
                    passwordField.setForeground(new Color(170, 170, 170));
                }
            }
        });
        
        // LOGIN button with rounded corners and dark blue background (matching the image)
        gbc.insets = new Insets(0, 0, 0, 0);
        loginButton = new JButton("LOGIN") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Dark blue background with rounded corners
                g2d.setColor(new Color(0, 51, 102)); // Dark blue matching image
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                
                // Hover effect
                if (getModel().isRollover()) {
                    g2d.setColor(new Color(255, 255, 255, 30));
                    g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                }
                
                // Pressed effect
                if (getModel().isPressed()) {
                    g2d.setColor(new Color(0, 0, 0, 50));
                    g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                }
                
                // Call super.paintComponent AFTER drawing background to render text
                super.paintComponent(g);
            }
            
            @Override
            protected void paintBorder(Graphics g) {
                // Don't paint border to avoid double painting
            }
        };
        loginButton.setFont(new Font("Times New Roman", Font.BOLD, 16));
        loginButton.setForeground(Color.WHITE);
        loginButton.setContentAreaFilled(false);
        loginButton.setBorderPainted(false);
        loginButton.setFocusPainted(false);
        loginButton.setOpaque(false);
        loginButton.setBorder(BorderFactory.createEmptyBorder(12, 25, 12, 25));
        loginButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        loginButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                loginButton.revalidate();
                loginButton.repaint();
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                loginButton.revalidate();
                loginButton.repaint();
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                loginButton.revalidate();
                loginButton.repaint();
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                loginButton.revalidate();
                loginButton.repaint();
            }
        });
        loginFormPanel.add(loginButton, gbc);
        
        // Add login form to center panel
        centerPanel.add(loginFormPanel);
        
        // Footer Panel (keep original)
        JPanel footerPanel = new JPanel(new FlowLayout());
        footerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 20, 10));
        footerPanel.setBackground(new Color(240, 240, 240));
        JLabel footerLabel = new JLabel(" 2026 School Helpdesk System");
        footerLabel.setFont(new Font("Times New Roman", Font.PLAIN, 12));
        footerLabel.setForeground(Color.GRAY);
        footerPanel.add(footerLabel);
        
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        
        add(headerPanel, BorderLayout.NORTH);
        add(mainPanel, BorderLayout.CENTER);
        add(footerPanel, BorderLayout.SOUTH);
    }
    
    private void setupEventHandlers() {
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performLogin();
            }
        });
        
        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        
        // Add key listener for Enter key
        KeyListener enterKeyListener = new KeyListener() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    performLogin();
                }
            }
            
            @Override
            public void keyReleased(KeyEvent e) {}
            
            @Override
            public void keyTyped(KeyEvent e) {}
        };
        
        usernameField.addKeyListener(enterKeyListener);
        passwordField.addKeyListener(enterKeyListener);
    }
    
    private void performLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        
        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Please enter both username and password", 
                "Login Error", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        User user = userDAO.authenticateUser(username, password);
        
        if (user != null) {
            JOptionPane.showMessageDialog(this, 
                "Login successful! Welcome " + user.getFullName(), 
                "Success", 
                JOptionPane.INFORMATION_MESSAGE);
            
            // Open appropriate dashboard based on user role
            openDashboard(user);
            
            // Close login frame
            this.dispose();
        } else {
            JOptionPane.showMessageDialog(this, 
                "Invalid username or password", 
                "Login Failed", 
                JOptionPane.ERROR_MESSAGE);
            passwordField.setText("");
            usernameField.requestFocus();
        }
    }
    
    private void openDashboard(User user) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    switch (user.getRole().toLowerCase()) {
                        case "admin":
                            new BeautifulAdminDashboard(user).setVisible(true);
                            break;
                        case "staff":
                            new StaffDashboard(user).setVisible(true);
                            break;
                        case "student":
                            new StudentDashboard(user).setVisible(true);
                            break;
                        default:
                            JOptionPane.showMessageDialog(null, 
                                "Unknown user role: " + user.getRole(), 
                                "Error", 
                                JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(null, 
                        "Error opening dashboard: " + e.getMessage(), 
                        "Error", 
                        JOptionPane.ERROR_MESSAGE);
                    e.printStackTrace();
                }
            }
        });
    }
    
    private void setupFrame() {
        setTitle("School Helpdesk - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // Make full screen
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        
        // Alternative approach for true full screen (uncomment if needed):
        // GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        // if (gd.isFullScreenSupported()) {
        //     setUndecorated(true);
        //     gd.setFullScreenWindow(this);
        // } else {
        //     setExtendedState(JFrame.MAXIMIZED_BOTH);
        // }
        
        // Set window icon
        try {
            setIconImage(new ImageIcon(getClass().getResource("/images/helpdesk_icon.png")).getImage());
        } catch (Exception e) {
            // Icon not found, continue without it
        }
    }
    
    @Override
    public void dispose() {
        if (userDAO != null) {
            userDAO.close();
        }
        super.dispose();
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    // Set look and feel
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                
                new LoginFrame().setVisible(true);
            }
        });
    }
}
