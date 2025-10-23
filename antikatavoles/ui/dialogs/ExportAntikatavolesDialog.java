package org.pms.antikatavoles.ui.dialogs;

import org.pms.antikatavoles.model.AntikatavoliFullView;
import org.pms.antikatavoles.service.AntikatavoliExcelExporter;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class ExportAntikatavolesDialog extends JDialog {
    private JCheckBox chkExportPDF;
    private JCheckBox chkExportKatastashPDF;
    private JCheckBox chkExportKatastashExcel;
    private JButton btnExport;
    private JButton btnCancel;

    private final List<AntikatavoliFullView> data;
    private boolean exported = false;

    public ExportAntikatavolesDialog(Frame parent, List<AntikatavoliFullView> data) {
        super(parent, "Εξαγωγή Αντικαταβολών", true);
        this.data = data;

        initComponents();
        setupLayout();
        setupEvents();

        setSize(450, 250);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

    private void initComponents() {
        chkExportPDF = new JCheckBox("Αντικαταβολές (PDF)");
        chkExportPDF.setEnabled(false); // Προς το παρόν απενεργοποιημένο

        chkExportKatastashPDF = new JCheckBox("Κατάσταση Αντικαταβολών (PDF)");
        chkExportKatastashPDF.setEnabled(false); // Προς το παρόν απενεργοποιημένο

        chkExportKatastashExcel = new JCheckBox("Κατάσταση Αντικαταβολών (Excel)");
        chkExportKatastashExcel.setSelected(true); // Επιλεγμένο by default

        btnExport = new JButton("Εξαγωγή");
        btnExport.setBackground(new Color(0, 120, 215));
        btnExport.setForeground(Color.BLACK);
        btnExport.setFont(btnExport.getFont().deriveFont(Font.BOLD));

        btnCancel = new JButton("Ακύρωση");
    }

    private void setupLayout() {
        setLayout(new BorderLayout(10, 10));

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel lblTitle = new JLabel("Επιλέξτε τύπο εξαγωγής:");
        lblTitle.setFont(lblTitle.getFont().deriveFont(Font.BOLD, 14f));
        lblTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        mainPanel.add(lblTitle);
        mainPanel.add(Box.createVerticalStrut(15));

        chkExportPDF.setAlignmentX(Component.LEFT_ALIGNMENT);
        chkExportKatastashPDF.setAlignmentX(Component.LEFT_ALIGNMENT);
        chkExportKatastashExcel.setAlignmentX(Component.LEFT_ALIGNMENT);

        mainPanel.add(chkExportPDF);
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(chkExportKatastashPDF);
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(chkExportKatastashExcel);

        add(mainPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonPanel.add(btnCancel);
        buttonPanel.add(btnExport);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void setupEvents() {
        btnExport.addActionListener(e -> performExport());
        btnCancel.addActionListener(e -> dispose());
    }

    private void performExport() {
        if (!chkExportPDF.isSelected() &&
                !chkExportKatastashPDF.isSelected() &&
                !chkExportKatastashExcel.isSelected()) {
            JOptionPane.showMessageDialog(this,
                    "Παρακαλώ επιλέξτε τουλάχιστον έναν τύπο εξαγωγής.",
                    "Προειδοποίηση",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            if (chkExportKatastashExcel.isSelected()) {
                exportToExcel();
            }

            if (chkExportPDF.isSelected()) {
                JOptionPane.showMessageDialog(this,
                        "Η εξαγωγή σε PDF θα υλοποιηθεί σύντομα.",
                        "Πληροφορία",
                        JOptionPane.INFORMATION_MESSAGE);
            }

            if (chkExportKatastashPDF.isSelected()) {
                JOptionPane.showMessageDialog(this,
                        "Η εξαγωγή κατάστασης σε PDF θα υλοποιηθεί σύντομα.",
                        "Πληροφορία",
                        JOptionPane.INFORMATION_MESSAGE);
            }

            exported = true;

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Σφάλμα κατά την εξαγωγή: " + ex.getMessage(),
                    "Σφάλμα",
                    JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void exportToExcel() throws Exception {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Αποθήκευση αρχείου Excel");
        fileChooser.setSelectedFile(new java.io.File("Antikatavoles_" +
                java.time.LocalDate.now() + ".xlsx"));

        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            String filePath = fileChooser.getSelectedFile().getAbsolutePath();
            if (!filePath.toLowerCase().endsWith(".xlsx")) {
                filePath += ".xlsx";
            }

            AntikatavoliExcelExporter exporter = new AntikatavoliExcelExporter();
            exporter.exportToExcel(data, filePath);

            int response = JOptionPane.showConfirmDialog(this,
                    "Η εξαγωγή ολοκληρώθηκε επιτυχώς!\nΘέλετε να ανοίξετε το αρχείο;",
                    "Επιτυχία",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.INFORMATION_MESSAGE);

            if (response == JOptionPane.YES_OPTION) {
                Desktop.getDesktop().open(new java.io.File(filePath));
            }

            dispose();
        }
    }

    public boolean isExported() {
        return exported;
    }
}