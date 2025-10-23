package org.pms.customers.ui.dialogs;

import org.pms.customers.dao.PelatisDAO;
import org.pms.customers.model.Pelatis;
import javax.swing.*;
import java.awt.*;
import org.pms.customers.dao.EtairiaDAO;
import org.pms.customers.dao.YpeuthynosDAO;
import org.pms.customers.model.Etairia;
import org.pms.customers.model.Ypeuthynos;

import java.util.List;

/**
 * Ενημερωμένο dialog για προβολή λεπτομερειών πελάτη
 */
public class PelatisDetailsDialog extends JDialog {

    private final PelatisDAO pelatisDAO;
    private final String kodikosPelati;
    private Pelatis pelatis;

    // Components
    private JPanel mainPanel;
    private JTabbedPane detailsTabbedPane;

    public PelatisDetailsDialog(Frame parent, String kodikosPelati) {
        super(parent, "Λεπτομέρειες Πελάτη", true);
        this.pelatisDAO = new PelatisDAO();
        this.kodikosPelati = kodikosPelati;

        loadData();
        initComponents();
        setupLayout();
        setupEvents();

        setSize(900, 700);
        setLocationRelativeTo(null); // Κεντράρισμα στην οθόνη
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

    private void loadData() {
        pelatis = pelatisDAO.findById(kodikosPelati);
    }

    private void initComponents() {
        mainPanel = new JPanel(new BorderLayout());
        detailsTabbedPane = new JTabbedPane();

        if (pelatis != null) {
            // Basic info tab
            JPanel basicInfoPanel = createBasicInfoPanel();
            detailsTabbedPane.addTab("Βασικά Στοιχεία", basicInfoPanel);

            // Address & Contact tab
            JPanel addressContactPanel = createAddressContactPanel();
            detailsTabbedPane.addTab("Διεύθυνση & Επικοινωνία", addressContactPanel);

            // Contact person tab
            JPanel contactPersonPanel = createContactPersonPanel();
            detailsTabbedPane.addTab("Υπεύθυνοι", contactPersonPanel);

        }
    }

    private JPanel createBasicInfoPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;

        // Title
        JLabel titleLabel = new JLabel(pelatis.getEponymiaEtairias());
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 18f));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 4;
        panel.add(titleLabel, gbc);

        gbc.gridwidth = 1; // Reset gridwidth

        int row = 1;

        // Row 1: Basic identification
        gbc.gridy = row;
        gbc.gridx = 0; panel.add(createLabel("Κωδικός Πελάτη:"), gbc);
        gbc.gridx = 1; panel.add(createValueLabel(pelatis.getKodikosPelati()), gbc);
        gbc.gridx = 2; panel.add(createLabel("Κατηγορία:"), gbc);
        gbc.gridx = 3; panel.add(createValueLabel(pelatis.getKategoria()), gbc);

        row++;

        // Row 2: Company info
        gbc.gridy = row;
        gbc.gridx = 0; panel.add(createLabel("ΑΦΜ Εταιρίας:"), gbc);
        gbc.gridx = 1; panel.add(createValueLabel(pelatis.getAfmEtairias()), gbc);
        gbc.gridx = 2; panel.add(createLabel("ΔΟΥ:"), gbc);
        gbc.gridx = 3; panel.add(createValueLabel(pelatis.getDouEtairias()), gbc);

        row++;

        // Row 3: Legal representative
        gbc.gridy = row;
        gbc.gridx = 0; panel.add(createLabel("Νόμιμος Εκπρόσωπος:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 3; gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(createValueLabel(pelatis.getNomimosEkprospos()), gbc);

        row++;
        gbc.gridwidth = 1; gbc.fill = GridBagConstraints.NONE;

        // Row 4: Representative details
        gbc.gridy = row;
        gbc.gridx = 0; panel.add(createLabel("ΑΦΜ Εκπροσώπου:"), gbc);
        gbc.gridx = 1; panel.add(createValueLabel(pelatis.getAfmNomimuEkprosopu()), gbc);
        gbc.gridx = 2; panel.add(createLabel("Νομική Μορφή:"), gbc);
        gbc.gridx = 3; panel.add(createValueLabel(pelatis.getNomikiMorfi()), gbc);

        row++;

        // Row 5: Distinctive title
        gbc.gridy = row;
        gbc.gridx = 0; panel.add(createLabel("Διακριτικός Τίτλος:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 3; gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(createValueLabel(pelatis.getDiakritikosTitlos()), gbc);

        row++;
        gbc.gridwidth = 1; gbc.fill = GridBagConstraints.NONE;

        // Row 6: Business description (multiline)
        gbc.gridy = row;
        gbc.gridx = 0; gbc.anchor = GridBagConstraints.NORTHWEST;
        panel.add(createLabel("Επάγγελμα/Αντικείμενο:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 3; gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0; gbc.weighty = 1.0;

        JTextArea businessArea = new JTextArea(pelatis.getEpaggelmaAntikimeno());
        businessArea.setEditable(false);
        businessArea.setWrapStyleWord(true);
        businessArea.setLineWrap(true);
        businessArea.setBackground(panel.getBackground());
        businessArea.setBorder(BorderFactory.createLoweredBevelBorder());

        JScrollPane businessScrollPane = new JScrollPane(businessArea);
        businessScrollPane.setPreferredSize(new Dimension(400, 80));
        panel.add(businessScrollPane, gbc);

        return panel;
    }

    private JPanel createAddressContactPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;

        int row = 0;

        // Contact section header
        JLabel contactHeader = new JLabel("Στοιχεία Επικοινωνίας");
        contactHeader.setFont(contactHeader.getFont().deriveFont(Font.BOLD, 16f));
        contactHeader.setForeground(new Color(0, 100, 200));
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 4;
        panel.add(contactHeader, gbc);
        gbc.gridwidth = 1;

        row++;

        // Email
        gbc.gridy = row;
        gbc.gridx = 0; panel.add(createLabel("Email:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 3; gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(createValueLabel(pelatis.getEmail()), gbc);
        gbc.gridwidth = 1; gbc.fill = GridBagConstraints.NONE;

        row++;

        // Phones
        gbc.gridy = row;
        gbc.gridx = 0; panel.add(createLabel("Τηλέφωνο Σταθερό:"), gbc);
        gbc.gridx = 1; panel.add(createValueLabel(pelatis.getTilefonoStathero()), gbc);
        gbc.gridx = 2; panel.add(createLabel("Κινητό:"), gbc);
        gbc.gridx = 3; panel.add(createValueLabel(pelatis.getTilefonoKinito()), gbc);

        row += 2;

        // Address section header
        JLabel addressHeader = new JLabel("Διεύθυνση Εταιρίας");
        addressHeader.setFont(addressHeader.getFont().deriveFont(Font.BOLD, 16f));
        addressHeader.setForeground(new Color(0, 100, 200));
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 4;
        panel.add(addressHeader, gbc);
        gbc.gridwidth = 1;

        row++;

        // Φορτώνουμε τη διεύθυνση από την εταιρία
        EtairiaDAO etairiaDAO = new EtairiaDAO();
        List<Etairia> etairias = etairiaDAO.findByPelatis(pelatis.getKodikosPelati());

        if (!etairias.isEmpty()) {
            Etairia etairia = etairias.get(0);

            // City and Area
            gbc.gridy = row;
            gbc.gridx = 0; panel.add(createLabel("Πόλη:"), gbc);
            gbc.gridx = 1; panel.add(createValueLabel(etairia.getPoli()), gbc);
            gbc.gridx = 2; panel.add(createLabel("Περιοχή:"), gbc);
            gbc.gridx = 3; panel.add(createValueLabel(etairia.getPerioxi()), gbc);

            row++;

            // Street and Number
            gbc.gridy = row;
            gbc.gridx = 0; panel.add(createLabel("Οδός:"), gbc);
            gbc.gridx = 1; panel.add(createValueLabel(etairia.getOdos()), gbc);
            gbc.gridx = 2; panel.add(createLabel("Αριθμός:"), gbc);
            gbc.gridx = 3; panel.add(createValueLabel(etairia.getArithmos()), gbc);

            row++;

            // Postal code
            gbc.gridy = row;
            gbc.gridx = 0; panel.add(createLabel("ΤΚ:"), gbc);
            gbc.gridx = 1; panel.add(createValueLabel(etairia.getTk()), gbc);

            row++;

            // Full address
            gbc.gridy = row;
            gbc.gridx = 0; panel.add(createLabel("Πλήρης Διεύθυνση:"), gbc);
            gbc.gridx = 1; gbc.gridwidth = 3; gbc.fill = GridBagConstraints.HORIZONTAL;
            JLabel fullAddressLabel = createValueLabel(etairia.getFullAddress());
            fullAddressLabel.setFont(fullAddressLabel.getFont().deriveFont(Font.ITALIC));
            fullAddressLabel.setForeground(new Color(100, 100, 100));
            panel.add(fullAddressLabel, gbc);
        } else {
            // Δεν υπάρχει εταιρία
            gbc.gridy = row;
            gbc.gridx = 0; gbc.gridwidth = 4;
            JLabel noAddressLabel = createValueLabel("Δεν έχουν καταχωρηθεί στοιχεία διεύθυνσης.");
            panel.add(noAddressLabel, gbc);
        }

        return panel;
    }

    private JPanel createContactPersonPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Header
        JLabel header = new JLabel("Στοιχεία Υπευθύνων");
        header.setFont(header.getFont().deriveFont(Font.BOLD, 16f));
        header.setForeground(new Color(0, 100, 200));
        header.setHorizontalAlignment(SwingConstants.CENTER);
        mainPanel.add(header, BorderLayout.NORTH);

        // Panel που περιέχει όλους τους υπευθύνους
        JPanel allYpeuthynoiPanel = new JPanel();
        allYpeuthynoiPanel.setLayout(new BoxLayout(allYpeuthynoiPanel, BoxLayout.Y_AXIS));

        String[] allCategories = {"ΣΥΜΒΑΣΗΣ", "ΕΠΙΚΟΙΝΩΝΙΑΣ", "ΠΛΗΡΩΜΩΝ", "ΛΟΓΙΣΤΗΡΙΟΥ", "ΑΝΤΙΚΑΤΑΒΟΛΩΝ"};

        for (String category : allCategories) {
            JPanel categoryPanel = createYpeuthynosDetailsPanel(category);
            allYpeuthynoiPanel.add(categoryPanel);
            allYpeuthynoiPanel.add(Box.createVerticalStrut(15));
        }

        JScrollPane scrollPane = new JScrollPane(allYpeuthynoiPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        return mainPanel;
    }

    private JPanel createYpeuthynosDetailsPanel(String category) {
        JPanel panel = new JPanel(new GridBagLayout());

        // Titled border για κάθε κατηγορία
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createEtchedBorder(),
                        "Υπεύθυνος " + category,
                        javax.swing.border.TitledBorder.LEFT,
                        javax.swing.border.TitledBorder.TOP,
                        new Font(Font.SANS_SERIF, Font.BOLD, 12),
                        new Color(0, 100, 200)
                ),
                BorderFactory.createEmptyBorder(10, 15, 15, 15)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // Βρίσκουμε τον υπεύθυνο αυτής της κατηγορίας
        EtairiaDAO etairiaDAO = new EtairiaDAO();
        List<Etairia> etairias = etairiaDAO.findByPelatis(pelatis.getKodikosPelati());

        Ypeuthynos ypeuthynos = null;
        if (!etairias.isEmpty()) {
            YpeuthynosDAO ypeuthynosDAO = new YpeuthynosDAO();
            Ypeuthynos.EidosYpeuthynou eidos = Ypeuthynos.EidosYpeuthynou.fromString(category);
            if (eidos != null) {
                ypeuthynos = ypeuthynosDAO.findByEtairiaAndEidos(etairias.get(0).getIdEtairias(), eidos);
            }
        }

        if (ypeuthynos != null && ypeuthynos.getOnomaYpeuthynou() != null && !ypeuthynos.getOnomaYpeuthynou().trim().isEmpty()) {
            int row = 0;

            // Όνομα
            gbc.gridx = 0; gbc.gridy = row;
            panel.add(createLabel("Όνομα:"), gbc);
            gbc.gridx = 1; gbc.gridwidth = 3; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
            panel.add(createValueLabel(ypeuthynos.getOnomaYpeuthynou()), gbc);
            gbc.gridwidth = 1; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0.0;

            row++;

            // Τίτλος
            gbc.gridx = 0; gbc.gridy = row;
            panel.add(createLabel("Τίτλος:"), gbc);
            gbc.gridx = 1; gbc.gridwidth = 3; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
            panel.add(createValueLabel(ypeuthynos.getTitlos()), gbc);
            gbc.gridwidth = 1; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0.0;

            row++;

            // Τηλέφωνα
            gbc.gridx = 0; gbc.gridy = row;
            panel.add(createLabel("Τηλέφωνο:"), gbc);
            gbc.gridx = 1;
            panel.add(createValueLabel(ypeuthynos.getTilefono()), gbc);
            gbc.gridx = 2;
            panel.add(createLabel("Κινητό:"), gbc);
            gbc.gridx = 3;
            panel.add(createValueLabel(ypeuthynos.getKinito()), gbc);

            row++;

            // Email
            gbc.gridx = 0; gbc.gridy = row;
            panel.add(createLabel("Email:"), gbc);
            gbc.gridx = 1; gbc.gridwidth = 3; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
            panel.add(createValueLabel(ypeuthynos.getEmail()), gbc);
        } else {
            // Δεν υπάρχουν στοιχεία για αυτήν την κατηγορία
            JLabel noDataLabel = new JLabel("Δεν έχουν καταχωρηθεί στοιχεία.");
            noDataLabel.setFont(noDataLabel.getFont().deriveFont(Font.ITALIC));
            noDataLabel.setForeground(Color.GRAY);
            panel.add(noDataLabel);
        }

        return panel;
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(label.getFont().deriveFont(Font.BOLD));
        return label;
    }

    private JLabel createValueLabel(String text) {
        JLabel label = new JLabel(text != null && !text.trim().isEmpty() ? text : "-");
        if (text == null || text.trim().isEmpty()) {
            label.setForeground(Color.GRAY);
            label.setFont(label.getFont().deriveFont(Font.ITALIC));
        }
        return label;
    }

    private void setupLayout() {
        if (pelatis == null) {
            // Show error message if customer not found
            mainPanel.add(new JLabel("Δεν βρέθηκε πελάτης με κωδικό: " + kodikosPelati, SwingConstants.CENTER), BorderLayout.CENTER);
        } else {
            mainPanel.add(detailsTabbedPane, BorderLayout.CENTER);
        }

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton btnEdit = new JButton("Επεξεργασία");
        JButton btnClose = new JButton("Κλείσιμο");

        btnEdit.addActionListener(e -> {
            // Open edit form
            PelatisForm form = new PelatisForm(pelatis);
            form.setVisible(true);
            form.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosed(java.awt.event.WindowEvent windowEvent) {
                    // Refresh data after edit
                    loadData();
                    refreshDisplay();
                }
            });
        });

        btnClose.addActionListener(e -> dispose());

        if (pelatis != null) {
            buttonPanel.add(btnEdit);
        }
        buttonPanel.add(btnClose);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private void refreshDisplay() {
        // Recreate components with updated data
        detailsTabbedPane.removeAll();

        if (pelatis != null) {
            detailsTabbedPane.addTab("Βασικά Στοιχεία", createBasicInfoPanel());
            detailsTabbedPane.addTab("Διεύθυνση & Επικοινωνία", createAddressContactPanel());
            detailsTabbedPane.addTab("Υπεύθυνοι", createContactPersonPanel());
        }

        revalidate();
        repaint();
    }

    private void setupEvents() {
        // Additional event handling if needed
    }
}