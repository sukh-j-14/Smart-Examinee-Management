/**
 * Main Entry Point for Smart Examinee Management System (SEMS)
 * 
 * This class serves as the application entry point and launches the
 * login interface using Java Swing. All GUI components are initialized
 * on the Event Dispatch Thread (EDT) to ensure thread safety.
 * 
 * To run the application, execute:
 *   java Main
 * 
 * @author SEMS Development Team
 * @version 1.0
 */
public class Main {
    
    /**
     * Main method - Entry point of the application
     * 
     * Initializes and displays the LoginPage on the Event Dispatch Thread (EDT)
     * to ensure proper Swing threading and thread safety.
     * 
     * @param args command line arguments (not used)
     */
    public static void main(String[] args) {
        // Launch the login page on the Event Dispatch Thread (EDT)
        // This ensures proper Swing initialization and thread safety
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                // Create and display the login page
                new ui.LoginPage().setVisible(true);
            }
        });
    }
}

