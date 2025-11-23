package ui;

import dao.ExamDAO;
import dao.ExamRegistrationDAO;
import dao.ResultDAO;
import model.Exam;
import model.ExamRegistration;
import model.Result;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.util.List;

/**
 * Enter Results Frame GUI Class
 * 
 * Allows admin/staff to enter and update exam results for registered examinees.
 * 
 * @author SEMS Development Team
 * @version 1.0
 */
public class EnterResultsFrame extends JFrame {
    
    private ExamDAO examDAO;
    private ExamRegistrationDAO registrationDAO;
    private ResultDAO resultDAO;
    
    // Top Panel: Exam Selection
    private JComboBox<Exam> examComboBox;
    private JButton refreshExamButton;
    
    // Main Table: Examinees with Marks Input
    private JTable resultsTable;
    private DefaultTableModel resultsTableModel;
    private JScrollPane resultsScrollPane;
    
    // Bottom Panel: Action Buttons
    private JButton saveAllButton;
    private JButton clearAllButton;
    
    // Currently selected exam
    private Exam selectedExam = null;
    
    public EnterResultsFrame() {
        examDAO = new ExamDAO();
        registrationDAO = new ExamRegistrationDAO();
        resultDAO = new ResultDAO();
        
        initializeFrame();
        initializeComponents();
        setupLayout();
        setupEventListeners();
        loadExamsIntoComboBox();
    }
    
    private void initializeFrame() {
        setTitle("Enter Results - SEMS");
        setSize(900, 650);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
    }
    
