package org.pms.shipments.ui.panels;

import org.pms.constants.UIConstants;
import org.pms.shipments.dao.ACSSummaryDAO;
import org.pms.shipments.dao.ApostoliDAO;
import org.pms.shipments.model.ACSSummary;
import org.pms.shipments.model.Apostoli;
import org.pms.shipments.service.ACSTrackingService;
import org.pms.shipments.ui.dialogs.ApostoliDetailsDialog;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;

public class ApostoliDetailsStatusCourierPanel extends JPanel {

    private final ApostoliDetailsDialog parentDialog;
    private final Apostoli apostoli;
    private final ApostoliDAO apostoliDAO;
    private final ACSSummaryDAO acsSummaryDAO;
    private final ACSTrackingService trackingService;

    // Status Panel Components
    private JLabel statusValueLabel;
    private JButton updateButton;

    // Details Panel Components
    private JLabel voucherNoLabel;
    private JLabel senderLabel;
    private JLabel recipientLabel;
    private JLabel recipientAddressLabel;
    private JLabel consigneeLabel;
    private JLabel stationOriginLabel;
    private JLabel stationDestinationLabel;
    private JLabel phoneOriginLabel;
    private JLabel phoneDestinationLabel;
    private JLabel pickupDateLabel;
    private JLabel deliveryDateLabel;
    private JLabel expectedDateLabel;
    private JLabel deliveryFlagLabel;
    private JLabel returnedFlagLabel;
    private JLabel shipmentStatusLabel;
    private JTextArea deliveryInfoArea;
    private JLabel nonDeliveryReasonLabel;

    private final SimpleDateFormat inputDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    private final SimpleDateFormat outputDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");

    public ApostoliDetailsStatusCourierPanel(ApostoliDetailsDialog parentDialog,
                                             Apostoli apostoli, ApostoliDAO apostoliDAO) {
        this.parentDialog = parentDialog;
        this.apostoli = apostoli;
        this.apostoliDAO = apostoliDAO;
        this.acsSummaryDAO = new ACSSummaryDAO();
        this.trackingService = new ACSTrackingService(apostoliDAO);

        initComponents();
        setupLayout();
        loadCurrentData();
    }

    private void initComponents() {
        // Status Components
        statusValueLabel = new JLabel("Δεν υπάρχει κατάσταση");
        statusValueLabel.setFont(UIConstants.LARGE_FONT.deriveFont(Font.BOLD));
        statusValueLabel.setBorder(new CompoundBorder(
                BorderFactory.createLoweredBevelBorder(),
                new EmptyBorder(UIConstants.SPACING_MD, UIConstants.SPACING_LG, UIConstants.SPACING_MD, UIConstants.SPACING_LG)
        ));
        statusValueLabel.setOpaque(true);
        statusValueLabel.setBackground(UIConstants.BG_WHITE);
        statusValueLabel.setForeground(UIConstants.TEXT_PRIMARY);

        updateButton = new JButton("Update Tracking Summary");
        updateButton.setBackground(UIConstants.BUTTON_SUCCESS_BG);
        updateButton.setForeground(UIConstants.TEXT_BLACK);
        updateButton.setFont(UIConstants.BUTTON_FONT);
        updateButton.setPreferredSize(UIConstants.BUTTON_XL);
        updateButton.addActionListener(e -> updateTrackingSummary());

        // Details Components
        initDetailLabels();

        deliveryInfoArea = new JTextArea(3, 30);
        deliveryInfoArea.setEditable(false);
        deliveryInfoArea.setWrapStyleWord(true);
        deliveryInfoArea.setLineWrap(true);
        deliveryInfoArea.setBackground(UIConstants.INPUT_BG);
        deliveryInfoArea.setForeground(UIConstants.INPUT_TEXT);
        deliveryInfoArea.setBorder(new EmptyBorder(UIConstants.SPACING_SM, UIConstants.SPACING_SM,
                UIConstants.SPACING_SM, UIConstants.SPACING_SM));
    }

