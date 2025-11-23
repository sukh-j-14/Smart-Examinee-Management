package ui;

import dao.ExamineeDAO;
import model.Examinee;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Manage Examinees Frame GUI Class
 * 
 * Provides a comprehensive interface for managing examinees in the
 * Smart Examinee Management System (SEMS).
 * 
 * Features:
 * - View all examinees in a table
 * - Search examinees by keyword
 * - Add new examinees
 * - Edit existing examinees
 * - Delete examinees
 * - Form validation and error handling
 * 
 * @author SEMS Development Team
 * @version 1.0
 */
public class ManageExamineesFrame extends JFrame {
    
    // DAO for database operations
    private ExamineeDAO examineeDAO;
    
    // Table components
    private JTable examineesTable;
    private DefaultTableModel tableModel;
    private JScrollPane tableScrollPane;
    
    // Search components
    private JTextField searchField;
    private JButton searchButton;
    private JButton refreshButton;
    
    // Form components
    private JTextField registrationNumberField;
    private JTextField firstNameField;
    private JTextField lastNameField;
    private JTextField emailField;
    private JTextField phoneField;
    private JTextField dateOfBirthField;
    private JTextField addressField;
    private JTextField cityField;
    private JTextField stateField;
    private JTextField pincodeField;
    
    // Action buttons
    private JButton saveButton;
    private JButton clearButton;
    private JButton deleteButton;
    
    // Currently selected examinee ID (for editing)
    private int selectedExamineeId = -1;
    
