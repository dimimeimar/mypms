package org.example.ui.components.managers;

import org.example.dao.ApostoliDAO;
import org.example.service.shipment.ACSDateTrackingService;

import javax.swing.*;
import java.awt.*;

public class ACSDateTrackingManager {

    private final ACSDateTrackingService dateTrackingService;
    private JDialog dialog;
    private JLabel statusLabel;
    private JButton startButton;
    private JButton pauseButton;
    private JButton stopButton;
    private Timer statusUpdateTimer;

    public ACSDateTrackingManager(ApostoliDAO apostoliDAO) {
        this.dateTrackingService = new ACSDateTrackingService(apostoliDAO);
        initializeStatusTimer();
    }

    public void setRefreshCallback(Runnable callback) {
        dateTrackingService.setRefreshCallback(callback);
    }

    private void initializeStatusTimer() {
        statusUpdateTimer = new Timer(2000, e -> updateStatusDisplay());
        statusUpdateTimer.start();
    }

    public void showAutomationDialog() {
        if (dialog == null) {
            createDialog();
        }
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }

    private void createDialog() {
        dialog = new JDialog((Frame) null, "ACS Αυτοματισμός Ημερομηνιών Έκδοσης", true);
        dialog.setSize(500, 300);
        dialog.setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JLabel titleLabel = new JLabel("Αυτοματισμός Ενημέρωσης Ημερομηνιών Έκδοσης");
        titleLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));
        headerPanel.add(titleLabel);

        JPanel statusPanel = new JPanel(new BorderLayout());
        statusPanel.setBorder(BorderFactory.createTitledBorder("Κατάσταση Συστήματος"));

        statusLabel = new JLabel("Σταματημένο", SwingConstants.CENTER);
        statusLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
        statusLabel.setForeground(Color.RED);

        JTextArea infoArea = new JTextArea();
        infoArea.setText(
                "Αυτή η λειτουργία ενημερώνει τις ημερομηνίες έκδοσης των αποστολών ACS\n" +
                        "μέσω του API 'ACS_TrackingDetails'.\n\n" +
                        "Επεξεργάζεται μόνο αποστολές που:\n" +
                        "• Έχουν courier = 'ACS'\n" +
                        "• Δεν είναι κλειδωμένες\n" +
                        "• Δεν έχουν ημερομηνία έκδοσης ή έχουν NULL\n\n" +
                        "Χρόνος ανάμεσα σε κλήσεις API: 4 δευτερόλεπτα"
        );
        infoArea.setEditable(false);
        infoArea.setOpaque(false);
        infoArea.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));

        statusPanel.add(statusLabel, BorderLayout.NORTH);
        statusPanel.add(new JScrollPane(infoArea), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

        startButton = new JButton("Εκκίνηση");
        startButton.setPreferredSize(new Dimension(100, 35));
        startButton.addActionListener(e -> {
            dateTrackingService.startAutomaticUpdates();
            updateButtonStates();
        });

        pauseButton = new JButton("Παύση");
        pauseButton.setPreferredSize(new Dimension(100, 35));
        pauseButton.setEnabled(false);
        pauseButton.addActionListener(e -> {
            dateTrackingService.pauseAutomaticUpdates();
            updateButtonStates();
        });

        stopButton = new JButton("Στόπ");
        stopButton.setPreferredSize(new Dimension(100, 35));
        stopButton.setEnabled(false);
        stopButton.addActionListener(e -> {
            dateTrackingService.stopAutomaticUpdates();
            updateButtonStates();
        });

        JButton closeButton = new JButton("Κλείσιμο");
        closeButton.setPreferredSize(new Dimension(100, 35));
        closeButton.addActionListener(e -> dialog.setVisible(false));

        buttonPanel.add(startButton);
        buttonPanel.add(pauseButton);
        buttonPanel.add(stopButton);
        buttonPanel.add(closeButton);

        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(statusPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        dialog.add(mainPanel);
    }

    private void updateButtonStates() {
        boolean isRunning = dateTrackingService.isRunning();
        boolean isPaused = dateTrackingService.isPaused();

        startButton.setEnabled(!isRunning);
        pauseButton.setEnabled(isRunning);
        stopButton.setEnabled(isRunning);

        if (isPaused) {
            pauseButton.setText("Συνέχεια");
        } else {
            pauseButton.setText("Παύση");
        }
    }

    private void updateStatusDisplay() {
        if (statusLabel == null) return;

        String status = dateTrackingService.getCurrentStatus();
        statusLabel.setText(status);

        if (dateTrackingService.isRunning()) {
            if (dateTrackingService.isPaused()) {
                statusLabel.setForeground(Color.ORANGE);
            } else {
                statusLabel.setForeground(Color.blue);
            }
        } else {
            statusLabel.setForeground(Color.RED);
        }

        updateButtonStates();
    }

    public void cleanup() {
        if (statusUpdateTimer != null) {
            statusUpdateTimer.stop();
        }
        if (dateTrackingService != null) {
            dateTrackingService.stopAutomaticUpdates();
        }
    }
}