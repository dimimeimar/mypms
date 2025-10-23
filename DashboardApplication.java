package org.pms;

import org.pms.config.DatabaseConfig;
import org.pms.customers.ui.CustomerManagementApplication;
import org.pms.shipments.ui.main.ShipmentManagementApplication;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Κεντρικό Dashboard της εφαρμογής MyPMS
 */
public class DashboardApplication extends JFrame {
    private CustomerManagementApplication customerApp;
    private ShipmentManagementApplication shipmentApp;
    private JFrame antikatavoliFrame;

    public DashboardApplication() {

        initComponents();
        setupLayout();
        setupEvents();

        setTitle("MyPMS - Κεντρικό Σύστημα Διαχείρισης");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        // Set application icon if available
        try {
            setIconImage(new ImageIcon(getClass().getResource("/icons/app-icon.png")).getImage());
        } catch (Exception e) {
            // Icon not found, continue without it
        }
    }

    private void initComponents() {
        // Components will be initialized in setupLayout
    }

    private void setupLayout() {
        setLayout(new BorderLayout());

        // Header Panel
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);

        // Main Dashboard Panel
        JPanel dashboardPanel = createDashboardPanel();
        add(dashboardPanel, BorderLayout.CENTER);

        // Footer Panel
        JPanel footerPanel = createFooterPanel();
        add(footerPanel, BorderLayout.SOUTH);
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(0, 100, 200));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("MyPMS - Σύστημα Διαχείρισης");
        titleLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 28));
        titleLabel.setForeground(Color.BLACK);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel subtitleLabel = new JLabel("Κεντρικό Dashboard");
        subtitleLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 16));
        subtitleLabel.setForeground(Color.BLACK);
        subtitleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        panel.add(titleLabel, BorderLayout.CENTER);
        panel.add(subtitleLabel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createDashboardPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(255, 255, 255));
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 0.0;

        // Customer Management Button
        JButton btnCustomers = createCompactDashboardButton(
                "Διαχείριση Πελατών",
                "Πελάτες, εταιρίες και υπεύθυνοι"
        );

        // Shipments Management Button
        JButton btnShipments = createCompactDashboardButton(
                "Διαχείριση Αποστολών",
                "Αποστολές και παρακολούθηση"
        );

        // Antikatavoles Management Button
        JButton btnAntikatavoles = createCompactDashboardButton(
                "Διαχείριση Αντικαταβολών",
                "Αντικαταβολές και αποδόσεις"
        );

        // Layout: 3 στηλες
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(btnCustomers, gbc);

        gbc.gridx = 1; gbc.gridy = 0;
        panel.add(btnShipments, gbc);

        gbc.gridx = 2; gbc.gridy = 0;
        panel.add(btnAntikatavoles, gbc);

        // Spacer για να κρατήσει τα κουμπιά στην κορυφή
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 3;
        gbc.weighty = 1.0;
        panel.add(Box.createVerticalGlue(), gbc);

        // Button Events
        btnCustomers.addActionListener(e -> openCustomerManagement());
        btnShipments.addActionListener(e -> openShipmentManagement());
        btnAntikatavoles.addActionListener(e -> openAntikatavoliManagement());

        return panel;
    }

    private JButton createCompactDashboardButton(String title, String description ) {
        JButton button = new JButton();
        button.setLayout(new BorderLayout(10, 10));
        button.setPreferredSize(new Dimension(220, 120));
        button.setBackground(new Color(25, 42, 86));
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(15, 25, 50), 2),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Title Label
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));
        titleLabel.setForeground(Color.BLACK);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // Description Label
        JLabel descLabel = new JLabel("<html><center>" + description + "</center></html>");
        descLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        descLabel.setForeground(new Color(0, 0, 0, 200));
        descLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel contentPanel = new JPanel(new BorderLayout(5, 5));
        contentPanel.setOpaque(false);
        contentPanel.add(titleLabel, BorderLayout.NORTH);
        contentPanel.add(descLabel, BorderLayout.CENTER);

        button.add(contentPanel, BorderLayout.CENTER);

        // Hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            Color originalColor = new Color(25, 42, 86); ;
            Color hoverColor = new Color(35, 60, 110);

            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                button.setBackground(hoverColor);
                button.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(hoverColor.darker(), 3),
                        BorderFactory.createEmptyBorder(15, 15, 15, 15)
                ));
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                button.setBackground(originalColor);
                button.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(originalColor.darker(), 2),
                        BorderFactory.createEmptyBorder(15, 15, 15, 15)
                ));
            }

            @Override
            public void mousePressed(java.awt.event.MouseEvent e) {
                button.setBackground(originalColor.darker());
            }

            @Override
            public void mouseReleased(java.awt.event.MouseEvent e) {
                button.setBackground(hoverColor);
            }
        });

        return button;
    }


    private JPanel createFooterPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createLoweredBevelBorder());

        JLabel statusLabel = new JLabel("Σύνδεση ενεργή");
        statusLabel.setForeground(Color.GREEN.darker());
        statusLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        JLabel versionLabel = new JLabel("Έκδοση 2.0");
        versionLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        panel.add(statusLabel, BorderLayout.WEST);
        panel.add(versionLabel, BorderLayout.EAST);

        return panel;
    }

    private void setupEvents() {
        // Window closing event
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                exitApplication();
            }
        });

        // ESC key to exit
        getRootPane().registerKeyboardAction(
                e -> exitApplication(),
                KeyStroke.getKeyStroke("ESCAPE"),
                JComponent.WHEN_IN_FOCUSED_WINDOW
        );
    }

    private void openCustomerManagement() {
        if (customerApp == null || !customerApp.isDisplayable()) {
            customerApp = new CustomerManagementApplication();
            customerApp.setVisible(true);
        } else {
            customerApp.toFront();
            customerApp.requestFocus();
            if (customerApp.getState() == JFrame.ICONIFIED) {
                customerApp.setState(JFrame.NORMAL);
            }
        }
    }

    private void openShipmentManagement() {
        if (shipmentApp == null || !shipmentApp.isDisplayable()) {
            shipmentApp = new ShipmentManagementApplication();
            shipmentApp.setVisible(true);
        } else {
            shipmentApp.toFront();
            shipmentApp.requestFocus();
            if (shipmentApp.getState() == JFrame.ICONIFIED) {
                shipmentApp.setState(JFrame.NORMAL);
            }
        }
    }

    private void openAntikatavoliManagement() {
        if (antikatavoliFrame == null || !antikatavoliFrame.isDisplayable()) {
            antikatavoliFrame = new JFrame("Διαχείριση Αντικαταβολών");
            antikatavoliFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            antikatavoliFrame.setSize(1400, 800);
            antikatavoliFrame.setLocationRelativeTo(this);

            try {
                antikatavoliFrame.setIconImage(new ImageIcon(getClass().getResource("/icons/app-icon.png")).getImage());
            } catch (Exception e) {
                // Icon not found, continue without it
            }

            org.pms.antikatavoles.ui.panels.AntikatavoliManagementPanel panel =
                    new org.pms.antikatavoles.ui.panels.AntikatavoliManagementPanel();

            antikatavoliFrame.add(panel);
            antikatavoliFrame.setVisible(true);
        } else {
            antikatavoliFrame.toFront();
            antikatavoliFrame.requestFocus();
            if (antikatavoliFrame.getState() == JFrame.ICONIFIED) {
                antikatavoliFrame.setState(JFrame.NORMAL);
            }
        }
    }

    private void exitApplication() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Είστε σίγουροι ότι θέλετε να κλείσετε την εφαρμογή;",
                "Επιβεβαίωση Εξόδου",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                DatabaseConfig.getInstance().closeConnection();
            } catch (Exception e) {
                System.err.println("Σφάλμα κλεισίματος σύνδεσης: " + e.getMessage());
            }
            System.exit(0);
        }
    }

    /**
     * Main method to start the application
     */
    public static void main(String[] args) {
        // Set Look and Feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            try {
                UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            } catch (Exception ex) {
                System.err.println("Αδυναμία ρύθμισης Look and Feel: " + ex.getMessage());
            }
        }

        // Set larger fonts globally
        Font defaultFont = UIManager.getFont("Label.font");
        if (defaultFont != null) {
            int newSize = defaultFont.getSize() + 3;
            UIManager.put("Label.font", new Font(defaultFont.getName(), defaultFont.getStyle(), newSize));
            UIManager.put("Button.font", new Font(defaultFont.getName(), defaultFont.getStyle(), newSize));
            UIManager.put("TextField.font", new Font(defaultFont.getName(), defaultFont.getStyle(), newSize));
            UIManager.put("TextArea.font", new Font(defaultFont.getName(), defaultFont.getStyle(), newSize));
            UIManager.put("ComboBox.font", new Font(defaultFont.getName(), defaultFont.getStyle(), newSize));
            UIManager.put("Table.font", new Font(defaultFont.getName(), defaultFont.getStyle(), newSize));
            UIManager.put("TableHeader.font", new Font(defaultFont.getName(), Font.BOLD, newSize));
            UIManager.put("Menu.font", new Font(defaultFont.getName(), defaultFont.getStyle(), newSize));
            UIManager.put("MenuItem.font", new Font(defaultFont.getName(), defaultFont.getStyle(), newSize));
            UIManager.put("TabbedPane.font", new Font(defaultFont.getName(), defaultFont.getStyle(), newSize));
            UIManager.put("List.font", new Font(defaultFont.getName(), defaultFont.getStyle(), newSize));
            UIManager.put("Tree.font", new Font(defaultFont.getName(), defaultFont.getStyle(), newSize));
            UIManager.put("ToolTip.font", new Font(defaultFont.getName(), defaultFont.getStyle(), newSize));
            UIManager.put("TitledBorder.font", new Font(defaultFont.getName(), defaultFont.getStyle(), newSize));
        }

        // Create and show the dashboard
        SwingUtilities.invokeLater(() -> {
            try {
                // Test database connection first
                org.pms.config.DatabaseConfig.getInstance().getConnection();

                // Show dashboard
                DashboardApplication dashboard = new DashboardApplication();
                dashboard.setVisible(true);

            } catch (Exception e) {
                JOptionPane.showMessageDialog(null,
                        "Σφάλμα σύνδεσης με τη βάση δεδομένων:\n" + e.getMessage() +
                                "\n\nΠαρακαλώ ελέγξτε τις ρυθμίσεις σύνδεσης.",
                        "Σφάλμα Σύνδεσης", JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            }
        });
    }
}