package org.pms.shipments.ui.panels;

import org.pms.customers.dao.PelatisDAO;
import org.pms.customers.model.Pelatis;
import org.pms.shipments.model.Apostoli;

import org.pms.shipments.ui.components.managers.ApostoliDetailsDataManager;
import org.pms.shipments.ui.dialogs.ApostoliDetailsDialog;

import javax.swing.*;
import java.awt.*;

public class ApostoliDetailsInfoPanel extends JPanel {

    private final ApostoliDetailsDialog parentDialog;
    private final Apostoli apostoli;
    private final PelatisDAO pelatisDAO;
    private final ApostoliDetailsDataManager dataManager;

    // Components
    private final JTextField txtKodikosPelati;
    private final JComboBox<String> cmbCourier;
    private final JTextField txtArithmosApostolis;
    private final JTextField txtArithmosParaggelias;
    private final JTextField txtImerominia;
    private final JTextField txtImerominiaEkdosis;
    private final JTextField txtAntikatavolos;
    private final JTextField txtParaliptis;
    private final JComboBox<String> cmbXora;
    private final JTextField txtPoli;
    private final JTextArea txtDiefthinsi;
    private final JTextField txtTilefonoStathero;
    private final JTextField txtTilefonoKinito;
    private final JComboBox<String> cmbStatus;
    private final JTextArea txtIstoriko;
    private final JTextArea txtSxolia;

    private boolean editMode = false;

    public ApostoliDetailsInfoPanel(ApostoliDetailsDialog parentDialog, Apostoli apostoli,
                                    PelatisDAO pelatisDAO, ApostoliDetailsDataManager dataManager,
                                    JTextField txtKodikosPelati, JComboBox<String> cmbCourier,
                                    JTextField txtArithmosApostolis, JTextField txtArithmosParaggelias,
                                    JTextField txtImerominia,JTextField txtImerominiaEkdosis, JTextField txtAntikatavolos,
                                    JTextField txtParaliptis, JComboBox<String> cmbXora, JTextField txtPoli,
                                    JTextArea txtDiefthinsi, JTextField txtTilefonoStathero, JTextField txtTilefonoKinito,
                                    JComboBox<String> cmbStatus, JTextArea txtIstoriko, JTextArea txtSxolia) {

        this.parentDialog = parentDialog;
        this.apostoli = apostoli;
        this.pelatisDAO = pelatisDAO;
        this.dataManager = dataManager;
        this.txtKodikosPelati = txtKodikosPelati;
        this.cmbCourier = cmbCourier;
        this.txtArithmosApostolis = txtArithmosApostolis;
        this.txtArithmosParaggelias = txtArithmosParaggelias;
        this.txtImerominia = txtImerominia;
        this.txtImerominiaEkdosis = txtImerominiaEkdosis;
        this.txtAntikatavolos = txtAntikatavolos;
        this.txtParaliptis = txtParaliptis;
        this.cmbXora = cmbXora;
        this.txtPoli = txtPoli;
        this.txtDiefthinsi = txtDiefthinsi;
        this.txtTilefonoStathero = txtTilefonoStathero;
        this.txtTilefonoKinito = txtTilefonoKinito;
        this.cmbStatus = cmbStatus;
        this.txtIstoriko = txtIstoriko;
        this.txtSxolia = txtSxolia;

        initializePanel();
    }

