package ui;

import dao.ExamDAO;
import model.Exam;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Date;
import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Manage Exams Frame GUI Class
 * 
 * Provides a comprehensive interface for managing exams in the
 * Smart Examinee Management System (SEMS).
 * 
 * Features:
 * - View all exams in a table
 * - Search exams by keyword
 * - Add new exams
 * - Edit existing exams
 * - Delete exams
 * - Form validation and error handling
 * 
 * @author SEMS Development Team
 * @version 1.0
 */
public class ManageExamsFrame extends JFrame {
    
    // DAO for database operations
    private ExamDAO examDAO;
    
    // Table components
    private JTable examsTable;
    private DefaultTableModel tableModel;
    private JScrollPane tableScrollPane;
    
    // Search components
    private JTextField searchField;
    private JButton searchButton;
    private JButton refreshButton;
    
    // Form components
    private JTextField examNameField;
    private JTextField examCodeField;
    private JTextArea descriptionArea;
    private JTextField examDateField;
    private JTextField startTimeField;
    private JTextField endTimeField;
    private JTextField venueField;
    private JTextField maxCapacityField;
    private JComboBox<String> statusComboBox;
    
    // Action buttons
    private JButton saveButton;
    private JButton clearButton;
    private JButton deleteButton;
    
    // Currently selected exam ID (for editing)
    private int selectedExamId = -1;
    
    // Date format for date input
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    
    // Time format for time input
    private SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
    
    /**
     * Constructor - Initializes and displays the manage exams frame
     */
    public ManageExamsFrame() {
        // Initialize DAO
        examDAO = new ExamDAO();
        
        // Setup the frame
        initializeFrame();
        
        // Create and add components
        initializeComponents();
        
        // Setup layout
        setupLayout();
        
        // Setup event listeners
        setupEventListeners();
        
        // Load initial data
        loadExams();
        
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
        setTitle("Manage Exams - SEMS");
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
        searchField.setToolTipText("Search exams by name, code, or venue...");
        searchField.setFont(new Font("Arial", Font.PLAIN, 12));
        
        searchButton = new JButton("Search");
        searchButton.setFont(new Font("Arial", Font.BOLD, 12));
        searchButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        refreshButton = new JButton("Refresh");
        refreshButton.setFont(new Font("Arial", Font.BOLD, 12));
        refreshButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Table Components
        String[] columnNames = {"ID", "Exam Code", "Name", "Date", "Time", "Venue", "Status", "Capacity"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table non-editable
            }
        };
        
        examsTable = new JTable(tableModel);
        examsTable.setFont(new Font("Arial", Font.PLAIN, 12));
        examsTable.setRowHeight(25);
        examsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        examsTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        examsTable.getTableHeader().setReorderingAllowed(false);
        
        // Set column widths
        examsTable.getColumnModel().getColumn(0).setPreferredWidth(30);  // ID (hidden)
        examsTable.getColumnModel().getColumn(1).setPreferredWidth(100); // Exam Code
        examsTable.getColumnModel().getColumn(2).setPreferredWidth(200); // Name
        examsTable.getColumnModel().getColumn(3).setPreferredWidth(100); // Date
        examsTable.getColumnModel().getColumn(4).setPreferredWidth(120); // Time
        examsTable.getColumnModel().getColumn(5).setPreferredWidth(150); // Venue
        examsTable.getColumnModel().getColumn(6).setPreferredWidth(100); // Status
        examsTable.getColumnModel().getColumn(7).setPreferredWidth(80);  // Capacity
        
        // Hide ID column (still in model for reference)
        examsTable.removeColumn(examsTable.getColumnModel().getColumn(0));
        
        tableScrollPane = new JScrollPane(examsTable);
        tableScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        tableScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        
        // Form Components
        examNameField = new JTextField(20);
        examNameField.setFont(new Font("Arial", Font.PLAIN, 12));
        
        examCodeField = new JTextField(20);
        examCodeField.setFont(new Font("Arial", Font.PLAIN, 12));
        
        descriptionArea = new JTextArea(3, 30);
        descriptionArea.setFont(new Font("Arial", Font.PLAIN, 12));
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        
        examDateField = new JTextField(20);
        examDateField.setFont(new Font("Arial", Font.PLAIN, 12));
        examDateField.setToolTipText("Format: yyyy-MM-dd (e.g., 2024-12-15)");
        
        startTimeField = new JTextField(10);
        startTimeField.setFont(new Font("Arial", Font.PLAIN, 12));
        startTimeField.setToolTipText("Format: HH:mm (e.g., 10:00)");
        
