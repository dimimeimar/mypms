package org.pms.shipments.ui.panels;

import org.pms.shipments.dao.ApostoliDAO;
import org.pms.shipments.model.Apostoli;
import org.pms.shipments.model.TrackingDetail;
import org.pms.shipments.service.ACSTrackingDetailsService;
import org.pms.shipments.service.TrackingStatusFilter;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ApostoliDetailsTrackingPanel extends JPanel {

    private final Apostoli apostoli;
    private final ACSTrackingDetailsService trackingService;
    private JTable trackingTable;
    private DefaultTableModel tableModel;
    private JButton btnSync;
    private JLabel statusLabel;
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    public ApostoliDetailsTrackingPanel(Apostoli apostoli) {
        this.apostoli = apostoli;
        this.trackingService = new ACSTrackingDetailsService();

        initializeComponents();
        loadTrackingData();
    }

    private void initializeComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);

        JPanel tablePanel = createTablePanel();
        add(tablePanel, BorderLayout.CENTER);

        JPanel statusPanel = createStatusPanel();
        add(statusPanel, BorderLayout.SOUTH);
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Λεπτομέρειες Αποστολής"));

        JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel lblArithmos = new JLabel("Αριθμός Αποστολής: " + apostoli.getArithmosApostolis());
        lblArithmos.setFont(lblArithmos.getFont().deriveFont(Font.BOLD, 14f));
        infoPanel.add(lblArithmos);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnSync = new JButton("Συγχρονισμός Tracking");
        btnSync.setBackground(new Color(34, 139, 34));
        btnSync.setForeground(Color.BLACK);
        btnSync.setFont(btnSync.getFont().deriveFont(Font.BOLD));
        btnSync.addActionListener(new SyncActionListener());
        buttonPanel.add(btnSync);

        panel.add(infoPanel, BorderLayout.WEST);
        panel.add(buttonPanel, BorderLayout.EAST);

        return panel;
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());

        String[] columnNames = {"Ημερομηνία & Ώρα", "Ενέργεια", "Τοποθεσία", "Σημειώσεις"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        trackingTable = new JTable(tableModel);
        trackingTable.setRowHeight(30);
        trackingTable.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        trackingTable.getTableHeader().setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        trackingTable.getTableHeader().setBackground(new Color(25, 42, 86));
        trackingTable.getTableHeader().setForeground(Color.black);

        trackingTable.getColumnModel().getColumn(0).setPreferredWidth(150);
        trackingTable.getColumnModel().getColumn(1).setPreferredWidth(200);
        trackingTable.getColumnModel().getColumn(2).setPreferredWidth(150);
        trackingTable.getColumnModel().getColumn(3).setPreferredWidth(250);

        trackingTable.setDefaultRenderer(Object.class, new TrackingTableCellRenderer());

        JScrollPane scrollPane = new JScrollPane(trackingTable);
        scrollPane.setPreferredSize(new Dimension(750, 300));
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createStatusPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(240, 248, 255)); // Ανοιχτό μπλε background
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Κατάσταση Tracking"),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));

        statusLabel = new JLabel(" ");
        statusLabel.setFont(statusLabel.getFont().deriveFont(Font.BOLD, 13f));
        statusLabel.setOpaque(true);
        statusLabel.setBackground(Color.WHITE);
        statusLabel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLoweredBevelBorder(),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));

        panel.add(statusLabel, BorderLayout.CENTER);
        return panel;
    }

    private void loadTrackingData() {
        try {
            List<TrackingDetail> details = trackingService.getTrackingDetails(apostoli.getArithmosApostolis());
            updateTableData(details);

            if (details.isEmpty()) {
                statusLabel.setText("Δεν υπάρχουν tracking details. Κάντε συγχρονισμό για ενημέρωση.");
                statusLabel.setForeground(Color.ORANGE.darker());
            } else {
                String latestStatus = TrackingStatusFilter.getLatestFilteredStatus(details);
                String message = "Προβάλλονται " + details.size() + " καταχωρήσεις tracking.";
                if (latestStatus != null) {
                    message += " | Τελευταία κατάσταση: " + latestStatus;
                }
                statusLabel.setText(message);
                statusLabel.setForeground(Color.BLUE.darker());
                statusLabel.setForeground(Color.BLUE.darker());
            }
        } catch (Exception e) {
            statusLabel.setText("Σφάλμα φόρτωσης tracking details: " + e.getMessage());
            statusLabel.setForeground(Color.RED);
            System.err.println("Σφάλμα loadTrackingData: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void updateTableData(List<TrackingDetail> details) {
        tableModel.setRowCount(0);

        for (TrackingDetail detail : details) {
            Object[] row = {
                    detail.getCheckpointDateTime().format(dateTimeFormatter),
                    detail.getCheckpointAction(),
                    detail.getCheckpointLocation(),
                    detail.getCheckpointNotes()
            };
            tableModel.addRow(row);
        }
    }

    private class SyncActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            btnSync.setEnabled(false);
            btnSync.setText("Συγχρονισμός...");
            statusLabel.setText("Συγχρονισμός σε εξέλιξη...");
            statusLabel.setForeground(Color.BLUE);

            SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
                @Override
                protected Boolean doInBackground() throws Exception {
                    return trackingService.fetchAndSaveTrackingDetails(apostoli.getArithmosApostolis());
                }

                @Override
                protected void done() {
                    try {
                        boolean success = get();
                        if (success) {
                            loadTrackingData();

                            // Πάρε το φιλτραρισμένο status για debug
                            List<TrackingDetail> details = trackingService.getTrackingDetails(apostoli.getArithmosApostolis());
                            String latestStatus = TrackingStatusFilter.getLatestFilteredStatus(details);

                            String successMessage = "Συγχρονισμός ολοκληρώθηκε επιτυχώς!";
                            if (latestStatus != null) {
                                successMessage += " | Τελευταία κατάσταση: " + latestStatus;
                                System.out.println("DEBUG - Συγχρονισμός " + apostoli.getArithmosApostolis() +
                                        " | Φιλτραρισμένο Status: " + latestStatus);
                            } else {
                                System.out.println("DEBUG - Συγχρονισμός " + apostoli.getArithmosApostolis() +
                                        " | Δεν βρέθηκε έγκυρο φιλτραρισμένο status");
                            }

                            statusLabel.setText(successMessage);
                            statusLabel.setForeground(Color.GREEN.darker());
                        } else {
                            // Έλεγχος και δημιουργία tracking detail για αποστολή που δε βρέθηκε
                            if (!trackingService.hasNotFoundTrackingDetail(apostoli.getArithmosApostolis())) {
                                boolean trackingCreated = trackingService.createNotFoundTrackingDetail(apostoli.getArithmosApostolis());
                                ApostoliDAO apostoliDAO = new ApostoliDAO();
                                boolean dateUpdated = apostoliDAO.updateShipmentNotFound(apostoli.getArithmosApostolis());
                                if (trackingCreated && dateUpdated) {
                                    loadTrackingData(); // Ανανέωση για να φανεί το νέο tracking detail
                                    statusLabel.setText("Αποστολή δε βρέθηκε. Δημιουργήθηκε tracking detail: ΑΠΟΣΤΟΛΗ ΔΕ ΒΡΕΘΗΚΕ");
                                    statusLabel.setForeground(Color.ORANGE);
                                    System.out.println("🔄 Δημιουργήθηκε tracking detail: ΑΠΟΣΤΟΛΗ ΔΕ ΒΡΕΘΗΚΕ για " + apostoli.getArithmosApostolis());
                                } else {
                                    statusLabel.setText("Αποτυχία συγχρονισμού. Ελέγξτε τη σύνδεση.");
                                    statusLabel.setForeground(Color.RED);
                                }
                            } else {
                                statusLabel.setText("Αποστολή δε βρέθηκε (ήδη καταγεγραμμένη)");
                                statusLabel.setForeground(Color.ORANGE);
                                System.out.println("ℹ️ Υπάρχει ήδη tracking detail 'ΑΠΟΣΤΟΛΗ ΔΕ ΒΡΕΘΗΚΕ' για " + apostoli.getArithmosApostolis());
                            }
                        }
                    } catch (Exception ex) {
                        statusLabel.setText("Σφάλμα κατά τον συγχρονισμό: " + ex.getMessage());
                        statusLabel.setForeground(Color.RED);
                    } finally {
                        btnSync.setEnabled(true);
                        btnSync.setText("Συγχρονισμός Tracking");
                    }
                }
            };

            worker.execute();
        }
    }

    private static class TrackingTableCellRenderer extends JLabel implements TableCellRenderer {
        public TrackingTableCellRenderer() {
            setOpaque(true);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                       boolean hasFocus, int row, int column) {
            setText(value != null ? value.toString() : "");

            if (isSelected) {
                setBackground(new Color(184, 207, 229));
                setForeground(Color.BLACK);
            } else {
                if (row % 2 == 0) {
                    setBackground(Color.WHITE);
                } else {
                    setBackground(new Color(245, 245, 245));
                }
                setForeground(Color.BLACK);
            }

            setBorder(BorderFactory.createEmptyBorder(5, 8, 5, 8));
            return this;
        }
    }
}