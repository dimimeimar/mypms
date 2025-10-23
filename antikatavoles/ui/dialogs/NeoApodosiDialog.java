package org.pms.antikatavoles.ui.dialogs;

import org.pms.antikatavoles.model.AntikatavoliFullView;
import org.pms.antikatavoles.service.AntikatavoliService;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.util.List;

public class NeoApodosiDialog extends JDialog {
    private final List<AntikatavoliFullView> selectedAntikatavoles;
    private final AntikatavoliService antikatavoliService;

    private JLabel lblPlithos;
    private JLabel lblSynolo;
    private JLabel lblPelatis;
    private JButton btnSave;
    private JButton btnCancel;

    private boolean saved = false;

    public NeoApodosiDialog(Frame parent, List<AntikatavoliFullView> selectedAntikatavoles) {
        super(parent, "Νέα Απόδοση", true);
        this.selectedAntikatavoles = selectedAntikatavoles;
        this.antikatavoliService = new AntikatavoliService();

        initComponents();
        setupLayout();
        setupEvents();
        calculateSummary();

        setSize(500, 300);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

    private void initComponents() {
        lblPlithos = new JLabel();
        lblSynolo = new JLabel();
        lblPelatis = new JLabel();

        btnSave = new JButton("Αποθήκευση ως Προς Απόδοση");
        btnSave.setBackground(new Color(0, 150, 0));
        btnSave.setForeground(Color.WHITE);
        btnSave.setFont(btnSave.getFont().deriveFont(Font.BOLD));

        btnCancel = new JButton("Ακύρωση");
    }

    private void setupLayout() {
        setLayout(new BorderLayout(10, 10));

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel lblTitle = new JLabel("Περίληψη Απόδοσης");
        lblTitle.setFont(lblTitle.getFont().deriveFont(Font.BOLD, 16f));
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        mainPanel.add(lblTitle);
        mainPanel.add(Box.createVerticalStrut(20));

        JPanel infoPanel = new JPanel(new GridLayout(3, 1, 10, 10));
        infoPanel.setBorder(BorderFactory.createTitledBorder("Στοιχεία"));

        lblPelatis.setFont(lblPelatis.getFont().deriveFont(Font.BOLD, 14f));
        lblPlithos.setFont(lblPlithos.getFont().deriveFont(Font.BOLD, 14f));
        lblSynolo.setFont(lblSynolo.getFont().deriveFont(Font.BOLD, 14f));

        infoPanel.add(lblPelatis);
        infoPanel.add(lblPlithos);
        infoPanel.add(lblSynolo);

        mainPanel.add(infoPanel);
        mainPanel.add(Box.createVerticalStrut(20));

        add(mainPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonPanel.add(btnCancel);
        buttonPanel.add(btnSave);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void setupEvents() {
        btnSave.addActionListener(e -> saveAsProApodosi());
        btnCancel.addActionListener(e -> dispose());
    }

    private void calculateSummary() {
        if (selectedAntikatavoles.isEmpty()) {
            return;
        }

        String kodikosPelati = selectedAntikatavoles.get(0).getKodikosPelati();
        String eponimiaEtairias = selectedAntikatavoles.get(0).getEponimiaEtairias();

        BigDecimal synolo = BigDecimal.ZERO;
        for (AntikatavoliFullView view : selectedAntikatavoles) {
            if (view.getAntikatavoli() != null) {
                synolo = synolo.add(view.getAntikatavoli());
            }
        }

        lblPelatis.setText("Πελάτης: " + kodikosPelati + " - " + eponimiaEtairias);
        lblPlithos.setText("Πλήθος Αντικαταβολών: " + selectedAntikatavoles.size());
        lblSynolo.setText(String.format("Σύνολο: %.2f€", synolo));
    }

    private void saveAsProApodosi() {
        if (selectedAntikatavoles.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Δεν υπάρχουν επιλεγμένες αντικαταβολές.",
                    "Προειδοποίηση",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        String kodikosPelati = selectedAntikatavoles.get(0).getKodikosPelati();
        String eponimiaEtairias = selectedAntikatavoles.get(0).getEponimiaEtairias();

        boolean success = antikatavoliService.createProApodosiForPelatis(
                kodikosPelati,
                eponimiaEtairias,
                selectedAntikatavoles
        );

        if (success) {
            JOptionPane.showMessageDialog(this,
                    "Οι αντικαταβολές σημειώθηκαν ως 'Προς Απόδοση' επιτυχώς!",
                    "Επιτυχία",
                    JOptionPane.INFORMATION_MESSAGE);
            saved = true;
            dispose();
        } else {
            JOptionPane.showMessageDialog(this,
                    "Σφάλμα κατά την αποθήκευση.",
                    "Σφάλμα",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isSaved() {
        return saved;
    }
}