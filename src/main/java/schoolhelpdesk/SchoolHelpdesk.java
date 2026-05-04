/*
 * Decompiled with CFR 0.152.
 */
package schoolhelpdesk;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import schoolhelpdesk.gui.LoginFrame;
import schoolhelpdesk.util.LoggerConfig;

public class SchoolHelpdesk {
    public static void main(String[] args) {
        LoggerConfig.info("Starting School Helpdesk Application...");
        SwingUtilities.invokeLater(new Runnable(){

            @Override
            public void run() {
                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                    LoggerConfig.info("Look and feel set successfully");
                }
                catch (Exception e) {
                    LoggerConfig.error("Error setting look and feel: " + e.getMessage());
                }
                try {
                    new LoginFrame().setVisible(true);
                    LoggerConfig.info("Login frame displayed successfully");
                }
                catch (Exception e) {
                    LoggerConfig.error("Error starting application: " + e.getMessage(), e);
                }
            }
        });
    }
}

