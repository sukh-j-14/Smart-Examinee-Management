package ui;

import model.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Staff Dashboard GUI Class
 * 
 * Main navigation hub for staff users in the Smart Examinee Management System.
 * 
 * @author SEMS Development Team
 * @version 1.0
 */
public class StaffDashboard extends JFrame {
    
    private User currentUser;
    
    /**
     * Constructor - Initializes the staff dashboard
     * 
     * @param user the logged-in staff user
     */
    public StaffDashboard(User user) {
        this.currentUser = user;
        
        // Setup the frame
        initializeFrame();
        
        // Create and add components
        initializeComponents();
        
        // Setup layout
        setupLayout();
    }
    
    /**
     * Initializes the JFrame properties
     */
    private void initializeFrame() {
        setTitle("SEMS - Staff Dashboard");
        setSize(950, 720); // Slightly larger for 6 buttons
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Center on screen
        
        // Handle window close event
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                int option = JOptionPane.showConfirmDialog(
                    StaffDashboard.this,
                    "Are you sure you want to exit?",
                    "Exit Confirmation",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);
                
                if (option == JOptionPane.YES_OPTION) {
                    System.exit(0);
                }
            }
        });
    }
    
    /**
     * Initializes all UI components
     */
    private void initializeComponents() {
        // Components will be initialized in setupLayout method
    }
    
    /**
     * Sets up the layout
     */
    private void setupLayout() {
        setLayout(new BorderLayout(10, 10));
        
        // Welcome Label
        JLabel welcomeLabel = new JLabel("Welcome Staff: " + 
            (currentUser != null ? currentUser.getFullName() : "Staff Member"));
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 18));
        welcomeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        welcomeLabel.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));
        
        // Navigation Panel with module buttons
        JPanel navigationPanel = createNavigationPanel();
        
        // Logout Button Panel
        JPanel logoutPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        logoutPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JButton logoutButton = new JButton("Logout");
        logoutButton.setFont(new Font("Arial", Font.BOLD, 12));
        logoutButton.setPreferredSize(new Dimension(100, 30));
        logoutButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleLogout();
            }
        });
        logoutPanel.add(logoutButton);
        
        // Add components to frame
        add(welcomeLabel, BorderLayout.NORTH);
        add(navigationPanel, BorderLayout.CENTER);
        add(logoutPanel, BorderLayout.SOUTH);
    }
    
    /**
     * Creates the navigation panel with module buttons (3x2 grid)
     * 
     * @return JPanel containing navigation buttons
     */
    private JPanel createNavigationPanel() {
        JPanel panel = new JPanel(new GridLayout(3, 2, 15, 15)); // 3 rows, 2 columns
        panel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));
        
        // Add all 6 buttons
        panel.add(createModuleButton("Manage Examinees", e -> handleManageExaminees()));
        panel.add(createModuleButton("Manage Exams", e -> handleManageExams()));
        panel.add(createModuleButton("Exam Registration", e -> handleExamRegistration()));
        panel.add(createModuleButton("Enter Results", e -> handleEnterResults()));
        panel.add(createModuleButton("View Results", e -> handleViewResults()));
        panel.add(createModuleButton("Reports", e -> handleReports())); // ← NEW BUTTON!
        
        return panel;
    }
    
    /**
     * Creates a styled module button
     * 
     * @param text the button text
     * @param listener the action listener
     * @return styled JButton
     */
    private JButton createModuleButton(String text, ActionListener listener) {
        JButton button = new JButton("<html><div style='text-align: center;'>" +
            "<b>" + text + "</b></div></html>");
        button.setFont(new Font("Arial", Font.PLAIN, 14));
        button.setPreferredSize(new Dimension(200, 100));
        button.setMinimumSize(new Dimension(200, 100));
        button.setMaximumSize(new Dimension(250, 120));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setFocusPainted(true);
        button.addActionListener(listener);
        return button;
    }
    
    /**
     * Handles Manage Examinees button click
     * Opens the ManageExamineesFrame
     */
    private void handleManageExaminees() {
        SwingUtilities.invokeLater(() -> {
            new ManageExamineesFrame().setVisible(true);
        });
    }
    
    /**
     * Handles Manage Exams button click
     * Opens the ManageExamsFrame
     */
    private void handleManageExams() {
        SwingUtilities.invokeLater(() -> {
            new ManageExamsFrame().setVisible(true);
        });
    }
    
    /**
     * Handles Exam Registration button click
     * Opens the ExamRegistrationFrame
     */
    private void handleExamRegistration() {
        SwingUtilities.invokeLater(() -> {
            new ExamRegistrationFrame().setVisible(true);
        });
    }
    
    /**
     * Handles Enter Results button click
     * Opens the EnterResultsFrame
     */
    private void handleEnterResults() {
        SwingUtilities.invokeLater(() -> {
            new EnterResultsFrame().setVisible(true);
        });
    }
    
    /**
     * Handles View Results button click
     * Opens the ViewResultsFrame
     */
    private void handleViewResults() {
        SwingUtilities.invokeLater(() -> {
            new ViewResultsFrame().setVisible(true);
        });
    }
    
    /**
     * Handles Reports button click
     * Opens the ReportsDashboard
     */
    private void handleReports() {
        SwingUtilities.invokeLater(() -> {
            new ReportsDashboard().setVisible(true);
        });
    }
    
    /**
     * Handles logout action
     */
    private void handleLogout() {
        int option = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to logout?",
            "Logout Confirmation",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE);
        
        if (option == JOptionPane.YES_OPTION) {
            // Close dashboard
            dispose();
            
            // Open login page
            SwingUtilities.invokeLater(() -> {
                new LoginPage().setVisible(true);
            });
        }
    }
}