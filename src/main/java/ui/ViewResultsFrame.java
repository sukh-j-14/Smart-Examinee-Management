package ui;

import dao.ExamDAO;
import dao.ExamineeDAO;
import dao.ResultDAO;
import model.Exam;
import model.Examinee;
import model.Result;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

/**
 * View Results Frame GUI Class
 * 
 * Provides a read-only view of exam results with filtering and export capability.
 * 
 * @author SEMS Development Team
 * @version 1.0
 */
public class ViewResultsFrame extends JFrame {
    
    private ResultDAO resultDAO;
    private ExamDAO examDAO;
    private ExamineeDAO examineeDAO;
    
    // Filter Panel
    private JComboBox<Exam> examFilterCombo;
    private JTextField examineeSearchField;
    private JButton applyFilterButton;
    private JButton clearFilterButton;
    private JButton exportButton;
    
    // Results Table
    private JTable resultsTable;
    private DefaultTableModel resultsTableModel;
    private JScrollPane resultsScrollPane;
    
    public ViewResultsFrame() {
        resultDAO = new ResultDAO();
        examDAO = new ExamDAO();
        examineeDAO = new ExamineeDAO();
        
        initializeFrame();
        initializeComponents();
        setupLayout();
        setupEventListeners();
        loadAllResults();
        loadExamsIntoFilter();
    }
    
    private void initializeFrame() {
        setTitle("View Results - SEMS");
        setSize(950, 650);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
    }
    
