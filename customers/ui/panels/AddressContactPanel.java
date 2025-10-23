package org.pms.customers.ui.panels;

import org.pms.customers.dao.EtairiaDAO;
import org.pms.customers.model.Etairia;
import org.pms.customers.model.Pelatis;
import org.pms.utils.ValidationUtils;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Panel για διεύθυνση και στοιχεία επικοινωνίας
 */
public class AddressContactPanel extends JPanel {

    // Contact info components
    private JTextField txtEmail;
    private JTextField txtTilefonoStathero;
    private JTextField txtTilefonoKinito;

    // Company address components
    private JTextField txtPoli;
    private JTextField txtPerioxi;
    private JTextField txtOdos;
    private JTextField txtArithmosDiefthinsis;
    private JTextField txtTk;

    public AddressContactPanel() {
        initComponents();
        setupLayout();
        setupFormatting();
    }

    private void initComponents() {
        // Contact info
        txtEmail = new JTextField(25);
        txtTilefonoStathero = new JTextField(15);
        txtTilefonoKinito = new JTextField(15);

        // Company address
        txtPoli = new JTextField(15);
        txtPerioxi = new JTextField(15);
        txtOdos = new JTextField(20);
        txtArithmosDiefthinsis = new JTextField(10);
        txtTk = new JTextField(10);
    }

