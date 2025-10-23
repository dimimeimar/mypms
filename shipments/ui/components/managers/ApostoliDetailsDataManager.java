package org.pms.shipments.ui.components.managers;

import org.pms.shipments.dao.ApostoliDAO;
import org.pms.shipments.model.Apostoli;
import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class ApostoliDetailsDataManager {

    private final ApostoliDAO apostoliDAO;
    private final Apostoli apostoli;
    private final DateTimeFormatter dateFormatter;

    public ApostoliDetailsDataManager(ApostoliDAO apostoliDAO, Apostoli apostoli, DateTimeFormatter dateFormatter) {
        this.apostoliDAO = apostoliDAO;
        this.apostoli = apostoli;
        this.dateFormatter = dateFormatter;
    }

    public boolean saveAllInfo(JTextField txtKodikosPelati, JComboBox<String> cmbCourier,
                               JTextField txtArithmosApostolis, JTextField txtArithmosParaggelias,
                               JTextField txtImerominia, JTextField txtAntikatavolos,
                               JTextField txtParaliptis, JComboBox<String> cmbXora, JTextField txtPoli,
                               JTextArea txtDiefthinsi, JTextField txtTilefonoStathero, JTextField txtTilefonoKinito,
                               JComboBox<String> cmbStatus, JTextArea txtIstoriko, JTextArea txtSxolia,
                               JComponent parentComponent) {

        try {
            // Update Basic Info
            apostoli.setKodikosPelati(txtKodikosPelati.getText().trim());
            apostoli.setCourier((String) cmbCourier.getSelectedItem());
            apostoli.setArithmosApostolis(txtArithmosApostolis.getText().trim());
            String value = txtArithmosParaggelias.getText().trim();
            apostoli.setArithmosParaggelias(value.isEmpty() ? null : value);

            String dateText = txtImerominia.getText().trim();
            if (!dateText.isEmpty()) {
                apostoli.setImerominiaParalabis(LocalDate.parse(dateText, dateFormatter));
            }

            String antikataboliText = txtAntikatavolos.getText().trim();
            if (!antikataboliText.isEmpty()) {
                apostoli.setAntikatavoli(new BigDecimal(antikataboliText));
            }

            // Update Recipient Info
            apostoli.setParaliptis(txtParaliptis.getText().trim());
            apostoli.setXora((String) cmbXora.getSelectedItem());
            apostoli.setPoli(txtPoli.getText().trim());
            apostoli.setDiefthinsi(txtDiefthinsi.getText().trim());
            apostoli.setTilefonoStathero(txtTilefonoStathero.getText().trim());
            apostoli.setTilefonoKinito(txtTilefonoKinito.getText().trim());

            // Update Status Info
            apostoli.setStatusApostolis((String) cmbStatus.getSelectedItem());
            apostoli.setIstoriko(txtIstoriko.getText().trim());
            apostoli.setSxolia(txtSxolia.getText().trim());

            // Save to database
            if (apostoliDAO.update(apostoli)) {
                JOptionPane.showMessageDialog(parentComponent,
                        "Όλα τα στοιχεία αποθηκεύτηκαν επιτυχώς!",
                        "Επιτυχής Αποθήκευση",
                        JOptionPane.INFORMATION_MESSAGE);
                return true;
            } else {
                JOptionPane.showMessageDialog(parentComponent,
                        "Σφάλμα κατά την αποθήκευση!",
                        "Σφάλμα",
                        JOptionPane.ERROR_MESSAGE);
                return false;
            }

        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(parentComponent,
                    "Μη έγκυρη ημερομηνία! Χρησιμοποιήστε τη μορφή dd/MM/yyyy",
                    "Σφάλμα Ημερομηνίας",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(parentComponent,
                    "Μη έγκυρο ποσό αντικαταβολής!",
                    "Σφάλμα Αριθμού",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(parentComponent,
                    "Σφάλμα: " + e.getMessage(),
                    "Σφάλμα",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    public void setFieldsEditable(boolean editable,
                                  JTextField txtKodikosPelati, JComboBox<String> cmbCourier,
                                  JTextField txtArithmosApostolis, JTextField txtArithmosParaggelias,
                                  JTextField txtImerominia, JTextField txtAntikatavolos,
                                  JTextField txtParaliptis, JComboBox<String> cmbXora, JTextField txtPoli,
                                  JTextArea txtDiefthinsi, JTextField txtTilefonoStathero, JTextField txtTilefonoKinito,
                                  JComboBox<String> cmbStatus, JTextArea txtIstoriko, JTextArea txtSxolia,
                                  java.awt.Color backgroundColor) {

        // Basic Info
        txtKodikosPelati.setEditable(editable);
        cmbCourier.setEnabled(editable);
        txtArithmosApostolis.setEditable(editable);
        txtArithmosParaggelias.setEditable(editable);
        txtImerominia.setEditable(editable);
        txtAntikatavolos.setEditable(editable);

        // Recipient
        txtParaliptis.setEditable(editable);
        cmbXora.setEnabled(editable);
        txtPoli.setEditable(editable);
        txtDiefthinsi.setEditable(editable);
        txtTilefonoStathero.setEditable(editable);
        txtTilefonoKinito.setEditable(editable);

        // Status
        cmbStatus.setEnabled(editable);
        txtIstoriko.setEditable(editable);
        txtSxolia.setEditable(editable);

        java.awt.Color bgColor = editable ? java.awt.Color.WHITE : backgroundColor;

        txtKodikosPelati.setBackground(bgColor);
        txtArithmosApostolis.setBackground(bgColor);
        txtArithmosParaggelias.setBackground(bgColor);
        txtImerominia.setBackground(bgColor);
        txtAntikatavolos.setBackground(bgColor);
        txtParaliptis.setBackground(bgColor);
        txtPoli.setBackground(bgColor);
        txtDiefthinsi.setBackground(bgColor);
        txtTilefonoStathero.setBackground(bgColor);
        txtTilefonoKinito.setBackground(bgColor);
        txtIstoriko.setBackground(bgColor);
        txtSxolia.setBackground(bgColor);
    }

    public <T extends JTextComponent> T addCopyMenu(T component) {
        JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem copyItem = new JMenuItem("Αντιγραφή");
        copyItem.addActionListener(e -> {
            String text = component.getText();
            if (text != null && !text.trim().isEmpty()) {
                java.awt.Toolkit.getDefaultToolkit()
                        .getSystemClipboard()
                        .setContents(new java.awt.datatransfer.StringSelection(text), null);
            }
        });
        popupMenu.add(copyItem);
        component.setComponentPopupMenu(popupMenu);
        return component;
    }
}