    // Date format for date input
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    
    /**
     * Constructor - Initializes and displays the manage examinees frame
     */
    public ManageExamineesFrame() {
        // Initialize DAO
        examineeDAO = new ExamineeDAO();
        
        // Setup the frame
        initializeFrame();
        
        // Create and add components
        initializeComponents();
        
        // Setup layout
        setupLayout();
        
        // Setup event listeners
        setupEventListeners();
        
        // Load initial data
        loadExaminees();
        
        // Auto-focus search field
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                searchField.requestFocus();
            }
        });
    }
    
    /**
     * Initializes the JFrame properties
     */
    private void initializeFrame() {
        setTitle("Manage Examinees - SEMS");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null); // Center on screen
        setResizable(true);
    }
    
    /**
     * Initializes all UI components
     */
    private void initializeComponents() {
        // Search Panel Components
        searchField = new JTextField(20);
        searchField.setToolTipText("Search examinees by name, email, or registration number...");
        searchField.setFont(new Font("Arial", Font.PLAIN, 12));
        
        searchButton = new JButton("Search");
        searchButton.setFont(new Font("Arial", Font.BOLD, 12));
        searchButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        refreshButton = new JButton("Refresh");
        refreshButton.setFont(new Font("Arial", Font.BOLD, 12));
        refreshButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Table Components
        String[] columnNames = {"ID", "Reg Number", "Name", "Email", "Phone", "DOB", "City"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table non-editable
            }
        };
        
        examineesTable = new JTable(tableModel);
        examineesTable.setFont(new Font("Arial", Font.PLAIN, 12));
        examineesTable.setRowHeight(25);
        examineesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        examineesTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        examineesTable.getTableHeader().setReorderingAllowed(false);
        
        // Set column widths
        examineesTable.getColumnModel().getColumn(0).setPreferredWidth(30);  // ID
        examineesTable.getColumnModel().getColumn(1).setPreferredWidth(100); // Reg Number
        examineesTable.getColumnModel().getColumn(2).setPreferredWidth(150); // Name
        examineesTable.getColumnModel().getColumn(3).setPreferredWidth(180); // Email
        examineesTable.getColumnModel().getColumn(4).setPreferredWidth(120); // Phone
        examineesTable.getColumnModel().getColumn(5).setPreferredWidth(100); // DOB
        examineesTable.getColumnModel().getColumn(6).setPreferredWidth(100); // City
        
        // Hide ID column (still in model for reference)
        examineesTable.removeColumn(examineesTable.getColumnModel().getColumn(0));
        
        tableScrollPane = new JScrollPane(examineesTable);
        tableScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        tableScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        
        // Form Components
        registrationNumberField = new JTextField(20);
        registrationNumberField.setFont(new Font("Arial", Font.PLAIN, 12));
        
        firstNameField = new JTextField(20);
        firstNameField.setFont(new Font("Arial", Font.PLAIN, 12));
        
        lastNameField = new JTextField(20);
        lastNameField.setFont(new Font("Arial", Font.PLAIN, 12));
        
        emailField = new JTextField(20);
        emailField.setFont(new Font("Arial", Font.PLAIN, 12));
        
        phoneField = new JTextField(20);
        phoneField.setFont(new Font("Arial", Font.PLAIN, 12));
        
        dateOfBirthField = new JTextField(20);
        dateOfBirthField.setFont(new Font("Arial", Font.PLAIN, 12));
        dateOfBirthField.setToolTipText("Format: yyyy-MM-dd (e.g., 2000-05-15)");
        
        addressField = new JTextField(20);
        addressField.setFont(new Font("Arial", Font.PLAIN, 12));
        
        cityField = new JTextField(20);
        cityField.setFont(new Font("Arial", Font.PLAIN, 12));
        
        stateField = new JTextField(20);
        stateField.setFont(new Font("Arial", Font.PLAIN, 12));
        
        pincodeField = new JTextField(20);
        pincodeField.setFont(new Font("Arial", Font.PLAIN, 12));
        
        // Action Buttons
        saveButton = new JButton("Save");
        saveButton.setFont(new Font("Arial", Font.BOLD, 12));
        saveButton.setPreferredSize(new Dimension(100, 30));
        saveButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        clearButton = new JButton("Clear Form");
        clearButton.setFont(new Font("Arial", Font.BOLD, 12));
        clearButton.setPreferredSize(new Dimension(100, 30));
        clearButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        deleteButton = new JButton("Delete Selected");
        deleteButton.setFont(new Font("Arial", Font.BOLD, 12));
        deleteButton.setPreferredSize(new Dimension(120, 30));
        deleteButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        deleteButton.setEnabled(false); // Disabled initially
    }
    
    /**
     * Sets up the layout using BorderLayout
     */
    private void setupLayout() {
        setLayout(new BorderLayout(10, 10));
        
        // Search Panel (Top)
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        searchPanel.add(new JLabel("Search:"));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        searchPanel.add(refreshButton);
        add(searchPanel, BorderLayout.NORTH);
        
        // Table Panel (Center)
        add(tableScrollPane, BorderLayout.CENTER);
        
        // Form Panel (Bottom)
        JPanel formPanel = createFormPanel();
        add(formPanel, BorderLayout.SOUTH);
    }
    
    /**
     * Creates the form panel with all input fields and buttons
     * 
     * @return JPanel containing the form
     */
    private JPanel createFormPanel() {
        JPanel formPanel = new JPanel(new BorderLayout(10, 10));
        formPanel.setBorder(BorderFactory.createTitledBorder("Examinee Information"));
        
        // Form fields panel
        JPanel fieldsPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Row 1: Registration Number, First Name
        gbc.gridx = 0;
        gbc.gridy = 0;
        fieldsPanel.add(new JLabel("Registration Number *:"), gbc);
        gbc.gridx = 1;
        fieldsPanel.add(registrationNumberField, gbc);
        gbc.gridx = 2;
        fieldsPanel.add(new JLabel("First Name *:"), gbc);
        gbc.gridx = 3;
        fieldsPanel.add(firstNameField, gbc);
        
        // Row 2: Last Name, Email
        gbc.gridx = 0;
        gbc.gridy = 1;
        fieldsPanel.add(new JLabel("Last Name *:"), gbc);
        gbc.gridx = 1;
        fieldsPanel.add(lastNameField, gbc);
        gbc.gridx = 2;
        fieldsPanel.add(new JLabel("Email *:"), gbc);
        gbc.gridx = 3;
        fieldsPanel.add(emailField, gbc);
        
        // Row 3: Phone, Date of Birth
        gbc.gridx = 0;
        gbc.gridy = 2;
        fieldsPanel.add(new JLabel("Phone:"), gbc);
        gbc.gridx = 1;
        fieldsPanel.add(phoneField, gbc);
        gbc.gridx = 2;
        fieldsPanel.add(new JLabel("Date of Birth (yyyy-MM-dd):"), gbc);
        gbc.gridx = 3;
        fieldsPanel.add(dateOfBirthField, gbc);
        
        // Row 4: Address
        gbc.gridx = 0;
        gbc.gridy = 3;
        fieldsPanel.add(new JLabel("Address:"), gbc);
        gbc.gridx = 1;
        gbc.gridwidth = 3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        fieldsPanel.add(addressField, gbc);
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;
        
        // Row 5: City, State, Pincode
        gbc.gridx = 0;
        gbc.gridy = 4;
        fieldsPanel.add(new JLabel("City:"), gbc);
        gbc.gridx = 1;
        fieldsPanel.add(cityField, gbc);
        gbc.gridx = 2;
        fieldsPanel.add(new JLabel("State:"), gbc);
        gbc.gridx = 3;
        fieldsPanel.add(stateField, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 5;
        fieldsPanel.add(new JLabel("Pincode:"), gbc);
        gbc.gridx = 1;
        fieldsPanel.add(pincodeField, gbc);
        
        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.add(saveButton);
        buttonPanel.add(clearButton);
        buttonPanel.add(deleteButton);
        
        // Add panels to form panel
        formPanel.add(fieldsPanel, BorderLayout.CENTER);
        formPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        return formPanel;
    }
    
    /**
     * Sets up event listeners for all interactive components
     */
    private void setupEventListeners() {
        // Search Button Action
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleSearch();
            }
        });
        
        // Search Field Enter Key Action
        searchField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleSearch();
            }
        });
        
        // Refresh Button Action
        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                searchField.setText("");
                loadExaminees();
            }
        });
        
        // Save Button Action
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleSave();
            }
        });
        
        // Clear Button Action
        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearForm();
            }
        });
        
        // Delete Button Action
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleDelete();
            }
        });
        
        // Table Row Selection Action
        examineesTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                handleTableRowSelection();
            }
        });
    }
    
    /**
     * Loads all examinees into the table
     */
    private void loadExaminees() {
        try {
            List<Examinee> examinees = examineeDAO.getAllExaminees();
            populateTable(examinees);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error loading examinees: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    /**
     * Populates the table with examinee data
     * 
     * @param examinees the list of examinees to display
     */
    private void populateTable(List<Examinee> examinees) {
        // Clear existing data
        tableModel.setRowCount(0);
        
        // Add examinees to table
        for (Examinee examinee : examinees) {
            Object[] row = {
                examinee.getExamineeId(),
                examinee.getRegistrationNumber(),
                examinee.getFirstName() + " " + examinee.getLastName(),
                examinee.getEmail(),
                examinee.getPhone() != null ? examinee.getPhone() : "",
                examinee.getDateOfBirth() != null ? examinee.getDateOfBirth().toString() : "",
                examinee.getCity() != null ? examinee.getCity() : ""
            };
            tableModel.addRow(row);
        }
        
        // Clear selection
        selectedExamineeId = -1;
        deleteButton.setEnabled(false);
    }
    
    /**
     * Handles the search operation
     */
    private void handleSearch() {
        String keyword = searchField.getText().trim();
        
        if (keyword.isEmpty()) {
            loadExaminees();
            return;
        }
        
        try {
            List<Examinee> examinees = examineeDAO.searchExaminees(keyword);
            populateTable(examinees);
            
            if (examinees.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "No examinees found matching: " + keyword,
                    "Search Results",
                    JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error searching examinees: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    /**
     * Handles table row selection for editing
     */
    private void handleTableRowSelection() {
        int selectedRow = examineesTable.getSelectedRow();
        
        if (selectedRow >= 0) {
            // Get examinee ID from hidden column (column 0 in model)
            int modelRow = examineesTable.convertRowIndexToModel(selectedRow);
            selectedExamineeId = (Integer) tableModel.getValueAt(modelRow, 0);
            
            // Load examinee data into form
            loadExamineeIntoForm(selectedExamineeId);
            
            // Enable delete button
            deleteButton.setEnabled(true);
        }
    }
    
    /**
     * Loads examinee data into the form for editing
     * 
     * @param examineeId the ID of the examinee to load
     */
    private void loadExamineeIntoForm(int examineeId) {
        try {
            Examinee examinee = examineeDAO.getExamineeById(examineeId);
            
            if (examinee != null) {
                registrationNumberField.setText(examinee.getRegistrationNumber());
                firstNameField.setText(examinee.getFirstName());
                lastNameField.setText(examinee.getLastName());
                emailField.setText(examinee.getEmail());
                phoneField.setText(examinee.getPhone() != null ? examinee.getPhone() : "");
                
                if (examinee.getDateOfBirth() != null) {
                    dateOfBirthField.setText(examinee.getDateOfBirth().toString());
                } else {
                    dateOfBirthField.setText("");
                }
                
                addressField.setText(examinee.getAddress() != null ? examinee.getAddress() : "");
                cityField.setText(examinee.getCity() != null ? examinee.getCity() : "");
                stateField.setText(examinee.getState() != null ? examinee.getState() : "");
                pincodeField.setText(examinee.getPincode() != null ? examinee.getPincode() : "");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error loading examinee data: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    /**
     * Handles the save operation (add or update)
     */
    private void handleSave() {
        try {
            // Validate required fields
            if (!validateForm()) {
                return;
            }
            
            // Create Examinee object from form data
            Examinee examinee = createExamineeFromForm();
            
            boolean success = false;
            String message = "";
            
            if (selectedExamineeId > 0) {
                // Update existing examinee
                examinee.setExamineeId(selectedExamineeId);
                success = examineeDAO.updateExaminee(examinee);
                message = success ? "Examinee updated successfully!" : "Failed to update examinee.";
            } else {
                // Add new examinee
                success = examineeDAO.addExaminee(examinee);
                message = success ? "Examinee added successfully!" : "Failed to add examinee.";
            }
            
            if (success) {
                JOptionPane.showMessageDialog(this,
                    message,
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
                
                // Refresh table and clear form
                loadExaminees();
                clearForm();
            } else {
                JOptionPane.showMessageDialog(this,
                    message + "\nPlease check for duplicate email or registration number.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error saving examinee: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    /**
     * Validates the form data
     * 
     * @return true if valid, false otherwise
     */
    private boolean validateForm() {
        // Check required fields
        if (registrationNumberField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Registration Number is required.",
                "Validation Error",
                JOptionPane.WARNING_MESSAGE);
            registrationNumberField.requestFocus();
            return false;
        }
        
        if (firstNameField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "First Name is required.",
                "Validation Error",
                JOptionPane.WARNING_MESSAGE);
            firstNameField.requestFocus();
            return false;
        }
        
        if (lastNameField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Last Name is required.",
                "Validation Error",
                JOptionPane.WARNING_MESSAGE);
            lastNameField.requestFocus();
            return false;
        }
        
        if (emailField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Email is required.",
                "Validation Error",
                JOptionPane.WARNING_MESSAGE);
            emailField.requestFocus();
            return false;
        }
        
        // Validate email format (basic validation)
        String email = emailField.getText().trim();
        if (!email.contains("@") || !email.contains(".")) {
            JOptionPane.showMessageDialog(this,
                "Please enter a valid email address.",
                "Validation Error",
                JOptionPane.WARNING_MESSAGE);
            emailField.requestFocus();
            return false;
        }
        
        // Validate date format if provided
        String dobText = dateOfBirthField.getText().trim();
        if (!dobText.isEmpty()) {
            try {
                dateFormat.parse(dobText);
            } catch (ParseException e) {
                JOptionPane.showMessageDialog(this,
                    "Invalid date format. Please use yyyy-MM-dd (e.g., 2000-05-15).",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE);
                dateOfBirthField.requestFocus();
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * Creates an Examinee object from form data
     * 
     * @return Examinee object populated with form data
     */
    private Examinee createExamineeFromForm() {
        Examinee examinee = new Examinee();
        
        examinee.setRegistrationNumber(registrationNumberField.getText().trim());
        examinee.setFirstName(firstNameField.getText().trim());
        examinee.setLastName(lastNameField.getText().trim());
        examinee.setEmail(emailField.getText().trim());
        examinee.setPhone(phoneField.getText().trim().isEmpty() ? null : phoneField.getText().trim());
        
        // Parse date of birth
        String dobText = dateOfBirthField.getText().trim();
        if (!dobText.isEmpty()) {
            try {
                java.util.Date parsedDate = dateFormat.parse(dobText);
                examinee.setDateOfBirth(new Date(parsedDate.getTime()));
            } catch (ParseException e) {
                examinee.setDateOfBirth(null);
            }
        } else {
            examinee.setDateOfBirth(null);
        }
        
        examinee.setAddress(addressField.getText().trim().isEmpty() ? null : addressField.getText().trim());
        examinee.setCity(cityField.getText().trim().isEmpty() ? null : cityField.getText().trim());
        examinee.setState(stateField.getText().trim().isEmpty() ? null : stateField.getText().trim());
        examinee.setPincode(pincodeField.getText().trim().isEmpty() ? null : pincodeField.getText().trim());
        
        return examinee;
    }
    
    /**
     * Handles the delete operation
     */
    private void handleDelete() {
        if (selectedExamineeId <= 0) {
            JOptionPane.showMessageDialog(this,
                "Please select an examinee to delete.",
                "No Selection",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Confirm deletion
        int option = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete this examinee?\n" +
            "This will also delete all associated exam registrations and results.",
            "Confirm Delete",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
        
        if (option == JOptionPane.YES_OPTION) {
            try {
                boolean success = examineeDAO.deleteExaminee(selectedExamineeId);
                
                if (success) {
                    JOptionPane.showMessageDialog(this,
                        "Examinee deleted successfully!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                    
                    // Refresh table and clear form
                    loadExaminees();
                    clearForm();
                } else {
                    JOptionPane.showMessageDialog(this,
                        "Failed to delete examinee.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                }
                
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                    "Error deleting examinee: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }
    
    /**
     * Clears all form fields and resets selection
     */
    private void clearForm() {
        registrationNumberField.setText("");
        firstNameField.setText("");
        lastNameField.setText("");
        emailField.setText("");
        phoneField.setText("");
        dateOfBirthField.setText("");
        addressField.setText("");
        cityField.setText("");
        stateField.setText("");
        pincodeField.setText("");
        
        selectedExamineeId = -1;
        examineesTable.clearSelection();
        deleteButton.setEnabled(false);
        
        // Focus on registration number field
        registrationNumberField.requestFocus();
    }
    
    /**
     * Main method for testing (optional)
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
        
        // Create and show frame on EDT
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new ManageExamineesFrame().setVisible(true);
            }
        });
    }
}

