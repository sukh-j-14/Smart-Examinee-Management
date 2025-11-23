package ui;

import dao.ExamineeDAO;
import dao.ExamRegistrationDAO;
import dao.ExamDAO;
import model.Examinee;
import model.ExamRegistration;
import model.Exam;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.JTable;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

import java.awt.*;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.util.List;

/**
 * Hall Ticket Generator Frame GUI Class
 * 
 * Allows admin/staff to generate and print hall tickets for examinees.
 * 
 * @author SEMS Development Team
 * @version 1.0
 */
public class HallTicketFrame extends JFrame {
    
    private ExamineeDAO examineeDAO;
    private ExamRegistrationDAO registrationDAO;
    private ExamDAO examDAO;
    
    // Top Panel: Examinee Search
    private JTextField searchField;
    private JButton searchButton;
    private JButton refreshButton;
    
    // Center Panel: Examinee Info + Registrations
    private JLabel examineeInfoLabel;
    private JTable registrationsTable;
    private DefaultTableModel registrationsTableModel;
    
    // Bottom Panel: Action Buttons
    private JButton generateButton;
    private JButton printButton;
    
    // Selected examinee
    private Examinee selectedExaminee = null;
    
    public HallTicketFrame() {
        examineeDAO = new ExamineeDAO();
        registrationDAO = new ExamRegistrationDAO();
        examDAO = new ExamDAO();
        
        initializeFrame();
        initializeComponents();
        setupLayout();
        setupEventListeners();
    }
    
    private void initializeFrame() {
        setTitle("Hall Ticket Generator - SEMS");
        setSize(850, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
    }
    
    private void initializeComponents() {
        // Search Panel
        searchField = new JTextField(20);
        searchField.setFont(new Font("Arial", Font.PLAIN, 12));
        searchField.setToolTipText("Search by registration number, name, or email");
        
        searchButton = new JButton("Search");
        searchButton.setFont(new Font("Arial", Font.BOLD, 12));
        
        refreshButton = new JButton("Refresh");
        refreshButton.setFont(new Font("Arial", Font.BOLD, 12));
        
        // Examinee Info Label
        examineeInfoLabel = new JLabel("No examinee selected");
        examineeInfoLabel.setFont(new Font("Arial", Font.BOLD, 14));
        examineeInfoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        // Registrations Table
        String[] columns = {"Hall Ticket", "Exam", "Date", "Time", "Venue", "Status"};
        registrationsTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        registrationsTable = new JTable(registrationsTableModel);
        registrationsTable.setFont(new Font("Arial", Font.PLAIN, 12));
        registrationsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        registrationsTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        
        // Action Buttons
        generateButton = new JButton("Generate Hall Ticket");
        generateButton.setFont(new Font("Arial", Font.BOLD, 12));
        generateButton.setEnabled(false);
        
        printButton = new JButton("Print Hall Ticket");
        printButton.setFont(new Font("Arial", Font.BOLD, 12));
        printButton.setEnabled(false);
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout(10, 10));
        
        // Top Panel
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(new JLabel("Search Examinee:"));
        topPanel.add(searchField);
        topPanel.add(searchButton);
        topPanel.add(refreshButton);
        add(topPanel, BorderLayout.NORTH);
        
        // Center Panel
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBorder(BorderFactory.createTitledBorder("Examinee Information"));
        centerPanel.add(examineeInfoLabel, BorderLayout.NORTH);
        
        JScrollPane tableScrollPane = new JScrollPane(registrationsTable);
        centerPanel.add(tableScrollPane, BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);
        
        // Bottom Panel
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        bottomPanel.add(generateButton);
        bottomPanel.add(printButton);
        add(bottomPanel, BorderLayout.SOUTH);
    }
    
