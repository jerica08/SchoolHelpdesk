/*
 * Decompiled with CFR 0.152.
 */
package schoolhelpdesk;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import schoolhelpdesk.gui.LoginFrame;

public class SimpleMain {
    public static void main(String[] args) {
        System.out.println("Starting School Helpdesk Application...");
        SwingUtilities.invokeLater(new Runnable(){

            @Override
            public void run() {
                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                    System.out.println("Look and feel set successfully");
                }
                catch (Exception e) {
                    System.err.println("Error setting look and feel: " + e.getMessage());
                }
                try {
                    new LoginFrame().setVisible(true);
                    System.out.println("Login frame displayed successfully");
                }
                catch (Exception e) {
                    System.err.println("Error starting application: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        });
    }
}

