package org.pms.customers.ui;

import org.pms.customers.ui.dialogs.PelatisForm;
import org.pms.customers.ui.panels.PelatisManagementPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Ενημερωμένη κύρια εφαρμογή - Customer Management Window με απλοποιημένο interface (χωρίς αναφορές)
 */
public class CustomerManagementApplication extends JFrame {

    private PelatisManagementPanel pelatisPanel;

    public CustomerManagementApplication() {
        initComponents();
        setupLayout();
        setupEvents();

        setTitle("MyPMS - Σύστημα Διαχείρισης Πελατών");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1200, 700);
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
        pelatisPanel = new PelatisManagementPanel();
    }

    private void setupLayout() {
        setLayout(new BorderLayout());

        // Menu bar
        JMenuBar menuBar = createMenuBar();
        setJMenuBar(menuBar);

        // Main content - directly add the customer management panel
        add(pelatisPanel, BorderLayout.CENTER);

        // Status bar
        JPanel statusBar = createStatusBar();
        add(statusBar, BorderLayout.SOUTH);
    }

    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        // File Menu
        JMenu fileMenu = new JMenu("Αρχείο");
        fileMenu.setMnemonic('Α');

        JMenuItem newCustomerItem = new JMenuItem("Νέος Πελάτης");
        newCustomerItem.setAccelerator(KeyStroke.getKeyStroke("ctrl N"));
        newCustomerItem.addActionListener(e -> {
            // Trigger new customer form
            PelatisForm form = new PelatisForm();
            form.setVisible(true);
            form.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent windowEvent) {
                    pelatisPanel.refreshData();
                }
            });
        });

        JMenuItem exitItem = new JMenuItem("Έξοδος");
        exitItem.setAccelerator(KeyStroke.getKeyStroke("ctrl Q"));
        exitItem.addActionListener(e -> dispose());

        fileMenu.add(newCustomerItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);

        // View Menu
        JMenu viewMenu = new JMenu("Προβολή");
        viewMenu.setMnemonic('Π');

        JMenuItem refreshItem = new JMenuItem("Ανανέωση");
        refreshItem.setAccelerator(KeyStroke.getKeyStroke("F5"));
        refreshItem.addActionListener(e -> pelatisPanel.refreshData());

        viewMenu.add(refreshItem);

        // Tools Menu
        JMenu toolsMenu = new JMenu("Εργαλεία");
        toolsMenu.setMnemonic('Ε');

        JMenuItem exportItem = new JMenuItem("Εξαγωγή Δεδομένων");
        exportItem.addActionListener(e -> JOptionPane.showMessageDialog(this,
                "Δυνατότητα εξαγωγής - Σε ανάπτυξη"));

        JMenuItem importItem = new JMenuItem("Εισαγωγή Δεδομένων");
        importItem.addActionListener(e -> JOptionPane.showMessageDialog(this,
                "Δυνατότητα εισαγωγής - Σε ανάπτυξη"));

        JMenuItem backupItem = new JMenuItem("Δημιουργία Backup");
        backupItem.addActionListener(e -> JOptionPane.showMessageDialog(this,
                "Δυνατότητα Backup - Σε ανάπτυξη"));

        JMenuItem settingsItem = new JMenuItem("Ρυθμίσεις");
        settingsItem.addActionListener(e -> JOptionPane.showMessageDialog(this,
                "Ρυθμίσεις - Σε ανάπτυξη"));

        toolsMenu.add(exportItem);
        toolsMenu.add(importItem);
        toolsMenu.addSeparator();
        toolsMenu.add(backupItem);
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

        JLabel statusLabel = new JLabel("Διαχείριση Πελατών");
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
                Οδηγίες Χρήσης - MyPMS
                            
                Διαχείριση Πελατών:
                • Νέος Πελάτης: Ctrl+N ή κουμπί "Νέος"
                • Επεξεργασία: Διπλό κλικ ή κουμπί "Επεξεργασία"
                • Διαγραφή: Επιλογή πελάτη και κουμπί "Διαγραφή"
                • Λεπτομέρειες: Κουμπί "Λεπτομέρειες"
                • Αναζήτηση: Πληκτρολογήστε στο πεδίο αναζήτησης
                            
                Γενικά:
                • Ανανέωση: F5
                • Έξοδος: Ctrl+Q
                • Βοήθεια: F1
                            
                Σημείωση: Όλα τα στοιχεία (εταιρία, υπεύθυνος, αντικαταβολές)
                διαχειρίζονται από τη φόρμα του πελάτη.
                """;

        JOptionPane.showMessageDialog(this, message, "Οδηγίες Χρήσης",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void showAboutDialog() {
        String message = """
                MyPMS - Σύστημα Διαχείρισης Πελατών
                Έκδοση: 2.0
                            
                Ένα ολοκληρωμένο σύστημα για τη διαχείριση:
                • Πελατών με πλήρη στοιχεία
                • Διευθύνσεων εταιριών
                • Υπευθύνων επικοινωνίας
                • Στοιχείων αντικαταβολών
                            
                Χαρακτηριστικά:
                • Ενιαία διαχείριση όλων των στοιχείων
                • Προηγμένη αναζήτηση και φιλτράρισμα
                • Φιλικό περιβάλλον χρήσης
                • Αυτόματη αποθήκευση και ανάκτηση
                            
                Δημιουργήθηκε με Java Swing & MySQL
                """;

        JOptionPane.showMessageDialog(this, message, "Σχετικά με το MyPMS",
                JOptionPane.INFORMATION_MESSAGE);
    }
}