        endTimeField = new JTextField(10);
        endTimeField.setFont(new Font("Arial", Font.PLAIN, 12));
        endTimeField.setToolTipText("Format: HH:mm (e.g., 13:00)");
        
        venueField = new JTextField(20);
        venueField.setFont(new Font("Arial", Font.PLAIN, 12));
        
        maxCapacityField = new JTextField(10);
        maxCapacityField.setFont(new Font("Arial", Font.PLAIN, 12));
        maxCapacityField.setText("100"); // Default value
        
        // Status ComboBox
        String[] statusOptions = {"scheduled", "ongoing", "completed", "cancelled"};
        statusComboBox = new JComboBox<>(statusOptions);
        statusComboBox.setFont(new Font("Arial", Font.PLAIN, 12));
        statusComboBox.setSelectedItem("scheduled");
        
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
        formPanel.setBorder(BorderFactory.createTitledBorder("Exam Information"));
        
        // Form fields panel
        JPanel fieldsPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Row 1: Exam Name, Exam Code
        gbc.gridx = 0;
        gbc.gridy = 0;
        fieldsPanel.add(new JLabel("Exam Name *:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        fieldsPanel.add(examNameField, gbc);
        gbc.gridx = 2;
        fieldsPanel.add(new JLabel("Exam Code *:"), gbc);
        gbc.gridx = 3;
        fieldsPanel.add(examCodeField, gbc);
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        
        // Row 2: Description (full width)
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 4;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        JPanel descriptionPanel = new JPanel(new BorderLayout());
        descriptionPanel.add(new JLabel("Description:"), BorderLayout.WEST);
        descriptionPanel.add(new JScrollPane(descriptionArea), BorderLayout.CENTER);
        fieldsPanel.add(descriptionPanel, gbc);
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        
        // Row 3: Exam Date, Start Time, End Time
        gbc.gridx = 0;
        gbc.gridy = 2;
        fieldsPanel.add(new JLabel("Exam Date * (yyyy-MM-dd):"), gbc);
        gbc.gridx = 1;
        fieldsPanel.add(examDateField, gbc);
        gbc.gridx = 2;
        fieldsPanel.add(new JLabel("Start Time * (HH:mm):"), gbc);
        gbc.gridx = 3;
        fieldsPanel.add(startTimeField, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 3;
        fieldsPanel.add(new JLabel("End Time * (HH:mm):"), gbc);
        gbc.gridx = 1;
        fieldsPanel.add(endTimeField, gbc);
        
        // Row 4: Venue, Max Capacity, Status
        gbc.gridx = 2;
        fieldsPanel.add(new JLabel("Venue:"), gbc);
        gbc.gridx = 3;
        fieldsPanel.add(venueField, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 4;
        fieldsPanel.add(new JLabel("Max Capacity:"), gbc);
        gbc.gridx = 1;
        fieldsPanel.add(maxCapacityField, gbc);
        gbc.gridx = 2;
        fieldsPanel.add(new JLabel("Status:"), gbc);
        gbc.gridx = 3;
        fieldsPanel.add(statusComboBox, gbc);
        
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
                loadExams();
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
        examsTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                handleTableRowSelection();
            }
        });
    }
    
