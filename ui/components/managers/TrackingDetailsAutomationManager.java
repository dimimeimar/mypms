package org.example.ui.components.managers;

import org.example.dao.ApostoliDAO;
import org.example.model.Apostoli;
import org.example.service.shipment.ACSTrackingDetailsService;
import org.example.ui.panels.shipment.ApostoliManagementPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class TrackingDetailsAutomationManager extends JFrame {

    private final ACSTrackingDetailsService trackingDetailsService;
    private final ApostoliDAO apostoliDAO;
    private JLabel statusLabel;
    private JLabel countLabel;
    private JLabel progressDetailsLabel;
    private JButton startStopButton;
    private JButton pauseResumeButton;
    private JProgressBar progressBar;
    private Timer statusUpdateTimer;

    private ScheduledExecutorService scheduler;
    private List<Apostoli> pendingUpdates;
    private int currentIndex = 0;
    private boolean isRunning = false;
    private boolean isPaused = false;
    private int totalProcessed = 0;
    private int successfulUpdates = 0;
    private int failedUpdates = 0;

    private Runnable refreshCallback;

    public TrackingDetailsAutomationManager(ApostoliDAO apostoliDAO, ApostoliManagementPanel managementPanel) {
        this.trackingDetailsService = new ACSTrackingDetailsService();
        this.apostoliDAO = apostoliDAO;
        this.refreshCallback = () -> managementPanel.refreshData();

        initializeUI();
        setupStatusUpdater();
    }

    private void initializeUI() {
        setTitle("Αυτοματισμός Tracking Details");
        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        setSize(650, 350);
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
        JPanel panel = new JPanel(new GridLayout(4, 1, 5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Πληροφορίες Συστήματος"));

        JLabel infoLabel1 = new JLabel("• Συγχρονίζει αυτόματα όλες τις λεπτομέρειες tracking των ACS αποστολών");
        JLabel infoLabel2 = new JLabel("• Ταχύτητα: 2 requests/δευτερόλεπτο (προστασία API)");
        JLabel infoLabel3 = new JLabel("• Προτεραιότητα: Νεότερες προς παλαιότερες αποστολές");
        JLabel infoLabel4 = new JLabel("• Αποθηκεύει όλο το ιστορικό tracking για κάθε αποστολή");

        panel.add(infoLabel1);
        panel.add(infoLabel2);
        panel.add(infoLabel3);
        panel.add(infoLabel4);

        return panel;
    }

    private JPanel createControlPanel() {
        JPanel panel = new JPanel(new FlowLayout());

        startStopButton = new JButton("Εκκίνηση Συγχρονισμού");
        startStopButton.setPreferredSize(new Dimension(200, 40));
        startStopButton.setFont(startStopButton.getFont().deriveFont(Font.BOLD, 14f));
        startStopButton.setBackground(new Color(34, 139, 34));
        startStopButton.setForeground(Color.BLACK);

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

        countLabel = new JLabel("Αποστολές προς συγχρονισμό: -", JLabel.CENTER);

        progressDetailsLabel = new JLabel("Επιτυχείς: 0 | Αποτυχίες: 0", JLabel.CENTER);
        progressDetailsLabel.setFont(progressDetailsLabel.getFont().deriveFont(Font.ITALIC));

        progressBar = new JProgressBar();
        progressBar.setStringPainted(true);
        progressBar.setString("Αναμονή...");

        JPanel topStatusPanel = new JPanel(new GridLayout(3, 1));
        topStatusPanel.add(statusLabel);
        topStatusPanel.add(countLabel);
        topStatusPanel.add(progressDetailsLabel);

        panel.add(topStatusPanel, BorderLayout.CENTER);
        panel.add(progressBar, BorderLayout.SOUTH);

        return panel;
    }

    private void setupStatusUpdater() {
        statusUpdateTimer = new Timer(1000, e -> updateStatusDisplay());
    }

    private void updateStatusDisplay() {
        if (!isRunning) {
            statusLabel.setText("Σταματημένο");
            statusLabel.setForeground(Color.BLACK);
            return;
        }

        if (isPaused) {
            statusLabel.setText("Σε παύση");
            statusLabel.setForeground(Color.ORANGE);
        } else {
            statusLabel.setText("Εκτελείται");
            statusLabel.setForeground(Color.GREEN.darker());
        }

        if (pendingUpdates != null) {
            countLabel.setText("Αποστολές: " + currentIndex + "/" + pendingUpdates.size());
            progressDetailsLabel.setText("Επιτυχείς: " + successfulUpdates + " | Αποτυχίες: " + failedUpdates);

            if (pendingUpdates.size() > 0) {
                int progress = (int) ((double) currentIndex / pendingUpdates.size() * 100);
                progressBar.setValue(progress);
                progressBar.setString(progress + "% (" + currentIndex + "/" + pendingUpdates.size() + ")");
            }
        }
    }

    private void toggleAutomation() {
        if (!isRunning) {
            startAutomation();
        } else {
            stopAutomation();
        }
    }

    private void togglePause() {
        if (isPaused) {
            resumeAutomation();
        } else {
            pauseAutomation();
        }
    }

    private void startAutomation() {
        if (isRunning) {
            return;
        }

        System.out.println("=== ΕΚΚΙΝΗΣΗ TRACKING DETAILS AUTOMATION ===");

        loadPendingShipments();

        if (pendingUpdates.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Δεν βρέθηκαν ACS αποστολές για συγχρονισμό tracking details.",
                    "Πληροφορία", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        currentIndex = 0;
        totalProcessed = 0;
        successfulUpdates = 0;
        failedUpdates = 0;
        isRunning = true;
        isPaused = false;

        startStopButton.setText("Διακοπή");
        startStopButton.setBackground(new Color(220, 20, 60));
        pauseResumeButton.setEnabled(true);

        scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(this::processNextShipment, 0, 500, TimeUnit.MILLISECONDS);

        statusUpdateTimer.start();
    }

    private void stopAutomation() {
        if (!isRunning) {
            return;
        }

        isRunning = false;
        isPaused = false;

        System.out.println("=== ΔΙΑΚΟΠΗ TRACKING DETAILS AUTOMATION ===");
        System.out.println("Συνολικά επεξεργασμένες: " + totalProcessed);
        System.out.println("Επιτυχείς: " + successfulUpdates);
        System.out.println("Αποτυχίες: " + failedUpdates);

        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdown();
            try {
                if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                    scheduler.shutdownNow();
                }
            } catch (InterruptedException e) {
                scheduler.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }

        startStopButton.setText("Εκκίνηση Συγχρονισμού");
        startStopButton.setBackground(new Color(34, 139, 34));
        pauseResumeButton.setEnabled(false);
        pauseResumeButton.setText("Παύση");

        statusUpdateTimer.stop();
        updateStatusDisplay();

        if (refreshCallback != null) {
            SwingUtilities.invokeLater(refreshCallback);
        }
    }

    private void pauseAutomation() {
        if (!isRunning) return;
        isPaused = true;
        pauseResumeButton.setText("Συνέχεια");
        System.out.println("ΠΑΥΣΗ tracking details automation...");
    }

    private void resumeAutomation() {
        if (!isRunning) return;
        isPaused = false;
        pauseResumeButton.setText("Παύση");
        System.out.println("ΣΥΝΕΧΙΣΗ tracking details automation...");
    }

    private void loadPendingShipments() {
        System.out.println("\n=== ΦΟΡΤΩΣΗ ΑΠΟΣΤΟΛΩΝ ===");
        System.out.println("Αναζήτηση ACS αποστολών για tracking details...");

        pendingUpdates = apostoliDAO.findACSShipmentsForTracking();
        System.out.println("Βρέθηκαν " + pendingUpdates.size() + " ACS αποστολές");

        for (Apostoli apostoli : pendingUpdates) {
            System.out.println("- Αποστολή: " + apostoli.getArithmosApostolis() +
                    " | Status: " + apostoli.getStatusApostolis() +
                    " | Ημ/νία: " + apostoli.getImerominiaParalabis());
        }
    }

    private void processNextShipment() {
        if (!isRunning || isPaused) {
            return;
        }

        if (currentIndex >= pendingUpdates.size()) {
            System.out.println("=== ΟΛΟΚΛΗΡΩΣΗ TRACKING DETAILS AUTOMATION ===");
            stopAutomation();

            String message = String.format(
                    "Ολοκληρώθηκε ο συγχρονισμός!\n\n" +
                            "Συνολικά επεξεργασμένες: %d\n" +
                            "Επιτυχείς ενημερώσεις: %d\n" +
                            "Αποτυχίες: %d",
                    totalProcessed, successfulUpdates, failedUpdates
            );

            JOptionPane.showMessageDialog(this, message, "Ολοκλήρωση", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        Apostoli apostoli = pendingUpdates.get(currentIndex);
        currentIndex++;
        totalProcessed++;

        System.out.println("\n--- ΕΠΕΞΕΡΓΑΣΙΑ ΑΠΟΣΤΟΛΗΣ " + currentIndex + "/" + pendingUpdates.size() + " ---");
        System.out.println("Αποστολή: " + apostoli.getArithmosApostolis());

        try {
            boolean success = trackingDetailsService.fetchAndSaveTrackingDetails(apostoli.getArithmosApostolis());

            if (success) {
                successfulUpdates++;
                System.out.println("✅ ΕΠΙΤΥΧΗΣ ΣΥΓΧΡΟΝΙΣΜΟΣ: " + apostoli.getArithmosApostolis());
            } else {
                failedUpdates++;
                System.err.println("❌ ΑΠΟΤΥΧΙΑ ΣΥΓΧΡΟΝΙΣΜΟΥ: " + apostoli.getArithmosApostolis());
            }

        } catch (Exception e) {
            failedUpdates++;
            System.err.println("ΣΦΑΛΜΑ συγχρονισμού αποστολής " + apostoli.getArithmosApostolis() + ": " + e.getMessage());
        }
    }

    public boolean isRunning() {
        return isRunning;
    }

    public boolean isPaused() {
        return isPaused;
    }

    public int getCurrentIndex() {
        return currentIndex;
    }

    public int getTotalShipments() {
        return pendingUpdates != null ? pendingUpdates.size() : 0;
    }
}