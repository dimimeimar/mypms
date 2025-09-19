package org.example.view;

import org.example.view.client.CustomerManagementApplication;
import org.example.view.shipment.ShipmentManagementApplication;
import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Κεντρικό Dashboard της εφαρμογής MyPMS
 */
public class DashboardApplication extends JFrame {

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
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel subtitleLabel = new JLabel("Κεντρικό Dashboard");
        subtitleLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 16));
        subtitleLabel.setForeground(Color.WHITE);
        subtitleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        panel.add(titleLabel, BorderLayout.CENTER);
        panel.add(subtitleLabel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createDashboardPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(20, 20, 20, 20);

        // Customer Management Button
        JButton btnCustomers = createDashboardButton(
                "Διαχείριση Πελατών",
                "Διαχείριση πελατών, εταιριών και υπευθύνων",
                new Color(52, 152, 219)
        );

        // Shipments Management Button
        JButton btnShipments = createDashboardButton(
                "Διαχείριση Αποστολών",
                "Διαχείριση αποστολών και παρακολούθηση status",
                new Color(46, 204, 113)
        );

        // Add buttons to panel
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0; gbc.weighty = 1.0;
        panel.add(btnCustomers, gbc);

        gbc.gridx = 1; gbc.gridy = 0;
        panel.add(btnShipments, gbc);

        // Button Events
        btnCustomers.addActionListener(e -> openCustomerManagement());
        btnShipments.addActionListener(e -> openShipmentManagement());

        return panel;
    }

    private JButton createDashboardButton(String title, String description, Color color) {
        JButton button = new JButton();
        button.setLayout(new BorderLayout());
        button.setPreferredSize(new Dimension(300, 200));
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setBorder(BorderFactory.createRaisedBevelBorder());
        button.setFocusPainted(false);

        // Title Label
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 20));
        titleLabel.setForeground(Color.BLACK);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // Description Label
        JLabel descLabel = new JLabel("<html><center>" + description + "</center></html>");
        descLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        descLabel.setForeground(Color.BLACK);
        descLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // Add padding
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);
        titlePanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));
        titlePanel.add(titleLabel, BorderLayout.CENTER);

        JPanel descPanel = new JPanel(new BorderLayout());
        descPanel.setOpaque(false);
        descPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));
        descPanel.add(descLabel, BorderLayout.CENTER);

        button.add(titlePanel, BorderLayout.NORTH);
        button.add(descPanel, BorderLayout.CENTER);

        // Hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            Color originalColor = color;

            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                button.setBackground(originalColor.brighter());
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                button.setBackground(originalColor);
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
        CustomerManagementApplication customerApp = new CustomerManagementApplication();
        customerApp.setVisible(true);
    }

    private void openShipmentManagement() {
        ShipmentManagementApplication shipmentApp = new ShipmentManagementApplication();
        shipmentApp.setVisible(true);
    }

    private void exitApplication() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Είστε σίγουροι ότι θέλετε να κλείσετε την εφαρμογή;",
                "Επιβεβαίωση Εξόδου",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                org.example.config.DatabaseConfig.getInstance().closeConnection();
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
                org.example.config.DatabaseConfig.getInstance().getConnection();

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