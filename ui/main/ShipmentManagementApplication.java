package org.example.ui.main;

import org.example.ui.components.managers.ACSDateTrackingManager;
import org.example.ui.components.managers.TrackingAutomationManager;
import org.example.ui.components.managers.TrackingDetailsAutomationManager;
import org.example.ui.panels.shipment.ApostoliManagementPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Εφαρμογή διαχείρισης αποστολών
 */
public class ShipmentManagementApplication extends JFrame {

    private ApostoliManagementPanel apostoliPanel;
    private TrackingAutomationManager trackingManager;
    private ACSDateTrackingManager dateTrackingManager;
    private TrackingDetailsAutomationManager trackingDetailsManager;

    public ShipmentManagementApplication() {
        initComponents();
        setupLayout();
        setupEvents();

        setTitle("MyPMS - Σύστημα Διαχείρισης Αποστολών");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1400, 800);
        setLocationRelativeTo(null);

        // Set application icon if available
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
        this.dateTrackingManager = new ACSDateTrackingManager(apostoliPanel.getApostoliDAO());
        this.trackingDetailsManager = new TrackingDetailsAutomationManager(apostoliPanel.getApostoliDAO(), apostoliPanel);
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
        dateTrackingManager.setRefreshCallback(() -> apostoliPanel.refreshData());

        viewMenu.add(refreshItem);

        // Tools Menu
        JMenu toolsMenu = new JMenu("Εργαλεία");
        toolsMenu.setMnemonic('Ε');

        JMenuItem exportItem = new JMenuItem("Εξαγωγή Δεδομένων");
        exportItem.addActionListener(e -> JOptionPane.showMessageDialog(this,
                "Δυνατότητα εξαγωγής - Σε ανάπτυξη"));

        JMenuItem importItem = new JMenuItem("Εισαγωγή από API");
        importItem.addActionListener(e -> JOptionPane.showMessageDialog(this,
                "Εισαγωγή από API - Σε ανάπτυξη"));

        JMenuItem updateStatusItem = new JMenuItem("ACS Αυτοματισμός Tracking");
        updateStatusItem.addActionListener(e -> trackingManager.showAutomationDialog());

        JMenuItem updateDateItem = new JMenuItem("ACS Αυτοματισμός Ημερομηνιών");
        updateDateItem.addActionListener(e -> dateTrackingManager.showAutomationDialog());

        JMenuItem trackingDetailsItem = new JMenuItem("ACS Tracking Details Συγχρονισμός");
        trackingDetailsItem.addActionListener(e -> trackingDetailsManager.setVisible(true));

        JMenuItem settingsItem = new JMenuItem("Ρυθμίσεις API");
        settingsItem.addActionListener(e -> JOptionPane.showMessageDialog(this,
                "Ρυθμίσεις API - Σε ανάπτυξη"));

        toolsMenu.add(exportItem);
        toolsMenu.add(importItem);
        toolsMenu.addSeparator();
        toolsMenu.add(updateStatusItem);
        toolsMenu.add(updateDateItem);
        toolsMenu.add(trackingDetailsItem);
        toolsMenu.addSeparator();
        toolsMenu.add(settingsItem);

        // Help Menu
        JMenu helpMenu = new JMenu("Βοήθεια");
        helpMenu.setMnemonic('Β');

        JMenuItem helpItem = new JMenuItem("Οδηγίες Χρήσης");
        helpItem.setAccelerator(KeyStroke.getKeyStroke("F1"));
        helpItem.addActionListener(e -> showHelpDialog());

        JMenuItem aboutItem = new JMenuItem("Σχετικά");
        aboutItem.addActionListener(e -> showAboutDialog());

        helpMenu.add(helpItem);
        helpMenu.addSeparator();
        helpMenu.add(aboutItem);

        // Add menus to menu bar
        menuBar.add(fileMenu);
        menuBar.add(viewMenu);
        menuBar.add(toolsMenu);
        menuBar.add(Box.createHorizontalGlue());
        menuBar.add(helpMenu);

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
                            
                Διαχείριση Αποστολών:
                • Νέα Αποστολή: Ctrl+N ή κουμπί "Νέα"
                • Επεξεργασία: Διπλό κλικ ή κουμπί "Επεξεργασία"
                • Διαγραφή: Επιλογή αποστολής και κουμπί "Διαγραφή"
                • Λεπτομέρειες: Κουμπί "Λεπτομέρειες"
                • Αναζήτηση: Πληκτρολογήστε στο πεδίο αναζήτησης
                            
                Φίλτρα:
                • Courier: Φιλτράρισμα βάσει εταιρίας μεταφοράς
                • Status: Φιλτράρισμα βάσει κατάστασης
                • Πελάτης: Προβολή αποστολών συγκεκριμένου πελάτη
                            
                Γενικά:
                • Ανανέωση: F5
                • Έξοδος: Ctrl+Q
                • Βοήθεια: F1
                            
                Σημείωση: Στο μέλλον θα προστεθούν δυνατότητες 
                εισαγωγής και ενημέρωσης από API.
                """;

        JOptionPane.showMessageDialog(this, message, "Οδηγίες Χρήσης",
                JOptionPane.INFORMATION_MESSAGE);
    }

    public TrackingAutomationManager getTrackingManager() {
        return trackingManager;
    }

    private void showAboutDialog() {
        String message = """
                MyPMS - Σύστημα Διαχείρισης Αποστολών
                Έκδοση: 2.0
                            
                Ένα ολοκληρωμένο σύστημα για τη διαχείριση:
                • Αποστολών με πλήρη στοιχεία
                • Στοιχείων παραλήπτη
                • Παρακολούθησης κατάστασης
                • Συνδέσεων με πελάτες
                            
                Χαρακτηριστικά:
                • Πλήρης διαχείριση αποστολών
                • Προηγμένη αναζήτηση και φιλτράρισμα
                • Σύνδεση με βάση πελατών
                • Έτοιμο για API integration
                            
                Επόμενες λειτουργίες:
                • Εισαγωγή από API
                • Αυτόματη ενημέρωση status
                • Αναφορές και στατιστικά
                            
                Δημιουργήθηκε με Java Swing & MySQL
                """;

        JOptionPane.showMessageDialog(this, message, "Σχετικά με το Σύστημα Αποστολών",
                JOptionPane.INFORMATION_MESSAGE);
    }
}