    private void initializeComponents() {
        // Exam Combo Box
        examComboBox = new JComboBox<>();
        examComboBox.setFont(new Font("Arial", Font.PLAIN, 12));
        examComboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                    boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Exam) {
                    Exam exam = (Exam) value;
                    setText(exam.getExamCode() + " - " + exam.getExamName());
                }
                return this;
            }
        });
        
        refreshExamButton = new JButton("Refresh");
        refreshExamButton.setFont(new Font("Arial", Font.BOLD, 12));
        
        // Results Table
        String[] columns = {
            "Examinee ID", "Name", "Hall Ticket", 
            "Marks Obtained", "Max Marks", "Percentage", "Grade"
        };
        resultsTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                // Only Marks Obtained and Max Marks are editable
                return column == 3 || column == 4;
            }
        };
        resultsTable = new JTable(resultsTableModel);
        resultsTable.setFont(new Font("Arial", Font.PLAIN, 12));
        resultsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        resultsScrollPane = new JScrollPane(resultsTable);
        
        // Action Buttons
        saveAllButton = new JButton("Save All Results");
        saveAllButton.setFont(new Font("Arial", Font.BOLD, 12));
        
        clearAllButton = new JButton("Clear All");
        clearAllButton.setFont(new Font("Arial", Font.BOLD, 12));
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout(10, 10));
        
        // Top Panel
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(new JLabel("Select Exam:"));
        topPanel.add(examComboBox);
        topPanel.add(refreshExamButton);
        add(topPanel, BorderLayout.NORTH);
        
        // Center Panel
        add(resultsScrollPane, BorderLayout.CENTER);
        
        // Bottom Panel
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        bottomPanel.add(saveAllButton);
        bottomPanel.add(clearAllButton);
        add(bottomPanel, BorderLayout.SOUTH);
    }
    
    private void setupEventListeners() {
        examComboBox.addActionListener(e -> {
            Exam exam = (Exam) examComboBox.getSelectedItem();
            if (exam != null) {
                selectedExam = exam;
                loadRegistrationsForExam(exam.getExamId());
            }
        });
        
        refreshExamButton.addActionListener(e -> loadExamsIntoComboBox());
        saveAllButton.addActionListener(e -> saveAllResults());
        clearAllButton.addActionListener(e -> clearAllMarks());
    }
    
    private void loadExamsIntoComboBox() {
        try {
            examComboBox.removeAllItems();
            List<Exam> exams = examDAO.getAllExams();
            for (Exam exam : exams) {
                examComboBox.addItem(exam);
            }
            if (!exams.isEmpty()) {
                examComboBox.setSelectedIndex(0);
                selectedExam = exams.get(0);
                loadRegistrationsForExam(selectedExam.getExamId());
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error loading exams: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    private void loadRegistrationsForExam(int examId) {
        try {
            resultsTableModel.setRowCount(0);
            List<ExamRegistration> registrations = registrationDAO.getRegistrationsByExamId(examId);
            
            for (ExamRegistration reg : registrations) {
                // Get examinee details (we'll assume they're in the registration for simplicity)
                // In a full system, you'd join with examinees table
                String name = "Examinee " + reg.getExamineeId(); // Placeholder
                String hallTicket = reg.getHallTicketNumber();
                
                // Get existing result if any
                Result result = resultDAO.getResultByRegistrationId(reg.getRegistrationId());
                BigDecimal marksObtained = result != null ? result.getMarksObtained() : BigDecimal.ZERO;
                BigDecimal maxMarks = result != null ? result.getMaxMarks() : new BigDecimal("100.00");
                BigDecimal percentage = result != null ? result.getPercentage() : BigDecimal.ZERO;
                String grade = result != null ? result.getGrade() : "";
                
                Object[] row = {
                    reg.getExamineeId(),
                    name,
                    hallTicket,
                    marksObtained,
                    maxMarks,
                    percentage,
                    grade
                };
                resultsTableModel.addRow(row);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error loading registrations: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    private void saveAllResults() {
        if (selectedExam == null) {
            JOptionPane.showMessageDialog(this,
                "Please select an exam first.",
                "No Exam Selected", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int savedCount = 0;
        for (int i = 0; i < resultsTableModel.getRowCount(); i++) {
            try {
                int examineeId = (Integer) resultsTableModel.getValueAt(i, 0);
                String hallTicket = (String) resultsTableModel.getValueAt(i, 2);
                
                // Find registration ID by hall ticket (or re-query)
                List<ExamRegistration> regs = registrationDAO.getRegistrationsByExamId(selectedExam.getExamId());
                ExamRegistration targetReg = null;
                for (ExamRegistration reg : regs) {
                    if (hallTicket.equals(reg.getHallTicketNumber())) {
                        targetReg = reg;
                        break;
                    }
                }
                
                if (targetReg == null) continue;
                
                // Get marks from table
                Object marksObj = resultsTableModel.getValueAt(i, 3);
                Object maxMarksObj = resultsTableModel.getValueAt(i, 4);
                
                BigDecimal marksObtained = parseBigDecimal(marksObj, "0");
                BigDecimal maxMarks = parseBigDecimal(maxMarksObj, "100");
                
                if (maxMarks.compareTo(BigDecimal.ZERO) <= 0) {
                    JOptionPane.showMessageDialog(this,
                        "Max marks must be greater than 0 for examinee " + examineeId,
                        "Invalid Input", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                
                Result result = new Result();
                result.setRegistrationId(targetReg.getRegistrationId());
                result.setMarksObtained(marksObtained);
                result.setMaxMarks(maxMarks);
                // percentage and grade will be auto-calculated by ResultDAO
                
                boolean saved = resultDAO.saveResult(result, 1); // Hardcode user ID = 1 for now
                if (saved) savedCount++;
                
                // Update percentage and grade in table
                Result updatedResult = resultDAO.getResultByRegistrationId(targetReg.getRegistrationId());
                if (updatedResult != null) {
                    resultsTableModel.setValueAt(updatedResult.getPercentage(), i, 5);
                    resultsTableModel.setValueAt(updatedResult.getGrade(), i, 6);
                }
                
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                    "Error saving result for row " + (i + 1) + ": " + e.getMessage(),
                    "Save Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
        
        JOptionPane.showMessageDialog(this,
            "Saved " + savedCount + " results successfully!",
            "Success", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void clearAllMarks() {
        int option = JOptionPane.showConfirmDialog(this,
            "Clear all marks for this exam?",
            "Confirm Clear",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE);
        
        if (option == JOptionPane.YES_OPTION) {
            for (int i = 0; i < resultsTableModel.getRowCount(); i++) {
                resultsTableModel.setValueAt(BigDecimal.ZERO, i, 3); // Marks Obtained
                resultsTableModel.setValueAt(new BigDecimal("100.00"), i, 4); // Max Marks
                resultsTableModel.setValueAt(BigDecimal.ZERO, i, 5); // Percentage
                resultsTableModel.setValueAt("", i, 6); // Grade
            }
        }
    }
    
    private BigDecimal parseBigDecimal(Object value, String defaultValue) {
        if (value instanceof BigDecimal) {
            return (BigDecimal) value;
        }
        String str = value != null ? value.toString() : defaultValue;
        try {
            return new BigDecimal(str);
        } catch (NumberFormatException e) {
            return new BigDecimal(defaultValue);
        }
    }
    
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        SwingUtilities.invokeLater(() -> {
            new EnterResultsFrame().setVisible(true);
        });
    }
}