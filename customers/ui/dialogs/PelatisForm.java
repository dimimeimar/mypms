package org.pms.customers.ui.dialogs;

import org.pms.customers.dao.PelatisDAO;
import org.pms.customers.model.Pelatis;
import org.pms.customers.ui.panels.AddressContactPanel;
import org.pms.customers.ui.panels.BasicInfoPanel;
import org.pms.customers.ui.panels.ContactPersonPanel;

import javax.swing.*;
import java.awt.*;

/**
 * Απλοποιημένη φόρμα διαχείρισης πελατών με χρήση ξεχωριστών panels
 */
public class PelatisForm extends JDialog  {

    private final PelatisDAO pelatisDAO;
    private boolean isEditMode = false;
    private String originalKodikosPelati;

    // Panel components
    private BasicInfoPanel basicInfoPanel;
    private AddressContactPanel addressContactPanel;
    private ContactPersonPanel contactPersonPanel;

    // Control components
    private JButton btnSave;
    private JButton btnCancel;
    private JButton btnClear;

    public PelatisForm() {
        this(null, null);
    }

    public PelatisForm(Pelatis pelatis) {
        this(pelatis, null);
    }


    public PelatisForm(Pelatis pelatis, Frame parent) {
        super(parent, pelatis != null ? "Επεξεργασία Πελάτη" : "Νέος Πελάτης", true);
        this.pelatisDAO = new PelatisDAO();

        if (pelatis != null) {
            this.isEditMode = true;
            this.originalKodikosPelati = pelatis.getKodikosPelati();
        }

        initComponents();
        setupLayout();
        setupEvents();

        if (pelatis != null) {
            loadPelatisData(pelatis);
        }

        setTitle(isEditMode ? "Επεξεργασία Πελάτη" : "Νέος Πελάτης");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        pack();
        setLocationRelativeTo(null); // Κεντράρισμα στην οθόνη αντί για το parent

        // Μεγαλύτερα γράμματα
        Font currentFont = getFont();
        if (currentFont != null) {
            Font newFont = currentFont.deriveFont(currentFont.getSize() + 2f);
            setFont(newFont);
        }
    }

    private void initComponents() {
        // Initialize panels
        basicInfoPanel = new BasicInfoPanel(isEditMode);
        addressContactPanel = new AddressContactPanel();
        contactPersonPanel = new ContactPersonPanel();

        // Initialize buttons
        btnSave = new JButton(isEditMode ? "Ενημέρωση" : "Αποθήκευση");
        btnCancel = new JButton("Ακύρωση");
        btnClear = new JButton("Καθαρισμός");

        // Set button icons if available
        try {
            btnSave.setIcon(new ImageIcon(getClass().getResource("/icons/save.png")));
            btnCancel.setIcon(new ImageIcon(getClass().getResource("/icons/cancel.png")));
            btnClear.setIcon(new ImageIcon(getClass().getResource("/icons/clear.png")));
        } catch (Exception e) {
            // Icons not found, continue without them
        }
    }

    private void setupLayout() {
        setLayout(new BorderLayout());

        // Create tabbed pane
        JTabbedPane tabbedPane = new JTabbedPane();

        // Add tabs
        tabbedPane.addTab("Βασικά Στοιχεία", basicInfoPanel);
        tabbedPane.addTab("Διεύθυνση & Επικοινωνία", addressContactPanel);
        tabbedPane.addTab("Υπεύθυνοι", contactPersonPanel);

        add(tabbedPane, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        buttonPanel.add(btnSave);
        buttonPanel.add(btnClear);
        buttonPanel.add(btnCancel);
        add(buttonPanel, BorderLayout.SOUTH);

        // Required fields note
        JLabel lblRequired = new JLabel("* Υποχρεωτικά πεδία");
        lblRequired.setFont(lblRequired.getFont().deriveFont(Font.ITALIC));
        lblRequired.setForeground(Color.RED);
        lblRequired.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        add(lblRequired, BorderLayout.NORTH);
    }

    private void setupEvents() {
        // Button events
        btnSave.addActionListener(e -> savePelatis());
        btnCancel.addActionListener(e -> dispose());
        btnClear.addActionListener(e -> clearForm());

        // Keyboard shortcuts
        getRootPane().registerKeyboardAction(
                e -> dispose(),
                KeyStroke.getKeyStroke("ESCAPE"),
                JComponent.WHEN_IN_FOCUSED_WINDOW
        );

        getRootPane().registerKeyboardAction(
                e -> savePelatis(),
                KeyStroke.getKeyStroke("ctrl S"),
                JComponent.WHEN_IN_FOCUSED_WINDOW
        );

        // Set default button
        getRootPane().setDefaultButton(btnSave);
    }

    private void loadPelatisData(Pelatis pelatis) {
        basicInfoPanel.loadData(pelatis);
        addressContactPanel.loadData(pelatis);
        contactPersonPanel.loadData(pelatis);
    }

    private void savePelatis() {
        // Validate all panels
        if (!validateAllPanels()) {
            return;
        }

        try {
            Pelatis pelatis = createPelatisFromPanels();
            boolean success;

            if (isEditMode) {
                success = pelatisDAO.update(pelatis);
                if (success) {
                    addressContactPanel.saveData(pelatis);
                    contactPersonPanel.saveData(pelatis);
                    JOptionPane.showMessageDialog(this,
                            "Ο πελάτης ενημερώθηκε επιτυχώς!",
                            "Επιτυχία", JOptionPane.INFORMATION_MESSAGE);
                    dispose();
                }
            } else {
                // Check if customer already exists
                if (pelatisDAO.exists(pelatis.getKodikosPelati())) {
                    JOptionPane.showMessageDialog(this,
                            "Υπάρχει ήδη πελάτης με κωδικό: " + pelatis.getKodikosPelati(),
                            "Σφάλμα", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                success = pelatisDAO.create(pelatis);
                if (success) {
                    // Τώρα μπορούμε να αποθηκεύσουμε τα στοιχεία εταιρίας και υπευθύνων
                    // γιατί ο πελάτης υπάρχει ήδη στη βάση
                    addressContactPanel.saveData(pelatis);
                    contactPersonPanel.saveData(pelatis);
                    JOptionPane.showMessageDialog(this,
                            "Ο πελάτης δημιουργήθηκε επιτυχώς!",
                            "Επιτυχία", JOptionPane.INFORMATION_MESSAGE);
                    dispose();
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Σφάλμα: " + ex.getMessage(),
                    "Σφάλμα", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private boolean validateAllPanels() {
        // Validate each panel
        if (!basicInfoPanel.validateData()) {
            return false;
        }
        if (!addressContactPanel.validateData()) {
            return false;
        }
        if (!contactPersonPanel.validateData()) {
            return false;
        }
        return true;
    }

    private Pelatis createPelatisFromPanels() {
        Pelatis pelatis = new Pelatis();

        // Get data from all panels
        basicInfoPanel.saveData(pelatis);
        addressContactPanel.saveData(pelatis);
        contactPersonPanel.saveData(pelatis);

        return pelatis;
    }

    private void clearForm() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Είστε σίγουροι ότι θέλετε να καθαρίσετε όλα τα πεδία;",
                "Επιβεβαίωση Καθαρισμού",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            basicInfoPanel.clearData();
            addressContactPanel.clearData();
            contactPersonPanel.clearData();
        }
    }
}