    private void setupLayout() {
        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        int row = 0;

        // Contact section header
        JLabel contactHeader = new JLabel("Στοιχεία Επικοινωνίας");
        contactHeader.setFont(contactHeader.getFont().deriveFont(Font.BOLD, 14f));
        contactHeader.setForeground(new Color(0, 100, 200));
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 4;
        add(contactHeader, gbc);
        gbc.gridwidth = 1;

        row++;

        // Email
        gbc.gridx = 0; gbc.gridy = row;
        add(new JLabel("Email:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 3; gbc.fill = GridBagConstraints.HORIZONTAL;
        add(txtEmail, gbc);
        gbc.gridwidth = 1; gbc.fill = GridBagConstraints.NONE;

        row++;

        // Phones
        gbc.gridx = 0; gbc.gridy = row;
        add(new JLabel("Τηλέφωνο Σταθερό:"), gbc);
        gbc.gridx = 1;
        add(txtTilefonoStathero, gbc);
        gbc.gridx = 2;
        add(new JLabel("Κινητό:"), gbc);
        gbc.gridx = 3;
        add(txtTilefonoKinito, gbc);

        row += 2;

        // Address section header
        JLabel addressHeader = new JLabel("Διεύθυνση Εταιρίας");
        addressHeader.setFont(addressHeader.getFont().deriveFont(Font.BOLD, 14f));
        addressHeader.setForeground(new Color(0, 100, 200));
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 4;
        add(addressHeader, gbc);
        gbc.gridwidth = 1;

        row++;

        // City and Area
        gbc.gridx = 0; gbc.gridy = row;
        add(new JLabel("Πόλη:"), gbc);
        gbc.gridx = 1;
        add(txtPoli, gbc);
        gbc.gridx = 2;
        add(new JLabel("Περιοχή:"), gbc);
        gbc.gridx = 3;
        add(txtPerioxi, gbc);

        row++;

        // Street and Number
        gbc.gridx = 0; gbc.gridy = row;
        add(new JLabel("Οδός:"), gbc);
        gbc.gridx = 1;
        add(txtOdos, gbc);
        gbc.gridx = 2;
        add(new JLabel("Αριθμός:"), gbc);
        gbc.gridx = 3;
        add(txtArithmosDiefthinsis, gbc);

        row++;

        // Postal code
        gbc.gridx = 0; gbc.gridy = row;
        add(new JLabel("ΤΚ:"), gbc);
        gbc.gridx = 1;
        add(txtTk, gbc);

        // Add some spacing at the bottom
        gbc.gridx = 0; gbc.gridy = row + 1;
        gbc.weighty = 1.0;
        add(Box.createVerticalGlue(), gbc);
    }

    private void setupFormatting() {
        // Phone number formatting
        addPhoneFormatter(txtTilefonoStathero);
        addPhoneFormatter(txtTilefonoKinito);

        // Postal code formatting
        addNumericFormatter(txtTk, 5);

        // Auto-uppercase for city and area
        addUppercaseFormatter(txtPoli);
        addUppercaseFormatter(txtPerioxi);
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

    private void addNumericFormatter(JTextField textField, int maxLength) {
        textField.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyTyped(java.awt.event.KeyEvent e) {
                char c = e.getKeyChar();
                if (!Character.isDigit(c) && c != java.awt.event.KeyEvent.VK_BACK_SPACE) {
                    e.consume();
                }
                if (textField.getText().length() >= maxLength && c != java.awt.event.KeyEvent.VK_BACK_SPACE) {
                    e.consume();
                }
            }
        });
    }

    private void addUppercaseFormatter(JTextField textField) {
        textField.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyReleased(java.awt.event.KeyEvent e) {
                String text = textField.getText().toUpperCase();
                textField.setText(text);
            }
        });
    }

    // Validation method
    public boolean validateData() {
        // Email validation (if provided)
        String email = txtEmail.getText().trim();
        if (!email.isEmpty() && !ValidationUtils.isValidEmail(email)) {
            JOptionPane.showMessageDialog(this,
                    "Μη έγκυρη διεύθυνση email!",
                    "Σφάλμα", JOptionPane.ERROR_MESSAGE);
            txtEmail.requestFocus();
            return false;
        }

        // Phone number validation
        String stathero = txtTilefonoStathero.getText().trim();


        String kinito = txtTilefonoKinito.getText().trim();
        if (!ValidationUtils.isValidPhone(kinito)) {
            JOptionPane.showMessageDialog(this,
                    "Μη έγκυρος αριθμός κινητού τηλεφώνου! (10 ψηφία)",
                    "Σφάλμα", JOptionPane.ERROR_MESSAGE);
            txtTilefonoKinito.requestFocus();
            return false;
        }

        // Postal code validation
        String tk = txtTk.getText().trim();
        if (!ValidationUtils.isValidPostalCode(tk)) {
            JOptionPane.showMessageDialog(this,
                    "Μη έγκυρος ταχυδρομικός κώδικας! (5 ψηφία)",
                    "Σφάλμα", JOptionPane.ERROR_MESSAGE);
            txtTk.requestFocus();
            return false;
        }

        return true;
    }

    // Data loading and saving methods
    public void loadData(Pelatis pelatis) {
        // Φορτώνουμε τα στοιχεία επικοινωνίας από τον πελάτη
        txtEmail.setText(pelatis.getEmail());
        txtTilefonoStathero.setText(pelatis.getTilefonoStathero());
        txtTilefonoKinito.setText(pelatis.getTilefonoKinito());

        // Φορτώνουμε τη διεύθυνση από την εταιρία
        EtairiaDAO etairiaDAO = new EtairiaDAO();
        List<Etairia> etairias = etairiaDAO.findByPelatis(pelatis.getKodikosPelati());

        if (!etairias.isEmpty()) {
            Etairia etairia = etairias.get(0);
            txtPoli.setText(etairia.getPoli());
            txtPerioxi.setText(etairia.getPerioxi());
            txtOdos.setText(etairia.getOdos());
            txtArithmosDiefthinsis.setText(etairia.getArithmos());
            txtTk.setText(etairia.getTk());
        }
    }
    public void saveData(Pelatis pelatis) {
        // Αποθηκεύουμε τα στοιχεία επικοινωνίας στον πελάτη
        pelatis.setEmail(txtEmail.getText().trim());
        pelatis.setTilefonoStathero(txtTilefonoStathero.getText().trim());
        pelatis.setTilefonoKinito(txtTilefonoKinito.getText().trim());

        // Αποθηκεύουμε τη διεύθυνση στην εταιρία
        EtairiaDAO etairiaDAO = new EtairiaDAO();
        List<Etairia> etairias = etairiaDAO.findByPelatis(pelatis.getKodikosPelati());

        Etairia etairia;
        if (etairias.isEmpty()) {
            // Δημιουργούμε νέα εταιρία
            etairia = new Etairia();
            etairia.setAfmPelati(pelatis.getKodikosPelati());
            etairia.setPoli(txtPoli.getText().trim());
            etairia.setPerioxi(txtPerioxi.getText().trim());
            etairia.setOdos(txtOdos.getText().trim());
            etairia.setArithmos(txtArithmosDiefthinsis.getText().trim());
            etairia.setTk(txtTk.getText().trim());
            etairiaDAO.create(etairia);
        } else {
            // Ενημερώνουμε την υπάρχουσα
            etairia = etairias.get(0);
            etairia.setPoli(txtPoli.getText().trim());
            etairia.setPerioxi(txtPerioxi.getText().trim());
            etairia.setOdos(txtOdos.getText().trim());
            etairia.setArithmos(txtArithmosDiefthinsis.getText().trim());
            etairia.setTk(txtTk.getText().trim());
            etairiaDAO.update(etairia);
        }
    }

    public void clearData() {
        txtEmail.setText("");
        txtTilefonoStathero.setText("");
        txtTilefonoKinito.setText("");
        txtPoli.setText("");
        txtPerioxi.setText("");
        txtOdos.setText("");
        txtArithmosDiefthinsis.setText("");
        txtTk.setText("");
    }
}