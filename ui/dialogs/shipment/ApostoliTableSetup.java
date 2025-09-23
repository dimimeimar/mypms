package org.example.ui.dialogs.shipment;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.Image;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class ApostoliTableSetup {

    public static void setupTable(JTable table, ApostoliTableModel tableModel) {
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowHeight(25);
        table.getTableHeader().setReorderingAllowed(false);

        setupTableHeader(table);
        setupColumnWidths(table);
        setupTableSorter(table, tableModel);
    }

    private static void setupTableHeader(JTable table) {
        JTableHeader header = table.getTableHeader();
        header.setPreferredSize(new Dimension(header.getWidth(), 40));
        header.setBackground(new Color(220, 20, 60));
        header.setForeground(Color.WHITE);
        header.setFont(header.getFont().deriveFont(Font.BOLD));

        DefaultTableCellRenderer headerRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                if (value != null) {
                    String text = value.toString();
                    if (text.contains(" ")) {
                        text = "<html><center>" + text.replace(" ", "<br>") + "</center></html>";
                    }
                    setText(text);
                }

                setBackground(new Color(161, 14, 27));
                setForeground(Color.WHITE);
                setFont(getFont().deriveFont(Font.BOLD));
                setHorizontalAlignment(SwingConstants.CENTER);
                setBorder(BorderFactory.createRaisedBevelBorder());

                return c;
            }
        };

        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setHeaderRenderer(headerRenderer);
        }
    }

    private static void setupColumnWidths(JTable table) {
        table.getColumnModel().getColumn(0).setPreferredWidth(40);   // A/A
        table.getColumnModel().getColumn(0).setMaxWidth(50);
        table.getColumnModel().getColumn(1).setPreferredWidth(80);   // Courier
        table.getColumnModel().getColumn(2).setPreferredWidth(120);  // Αρ. Αποστολής
        table.getColumnModel().getColumn(3).setPreferredWidth(100);  // Ημερομηνία Έκδοσης
        table.getColumnModel().getColumn(4).setPreferredWidth(90);   // Αντικαταβολή
        table.getColumnModel().getColumn(5).setPreferredWidth(150);  // Αποστολέας
        table.getColumnModel().getColumn(6).setPreferredWidth(150);  // Παραλήπτης
        table.getColumnModel().getColumn(7).setPreferredWidth(100);  // Πόλη
        table.getColumnModel().getColumn(8).setPreferredWidth(100);  // Τηλέφωνο
        table.getColumnModel().getColumn(9).setPreferredWidth(100);  // Κινητό
        table.getColumnModel().getColumn(10).setPreferredWidth(80);   // Σε διακίνηση (ΝΕΑ)
        table.getColumnModel().getColumn(11).setPreferredWidth(120); // Status Courier
        table.getColumnModel().getColumn(12).setPreferredWidth(120); // Status MyPMS

        // Κεντράρισμα των αριθμητικών στηλών
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        table.getColumnModel().getColumn(10).setCellRenderer(new DiakinishRenderer());  // Σε διακίνηση
    }

    private static void setupTableSorter(JTable table, ApostoliTableModel tableModel) {
        TableRowSorter<ApostoliTableModel> sorter = new TableRowSorter<>(tableModel);

        // Custom comparator για την ημερομηνία (στήλη 3)
        sorter.setComparator(3, new java.util.Comparator<String>() {
            private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

            @Override
            public int compare(String date1, String date2) {
                if (date1 == null || date1.isEmpty()) return -1;
                if (date2 == null || date2.isEmpty()) return 1;

                try {
                    LocalDate d1 = LocalDate.parse(date1, formatter);
                    LocalDate d2 = LocalDate.parse(date2, formatter);
                    return d1.compareTo(d2);
                } catch (Exception e) {
                    // Fallback σε string comparison αν αποτύχει το parsing
                    return date1.compareTo(date2);
                }
            }
        });

        table.setRowSorter(sorter);
    }

    // Custom renderer για τη στήλη "Σε διακίνηση"
    static class DiakinishRenderer extends DefaultTableCellRenderer {
        private ImageIcon tickIcon;

        public DiakinishRenderer() {
            setHorizontalAlignment(SwingConstants.CENTER);
            try {
                tickIcon = new ImageIcon(getClass().getResource("/icons/tik_img.png"));
                // Προαιρετικά: Μείωση μεγέθους εικόνας αν χρειάζεται
                Image scaledImage = tickIcon.getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH);
                tickIcon = new ImageIcon(scaledImage);
            } catch (Exception e) {
                System.out.println("Tick image not found, using text fallback");
                tickIcon = null;
            }
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {

            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            // Έλεγχος για Integer.MAX_VALUE που αντιπροσωπεύει παραδομένη αποστολή
            if (value instanceof Integer) {
                Integer intValue = (Integer) value;
                if (intValue == Integer.MAX_VALUE && tickIcon != null) {
                    setIcon(tickIcon);
                    setText("");
                } else if (intValue == -1) {
                    setIcon(null);
                    setText("");
                } else {
                    setIcon(null);
                    setText(intValue.toString());
                }
            } else {
                setIcon(null);
                setText(value != null ? value.toString() : "");
            }

            return c;
        }
    }
}