    private void initializePanel() {
        setLayout(new BorderLayout());

        JPanel contentPanel = createContentPanel();
        JScrollPane mainScrollPane = new JScrollPane(contentPanel);
        mainScrollPane.getVerticalScrollBar().setUnitIncrement(16);
        mainScrollPane.getVerticalScrollBar().setBlockIncrement(64);
        mainScrollPane.getHorizontalScrollBar().setUnitIncrement(16);
        mainScrollPane.getHorizontalScrollBar().setBlockIncrement(64);
        mainScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        mainScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        add(mainScrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = createButtonPanel();
        add(buttonPanel, BorderLayout.SOUTH);

        // Initially set to read-only
        dataManager.setFieldsEditable(false, txtKodikosPelati, cmbCourier, txtArithmosApostolis,
                txtArithmosParaggelias, txtImerominia, txtAntikatavolos,
                txtParaliptis, cmbXora, txtPoli, txtDiefthinsi,
                txtTilefonoStathero, txtTilefonoKinito,
                cmbStatus, txtIstoriko, txtSxolia, getBackground());
    }

    private JPanel createContentPanel() {
        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.NORTHWEST;

        int row = 0;

        // === ΒΑΣΙΚΑ ΣΤΟΙΧΕΙΑ ΑΠΟΣΤΟΛΗΣ ===
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 4;
        JLabel basicInfoHeader = new JLabel("ΒΑΣΙΚΑ ΣΤΟΙΧΕΙΑ ΑΠΟΣΤΟΛΗΣ");
        basicInfoHeader.setFont(basicInfoHeader.getFont().deriveFont(Font.BOLD, 14f));
        basicInfoHeader.setForeground(new Color(0, 100, 200));
        contentPanel.add(basicInfoHeader, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row;
        contentPanel.add(createLabel("Ημ. Έκδοσης:"), gbc);
        gbc.gridx = 1;
        contentPanel.add(dataManager.addCopyMenu(txtImerominiaEkdosis), gbc);

        row++; gbc.gridwidth = 1;
        gbc.gridx = 0; gbc.gridy = row;
        contentPanel.add(createLabel("Κωδικός Πελάτη:"), gbc);
        gbc.gridx = 1;
        contentPanel.add(dataManager.addCopyMenu(txtKodikosPelati), gbc);

        Pelatis pelatis = pelatisDAO.findById(apostoli.getKodikosPelati());
        if (pelatis != null) {
            gbc.gridx = 2;
            contentPanel.add(createLabel("ΑΠΟΣΤΟΛΕΑΣ:"), gbc);
            gbc.gridx = 3;
            JTextField txtEponymia = new JTextField(pelatis.getEponymiaEtairias());
            txtEponymia.setEditable(false);
            contentPanel.add(dataManager.addCopyMenu(txtEponymia), gbc);
        }

        row++;
        gbc.gridx = 0; gbc.gridy = row;
        contentPanel.add(createLabel("Courier:"), gbc);
        gbc.gridx = 1;
        contentPanel.add(cmbCourier, gbc);

        gbc.gridx = 2;
        contentPanel.add(createLabel("Αρ. Αποστολής:"), gbc);
        gbc.gridx = 3;
        contentPanel.add(dataManager.addCopyMenu(txtArithmosApostolis), gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row;
        contentPanel.add(createLabel("Αρ. Παραγγελίας:"), gbc);
        gbc.gridx = 1;
        contentPanel.add(dataManager.addCopyMenu(txtArithmosParaggelias), gbc);

        gbc.gridx = 2;
        contentPanel.add(createLabel("Ημερομηνία:"), gbc);
        gbc.gridx = 3;
        contentPanel.add(dataManager.addCopyMenu(txtImerominia), gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row;
        contentPanel.add(createLabel("Αντικαταβολή:"), gbc);
        gbc.gridx = 1;
        contentPanel.add(dataManager.addCopyMenu(txtAntikatavolos), gbc);

        // === ΣΤΟΙΧΕΙΑ ΠΑΡΑΛΗΠΤΗ ===
        row++; gbc.gridwidth = 4;
        gbc.gridx = 0; gbc.gridy = row;
        gbc.insets = new Insets(25, 8, 8, 8);
        JLabel recipientHeader = new JLabel("ΣΤΟΙΧΕΙΑ ΠΑΡΑΛΗΠΤΗ");
        recipientHeader.setFont(recipientHeader.getFont().deriveFont(Font.BOLD, 14f));
        recipientHeader.setForeground(new Color(0, 100, 200));
        contentPanel.add(recipientHeader, gbc);

        row++; gbc.gridwidth = 1; gbc.insets = new Insets(8, 8, 8, 8);
        gbc.gridx = 0; gbc.gridy = row;
        contentPanel.add(createLabel("Παραλήπτης:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 3; gbc.fill = GridBagConstraints.HORIZONTAL;
        contentPanel.add(dataManager.addCopyMenu(txtParaliptis), gbc);

        row++; gbc.gridwidth = 1; gbc.fill = GridBagConstraints.NONE;
        gbc.gridx = 0; gbc.gridy = row;
        contentPanel.add(createLabel("Χώρα:"), gbc);
        gbc.gridx = 1;
        contentPanel.add(cmbXora, gbc);

        gbc.gridx = 2;
        contentPanel.add(createLabel("Πόλη:"), gbc);
        gbc.gridx = 3;
        contentPanel.add(dataManager.addCopyMenu(txtPoli), gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row;
        contentPanel.add(createLabel("Διεύθυνση:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 3; gbc.fill = GridBagConstraints.BOTH;
        JScrollPane diefthinsiScroll = new JScrollPane(dataManager.addCopyMenu(txtDiefthinsi));
        diefthinsiScroll.setPreferredSize(new Dimension(400, 80));
        contentPanel.add(diefthinsiScroll, gbc);

        row++; gbc.gridwidth = 1; gbc.fill = GridBagConstraints.NONE;
        gbc.gridx = 0; gbc.gridy = row;
        contentPanel.add(createLabel("Τηλέφωνο:"), gbc);
        gbc.gridx = 1;
        contentPanel.add(dataManager.addCopyMenu(txtTilefonoStathero), gbc);

        gbc.gridx = 2;
        contentPanel.add(createLabel("Κινητό:"), gbc);
        gbc.gridx = 3;
        contentPanel.add(dataManager.addCopyMenu(txtTilefonoKinito), gbc);

        // === ΚΑΤΑΣΤΑΣΗ & ΣΗΜΕΙΩΣΕΙΣ ===
        row++; gbc.gridwidth = 4;
        gbc.gridx = 0; gbc.gridy = row;
        gbc.insets = new Insets(25, 8, 8, 8);
        JLabel statusHeader = new JLabel("ΚΑΤΑΣΤΑΣΗ & ΣΗΜΕΙΩΣΕΙΣ");
        statusHeader.setFont(statusHeader.getFont().deriveFont(Font.BOLD, 14f));
        statusHeader.setForeground(new Color(0, 100, 200));
        contentPanel.add(statusHeader, gbc);

        row++; gbc.gridwidth = 1; gbc.insets = new Insets(8, 8, 8, 8);
        gbc.gridx = 0; gbc.gridy = row;
        contentPanel.add(createLabel("Status courier:"), gbc);
        gbc.gridx = 1;
        contentPanel.add(cmbStatus, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.anchor = GridBagConstraints.NORTHWEST;
        contentPanel.add(createLabel("Ιστορικό:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 3; gbc.fill = GridBagConstraints.BOTH; gbc.weightx = 1.0; gbc.weighty = 0.3;
        JScrollPane istorikoScroll = new JScrollPane(dataManager.addCopyMenu(txtIstoriko));
        istorikoScroll.setPreferredSize(new Dimension(400, 120));
        contentPanel.add(istorikoScroll, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.weighty = 0.3;
        contentPanel.add(createLabel("Σχόλια:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 3;
        JScrollPane sxoliaScroll = new JScrollPane(dataManager.addCopyMenu(txtSxolia));
        sxoliaScroll.setPreferredSize(new Dimension(400, 120));
        contentPanel.add(sxoliaScroll, gbc);

        return contentPanel;
    }

    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton editButton = new JButton("Επεξεργασία");
        JButton saveButton = new JButton("Αποθήκευση");
        JButton cancelButton = new JButton("Ακύρωση");

        editButton.setBackground(new Color(0, 150, 200));
        saveButton.setBackground(new Color(0, 150, 0));
        cancelButton.setBackground(new Color(150, 150, 150));

        saveButton.setVisible(false);
        cancelButton.setVisible(false);

        buttonPanel.add(editButton);
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        editButton.addActionListener(e -> {
            editMode = true;
            dataManager.setFieldsEditable(true, txtKodikosPelati, cmbCourier, txtArithmosApostolis,
                    txtArithmosParaggelias, txtImerominia, txtAntikatavolos,
                    txtParaliptis, cmbXora, txtPoli, txtDiefthinsi,
                    txtTilefonoStathero, txtTilefonoKinito,
                    cmbStatus, txtIstoriko, txtSxolia, getBackground());
            editButton.setVisible(false);
            saveButton.setVisible(true);
            cancelButton.setVisible(true);
        });

        saveButton.addActionListener(e -> {
            if (dataManager.saveAllInfo(txtKodikosPelati, cmbCourier, txtArithmosApostolis,
                    txtArithmosParaggelias, txtImerominia, txtAntikatavolos,
                    txtParaliptis, cmbXora, txtPoli, txtDiefthinsi,
                    txtTilefonoStathero, txtTilefonoKinito,
                    cmbStatus, txtIstoriko, txtSxolia, this)) {
                editMode = false;
                dataManager.setFieldsEditable(false, txtKodikosPelati, cmbCourier, txtArithmosApostolis,
                        txtArithmosParaggelias, txtImerominia, txtAntikatavolos,
                        txtParaliptis, cmbXora, txtPoli, txtDiefthinsi,
                        txtTilefonoStathero, txtTilefonoKinito,
                        cmbStatus, txtIstoriko, txtSxolia, getBackground());
                editButton.setVisible(true);
                saveButton.setVisible(false);
                cancelButton.setVisible(false);
            }
        });

        cancelButton.addActionListener(e -> {
            editMode = false;
            parentDialog.loadDataIntoComponents();
            dataManager.setFieldsEditable(false, txtKodikosPelati, cmbCourier, txtArithmosApostolis,
                    txtArithmosParaggelias, txtImerominia, txtAntikatavolos,
                    txtParaliptis, cmbXora, txtPoli, txtDiefthinsi,
                    txtTilefonoStathero, txtTilefonoKinito,
                    cmbStatus, txtIstoriko, txtSxolia, getBackground());
            editButton.setVisible(true);
            saveButton.setVisible(false);
            cancelButton.setVisible(false);
        });

        return buttonPanel;
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(label.getFont().deriveFont(Font.BOLD));
        return label;
    }
}