    private void initDetailLabels() {
        voucherNoLabel = createDetailLabel();
        senderLabel = createDetailLabel();
        recipientLabel = createDetailLabel();
        recipientAddressLabel = createDetailLabel();
        consigneeLabel = createDetailLabel();
        stationOriginLabel = createDetailLabel();
        stationDestinationLabel = createDetailLabel();
        phoneOriginLabel = createDetailLabel();
        phoneDestinationLabel = createDetailLabel();
        pickupDateLabel = createDetailLabel();
        deliveryDateLabel = createDetailLabel();
        expectedDateLabel = createDetailLabel();
        deliveryFlagLabel = createDetailLabel();
        returnedFlagLabel = createDetailLabel();
        shipmentStatusLabel = createDetailLabel();
        nonDeliveryReasonLabel = createDetailLabel();
    }

    private JLabel createDetailLabel() {
        JLabel label = new JLabel("-");
        label.setFont(UIConstants.DEFAULT_FONT);
        label.setForeground(UIConstants.TEXT_PRIMARY);
        return label;
    }

    private void setupLayout() {
        setLayout(new BorderLayout(UIConstants.SPACING_MD, UIConstants.SPACING_MD));
        setBorder(new EmptyBorder(UIConstants.SPACING_LG, UIConstants.SPACING_LG,
                UIConstants.SPACING_LG, UIConstants.SPACING_LG));
        setBackground(UIConstants.BG_WHITE);

        // Status Panel (Top)
        JPanel statusPanel = createStatusPanel();
        add(statusPanel, BorderLayout.NORTH);

        // Details Panel (Center)
        JPanel detailsPanel = createDetailsPanel();
        add(detailsPanel, BorderLayout.CENTER);

        // Button Panel (Bottom)
        JPanel buttonPanel = createButtonPanel();
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private JPanel createStatusPanel() {
        JPanel panel = new JPanel(new BorderLayout(UIConstants.SPACING_SM, UIConstants.SPACING_SM));
        panel.setBackground(UIConstants.BG_WHITE);

        TitledBorder border = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(UIConstants.BORDER_PRIMARY, UIConstants.BORDER_THICKNESS),
                "Τρέχουσα Κατάσταση από Summary"
        );
        border.setTitleFont(UIConstants.BOLD_FONT);
        border.setTitleColor(UIConstants.TEXT_PRIMARY);
        panel.setBorder(new CompoundBorder(border, new EmptyBorder(UIConstants.SPACING_MD, UIConstants.SPACING_MD,
                UIConstants.SPACING_MD, UIConstants.SPACING_MD)));

        panel.add(statusValueLabel, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createDetailsPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(UIConstants.BG_WHITE);

        TitledBorder border = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(UIConstants.BORDER_ACCENT, UIConstants.BORDER_THICKNESS),
                "Λεπτομέρειες από ACS API"
        );
        border.setTitleFont(UIConstants.BOLD_FONT);
        border.setTitleColor(UIConstants.TEXT_PRIMARY);
        mainPanel.setBorder(border);

        // Create tabbed pane for organized display
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setBackground(UIConstants.BG_WHITE);
        tabbedPane.setFont(UIConstants.DEFAULT_FONT);

        tabbedPane.addTab("Γενικές Πληροφορίες", createGeneralInfoPanel());
        tabbedPane.addTab("Σταθμοί & Ημερομηνίες", createStationsPanel());
        tabbedPane.addTab("Κατάσταση Παράδοσης", createDeliveryStatusPanel());

        mainPanel.add(tabbedPane, BorderLayout.CENTER);

        return mainPanel;
    }

    private JPanel createGeneralInfoPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(UIConstants.BG_WHITE);
        panel.setBorder(new EmptyBorder(UIConstants.SPACING_MD, UIConstants.SPACING_MD,
                UIConstants.SPACING_MD, UIConstants.SPACING_MD));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(UIConstants.SPACING_SM, UIConstants.SPACING_SM,
                UIConstants.SPACING_SM, UIConstants.SPACING_SM);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;
        addDetailRow(panel, gbc, row++, "Αρ. Αποστολής:", voucherNoLabel);
        addDetailRow(panel, gbc, row++, "Αποστολέας:", senderLabel);
        addDetailRow(panel, gbc, row++, "Παραλήπτης:", recipientLabel);
        addDetailRow(panel, gbc, row++, "Διεύθυνση Παραλήπτη:", recipientAddressLabel);
        addDetailRow(panel, gbc, row++, "Consignee:", consigneeLabel);

