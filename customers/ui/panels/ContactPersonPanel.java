package org.pms.customers.ui.panels;

import org.pms.customers.dao.EtairiaDAO;
import org.pms.customers.dao.YpeuthynosDAO;
import org.pms.customers.model.Etairia;
import org.pms.customers.model.Pelatis;
import org.pms.customers.model.Ypeuthynos;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Panel για στοιχεία υπευθύνων - ένας από κάθε κατηγορία
 */
public class ContactPersonPanel extends JPanel {

    // Map για να κρατάμε τα στοιχεία κάθε υπευθύνου
    private Map<String, YpeuthynosFields> ypeuthynoi;

    // Οι κατηγορίες υπευθύνων
    private final String[] CATEGORIES = {
            "ΣΥΜΒΑΣΗΣ", "ΕΠΙΚΟΙΝΩΝΙΑΣ", "ΠΛΗΡΩΜΩΝ", "ΛΟΓΙΣΤΗΡΙΟΥ", "ΑΝΤΙΚΑΤΑΒΟΛΩΝ"
    };

    public ContactPersonPanel() {
        ypeuthynoi = new HashMap<>();
        initComponents();
        setupLayout();
    }

    private void initComponents() {
        // Δημιουργία fields για κάθε κατηγορία υπευθύνου
        for (String category : CATEGORIES) {
            ypeuthynoi.put(category, new YpeuthynosFields());
        }
    }

    private void setupLayout() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Header
        JLabel header = new JLabel("Στοιχεία Υπευθύνων");
        header.setFont(header.getFont().deriveFont(Font.BOLD, 16f));
        header.setForeground(new Color(0, 100, 200));
        header.setHorizontalAlignment(SwingConstants.CENTER);
        add(header, BorderLayout.NORTH);

        // Tabbed pane για κάθε κατηγορία υπευθύνου
        JTabbedPane tabbedPane = new JTabbedPane();

        for (String category : CATEGORIES) {
            JPanel panel = createYpeuthynosPanel(category);
            tabbedPane.addTab(category, panel);
        }