    private void setupEventListeners() {
        searchButton.addActionListener(e -> searchExaminee());
        searchField.addActionListener(e -> searchExaminee());
        refreshButton.addActionListener(e -> {
            searchField.setText("");
            examineeInfoLabel.setText("No examinee selected");
            registrationsTableModel.setRowCount(0);
            selectedExaminee = null;
            generateButton.setEnabled(false);
            printButton.setEnabled(false);
        });
        
        registrationsTable.getSelectionModel().addListSelectionListener(e -> {
            boolean enabled = registrationsTable.getSelectedRow() >= 0 && selectedExaminee != null;
            generateButton.setEnabled(enabled);
            printButton.setEnabled(enabled);
        });
        
        generateButton.addActionListener(e -> generateHallTicket());
        printButton.addActionListener(e -> printHallTicket());
    }
    
    private void searchExaminee() {
        String keyword = searchField.getText().trim();
        if (keyword.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Please enter a search keyword.",
                "Search", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            List<Examinee> examinees = examineeDAO.searchExaminees(keyword);
            if (examinees.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "No examinee found matching: " + keyword,
                    "Search Results", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            
            // For simplicity, pick the first result
            selectedExaminee = examinees.get(0);
            examineeInfoLabel.setText(
                selectedExaminee.getRegistrationNumber() + " - " +
                selectedExaminee.getFirstName() + " " + selectedExaminee.getLastName() +
                " (" + selectedExaminee.getEmail() + ")"
            );
            
            loadRegistrationsForExaminee(selectedExaminee.getExamineeId());
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error searching examinee: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    private void loadRegistrationsForExaminee(int examineeId) {
        try {
            registrationsTableModel.setRowCount(0);
            List<ExamRegistration> registrations = registrationDAO.getRegistrationsByExamineeId(examineeId);
            
            for (ExamRegistration reg : registrations) {
                Exam exam = examDAO.getExamById(reg.getExamId());
                if (exam == null) continue;
                
                String timeRange = "";
                if (exam.getStartTime() != null && exam.getEndTime() != null) {
                    timeRange = exam.getStartTime().toString().substring(0, 5) + 
                               " - " + exam.getEndTime().toString().substring(0, 5);
                }
                
                Object[] row = {
                    reg.getHallTicketNumber(),
                    exam.getExamCode() + " - " + exam.getExamName(),
                    exam.getExamDate() != null ? exam.getExamDate().toString() : "",
                    timeRange,
                    exam.getVenue() != null ? exam.getVenue() : "",
                    reg.getStatus()
                };
                registrationsTableModel.addRow(row);
            }
            
            if (registrations.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "No exam registrations found for this examinee.",
                    "Info", JOptionPane.INFORMATION_MESSAGE);
            }
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error loading registrations: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    private void generateHallTicket() {
        int selectedRow = registrationsTable.getSelectedRow();
        if (selectedRow == -1 || selectedExaminee == null) return;
        
        int modelRow = registrationsTable.convertRowIndexToModel(selectedRow);
        String hallTicket = (String) registrationsTableModel.getValueAt(modelRow, 0);
        String examInfo = (String) registrationsTableModel.getValueAt(modelRow, 1);
        String examDate = (String) registrationsTableModel.getValueAt(modelRow, 2);
        String examTime = (String) registrationsTableModel.getValueAt(modelRow, 3);
        String venue = (String) registrationsTableModel.getValueAt(modelRow, 4);
        
        // Create hall ticket preview
        JFrame previewFrame = new JFrame("Hall Ticket Preview");
        previewFrame.setSize(600, 700);
        previewFrame.setLocationRelativeTo(this);
        
        HallTicketPanel panel = new HallTicketPanel(
            selectedExaminee, hallTicket, examInfo, examDate, examTime, venue
        );
        previewFrame.add(new JScrollPane(panel));
        previewFrame.setVisible(true);
    }
    
    private void printHallTicket() {
        int selectedRow = registrationsTable.getSelectedRow();
        if (selectedRow == -1 || selectedExaminee == null) return;
        
        int modelRow = registrationsTable.convertRowIndexToModel(selectedRow);
        String hallTicket = (String) registrationsTableModel.getValueAt(modelRow, 0);
        String examInfo = (String) registrationsTableModel.getValueAt(modelRow, 1);
        String examDate = (String) registrationsTableModel.getValueAt(modelRow, 2);
        String examTime = (String) registrationsTableModel.getValueAt(modelRow, 3);
        String venue = (String) registrationsTableModel.getValueAt(modelRow, 4);
        
        HallTicketPanel panel = new HallTicketPanel(
            selectedExaminee, hallTicket, examInfo, examDate, examTime, venue
        );
        
        PrinterJob job = PrinterJob.getPrinterJob();
        job.setPrintable(new HallTicketPrintable(panel));
        
        boolean doPrint = job.printDialog();
        if (doPrint) {
            try {
                job.print();
                JOptionPane.showMessageDialog(this,
                    "Hall ticket sent to printer successfully!",
                    "Print Complete", JOptionPane.INFORMATION_MESSAGE);
            } catch (PrinterException e) {
                JOptionPane.showMessageDialog(this,
                    "Error printing hall ticket: " + e.getMessage(),
                    "Print Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }
    
    // Inner class for hall ticket display
    private static class HallTicketPanel extends JPanel {
        public HallTicketPanel(Examinee examinee, String hallTicket, String examInfo,
                              String examDate, String examTime, String venue) {
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            setBorder(new LineBorder(Color.BLACK, 2));
            setBackground(Color.WHITE);
            setPreferredSize(new Dimension(500, 600));
            
            // Header
            JLabel header = new JLabel("HALL TICKET", JLabel.CENTER);
            header.setFont(new Font("Arial", Font.BOLD, 24));
            header.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
            add(header);
            
            // Divider
            JSeparator separator = new JSeparator();
            separator.setBorder(BorderFactory.createEmptyBorder(0, 20, 10, 20));
            add(separator);
            
            // Exam Details
            addDetail("Exam:", examInfo);
            addDetail("Date:", examDate);
            addDetail("Time:", examTime);
            addDetail("Venue:", venue);
            
            // Divider
            JSeparator separator2 = new JSeparator();
            separator2.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));
            add(separator2);
            
            // Examinee Details
            addDetail("Hall Ticket No.:", hallTicket);
            addDetail("Name:", examinee.getFirstName() + " " + examinee.getLastName());
            addDetail("Registration No.:", examinee.getRegistrationNumber());
            addDetail("Email:", examinee.getEmail());
            addDetail("Phone:", examinee.getPhone() != null ? examinee.getPhone() : "N/A");
            
            // Footer
            JLabel footer = new JLabel("Bring this hall ticket and valid ID to the exam center", JLabel.CENTER);
            footer.setFont(new Font("Arial", Font.ITALIC, 12));
            footer.setBorder(BorderFactory.createEmptyBorder(30, 20, 20, 20));
            add(footer);
        }
        
        private void addDetail(String label, String value) {
            JPanel detailPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 5));
            detailPanel.setBackground(Color.WHITE);
            JLabel key = new JLabel(label);
            key.setFont(new Font("Arial", Font.BOLD, 14));
            JLabel val = new JLabel(value);
            val.setFont(new Font("Arial", Font.PLAIN, 14));
            detailPanel.add(key);
            detailPanel.add(val);
            add(detailPanel);
        }
    }
    
    // Inner class for printing
    private static class HallTicketPrintable implements Printable {
        private final HallTicketPanel panel;
        
        public HallTicketPrintable(HallTicketPanel panel) {
            this.panel = panel;
        }
        
        @Override
        public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) {
            if (pageIndex > 0) return NO_SUCH_PAGE;
            
            Graphics2D g2d = (Graphics2D) graphics;
            g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());
            panel.printAll(g2d);
            
            return PAGE_EXISTS;
        }
    }
    
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        SwingUtilities.invokeLater(() -> {
            new HallTicketFrame().setVisible(true);
        });
    }
}