    /**
     * Loads all exams into the table
     */
    private void loadExams() {
        try {
            List<Exam> exams = examDAO.getAllExams();
            populateTable(exams);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error loading exams: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    /**
     * Populates the table with exam data
     * 
     * @param exams the list of exams to display
     */
    private void populateTable(List<Exam> exams) {
        // Clear existing data
        tableModel.setRowCount(0);
        
        // Add exams to table
        for (Exam exam : exams) {
            String timeRange = "";
            if (exam.getStartTime() != null && exam.getEndTime() != null) {
                timeRange = exam.getStartTime().toString().substring(0, 5) + 
                           " - " + exam.getEndTime().toString().substring(0, 5);
            }
            
            Object[] row = {
                exam.getExamId(),
                exam.getExamCode(),
                exam.getExamName(),
                exam.getExamDate() != null ? exam.getExamDate().toString() : "",
                timeRange,
                exam.getVenue() != null ? exam.getVenue() : "",
                exam.getStatus() != null ? exam.getStatus() : "",
                exam.getMaxCapacity() + "/" + exam.getCurrentRegistrations()
            };
            tableModel.addRow(row);
        }
        
        // Clear selection
        selectedExamId = -1;
        deleteButton.setEnabled(false);
    }
    
    /**
     * Handles the search operation
     */
    private void handleSearch() {
        String keyword = searchField.getText().trim();
        
        if (keyword.isEmpty()) {
            loadExams();
            return;
        }
        
        try {
            List<Exam> exams = examDAO.searchExams(keyword);
            populateTable(exams);
            
            if (exams.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "No exams found matching: " + keyword,
                    "Search Results",
                    JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error searching exams: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    /**
     * Handles table row selection for editing
     */
    private void handleTableRowSelection() {
        int selectedRow = examsTable.getSelectedRow();
        
        if (selectedRow >= 0) {
            // Get exam ID from hidden column (column 0 in model)
            int modelRow = examsTable.convertRowIndexToModel(selectedRow);
            selectedExamId = (Integer) tableModel.getValueAt(modelRow, 0);
            
            // Load exam data into form
            loadExamIntoForm(selectedExamId);
            
            // Enable delete button
            deleteButton.setEnabled(true);
        }
    }
    
    /**
     * Loads exam data into the form for editing
     * 
     * @param examId the ID of the exam to load
     */
    private void loadExamIntoForm(int examId) {
        try {
            Exam exam = examDAO.getExamById(examId);
            
            if (exam != null) {
                examNameField.setText(exam.getExamName());
                examCodeField.setText(exam.getExamCode());
                descriptionArea.setText(exam.getDescription() != null ? exam.getDescription() : "");
                
                if (exam.getExamDate() != null) {
                    examDateField.setText(exam.getExamDate().toString());
                } else {
                    examDateField.setText("");
                }
                
                if (exam.getStartTime() != null) {
                    startTimeField.setText(exam.getStartTime().toString().substring(0, 5));
                } else {
                    startTimeField.setText("");
                }
                
                if (exam.getEndTime() != null) {
                    endTimeField.setText(exam.getEndTime().toString().substring(0, 5));
                } else {
                    endTimeField.setText("");
                }
                
                venueField.setText(exam.getVenue() != null ? exam.getVenue() : "");
                maxCapacityField.setText(String.valueOf(exam.getMaxCapacity()));
                
                if (exam.getStatus() != null) {
                    statusComboBox.setSelectedItem(exam.getStatus());
                } else {
                    statusComboBox.setSelectedItem("scheduled");
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error loading exam data: " + e.getMessage(),
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
            
            // Create Exam object from form data
            Exam exam = createExamFromForm();
            
            boolean success = false;
            String message = "";
            
            if (selectedExamId > 0) {
                // Update existing exam - preserve currentRegistrations
                Exam existingExam = examDAO.getExamById(selectedExamId);
                if (existingExam != null) {
                    exam.setCurrentRegistrations(existingExam.getCurrentRegistrations());
                    exam.setCreatedBy(existingExam.getCreatedBy()); // Preserve created_by
                }
                exam.setExamId(selectedExamId);
                success = examDAO.updateExam(exam);
                message = success ? "Exam updated successfully!" : "Failed to update exam.";
            } else {
                // Add new exam
                // Hardcode createdBy = 1 for now (later use logged-in user)
                exam.setCreatedBy(1);
                exam.setCurrentRegistrations(0); // New exam starts with 0 registrations
                success = examDAO.addExam(exam);
                message = success ? "Exam added successfully!" : "Failed to add exam.";
            }
            
            if (success) {
                JOptionPane.showMessageDialog(this,
                    message,
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
                
                // Refresh table and clear form
                loadExams();
                clearForm();
            } else {
                JOptionPane.showMessageDialog(this,
                    message + "\nPlease check for duplicate exam code.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error saving exam: " + e.getMessage(),
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
        if (examNameField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Exam Name is required.",
                "Validation Error",
                JOptionPane.WARNING_MESSAGE);
            examNameField.requestFocus();
            return false;
        }
        
        if (examCodeField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Exam Code is required.",
                "Validation Error",
                JOptionPane.WARNING_MESSAGE);
            examCodeField.requestFocus();
            return false;
        }
        
        // Validate date format
        String dateText = examDateField.getText().trim();
        if (dateText.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Exam Date is required.",
                "Validation Error",
                JOptionPane.WARNING_MESSAGE);
            examDateField.requestFocus();
            return false;
        }
        try {
            dateFormat.parse(dateText);
        } catch (ParseException e) {
            JOptionPane.showMessageDialog(this,
                "Invalid date format. Please use yyyy-MM-dd (e.g., 2024-12-15).",
                "Validation Error",
                JOptionPane.WARNING_MESSAGE);
            examDateField.requestFocus();
            return false;
        }
        
        // Validate start time format
        String startTimeText = startTimeField.getText().trim();
        if (startTimeText.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Start Time is required.",
                "Validation Error",
                JOptionPane.WARNING_MESSAGE);
            startTimeField.requestFocus();
            return false;
        }
        try {
            timeFormat.parse(startTimeText);
        } catch (ParseException e) {
            JOptionPane.showMessageDialog(this,
                "Invalid start time format. Please use HH:mm (e.g., 10:00).",
                "Validation Error",
                JOptionPane.WARNING_MESSAGE);
            startTimeField.requestFocus();
            return false;
        }
        
        // Validate end time format
        String endTimeText = endTimeField.getText().trim();
        if (endTimeText.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "End Time is required.",
                "Validation Error",
                JOptionPane.WARNING_MESSAGE);
            endTimeField.requestFocus();
            return false;
        }
        try {
            timeFormat.parse(endTimeText);
        } catch (ParseException e) {
            JOptionPane.showMessageDialog(this,
                "Invalid end time format. Please use HH:mm (e.g., 13:00).",
                "Validation Error",
                JOptionPane.WARNING_MESSAGE);
            endTimeField.requestFocus();
            return false;
        }
        
        // Validate max capacity
        String capacityText = maxCapacityField.getText().trim();
        if (!capacityText.isEmpty()) {
            try {
                int capacity = Integer.parseInt(capacityText);
                if (capacity <= 0) {
                    JOptionPane.showMessageDialog(this,
                        "Max Capacity must be greater than 0.",
                        "Validation Error",
                        JOptionPane.WARNING_MESSAGE);
                    maxCapacityField.requestFocus();
                    return false;
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this,
                    "Max Capacity must be a valid number.",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE);
                maxCapacityField.requestFocus();
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * Creates an Exam object from form data
     * 
     * @return Exam object populated with form data
     */
    private Exam createExamFromForm() {
        Exam exam = new Exam();
        
        exam.setExamName(examNameField.getText().trim());
        exam.setExamCode(examCodeField.getText().trim());
        exam.setDescription(descriptionArea.getText().trim().isEmpty() ? null : descriptionArea.getText().trim());
        
        // Parse exam date
        try {
            java.util.Date parsedDate = dateFormat.parse(examDateField.getText().trim());
            exam.setExamDate(new Date(parsedDate.getTime()));
        } catch (ParseException e) {
            exam.setExamDate(null);
        }
        
        // Parse start time
        try {
            java.util.Date parsedStartTime = timeFormat.parse(startTimeField.getText().trim());
            exam.setStartTime(new Time(parsedStartTime.getTime()));
        } catch (ParseException e) {
            exam.setStartTime(null);
        }
        
        // Parse end time
        try {
            java.util.Date parsedEndTime = timeFormat.parse(endTimeField.getText().trim());
            exam.setEndTime(new Time(parsedEndTime.getTime()));
        } catch (ParseException e) {
            exam.setEndTime(null);
        }
        
        exam.setVenue(venueField.getText().trim().isEmpty() ? null : venueField.getText().trim());
        
        // Parse max capacity
        String capacityText = maxCapacityField.getText().trim();
        if (!capacityText.isEmpty()) {
            try {
                exam.setMaxCapacity(Integer.parseInt(capacityText));
            } catch (NumberFormatException e) {
                exam.setMaxCapacity(100); // Default
            }
        } else {
            exam.setMaxCapacity(100); // Default
        }
        
        exam.setStatus((String) statusComboBox.getSelectedItem());
        
        // Note: currentRegistrations and createdBy are handled in handleSave()
        // For new exams: set to 0 and set createdBy = 1
        // For updates: preserve existing values
        
        return exam;
    }
    
    /**
     * Handles the delete operation
     */
    private void handleDelete() {
        if (selectedExamId <= 0) {
            JOptionPane.showMessageDialog(this,
                "Please select an exam to delete.",
                "No Selection",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Confirm deletion
        int option = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete this exam?\n" +
            "This will also delete all associated exam registrations and results.",
            "Confirm Delete",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
        
        if (option == JOptionPane.YES_OPTION) {
            try {
                boolean success = examDAO.deleteExam(selectedExamId);
                
                if (success) {
                    JOptionPane.showMessageDialog(this,
                        "Exam deleted successfully!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                    
                    // Refresh table and clear form
                    loadExams();
                    clearForm();
                } else {
                    JOptionPane.showMessageDialog(this,
                        "Failed to delete exam.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                }
                
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                    "Error deleting exam: " + e.getMessage(),
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
        examNameField.setText("");
        examCodeField.setText("");
        descriptionArea.setText("");
        examDateField.setText("");
        startTimeField.setText("");
        endTimeField.setText("");
        venueField.setText("");
        maxCapacityField.setText("100");
        statusComboBox.setSelectedItem("scheduled");
        
        selectedExamId = -1;
        examsTable.clearSelection();
        deleteButton.setEnabled(false);
        
        // Focus on exam name field
        examNameField.requestFocus();
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
                new ManageExamsFrame().setVisible(true);
            }
        });
    }
}