    private void initializeComponents() {
        // Filter Components
        examFilterCombo = new JComboBox<>();
        examFilterCombo.setFont(new Font("Arial", Font.PLAIN, 12));
        examFilterCombo.addItem(new Exam() {{
            setExamId(-1);
            setExamName("All Exams");
            setExamCode("");
        }});
        examFilterCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                    boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Exam) {
                    Exam exam = (Exam) value;
                    if (exam.getExamId() == -1) {
                        setText("All Exams");
                    } else {
                        setText(exam.getExamCode() + " - " + exam.getExamName());
                    }
                }
                return this;
            }
        });
        
        examineeSearchField = new JTextField(15);
        examineeSearchField.setFont(new Font("Arial", Font.PLAIN, 12));
        examineeSearchField.setToolTipText("Search by registration number or name");
        
        applyFilterButton = new JButton("Apply Filter");
        applyFilterButton.setFont(new Font("Arial", Font.BOLD, 12));
        
        clearFilterButton = new JButton("Clear Filter");
        clearFilterButton.setFont(new Font("Arial", Font.BOLD, 12));
        
        exportButton = new JButton("Export to CSV");
        exportButton.setFont(new Font("Arial", Font.BOLD, 12));
        
        // Results Table
        String[] columns = {
            "Examinee", "Registration #", "Exam", "Marks", "Max Marks", "Percentage", "Grade", "Remarks"
        };
        resultsTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        resultsTable = new JTable(resultsTableModel);
        resultsTable.setFont(new Font("Arial", Font.PLAIN, 12));
        resultsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        resultsScrollPane = new JScrollPane(resultsTable);
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout(10, 10));
        
        // Filter Panel
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterPanel.add(new JLabel("Filter by Exam:"));
        filterPanel.add(examFilterCombo);
        filterPanel.add(new JLabel("  Search Examinee:"));
        filterPanel.add(examineeSearchField);
        filterPanel.add(applyFilterButton);
        filterPanel.add(clearFilterButton);
        filterPanel.add(exportButton);
        add(filterPanel, BorderLayout.NORTH);
        
        // Results Table
        add(resultsScrollPane, BorderLayout.CENTER);
    }
    
    private void setupEventListeners() {
        applyFilterButton.addActionListener(e -> applyFilter());
        clearFilterButton.addActionListener(e -> {
            examineeSearchField.setText("");
            examFilterCombo.setSelectedIndex(0);
            loadAllResults();
        });
        exportButton.addActionListener(e -> exportToCSV());
    }
    
    private void loadExamsIntoFilter() {
        try {
            List<Exam> exams = examDAO.getAllExams();
            for (Exam exam : exams) {
                examFilterCombo.addItem(exam);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void loadAllResults() {
        try {
            resultsTableModel.setRowCount(0);
            // Get all results (you could limit to recent if needed)
            // For simplicity, we'll get results for all exams
            List<Exam> exams = examDAO.getAllExams();
            for (Exam exam : exams) {
                List<Result> results = resultDAO.getResultsByExamId(exam.getExamId());
                for (Result result : results) {
                    // Get registration details
                    // In a real system, you'd join tables, but for demo:
                    String examineeName = "Examinee " + result.getRegistrationId();
                    String registrationNumber = "REG-" + result.getRegistrationId();
                    
                    Object[] row = {
                        examineeName,
                        registrationNumber,
                        exam.getExamCode() + " - " + exam.getExamName(),
                        result.getMarksObtained(),
                        result.getMaxMarks(),
                        result.getPercentage(),
                        result.getGrade(),
                        result.getRemarks() != null ? result.getRemarks() : ""
                    };
                    resultsTableModel.addRow(row);
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error loading results: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    private void applyFilter() {
        Exam selectedExam = (Exam) examFilterCombo.getSelectedItem();
        String examineeSearch = examineeSearchField.getText().trim();
        
        resultsTableModel.setRowCount(0);
        
        try {
            if (selectedExam.getExamId() == -1) {
                // All exams
                loadAllResults();
            } else {
                // Filter by exam
                List<Result> results = resultDAO.getResultsByExamId(selectedExam.getExamId());
                for (Result result : results) {
                    // Add examinee details here if needed
                    String examineeName = "Examinee " + result.getRegistrationId();
                    String registrationNumber = "REG-" + result.getRegistrationId();
                    
                    // Simple examinee search
                    boolean matchesExaminee = examineeSearch.isEmpty() ||
                        examineeName.toLowerCase().contains(examineeSearch.toLowerCase()) ||
                        registrationNumber.toLowerCase().contains(examineeSearch.toLowerCase());
                    
                    if (matchesExaminee) {
                        Object[] row = {
                            examineeName,
                            registrationNumber,
                            selectedExam.getExamCode() + " - " + selectedExam.getExamName(),
                            result.getMarksObtained(),
                            result.getMaxMarks(),
                            result.getPercentage(),
                            result.getGrade(),
                            result.getRemarks() != null ? result.getRemarks() : ""
                        };
                        resultsTableModel.addRow(row);
                    }
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error applying filter: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    private void exportToCSV() {
        if (resultsTableModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this,
                "No results to export.",
                "Export", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Results as CSV");
        fileChooser.setSelectedFile(new java.io.File("exam_results.csv"));
        
        int userSelection = fileChooser.showSaveDialog(this);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            java.io.File fileToSave = fileChooser.getSelectedFile();
            String filePath = fileToSave.getAbsolutePath();
            if (!filePath.toLowerCase().endsWith(".csv")) {
                filePath += ".csv";
            }
            
            try (FileWriter writer = new FileWriter(filePath)) {
                // Write header
                for (int i = 0; i < resultsTableModel.getColumnCount(); i++) {
                    writer.append(resultsTableModel.getColumnName(i));
                    if (i < resultsTableModel.getColumnCount() - 1) writer.append(",");
                }
                writer.append("\n");
                
                // Write data
                for (int i = 0; i < resultsTableModel.getRowCount(); i++) {
                    for (int j = 0; j < resultsTableModel.getColumnCount(); j++) {
                        Object value = resultsTableModel.getValueAt(i, j);
                        String cell = value != null ? value.toString().replace(",", ";") : "";
                        writer.append("\"").append(cell).append("\"");
                        if (j < resultsTableModel.getColumnCount() - 1) writer.append(",");
                    }
                    writer.append("\n");
                }
                
                JOptionPane.showMessageDialog(this,
                    "Results exported successfully to:\n" + filePath,
                    "Export Complete", JOptionPane.INFORMATION_MESSAGE);
                    
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this,
                    "Error exporting to CSV: " + e.getMessage(),
                    "Export Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }
    
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        SwingUtilities.invokeLater(() -> {
            new ViewResultsFrame().setVisible(true);
        });
    }
}