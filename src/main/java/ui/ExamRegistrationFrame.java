package ui;

import dao.ExamDAO;
import dao.ExamineeDAO;
import dao.ExamRegistrationDAO;
import model.Exam;
import model.Examinee;
import model.ExamRegistration;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

/**
 * Exam Registration Frame GUI Class
 * 
 * Allows admin/staff to register examinees for exams and manage registrations.
 * 
 * @author SEMS Development Team
 * @version 1.0
 */
public class ExamRegistrationFrame extends JFrame {
    
    private ExamDAO examDAO;
    private ExamineeDAO examineeDAO;
    private ExamRegistrationDAO registrationDAO;
    
    // Top Panel
    private JComboBox<Exam> examComboBox;
    private JButton refreshExamButton;
    
    // Main Table (Registrations)
    private JTable registrationsTable;
    private DefaultTableModel registrationsTableModel;
    private JScrollPane registrationsScrollPane;
    
    // Search Panel
    private JTextField searchExamineeField;
    private JButton searchExamineeButton;
    private JTable examineeSearchTable;
    private DefaultTableModel examineeSearchTableModel;
    private JScrollPane examineeSearchScrollPane;
    private JButton registerSelectedButton;
    
    // Currently selected exam
    private Exam selectedExam = null;
    
    public ExamRegistrationFrame() {
        // Initialize DAOs
        examDAO = new ExamDAO();
        examineeDAO = new ExamineeDAO();
        registrationDAO = new ExamRegistrationDAO();
        
        initializeFrame();
        initializeComponents();
        setupLayout();
        setupEventListeners();
        loadExamsIntoComboBox();
    }
    
