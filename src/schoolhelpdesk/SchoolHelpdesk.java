package schoolhelpdesk;

import schoolhelpdesk.gui.LoginFrame;
import schoolhelpdesk.util.LoggerConfig;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class SchoolHelpdesk {
    public static void main(String[] args) {
        // Initialize logging configuration
        LoggerConfig.info("Starting School Helpdesk Application...");
        
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    // Set system look and feel
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                    LoggerConfig.info("Look and feel set successfully");
                } catch (Exception e) {
                    LoggerConfig.error("Error setting look and feel: " + e.getMessage());
                }
                
                try {
                    // Start the application with login frame
                    new LoginFrame().setVisible(true);
                    LoggerConfig.info("Login frame displayed successfully");
                } catch (Exception e) {
                    LoggerConfig.error("Error starting application: " + e.getMessage(), e);
                }
            }
        });
    }
}
