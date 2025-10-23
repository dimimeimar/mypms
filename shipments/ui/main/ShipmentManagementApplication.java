package org.pms.shipments.ui.main;

import org.pms.firebase.FirebaseConfig;
import org.pms.firebase.FirebaseSyncService;
import org.pms.shipments.ui.components.managers.TrackingAutomationManager;
import org.pms.shipments.ui.components.managers.TrackingDetailsAutomationManager;
import org.pms.shipments.ui.panels.ApostoliManagementPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.InputStream;

/**
 * Εφαρμογή διαχείρισης αποστολών
 */
public class ShipmentManagementApplication extends JFrame {
    private ApostoliManagementPanel apostoliPanel;
    private TrackingAutomationManager trackingManager;
    private TrackingDetailsAutomationManager trackingDetailsManager;

    FirebaseSyncService firebaseSyncService;

    public ShipmentManagementApplication() {
        initComponents();
        setupLayout();
        setupEvents();


        setTitle("MyPMS - Σύστημα Διαχείρισης Αποστολών");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1400, 800);
        setLocationRelativeTo(null);

        try {
            setIconImage(new ImageIcon(getClass().getResource("/icons/app-icon.png")).getImage());
        } catch (Exception e) {
            // Icon not found, continue without it
        }

    }

    private void initComponents() {
        // Initialize management panel
        apostoliPanel = new ApostoliManagementPanel();
        trackingManager = new TrackingAutomationManager(apostoliPanel.getApostoliDAO(), apostoliPanel);
        this.trackingDetailsManager = new TrackingDetailsAutomationManager(apostoliPanel.getApostoliDAO(), apostoliPanel);

        try {
            InputStream firebaseStream = getClass().getResourceAsStream("/firebase-service-account.json");

            if (firebaseStream != null) {
                FirebaseConfig.getInstance().initializeWithStream(firebaseStream);
                firebaseSyncService = new FirebaseSyncService();
                apostoliPanel.setFirebaseSyncService(firebaseSyncService);
            } else {
                System.err.println("⚠ Firebase credentials file not found in resources");
                firebaseSyncService = null;
            }
        } catch (Exception e) {
            System.err.println("⚠ Firebase initialization failed: " + e.getMessage());
            e.printStackTrace();
            firebaseSyncService = null;
        }
    }

    private void setupLayout() {
        setLayout(new BorderLayout());

        // Menu bar
        JMenuBar menuBar = createMenuBar();
        setJMenuBar(menuBar);

        // Main content
        add(apostoliPanel, BorderLayout.CENTER);

        // Status bar
        JPanel statusBar = createStatusBar();
        add(statusBar, BorderLayout.SOUTH);
    }

    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        // File Menu
        JMenu fileMenu = new JMenu("Αρχείο");
        fileMenu.setMnemonic('Α');

        JMenuItem importItem = new JMenuItem("Εισαγωγή Αποστολών");
        importItem.addActionListener(e -> apostoliPanel.openAddDialog());

        JMenuItem exportItem = new JMenuItem("Εξαγωγή Excel");
        exportItem.addActionListener(e -> apostoliPanel.exportToExcel());

        fileMenu.add(importItem);
        fileMenu.add(exportItem);


        JMenuItem exitItem = new JMenuItem("Έξοδος");
        exitItem.setAccelerator(KeyStroke.getKeyStroke("ctrl Q"));
        exitItem.addActionListener(e -> dispose());

        fileMenu.addSeparator();
        fileMenu.add(exitItem);

        // View Menu
        JMenu viewMenu = new JMenu("Προβολή");
        viewMenu.setMnemonic('Π');

        JMenuItem refreshItem = new JMenuItem("Ανανέωση");
        refreshItem.setAccelerator(KeyStroke.getKeyStroke("F5"));
        refreshItem.addActionListener(e -> apostoliPanel.refreshData());

        viewMenu.add(refreshItem);

        // Tools Menu
        JMenu toolsMenu = new JMenu("Εργαλεία");
        toolsMenu.setMnemonic('Ε');

        JMenuItem updateStatusItem = new JMenuItem("UPDATE TRACKING SUMMARY");
        updateStatusItem.addActionListener(e -> trackingManager.showAutomationDialog());

        JMenuItem trackingDetailsItem = new JMenuItem("UPDATE TRACKING DETAILS");
        trackingDetailsItem.addActionListener(e -> trackingDetailsManager.setVisible(true));


        JMenuItem syncFirebaseItem = new JMenuItem("Sync Firebase");
        syncFirebaseItem.addActionListener(e -> apostoliPanel.performFirebaseSync());


        toolsMenu.add(updateStatusItem);
        toolsMenu.add(trackingDetailsItem);
        toolsMenu.addSeparator();

        toolsMenu.add(syncFirebaseItem);

        menuBar.add(fileMenu);
        menuBar.add(toolsMenu);
        menuBar.add(viewMenu);

        return menuBar;
    }

    private JPanel createStatusBar() {
        JPanel statusBar = new JPanel(new BorderLayout());
        statusBar.setBorder(BorderFactory.createLoweredBevelBorder());

        JLabel statusLabel = new JLabel("Διαχείριση Αποστολών");
        statusBar.add(statusLabel, BorderLayout.WEST);

        // Add connection status
        JLabel connectionLabel = new JLabel("Συνδεδεμένο στη βάση");
        connectionLabel.setForeground(Color.GREEN.darker());
        statusBar.add(connectionLabel, BorderLayout.EAST);

        return statusBar;
    }

    private void setupEvents() {
        // Window closing event
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                dispose();
            }
        });
    }

    private void showHelpDialog() {
        String message = """
                Οδηγίες Χρήσης - Διαχείριση Αποστολών
                ΣΕ ΑΝΑΠΤΥΞΗ ... ΘΑ ΠΡΟΣΤΕΘΕΙ ΠΑΡΑΘΥΡΟ ΒΟΗΘΕΙΑΣ""";

        JOptionPane.showMessageDialog(this, message, "Οδηγίες Χρήσης",
                JOptionPane.INFORMATION_MESSAGE);
    }

    public TrackingAutomationManager getTrackingManager() {
        return trackingManager;
    }

    private void showAboutDialog() {
        String message = """
                MyPMS - Σύστημα Διαχείρισης Αποστολών
                Έκδοση: ALPHA 2.0
                
                Ένα ολοκληρωμένο σύστημα για τη διαχείριση:
                • Αποστολών με πλήρη στοιχεία
                • Στοιχείων παραλήπτη
                • Παρακολούθησης κατάστασης
                • Συνδέσεων με πελάτες
                
                Χαρακτηριστικά:
                • Πλήρης διαχείριση αποστολών
                • Προηγμένη αναζήτηση και φιλτράρισμα
                • Σύνδεση με βάση πελατών
                """;

        JOptionPane.showMessageDialog(this, message, "Σχετικά με το Σύστημα Αποστολών",
                JOptionPane.INFORMATION_MESSAGE);
    }


}