    private void initializeFrame() {
        setTitle("Exam Registration - SEMS");
        setSize(950, 700);
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
        
        // Registrations Table
        String[] regColumns = {"Hall Ticket", "Examinee Name", "Email", "Status", "Registration Date"};
        registrationsTableModel = new DefaultTableModel(regColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        registrationsTable = new JTable(registrationsTableModel);
        registrationsTable.setFont(new Font("Arial", Font.PLAIN, 12));
        registrationsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        registrationsScrollPane = new JScrollPane(registrationsTable);
        
        // Examinee Search
        searchExamineeField = new JTextField(20);
        searchExamineeField.setFont(new Font("Arial", Font.PLAIN, 12));
        searchExamineeField.setToolTipText("Search by name, email, or registration number");
        
        searchExamineeButton = new JButton("Search");
        searchExamineeButton.setFont(new Font("Arial", Font.BOLD, 12));
        
        // Examinee Search Table
        String[] examineeColumns = {"ID", "Reg Number", "Name", "Email"};
        examineeSearchTableModel = new DefaultTableModel(examineeColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        examineeSearchTable = new JTable(examineeSearchTableModel);
        examineeSearchTable.setFont(new Font("Arial", Font.PLAIN, 12));
        examineeSearchTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        examineeSearchScrollPane = new JScrollPane(examineeSearchTable);
        
        registerSelectedButton = new JButton("Register Selected");
        registerSelectedButton.setFont(new Font("Arial", Font.BOLD, 12));
        registerSelectedButton.setEnabled(false);
        
        // Context menu for registrations table
        JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem confirmItem = new JMenuItem("Confirm Registration");
        JMenuItem cancelItem = new JMenuItem("Cancel Registration");
        
        confirmItem.addActionListener(e -> updateRegistrationStatus("confirmed"));
        cancelItem.addActionListener(e -> updateRegistrationStatus("cancelled"));
        
        popupMenu.add(confirmItem);
        popupMenu.add(cancelItem);
        
        registrationsTable.setComponentPopupMenu(popupMenu);
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout(10, 10));
        
        // Top Panel
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(new JLabel("Select Exam:"));
        topPanel.add(examComboBox);
        topPanel.add(refreshExamButton);
        add(topPanel, BorderLayout.NORTH);
        
        // Center Panel (Registrations)
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBorder(BorderFactory.createTitledBorder("Registered Examinees"));
        centerPanel.add(registrationsScrollPane, BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);
        
        // Bottom Panel (Search & Register)
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBorder(BorderFactory.createTitledBorder("Register New Examinee"));
        
        // Search sub-panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.add(new JLabel("Search Examinee:"));
        searchPanel.add(searchExamineeField);
        searchPanel.add(searchExamineeButton);
        bottomPanel.add(searchPanel, BorderLayout.NORTH);
        
        // Search results table
        bottomPanel.add(examineeSearchScrollPane, BorderLayout.CENTER);
        
        // Register button
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(registerSelectedButton);
        bottomPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        add(bottomPanel, BorderLayout.SOUTH);
    }
    
    private void setupEventListeners() {
        // Exam selection
        examComboBox.addActionListener(e -> {
            Exam exam = (Exam) examComboBox.getSelectedItem();
            if (exam != null) {
                selectedExam = exam;
                loadRegistrationsForExam(exam.getExamId());
            }
        });
        
        // Refresh exams
        refreshExamButton.addActionListener(e -> loadExamsIntoComboBox());
        
        // Search examinees
        searchExamineeButton.addActionListener(e -> searchExaminees());
        searchExamineeField.addActionListener(e -> searchExaminees());
        
        // Examinee table selection
        examineeSearchTable.getSelectionModel().addListSelectionListener(e -> {
            registerSelectedButton.setEnabled(examineeSearchTable.getSelectedRow() >= 0);
        });
        
        // Register button
        registerSelectedButton.addActionListener(e -> registerSelectedExaminee());
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
            registrationsTableModel.setRowCount(0);
            List<ExamRegistration> registrations = registrationDAO.getRegistrationsByExamId(examId);
            
            for (ExamRegistration reg : registrations) {
                // Fetch examinee details for display
                Examinee examinee = examineeDAO.getExamineeById(reg.getExamineeId());
                String name = examinee != null ? examinee.getFirstName() + " " + examinee.getLastName() : "Unknown";
                String email = examinee != null ? examinee.getEmail() : "";
                
                Object[] row = {
                    reg.getHallTicketNumber(),
                    name,
                    email,
                    reg.getStatus(),
                    reg.getRegistrationDate() != null ? reg.getRegistrationDate().toString() : ""
                };
                registrationsTableModel.addRow(row);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error loading registrations: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    private void searchExaminees() {
        String keyword = searchExamineeField.getText().trim();
        if (keyword.isEmpty()) {
            examineeSearchTableModel.setRowCount(0);
            registerSelectedButton.setEnabled(false);
            return;
        }
        
        try {
            examineeSearchTableModel.setRowCount(0);
            List<Examinee> examinees = examineeDAO.searchExaminees(keyword);
            for (Examinee e : examinees) {
                Object[] row = {
                    e.getExamineeId(),
                    e.getRegistrationNumber(),
                    e.getFirstName() + " " + e.getLastName(),
                    e.getEmail()
                };
                examineeSearchTableModel.addRow(row);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error searching examinees: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    private void registerSelectedExaminee() {
        int selectedRow = examineeSearchTable.getSelectedRow();
        if (selectedRow == -1 || selectedExam == null) return;
        
        int modelRow = examineeSearchTable.convertRowIndexToModel(selectedRow);
        int examineeId = (Integer) examineeSearchTableModel.getValueAt(modelRow, 0);
        
        try {
            boolean success = registrationDAO.registerExamineeForExam(examineeId, selectedExam.getExamId(), null);
            if (success) {
                JOptionPane.showMessageDialog(this,
                    "Examinee registered successfully!",
                    "Success", JOptionPane.INFORMATION_MESSAGE);
                loadRegistrationsForExam(selectedExam.getExamId());
                searchExamineeField.setText("");
                examineeSearchTableModel.setRowCount(0);
                registerSelectedButton.setEnabled(false);
            } else {
                JOptionPane.showMessageDialog(this,
                    "Failed to register examinee.\nThey may already be registered for this exam.",
                    "Registration Failed", JOptionPane.WARNING_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error registering examinee: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    private void updateRegistrationStatus(String newStatus) {
        int selectedRow = registrationsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                "Please select a registration to update.",
                "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int modelRow = registrationsTable.convertRowIndexToModel(selectedRow);
        String hallTicket = (String) registrationsTableModel.getValueAt(modelRow, 0);
        
        // Find registration by hall ticket (or reload from DB)
        List<ExamRegistration> regs = registrationDAO.getRegistrationsByExamId(selectedExam.getExamId());
        ExamRegistration targetReg = null;
        for (ExamRegistration reg : regs) {
            if (hallTicket.equals(reg.getHallTicketNumber())) {
                targetReg = reg;
                break;
            }
        }
        
        if (targetReg == null) {
            JOptionPane.showMessageDialog(this,
                "Registration not found.",
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try {
            boolean success = false;
            if ("cancelled".equals(newStatus)) {
                success = registrationDAO.cancelRegistration(targetReg.getRegistrationId());
            } else if ("confirmed".equals(newStatus)) {
                success = registrationDAO.confirmRegistration(targetReg.getRegistrationId());
            }
            
            if (success) {
                JOptionPane.showMessageDialog(this,
                    "Registration status updated to: " + newStatus,
                    "Success", JOptionPane.INFORMATION_MESSAGE);
                loadRegistrationsForExam(selectedExam.getExamId());
            } else {
                JOptionPane.showMessageDialog(this,
                    "Failed to update registration status.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error updating registration: " + e.getMessage(),
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
            new ExamRegistrationFrame().setVisible(true);
        });
    }
}