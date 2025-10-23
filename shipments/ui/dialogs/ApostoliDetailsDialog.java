package org.pms.shipments.ui.dialogs;

import org.pms.shipments.dao.ApostoliDAO;
import org.pms.shipments.dao.CustomerCareDAO;
import org.pms.customers.dao.PelatisDAO;
import org.pms.shipments.model.Apostoli;
import org.pms.shipments.model.CustomerCareComment;
import org.pms.shipments.ui.panels.ApostoliDetailsInfoPanel;
import org.pms.shipments.ui.panels.ApostoliDetailsStatusCourierPanel;
import org.pms.shipments.ui.panels.ApostoliDetailsTrackingPanel;
import org.pms.shipments.ui.components.managers.ApostoliDetailsDataManager;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ApostoliDetailsDialog extends JDialog {

    private final ApostoliDAO apostoliDAO;
    private final PelatisDAO pelatisDAO;
    private final CustomerCareDAO customerCareDAO;
    private final String arithmosApostolis;
    private Apostoli apostoli;
    private List<CustomerCareComment> customerCareComments = new ArrayList<>();

    public  JTabbedPane tabbedPane;
    private ApostoliDetailsDataManager dataManager;

    // Basic Info Panel components
    private JTextField txtKodikosPelati;
    private JComboBox<String> cmbCourier;
    private JTextField txtArithmosApostolis;
    private JTextField txtArithmosParaggelias;
    private JTextField txtImerominia;
    private JTextField txtImerominiaEkdosis;
    private JTextField txtAntikatavolos;

    // Recipient Panel components
    private JTextField txtParaliptis;
    private JComboBox<String> cmbXora;
    private JTextField txtPoli;
    private JTextArea txtDiefthinsi;
    private JTextField txtTilefonoStathero;
    private JTextField txtTilefonoKinito;

    // Status Panel components
    private JComboBox<String> cmbStatus;
    private JTextArea txtIstoriko;
    private JTextArea txtSxolia;

    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public ApostoliDetailsDialog(Frame parent, String arithmosApostolis) {
        super(parent, "Λεπτομέρειες Αποστολής", true);
        this.apostoliDAO = new ApostoliDAO();
        this.pelatisDAO = new PelatisDAO();
        this.customerCareDAO = new CustomerCareDAO();
        this.arithmosApostolis = arithmosApostolis;

        loadData();
        initComponents();
        setupLayout();

        setSize(1000, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        Font defaultFont = UIManager.getFont("Label.font");
        if (defaultFont != null) {
            Font newFont = defaultFont.deriveFont(Font.PLAIN);
            UIManager.put("Label.font", newFont);
            UIManager.put("Button.font", newFont);
            UIManager.put("TextField.font", newFont);
            UIManager.put("TextArea.font", newFont);
        }
    }

    private void loadData() {
        apostoli = apostoliDAO.findByArithmosApostolis(arithmosApostolis);
    }

    private void initComponents() {
        tabbedPane = new JTabbedPane();
        dataManager = new ApostoliDetailsDataManager(apostoliDAO, apostoli, dateFormatter);

        // Initialize Basic Info components
        txtKodikosPelati = new JTextField(15);
        cmbCourier = new JComboBox<>(new String[]{"ACS", "ELTA", "SPEEDEX", "GENIKI"});
        txtArithmosApostolis = new JTextField(15);
        txtArithmosParaggelias = new JTextField(15);
        txtImerominia = new JTextField(15);
        txtImerominiaEkdosis = new JTextField(15);
        txtAntikatavolos = new JTextField(15);

        // Initialize Recipient components
        txtParaliptis = new JTextField(25);
        cmbXora = new JComboBox<>(new String[]{"ΕΛΛΑΔΑ", "ΚΥΠΡΟΣ", "ΓΕΡΜΑΝΙΑ", "ΙΤΑΛΙΑ", "ΒΟΥΛΓΑΡΙΑ"});
        txtPoli = new JTextField(20);
        txtDiefthinsi = new JTextArea(3, 25);
        txtTilefonoStathero = new JTextField(15);
        txtTilefonoKinito = new JTextField(15);

        // Initialize Status components
        cmbStatus = new JComboBox<>(new String[]{
                "-", "ΣΕ ΜΕΤΑΦΟΡΑ", "ΠΑΡΑΔΟΘΗΚΕ", "ΑΝΑΜΟΝΗ ΠΑΡΑΔΟΣΗΣ", "ΑΚΥΡΩΘΗΚΕ",
                "ΠΡΟΣ ΕΠΙΣΤΡΟΦΗ", "ΕΠΕΣΤΡΑΦΗ ΣΤΟΝ ΑΠΟΣΤΟΛΕΑ", "ΕΝΤΟΛΗ ΠΑΡΑΛΑΒΗΣ ΑΠΟ ΓΡΑΦΕΙΟ",
                "ΒΡΙΣΚΕΤΑΙ ΣΤΗΝ ΔΙΑΔΡΟΜΗ ΠΡΟΣ ΤΟ ΚΑΤΑΣΤΗΜΑ ΠΑΡΑΔΟΣΗΣ", "ΠΑΡΑΔΟΣΗ RECEPTION ΕΝΤ.ΑΠΟΣΤ",
                "ΑΡΝΗΣΗ ΧΡΕΩΣΗΣ", "ΑΔΥΝΑΜΙΑ ΠΛΗΡΩΜΗΣ", "ΜΗ ΑΠΟΔΟΧΗ ΑΠΟΣΤΟΛΗΣ", "ΑΠΕΒΙΩΣΕ",
                "ΑΠΩΝ", "ΔΥΣΠΡΟΣΙΤΗ ΠΕΡΙΟΧΗ", "ΕΛΛΙΠΗ ΔΙΚΑΙΟΛΟΓΗΤΙΚΑ", "ΑΓΝΩΣΤΟΣ ΠΑΡΑΛΗΠΤΗΣ",
                "ΑΛΛΑΓΗ ΔΙΕΥΘΥΝΣΗΣ", "ΛΑΝΘΑΣΜΕΝΗ, ΕΛΛΙΠΗΣ ΔΙΕΥΘΥΝΣΗ", "ΝΕΑ ΗΜ/ΝΙΑ ΠΑΡΑΔ ΕΝΤΟΛΗ ΑΠΟΣΤ",
                "ΝΕΑ ΗΜ/ΝΙΑ ΠΑΡΑΔ ΕΝΤΟΛ ΠΑΡ/ΠΤΗ", "ΣΥΓΚΕΚΡΙΜΕΝΗ ΗΜ/ΝΙΑ ΠΑΡΑΔΟΣΗΣ ΕΝΤΟΛΗ ΠΑΡΑΛΗΠΤΗ ΑΝΑΚΑΤΕΥΘΥΝΣΗ"
        });
        txtIstoriko = new JTextArea(5, 30);
        txtSxolia = new JTextArea(5, 30);
    }

    private void setupLayout() {
        setLayout(new BorderLayout());

        if (apostoli != null) {
            ApostoliDetailsCustomerCarePanel customerCarePanel =
                    new ApostoliDetailsCustomerCarePanel(this, apostoli, customerCareDAO);
            tabbedPane.addTab("Ιστορικό Customer Care", customerCarePanel);

            ApostoliDetailsStatusCourierPanel statusCourierPanel =
                    new ApostoliDetailsStatusCourierPanel(this, apostoli, apostoliDAO);
            tabbedPane.addTab("Status Courier", statusCourierPanel);

            ApostoliDetailsInfoPanel infoPanel =
                    new ApostoliDetailsInfoPanel(this, apostoli, pelatisDAO, dataManager,
                            txtKodikosPelati, cmbCourier, txtArithmosApostolis,
                            txtArithmosParaggelias, txtImerominia, txtImerominiaEkdosis, txtAntikatavolos,
                            txtParaliptis, cmbXora, txtPoli, txtDiefthinsi,
                            txtTilefonoStathero, txtTilefonoKinito,
                            cmbStatus, txtIstoriko, txtSxolia);
            tabbedPane.addTab("Στοιχεία Αποστολής", infoPanel);

            // Προσθήκη καρτέλας Λεπτομέρειες Αποστολής (μόνο για ACS)
            if ("ACS".equals(apostoli.getCourier())) {
                ApostoliDetailsTrackingPanel trackingPanel =
                        new ApostoliDetailsTrackingPanel(apostoli);
                tabbedPane.addTab("Λεπτομέρειες Αποστολής", trackingPanel);
            }

            tabbedPane.setSelectedIndex(0);
            loadDataIntoComponents();
        } else {
            JLabel errorLabel = new JLabel("Δεν βρέθηκε αποστολή με αριθμό: " + arithmosApostolis);
            errorLabel.setHorizontalAlignment(SwingConstants.CENTER);
            add(errorLabel, BorderLayout.CENTER);
            return;
        }

        add(tabbedPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton closeButton = new JButton("Κλείσιμο");
        closeButton.addActionListener(e -> dispose());
        buttonPanel.add(closeButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    public void selectTrackingDetailsTab() {
        if (tabbedPane != null) {
            // Ψάχνουμε για την καρτέλα "Λεπτομέρειες Αποστολής"
            for (int i = 0; i < tabbedPane.getTabCount(); i++) {
                if ("Λεπτομέρειες Αποστολής".equals(tabbedPane.getTitleAt(i))) {
                    tabbedPane.setSelectedIndex(i);
                    break;
                }
            }
        }
    }

    public void loadDataIntoComponents() {
        // Load Basic Info
        txtKodikosPelati.setText(apostoli.getKodikosPelati() != null ? apostoli.getKodikosPelati() : "ΤΕΣΤ");
        txtArithmosApostolis.setText(apostoli.getArithmosApostolis() != null ? apostoli.getArithmosApostolis() : "");
        txtArithmosParaggelias.setText(apostoli.getArithmosParaggelias() != null ? apostoli.getArithmosParaggelias() : "");

        if (apostoli.getImerominiaParalabis() != null) {
            txtImerominia.setText(apostoli.getImerominiaParalabis().format(dateFormatter));
        }

        if (apostoli.getImerominiaEkdosis() != null) {
            // Έλεγχος αν είναι η ειδική ημερομηνία που σημαίνει "μη διαθέσιμη"
            if (apostoli.getImerominiaEkdosis().equals(LocalDate.of(1900, 1, 1))) {
                txtImerominiaEkdosis.setText("ΜΗ ΔΙΑΘΕΣΙΜΗ ΗΜ");
            } else {
                txtImerominiaEkdosis.setText(apostoli.getImerominiaEkdosis().format(dateFormatter));
            }
        } else {
            txtImerominiaEkdosis.setText("");
        }

        if (apostoli.getAntikatavoli() != null) {
            txtAntikatavolos.setText(apostoli.getAntikatavoli().toString());
        }

        // Load Recipient Info
        txtParaliptis.setText(apostoli.getParaliptis() != null ? apostoli.getParaliptis() : "");
        txtPoli.setText(apostoli.getPoli() != null ? apostoli.getPoli() : "");
        txtDiefthinsi.setText(apostoli.getDiefthinsi() != null ? apostoli.getDiefthinsi() : "");
        txtTilefonoStathero.setText(apostoli.getTilefonoStathero() != null ? apostoli.getTilefonoStathero() : "");
        txtTilefonoKinito.setText(apostoli.getTilefonoKinito() != null ? apostoli.getTilefonoKinito() : "");

        // Load Status Info
        cmbStatus.setSelectedItem(apostoli.getStatusApostolis());
        txtIstoriko.setText(apostoli.getIstoriko() != null ? apostoli.getIstoriko() : "");
        txtSxolia.setText(apostoli.getSxolia() != null ? apostoli.getSxolia() : "");
    }

    // Getters for data access
    public Apostoli getApostoli() { return apostoli; }
    public DateTimeFormatter getDateFormatter() { return dateFormatter; }
    public JTabbedPane getTabbedPane() {
        return tabbedPane;
    }
}