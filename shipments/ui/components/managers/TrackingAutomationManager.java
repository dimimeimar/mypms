package org.pms.shipments.ui.components.managers;

import org.pms.shipments.dao.ApostoliDAO;
import org.pms.shipments.service.ACSTrackingService;
import org.pms.shipments.ui.panels.ApostoliManagementPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class TrackingAutomationManager extends JFrame {

    private final ACSTrackingService trackingService;
    private JLabel statusLabel;
    private JLabel countLabel;
    private JButton startStopButton;
    private JButton pauseResumeButton;
    private JProgressBar progressBar;
    private Timer statusUpdateTimer;

    public TrackingAutomationManager(ApostoliDAO apostoliDAO, ApostoliManagementPanel managementPanel) {
        this.trackingService = new ACSTrackingService(apostoliDAO);

        // Ορισμός callback για ανανέωση πίνακα
        this.trackingService.setRefreshCallback(() -> managementPanel.refreshData());

        initializeUI();
        setupStatusUpdater();
    }

    private void initializeUI() {
        setTitle("Αυτοματισμός ACS Tracking");
        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        setSize(600, 300);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel infoPanel = createInfoPanel();
        JPanel controlPanel = createControlPanel();
        JPanel statusPanel = createStatusPanel();

        mainPanel.add(infoPanel, BorderLayout.NORTH);
        mainPanel.add(controlPanel, BorderLayout.CENTER);
        mainPanel.add(statusPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private JPanel createInfoPanel() {
        JPanel panel = new JPanel(new GridLayout(3, 1, 5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Πληροφορίες Συστήματος"));

        JLabel infoLabel1 = new JLabel("• Ενημερώνει αυτόματα όλες τις ACS αποστολές");
        JLabel infoLabel2 = new JLabel("• Ταχύτητα: 5 requests/δευτερόλεπτο");
        JLabel infoLabel3 = new JLabel("• Προτεραιότητα: Νεότερες προς παλαιότερες");

        panel.add(infoLabel1);
        panel.add(infoLabel2);
        panel.add(infoLabel3);

        return panel;
    }

    private JPanel createControlPanel() {
        JPanel panel = new JPanel(new FlowLayout());

        startStopButton = new JButton("Εκκίνηση Αυτοματισμού");
        startStopButton.setPreferredSize(new Dimension(200, 40));
        startStopButton.setFont(startStopButton.getFont().deriveFont(Font.BOLD, 14f));

        pauseResumeButton = new JButton("Παύση");
        pauseResumeButton.setPreferredSize(new Dimension(100, 40));
        pauseResumeButton.setFont(pauseResumeButton.getFont().deriveFont(Font.BOLD, 12f));
        pauseResumeButton.setEnabled(false);

        startStopButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                toggleAutomation();
            }
        });

        pauseResumeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                togglePause();
            }
        });

        panel.add(startStopButton);
        panel.add(Box.createHorizontalStrut(10));
        panel.add(pauseResumeButton);

        return panel;
    }

    private JPanel createStatusPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Κατάσταση"));

        statusLabel = new JLabel("Σταματημένο", JLabel.CENTER);
        statusLabel.setFont(statusLabel.getFont().deriveFont(Font.BOLD));

        countLabel = new JLabel("Αποστολές προς ενημέρωση: -", JLabel.CENTER);

        progressBar = new JProgressBar();
        progressBar.setStringPainted(true);
        progressBar.setString("Αναμονή εκκίνησης");

        JPanel labelPanel = new JPanel(new GridLayout(2, 1));
        labelPanel.add(statusLabel);
        labelPanel.add(countLabel);

        panel.add(labelPanel, BorderLayout.NORTH);
        panel.add(progressBar, BorderLayout.SOUTH);

        return panel;
    }

    private void setupStatusUpdater() {
        statusUpdateTimer = new Timer(1000, e -> updateStatus());
    }

    private void toggleAutomation() {
        if (trackingService.isRunning()) {
            trackingService.stopAutomaticUpdates();
            startStopButton.setText("Εκκίνηση Αυτοματισμού");
            startStopButton.setBackground(null);
            startStopButton.setForeground(null);
            pauseResumeButton.setEnabled(false);
            pauseResumeButton.setText("Παύση");
            pauseResumeButton.setBackground(null);
            pauseResumeButton.setForeground(null);
            statusUpdateTimer.stop();
            updateStatus();
        } else {
            trackingService.startAutomaticUpdates();
            startStopButton.setText("Διακοπή Αυτοματισμού");
            startStopButton.setBackground(new Color(220, 53, 69));
            startStopButton.setForeground(Color.BLACK);
            pauseResumeButton.setEnabled(true);
            statusUpdateTimer.start();
        }
    }

    private void togglePause() {
        if (trackingService.isPaused()) {
            trackingService.resumeAutomaticUpdates();
            pauseResumeButton.setText("Παύση");
            pauseResumeButton.setBackground(new Color(255, 193, 7));
            pauseResumeButton.setForeground(Color.BLACK);
        } else {
            trackingService.pauseAutomaticUpdates();
            pauseResumeButton.setText("Συνέχιση");
            pauseResumeButton.setBackground(new Color(40, 167, 69));
            pauseResumeButton.setForeground(Color.BLACK);
        }
    }

    private void updateStatus() {
        String status = trackingService.getCurrentStatus();
        statusLabel.setText(status);

        if (trackingService.isRunning()) {
            if (trackingService.isPaused()) {
                statusLabel.setForeground(new Color(255, 193, 7));
                statusLabel.setText("Σε παύση");
            } else {
                statusLabel.setForeground(new Color(40, 167, 69));
            }

            int remaining = trackingService.getPendingShipmentsCount();
            countLabel.setText("Αποστολές προς ενημέρωση: " + remaining);

            if (remaining > 0 && !trackingService.isPaused()) {
                progressBar.setString("Ενημέρωση σε εξέλιξη...");
                progressBar.setIndeterminate(true);
            } else if (trackingService.isPaused()) {
                progressBar.setString("Σε παύση");
                progressBar.setIndeterminate(false);
                progressBar.setValue(50);
            } else {
                progressBar.setString("Αναμονή νέων αποστολών");
                progressBar.setIndeterminate(true);
            }
        } else {
            statusLabel.setForeground(Color.GRAY);
            countLabel.setText("Αποστολές προς ενημέρωση: -");
            progressBar.setIndeterminate(false);
            progressBar.setValue(0);
            progressBar.setString("Σταματημένο");
        }
    }

    @Override
    public void dispose() {
        if (trackingService.isRunning()) {
            trackingService.stopAutomaticUpdates();
        }
        if (statusUpdateTimer != null) {
            statusUpdateTimer.stop();
        }
        super.dispose();
    }

    public void showAutomationDialog() {
        setVisible(true);
        toFront();
        requestFocus();
    }
}