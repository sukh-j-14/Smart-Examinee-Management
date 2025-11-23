package ui;

import dao.ExamDAO;
import dao.ResultDAO;
import model.Exam;
import model.Result;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * Reports Dashboard GUI Class
 * 
 * Displays key statistics and reports for the Smart Examinee Management System.
 * 
 * @author SEMS Development Team
 * @version 1.0
 */
public class ReportsDashboard extends JFrame {
    
    private ExamDAO examDAO;
    private ResultDAO resultDAO;
    
    // Stats Labels
    private JLabel totalExamineesLabel;
    private JLabel totalExamsLabel;
    private JLabel totalRegistrationsLabel;
    private JLabel passFailRatioLabel;
    private JLabel topExamineesLabel;
    
    public ReportsDashboard() {
        examDAO = new ExamDAO();
        resultDAO = new ResultDAO();
        
        initializeFrame();
        initializeComponents();
        setupLayout();
        loadStats();
    }
    
    private void initializeFrame() {
        setTitle("SEMS - Reports Dashboard");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
    }
    
    private void initializeComponents() {
        // Title
        JLabel titleLabel = new JLabel("System Reports", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Stats Panel
        JPanel statsPanel = new JPanel(new GridLayout(5, 1, 10, 10));
        statsPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        
        totalExamineesLabel = createStatLabel("Total Examinees: Loading...");
        totalExamsLabel = createStatLabel("Total Exams: Loading...");
        totalRegistrationsLabel = createStatLabel("Total Registrations: Loading...");
        passFailRatioLabel = createStatLabel("Pass/Fail Ratio: Loading...");
        topExamineesLabel = createStatLabel("Top 5 Examinees: Loading...");
        
        statsPanel.add(totalExamineesLabel);
        statsPanel.add(totalExamsLabel);
        statsPanel.add(totalRegistrationsLabel);
        statsPanel.add(passFailRatioLabel);
        statsPanel.add(topExamineesLabel);
        
        // Refresh Button
        JButton refreshButton = new JButton("Refresh Statistics");
        refreshButton.setFont(new Font("Arial", Font.BOLD, 12));
        refreshButton.addActionListener(e -> loadStats());
        
        // Add components
        setLayout(new BorderLayout(10, 10));
        add(titleLabel, BorderLayout.NORTH);
        add(statsPanel, BorderLayout.CENTER);
        add(refreshButton, BorderLayout.SOUTH);
    }
    
    private JLabel createStatLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.PLAIN, 16));
        label.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        return label;
    }
    
    private void setupLayout() {
        // Already done in initializeComponents()
    }
    
    private void loadStats() {
        try {
            // Total Examinees (you’d need a method in ExamineeDAO)
            // For demo, we'll simulate:
            int totalExaminees = 150; // Replace with actual count from DAO
            
            // Total Exams
            List<Exam> exams = examDAO.getAllExams();
            int totalExams = exams.size();
            
            // Total Registrations (you’d need a method in ExamRegistrationDAO)
            int totalRegistrations = 300; // Simulate
            
            // Pass/Fail Ratio
            int passCount = 0;
            int failCount = 0;
            for (Exam exam : exams) {
                List<Result> results = resultDAO.getResultsByExamId(exam.getExamId());
                for (Result result : results) {
                    if (result.getGrade() != null && !result.getGrade().startsWith("Fail")) {
                        passCount++;
                    } else {
                        failCount++;
                    }
                }
            }
            double passRatio = totalExams > 0 ? ((double) passCount / (passCount + failCount)) * 100 : 0;
            double failRatio = 100 - passRatio;
            
            // Top 5 Examinees (by highest percentage)
            StringBuilder topExaminees = new StringBuilder("Top 5 Examinees:\n");
            // This would require joining tables — for demo, show placeholder
            topExaminees.append("1. Examinee 1 - 98%\n");
            topExaminees.append("2. Examinee 2 - 95%\n");
            topExaminees.append("3. Examinee 3 - 92%\n");
            topExaminees.append("4. Examinee 4 - 90%\n");
            topExaminees.append("5. Examinee 5 - 88%");
            
            // Update labels
            totalExamineesLabel.setText("Total Examinees: " + totalExaminees);
            totalExamsLabel.setText("Total Exams: " + totalExams);
            totalRegistrationsLabel.setText("Total Registrations: " + totalRegistrations);
            passFailRatioLabel.setText(String.format(
                "Pass/Fail Ratio: %.1f%% Pass / %.1f%% Fail",
                passRatio, failRatio
            ));
            topExamineesLabel.setText(topExaminees.toString());
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error loading statistics: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        SwingUtilities.invokeLater(() -> {
            new ReportsDashboard().setVisible(true);
        });
    }
}