        return panel;
    }

    private JPanel createStationsPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(UIConstants.BG_WHITE);
        panel.setBorder(new EmptyBorder(UIConstants.SPACING_MD, UIConstants.SPACING_MD,
                UIConstants.SPACING_MD, UIConstants.SPACING_MD));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(UIConstants.SPACING_SM, UIConstants.SPACING_SM,
                UIConstants.SPACING_SM, UIConstants.SPACING_SM);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;
        addDetailRow(panel, gbc, row++, "Σταθμός Προέλευσης:", stationOriginLabel);
        addDetailRow(panel, gbc, row++, "Σταθμός Προορισμού:", stationDestinationLabel);
        addDetailRow(panel, gbc, row++, "Τηλ. Σταθμού Προέλευσης:", phoneOriginLabel);
        addDetailRow(panel, gbc, row++, "Τηλ. Σταθμού Προορισμού:", phoneDestinationLabel);
        addDetailRow(panel, gbc, row++, "Ημερομηνία Παραλαβής:", pickupDateLabel);
        addDetailRow(panel, gbc, row++, "Ημερομηνία Παράδοσης:", deliveryDateLabel);
        addDetailRow(panel, gbc, row++, "Αναμενόμενη Ημερομηνία:", expectedDateLabel);

        return panel;
    }

    private JPanel createDeliveryStatusPanel() {
        JPanel panel = new JPanel(new BorderLayout(UIConstants.SPACING_MD, UIConstants.SPACING_MD));
        panel.setBackground(UIConstants.BG_WHITE);
        panel.setBorder(new EmptyBorder(UIConstants.SPACING_MD, UIConstants.SPACING_MD,
                UIConstants.SPACING_MD, UIConstants.SPACING_MD));

        // Top section with status flags
        JPanel flagsPanel = new JPanel(new GridBagLayout());
        flagsPanel.setBackground(UIConstants.BG_WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(UIConstants.SPACING_SM, UIConstants.SPACING_SM,
                UIConstants.SPACING_SM, UIConstants.SPACING_SM);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;
        addDetailRow(flagsPanel, gbc, row++, "Κατάσταση Αποστολής:", shipmentStatusLabel);
        addDetailRow(flagsPanel, gbc, row++, "Flag Παράδοσης:", deliveryFlagLabel);
        addDetailRow(flagsPanel, gbc, row++, "Flag Επιστροφής:", returnedFlagLabel);
        addDetailRow(flagsPanel, gbc, row++, "Λόγος Μη Παράδοσης:", nonDeliveryReasonLabel);

        panel.add(flagsPanel, BorderLayout.NORTH);

        // Bottom section with delivery info
        JPanel infoPanel = new JPanel(new BorderLayout());
        infoPanel.setBackground(UIConstants.BG_WHITE);

        TitledBorder infoBorder = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(UIConstants.BORDER_INFO, UIConstants.BORDER_THICKNESS),
                "Πληροφορίες Παράδοσης"
        );
        infoBorder.setTitleFont(UIConstants.BOLD_FONT);
        infoBorder.setTitleColor(UIConstants.TEXT_PRIMARY);
        infoPanel.setBorder(infoBorder);

        JScrollPane scrollPane = new JScrollPane(deliveryInfoArea);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBackground(UIConstants.BG_WHITE);
        infoPanel.add(scrollPane, BorderLayout.CENTER);

        panel.add(infoPanel, BorderLayout.CENTER);

        return panel;
    }

    private void addDetailRow(JPanel panel, GridBagConstraints gbc, int row, String labelText, JComponent valueComponent) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.3;

        JLabel label = new JLabel(labelText);
        label.setFont(UIConstants.BOLD_FONT);
        label.setForeground(UIConstants.TEXT_PRIMARY);
        panel.add(label, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        panel.add(valueComponent, gbc);
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, UIConstants.SPACING_MD, UIConstants.SPACING_MD));
        panel.setBackground(UIConstants.BG_WHITE);
        panel.add(updateButton);
        return panel;
    }

    private void loadCurrentData() {
        String currentStatus = apostoli.getStatusApostolis();
        if (currentStatus != null && !currentStatus.trim().isEmpty()) {
            statusValueLabel.setText(currentStatus);
            statusValueLabel.setForeground(UIConstants.TEXT_PRIMARY);
        } else {
            statusValueLabel.setText("Δεν υπάρχει κατάσταση");
            statusValueLabel.setForeground(UIConstants.TEXT_MUTED);
        }

        // ΦΟΡΤΩΣΗ ΑΠΟ ΤΗ ΒΑΣΗ
        ACSSummary summary = acsSummaryDAO.findByArithmosApostolis(apostoli.getArithmosApostolis());
        if (summary != null) {
            loadDetailsFromSummary(summary);
        } else {
            clearDetailFields();
        }
    }

    private void loadDetailsFromSummary(ACSSummary summary) {
        voucherNoLabel.setText(summary.getVoucherNo() != null ? summary.getVoucherNo() : "-");
        senderLabel.setText(summary.getSender() != null ? summary.getSender() : "-");
        recipientLabel.setText(summary.getRecipient() != null ? summary.getRecipient() : "-");
        recipientAddressLabel.setText(summary.getRecipientAddress() != null ? summary.getRecipientAddress() : "-");
        consigneeLabel.setText(summary.getConsignee() != null ? summary.getConsignee() : "-");

        stationOriginLabel.setText(summary.getAcsStationOriginDescr() != null ? summary.getAcsStationOriginDescr() : "-");
        stationDestinationLabel.setText(summary.getAcsStationDestinationDescr() != null ? summary.getAcsStationDestinationDescr() : "-");
        phoneOriginLabel.setText(summary.getPhoneAcsStationOrigin() != null ? summary.getPhoneAcsStationOrigin() : "-");
        phoneDestinationLabel.setText(summary.getPhoneAcsStationDestination() != null ? summary.getPhoneAcsStationDestination() : "-");

        pickupDateLabel.setText(formatDateTime(summary.getPickupDate()));
        deliveryDateLabel.setText(formatDateTime(summary.getDeliveryDate()));
        expectedDateLabel.setText(formatDateTime(summary.getDeliveryDateExpected()));

        deliveryFlagLabel.setText(getDeliveryFlagText(summary.getDeliveryFlag()));
        returnedFlagLabel.setText(getReturnedFlagText(summary.getReturnedFlag()));
        shipmentStatusLabel.setText(summary.getShipmentStatus() != null ? String.valueOf(summary.getShipmentStatus()) : "-");

        String nonDeliveryReason = summary.getNonDeliveryReasonCode();
        nonDeliveryReasonLabel.setText((nonDeliveryReason == null || nonDeliveryReason.isEmpty()) ? "Κανένας" : nonDeliveryReason);

        deliveryInfoArea.setText(summary.getDeliveryInfo() != null ? summary.getDeliveryInfo() : "Δεν υπάρχουν πληροφορίες");
    }

    private String formatDateTime(LocalDateTime dateTime) {
        if (dateTime == null) {
            return "-";
        }

        java.time.format.DateTimeFormatter formatter =
                java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        return dateTime.format(formatter);
    }

    private void clearDetailFields() {
        voucherNoLabel.setText("-");
        senderLabel.setText("-");
        recipientLabel.setText("-");
        recipientAddressLabel.setText("-");
        consigneeLabel.setText("-");
        stationOriginLabel.setText("-");
        stationDestinationLabel.setText("-");
        phoneOriginLabel.setText("-");
        phoneDestinationLabel.setText("-");
        pickupDateLabel.setText("-");
        deliveryDateLabel.setText("-");
        expectedDateLabel.setText("-");
        deliveryFlagLabel.setText("-");
        returnedFlagLabel.setText("-");
        shipmentStatusLabel.setText("-");
        deliveryInfoArea.setText("");
        nonDeliveryReasonLabel.setText("-");
    }

    private String formatDate(String apiDate) {
        if (apiDate == null || apiDate.trim().isEmpty()) {
            return "-";
        }

        try {
            Date date = inputDateFormat.parse(apiDate);
            return outputDateFormat.format(date);
        } catch (ParseException e) {
            return apiDate; // Return original if parsing fails
        }
    }

    private String getDeliveryFlagText(Integer flag) {
        if (flag == null) return "-";
        return flag == 1 ? "✅ Παραδόθηκε" : "❌ Δεν παραδόθηκε";
    }

    private String getReturnedFlagText(Integer flag) {
        if (flag == null) return "-";
        return flag == 1 ? "↩️ Επιστράφηκε" : "➡️ Δεν επιστράφηκε";
    }

    private void updateTrackingSummary() {
        updateButton.setEnabled(false);
        updateButton.setText("Ενημέρωση...");

        SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                return trackingService.updateSingleShipmentStatusWithDetails(apostoli,
                        ApostoliDetailsStatusCourierPanel.this::updateDetailsFromAPI);
            }

            @Override
            protected void done() {
                try {
                    boolean success = get();

                    if (success) {
                        Apostoli updatedApostoli = apostoliDAO.findByArithmosApostolis(
                                apostoli.getArithmosApostolis());
                        if (updatedApostoli != null) {
                            apostoli.setStatusApostolis(updatedApostoli.getStatusApostolis());
                            loadCurrentData();
                        }

                        JOptionPane.showMessageDialog(ApostoliDetailsStatusCourierPanel.this,
                                "Η κατάσταση ενημερώθηκε επιτυχώς!",
                                "Επιτυχία", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(ApostoliDetailsStatusCourierPanel.this,
                                "Σφάλμα κατά την ενημέρωση της κατάστασης.",
                                "Σφάλμα", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(ApostoliDetailsStatusCourierPanel.this,
                            "Σφάλμα: " + e.getMessage(),
                            "Σφάλμα", JOptionPane.ERROR_MESSAGE);
                } finally {
                    updateButton.setEnabled(true);
                    updateButton.setText("Update Tracking Summary");
                }
            }
        };

        worker.execute();
    }

    // This method will be called by the service to update the UI with API details
    public void updateDetailsFromAPI(com.fasterxml.jackson.databind.JsonNode shipmentData) {
        SwingUtilities.invokeLater(() -> {
            voucherNoLabel.setText(shipmentData.path("voucher_no").asText("-"));
            senderLabel.setText(shipmentData.path("sender").asText("-"));
            recipientLabel.setText(shipmentData.path("recipient").asText("-"));
            recipientAddressLabel.setText(shipmentData.path("recipient_address").asText("-"));
            consigneeLabel.setText(shipmentData.path("consignee").asText("-"));

            stationOriginLabel.setText(shipmentData.path("acs_station_origin_descr").asText("-"));
            stationDestinationLabel.setText(shipmentData.path("acs_station_destination_descr").asText("-"));
            phoneOriginLabel.setText(shipmentData.path("phone_acs_station_origin").asText("-"));
            phoneDestinationLabel.setText(shipmentData.path("phone_acs_station_destination").asText("-"));

            pickupDateLabel.setText(formatDate(shipmentData.path("pickup_date").asText()));
            deliveryDateLabel.setText(formatDate(shipmentData.path("delivery_date").asText()));
            expectedDateLabel.setText(formatDate(shipmentData.path("delivery_date_expected").asText()));

            deliveryFlagLabel.setText(getDeliveryFlagText(shipmentData.path("delivery_flag").asInt()));
            returnedFlagLabel.setText(getReturnedFlagText(shipmentData.path("returned_flag").asInt()));
            shipmentStatusLabel.setText(String.valueOf(shipmentData.path("shipment_status").asInt()));

            String nonDeliveryReason = shipmentData.path("non_delivery_reason_code").asText("");
            nonDeliveryReasonLabel.setText(nonDeliveryReason.isEmpty() ? "Κανένας" : nonDeliveryReason);

            deliveryInfoArea.setText(shipmentData.path("delivery_info").asText("Δεν υπάρχουν πληροφορίες"));
        });
    }
}