        add(tabbedPane, BorderLayout.CENTER);
    }

    private JPanel createYpeuthynosPanel(String category) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;

        YpeuthynosFields fields = ypeuthynoi.get(category);

        int row = 0;

        // Όνομα
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Όνομα:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(fields.txtOnoma, gbc);
        gbc.gridwidth = 1; gbc.fill = GridBagConstraints.NONE;

        row++;

        // Τίτλος
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Τίτλος:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(fields.txtTitlos, gbc);
        gbc.gridwidth = 1; gbc.fill = GridBagConstraints.NONE;

        row++;

        // Τηλέφωνα
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Τηλέφωνο:"), gbc);
        gbc.gridx = 1;
        panel.add(fields.txtTilefono, gbc);
        gbc.gridx = 2;
        panel.add(new JLabel("Κινητό:"), gbc);
        gbc.gridx = 3;
        panel.add(fields.txtKinito, gbc);

        row++;

        // Email
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 3; gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(fields.txtEmail, gbc);

        // Add spacing at bottom
        gbc.gridx = 0; gbc.gridy = row + 1;
        gbc.weighty = 1.0;
        panel.add(Box.createVerticalGlue(), gbc);

        return panel;
    }

    // Validation method
    public boolean validateData() {
        for (Map.Entry<String, YpeuthynosFields> entry : ypeuthynoi.entrySet()) {
            YpeuthynosFields fields = entry.getValue();
            String category = entry.getKey();

            // Τηλέφωνο validation
            String tilefono = fields.txtTilefono.getText().trim();
            if (!isValidPhone(tilefono)) {
                JOptionPane.showMessageDialog(this,
                        "Μη έγκυρος αριθμός τηλεφώνου για υπεύθυνο " + category + "! (10 ψηφία)",
                        "Σφάλμα", JOptionPane.ERROR_MESSAGE);
                return false;
            }

            // Κινητό validation
            String kinito = fields.txtKinito.getText().trim();
            if (!isValidPhone(kinito)) {
                JOptionPane.showMessageDialog(this,
                        "Μη έγκυρος αριθμός κινητού για υπεύθυνο " + category + "! (10 ψηφία)",
                        "Σφάλμα", JOptionPane.ERROR_MESSAGE);
                return false;
            }

            // Email validation
            String email = fields.txtEmail.getText().trim();
            if (!email.isEmpty() && !isValidEmail(email)) {
                JOptionPane.showMessageDialog(this,
                        "Μη έγκυρη διεύθυνση email για υπεύθυνο " + category + "!",
                        "Σφάλμα", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        }
        return true;
    }

    private boolean isValidPhone(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            return true; // Empty is valid (optional field)
        }
        return phone.matches("\\d{10}");
    }

    private boolean isValidEmail(String email) {
        return email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    }

    // Data loading method - για backward compatibility με το παλιό format
    public void loadData(Pelatis pelatis) {
        // Τώρα φορτώνουμε τους υπευθύνους από τη βάση
        // Βρίσκουμε την εταιρία του πελάτη
        EtairiaDAO etairiaDAO = new EtairiaDAO();
        java.util.List<Etairia> etairias = etairiaDAO.findByPelatis(pelatis.getKodikosPelati());

        if (!etairias.isEmpty()) {
            Etairia etairia = etairias.get(0); // Παίρνουμε την πρώτη εταιρία

            // Φορτώνουμε τους υπευθύνους
            YpeuthynosDAO ypeuthynosDAO = new YpeuthynosDAO();
            java.util.List<Ypeuthynos> ypeuthynoiList = ypeuthynosDAO.findByEtairia(etairia.getIdEtairias());

            // Καθαρίζουμε πρώτα όλα τα πεδία
            clearData();

            // Γεμίζουμε τα αντίστοιχα πεδία
            for (Ypeuthynos ypeuthynos : ypeuthynoiList) {
                String category = ypeuthynos.getEidosYpeuthynou().getValue();
                YpeuthynosFields fields = ypeuthynoi.get(category);

                if (fields != null) {
                    fields.txtOnoma.setText(ypeuthynos.getOnomaYpeuthynou());
                    fields.txtTitlos.setText(ypeuthynos.getTitlos());
                    fields.txtTilefono.setText(ypeuthynos.getTilefono());
                    fields.txtKinito.setText(ypeuthynos.getKinito());
                    fields.txtEmail.setText(ypeuthynos.getEmail());
                }
            }
        }
    }

    // Data saving method - αποθηκεύουμε μόνο τον πρώτο μη κενό υπεύθυνο για backward compatibility
    public void saveData(Pelatis pelatis) {
        // Βρίσκουμε την εταιρία του πελάτη
        EtairiaDAO etairiaDAO = new EtairiaDAO();
        java.util.List<Etairia> etairias = etairiaDAO.findByPelatis(pelatis.getKodikosPelati());

        if (etairias.isEmpty()) {
            // Δημιουργούμε νέα εταιρία αν δεν υπάρχει
            // Για τώρα δεν κάνουμε τίποτα - θα το χειριστούμε στο AddressContactPanel
            return;
        }

        Etairia etairia = etairias.get(0);
        YpeuthynosDAO ypeuthynosDAO = new YpeuthynosDAO();

        // Δημιουργούμε λίστα με όλους τους υπευθύνους
        java.util.List<Ypeuthynos> ypeuthynoiList = new ArrayList<>();

        for (String category : CATEGORIES) {
            YpeuthynosFields fields = ypeuthynoi.get(category);

            // Ελέγχουμε αν έχει συμπληρωθεί τουλάχιστον το όνομα
            if (fields.txtOnoma.getText().trim().isEmpty()) {
                continue; // Παραλείπουμε κενούς υπευθύνους
            }

            Ypeuthynos ypeuthynos = new Ypeuthynos();
            ypeuthynos.setIdEtairias(etairia.getIdEtairias());
            ypeuthynos.setEidosYpeuthynou(Ypeuthynos.EidosYpeuthynou.fromString(category));
            ypeuthynos.setOnomaYpeuthynou(fields.txtOnoma.getText().trim());
            ypeuthynos.setTitlos(fields.txtTitlos.getText().trim());
            ypeuthynos.setTilefono(fields.txtTilefono.getText().trim());
            ypeuthynos.setKinito(fields.txtKinito.getText().trim());
            ypeuthynos.setEmail(fields.txtEmail.getText().trim());
            ypeuthynos.setMasterEmail(false); // Προς το παρόν false

            ypeuthynoiList.add(ypeuthynos);
        }

        // Αποθηκεύουμε όλους τους υπευθύνους
        ypeuthynosDAO.saveAllForEtairia(etairia.getIdEtairias(), ypeuthynoiList);
    }

    public void clearData() {
        for (YpeuthynosFields fields : ypeuthynoi.values()) {
            fields.txtOnoma.setText("");
            fields.txtTitlos.setText("");
            fields.txtTilefono.setText("");
            fields.txtKinito.setText("");
            fields.txtEmail.setText("");
        }
    }

    // Βοηθητική κλάση για τα fields κάθε υπευθύνου
    private class YpeuthynosFields {
        JTextField txtOnoma;
        JTextField txtTitlos;
        JTextField txtTilefono;
        JTextField txtKinito;
        JTextField txtEmail;

        public YpeuthynosFields() {
            txtOnoma = new JTextField(25);
            txtTitlos = new JTextField(20);
            txtTilefono = new JTextField(15);
            txtKinito = new JTextField(15);
            txtEmail = new JTextField(25);

            // Phone formatting
            addPhoneFormatter(txtTilefono);
            addPhoneFormatter(txtKinito);
        }

        private void addPhoneFormatter(JTextField textField) {
            textField.addKeyListener(new java.awt.event.KeyAdapter() {
                @Override
                public void keyTyped(java.awt.event.KeyEvent e) {
                    char c = e.getKeyChar();
                    if (!Character.isDigit(c) && c != java.awt.event.KeyEvent.VK_BACK_SPACE) {
                        e.consume();
                    }
                    if (textField.getText().length() >= 10 && c != java.awt.event.KeyEvent.VK_BACK_SPACE) {
                        e.consume();
                    }
                }
            });
        }
    }
}