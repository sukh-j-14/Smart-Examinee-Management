package ui;

import dao.UserDAO;
import model.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Login Page GUI Class
 * 
 * Provides the login interface for the Smart Examinee Management System (SEMS).
 * Handles user authentication and role-based navigation to appropriate dashboards.
 * 
 * Features:
 * - Username and password input fields
 * - Login and Exit buttons
 * - Role-based dashboard navigation (admin/staff)
 * - Secure password handling
 * - Exception handling
 * 
 * @author SEMS Development Team
 * @version 1.0
 */
public class LoginPage extends JFrame {
    
    // UI Components
    private JLabel titleLabel;
    private JLabel usernameLabel;
    private JLabel passwordLabel;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton exitButton;
    
    // DAO for user authentication
    private UserDAO userDAO;
    
    /**
     * Constructor - Initializes and displays the login page
     */
    public LoginPage() {
        // Initialize UserDAO
        userDAO = new UserDAO();
        
        // Setup the frame
        initializeFrame();
        
        // Create and add components
        initializeComponents();
        
        // Setup layout
        setupLayout();
        
        // Setup event listeners
        setupEventListeners();
    }
    
    /**
     * Initializes the JFrame properties
     */
    private void initializeFrame() {
        setTitle("SEMS - Login");
        setSize(400, 250);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE); // Handle close via window adapter
        setResizable(false);
        setLocationRelativeTo(null); // Center on screen
        
        // Handle window close event
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                handleExit();
            }
        });
    }
    
    /**
     * Initializes all UI components
     */
    private void initializeComponents() {
        // Title Label
        titleLabel = new JLabel("Smart Examinee Management System");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        // Username Label
        usernameLabel = new JLabel("Username:");
        usernameLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        
        // Password Label
        passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        
        // Username Text Field
        usernameField = new JTextField(20);
        usernameField.setFont(new Font("Arial", Font.PLAIN, 12));
        
        // Password Field
        passwordField = new JPasswordField(20);
        passwordField.setFont(new Font("Arial", Font.PLAIN, 12));
        
        // Login Button
        loginButton = new JButton("Login");
        loginButton.setFont(new Font("Arial", Font.BOLD, 12));
        loginButton.setPreferredSize(new Dimension(100, 30));
        loginButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Exit Button
        exitButton = new JButton("Exit");
        exitButton.setFont(new Font("Arial", Font.BOLD, 12));
        exitButton.setPreferredSize(new Dimension(100, 30));
        exitButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }
    
    /**
     * Sets up the layout using GridBagLayout for a clean, centered design
     */
    private void setupLayout() {
        setLayout(new BorderLayout(10, 10));
        
        // Main panel with padding
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Title
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        mainPanel.add(titleLabel, gbc);
        
        // Separator
        gbc.gridy = 1;
        mainPanel.add(new JSeparator(), gbc);
        
        // Username Label
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.EAST;
        mainPanel.add(usernameLabel, gbc);
        
        // Username Field
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        mainPanel.add(usernameField, gbc);
        
        // Password Label
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        mainPanel.add(passwordLabel, gbc);
        
        // Password Field
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        mainPanel.add(passwordField, gbc);
        
        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.add(loginButton);
        buttonPanel.add(exitButton);
        
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        mainPanel.add(buttonPanel, gbc);
        
        // Add main panel to frame
        add(mainPanel, BorderLayout.CENTER);
        
        // Set enter key to trigger login
        getRootPane().setDefaultButton(loginButton);
    }
    
    /**
     * Sets up event listeners for buttons
     */
    private void setupEventListeners() {
        // Login Button Action
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleLogin();
            }
        });
        
        // Exit Button Action
        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleExit();
            }
        });
        
        // Password Field Enter Key Action
        passwordField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleLogin();
            }
        });
    }
    
    /**
     * Handles the login process
     */
    private void handleLogin() {
        try {
            // Get username
            String username = usernameField.getText().trim();
            
            // Get password (handle securely)
            char[] passwordChars = passwordField.getPassword();
            String password = new String(passwordChars);
            
            // Clear password field immediately after reading
            passwordField.setText("");
            
            // Validate input
            if (username.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "Please enter your username.",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE);
                usernameField.requestFocus();
                clearPassword(passwordChars); // Clear password in memory
                return;
            }
            
            if (password.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "Please enter your password.",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE);
                passwordField.requestFocus();
                clearPassword(passwordChars); // Clear password in memory
                return;
            }
            
            // Authenticate user
            User user = userDAO.authenticateUser(username, password);
            
            // Clear password from memory after use
            clearPassword(passwordChars);
            
            // Check authentication result
            if (user != null) {
                // Login successful
                String role = user.getRole();
                
                // Navigate based on role
                if ("admin".equalsIgnoreCase(role)) {
                    // Open Admin Dashboard
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            new AdminDashboard(user).setVisible(true);
                        }
                    });
                    // Close login page
                    dispose();
                    
                } else if ("staff".equalsIgnoreCase(role)) {
                    // Open Staff Dashboard
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            new StaffDashboard(user).setVisible(true);
                        }
                    });
                    // Close login page
                    dispose();
                    
                } else {
                    // Unknown role
                    JOptionPane.showMessageDialog(this,
                        "Invalid user role. Please contact administrator.",
                        "Access Denied",
                        JOptionPane.ERROR_MESSAGE);
                }
                
            } else {
                // Login failed
                JOptionPane.showMessageDialog(this,
                    "Invalid username or password.\nPlease try again.",
                    "Login Failed",
                    JOptionPane.ERROR_MESSAGE);
                usernameField.requestFocus();
                usernameField.selectAll();
            }
            
        } catch (Exception e) {
            // Handle unexpected errors
            JOptionPane.showMessageDialog(this,
                "An error occurred during login:\n" + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    /**
     * Handles the exit process with confirmation
     */
    private void handleExit() {
        int option = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to exit?",
            "Exit Confirmation",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE);
        
        if (option == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }
    
    /**
     * Clears password from memory for security
     * 
     * @param passwordChars the password character array to clear
     */
    private void clearPassword(char[] passwordChars) {
        if (passwordChars != null) {
            // Overwrite the password array with zeros
            java.util.Arrays.fill(passwordChars, '\0');
        }
    }
    
    /**
     * Main method to launch the login page
     * 
     * @param args command line arguments (not used)
     */
    public static void main(String[] args) {
        // Set Look and Feel to System default
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // Create and show login page on EDT
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new LoginPage().setVisible(true);
            }
        });
    }
}

