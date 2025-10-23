package org.pms.shipments.ui.dialogs;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableRowSorter;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.awt.Image;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class ApostoliTableSetup {

    public static void setupTable(JTable table, ApostoliTableModel tableModel) {
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getTableHeader().setReorderingAllowed(false);

        setupTableHeader(table);
        setupColumnWidths(table);
        setupMultilineRenderers(table);
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
        table.getColumnModel().getColumn(2).setPreferredWidth(80);  // Αρ. Αποστολής
        table.getColumnModel().getColumn(3).setPreferredWidth(80);  // Ημερομηνία Έκδοσης
        table.getColumnModel().getColumn(4).setPreferredWidth(90);   // Αντικαταβολή
        table.getColumnModel().getColumn(5).setPreferredWidth(150);  // Αποστολέας
        table.getColumnModel().getColumn(6).setPreferredWidth(100);  // Παραλήπτης
        table.getColumnModel().getColumn(7).setPreferredWidth(100);  // Πόλη
        table.getColumnModel().getColumn(8).setPreferredWidth(100);  // Τηλέφωνο
        table.getColumnModel().getColumn(9).setPreferredWidth(100);  // Κινητό
        table.getColumnModel().getColumn(10).setPreferredWidth(80);   // Σε διακίνηση (ΝΕΑ)
        table.getColumnModel().getColumn(11).setPreferredWidth(140); // Status Courier
        table.getColumnModel().getColumn(12).setPreferredWidth(140); // Status MyPMS

        // Κεντράρισμα των αριθμητικών στηλών
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        table.getColumnModel().getColumn(10).setCellRenderer(new DiakinishRenderer());  // Σε διακίνηση

        DefaultTableCellRenderer centerRenderer2 = new DefaultTableCellRenderer();
        centerRenderer2.setHorizontalAlignment(SwingConstants.CENTER);

        for (int i = 0; i < table.getColumnCount(); i++) {
            if (i != 10) {
                table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer2);
            }
        }
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

        sorter.setComparator(10, new java.util.Comparator<Object>() {
            @Override
            public int compare(Object o1, Object o2) {
                Integer val1 = extractNumericValue(o1);
                Integer val2 = extractNumericValue(o2);
                return val1.compareTo(val2);
            }

            private Integer extractNumericValue(Object obj) {
                if (obj instanceof Integer) {
                    return (Integer) obj;
                }
                if (obj instanceof String) {
                    String str = (String) obj;
                    if (str.equals("TICK_IMAGE")) return Integer.MAX_VALUE;
                    if (str.equals("TIK_IMG_PURPLE")) return Integer.MAX_VALUE - 1;
                    if (str.equals("X_IMAGE")) return Integer.MAX_VALUE - 2;
                    if (str.equals("EXCL_IMAGE")) return Integer.MAX_VALUE - 3;
                    if (str.equals("RETURN_IMAGE")) return Integer.MAX_VALUE - 4;
                    if (str.isEmpty()) return -1;
                    try {
                        return Integer.parseInt(str);
                    } catch (NumberFormatException e) {
                        return -2;
                    }
                }
                return -2;
            }
        });




    }

    static class DiakinishRenderer extends DefaultTableCellRenderer {
        private ImageIcon tickIcon;
        private ImageIcon xIcon;
        private ImageIcon exclIcon;
        private ImageIcon purpleIcon;
        private ImageIcon returnIcon;

        public DiakinishRenderer() {
            setHorizontalAlignment(SwingConstants.CENTER);
            setVerticalAlignment(SwingConstants.CENTER);
            try {
                tickIcon = new ImageIcon(getClass().getResource("/icons/tik_img.png"));
                xIcon = new ImageIcon(getClass().getResource("/icons/x_img.png"));
                exclIcon = new ImageIcon(getClass().getResource("/icons/excl_mark.png"));
                purpleIcon = new ImageIcon(getClass().getResource("/icons/tik_img_purple.png"));

                if (tickIcon.getIconWidth() > 0) {
                    Image scaledTick = tickIcon.getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH);
                    tickIcon = new ImageIcon(scaledTick);
                }

                if (xIcon.getIconWidth() > 0) {
                    Image scaledX = xIcon.getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH);
                    xIcon = new ImageIcon(scaledX);
                }

                if (exclIcon.getIconWidth() > 0) {
                    Image scaledExcl = exclIcon.getImage().getScaledInstance(28, 28, Image.SCALE_SMOOTH);
                    exclIcon = new ImageIcon(scaledExcl);
                }

                if (purpleIcon.getIconWidth() > 0) {
                    Image scaledPurple = purpleIcon.getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH);
                    purpleIcon = new ImageIcon(scaledPurple);
                }

                returnIcon = new ImageIcon(getClass().getResource("/icons/return_img.png"));

                if (returnIcon.getIconWidth() > 0) {
                    Image scaledReturn = returnIcon.getImage().getScaledInstance(28, 28, Image.SCALE_SMOOTH);
                    returnIcon = new ImageIcon(scaledReturn);
                }
            } catch (Exception e) {
                System.err.println("Σφάλμα φόρτωσης εικόνων: " + e.getMessage());
            }
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus,
                                                       int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            setText("");
            setIcon(null);

            if (value == null) {
                return this;
            }

            if ("TICK_IMAGE".equals(value)) {
                if (tickIcon != null && tickIcon.getIconWidth() > 0) {
                    setIcon(tickIcon);
                } else {
                    setText("✓");
                }
            } else if ("X_IMAGE".equals(value)) {
                if (xIcon != null && xIcon.getIconWidth() > 0) {
                    setIcon(xIcon);
                } else {
                    setText("X");
                }
            } else if ("EXCL_IMAGE".equals(value)) {
                if (exclIcon != null && exclIcon.getIconWidth() > 0) {
                    setIcon(exclIcon);
                } else {
                    setText("!");
                }
            } else if ("TIK_IMG_PURPLE".equals(value)) {
                if (purpleIcon != null && purpleIcon.getIconWidth() > 0) {
                    setIcon(purpleIcon);
                } else {
                    setText("✓");
                }
            }
            else if (value instanceof Integer) {
                int days = (Integer) value;
                if (days >= 0) {
                    setText(String.valueOf(days));
                } else {
                    setText("");
                }
            }
            else if ("RETURN_IMAGE".equals(value)) {
                if (returnIcon != null && returnIcon.getIconWidth() > 0) {
                    setIcon(returnIcon);
                } else {
                    setText("↩");
                }
            }
            else {
                setText(value.toString());
            }

            return this;
        }
    }


    private static void setupMultilineRenderers(JTable table) {
        class MultilineRenderer extends JPanel implements TableCellRenderer {
            private JTextArea textArea;

            public MultilineRenderer() {
                setLayout(new BorderLayout());
                textArea = new JTextArea();
                textArea.setLineWrap(true);
                textArea.setWrapStyleWord(true);
                textArea.setOpaque(false);
                textArea.setEditable(false);
                textArea.setFocusable(false);

                JPanel wrapper = new JPanel(new GridBagLayout());
                wrapper.setOpaque(false);
                wrapper.add(textArea);

                add(wrapper, BorderLayout.CENTER);
            }

            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {

                textArea.setText(value != null ? value.toString() : "");
                textArea.setFont(table.getFont().deriveFont(11.5f));

                if (isSelected) {
                    setBackground(table.getSelectionBackground());
                    textArea.setForeground(table.getSelectionForeground());
                } else {
                    setBackground(table.getBackground());
                    textArea.setForeground(table.getForeground());
                }

                setOpaque(true);

                int columnWidth = table.getColumnModel().getColumn(column).getWidth();
                textArea.setSize(columnWidth - 8, Short.MAX_VALUE);

                int preferredHeight = Math.max(40, textArea.getPreferredSize().height + 10);
                int currentRowHeight = table.getRowHeight(row);

                if (preferredHeight > currentRowHeight) {
                    table.setRowHeight(row, preferredHeight);
                }

                return this;
            }
        }

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus,
                                                           int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (c instanceof JLabel) {
                    ((JLabel) c).setVerticalAlignment(SwingConstants.CENTER);
                }
                return c;
            }
        };
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);

        table.getColumnModel().getColumn(6).setCellRenderer(new MultilineRenderer());
        table.getColumnModel().getColumn(11).setCellRenderer(new MultilineRenderer());
        table.getColumnModel().getColumn(12).setCellRenderer(new MultilineRenderer());

        for (int i = 0; i < table.getColumnCount(); i++) {
            if (i != 6 && i != 10 && i != 11 && i != 12) {
                table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
            }
        }
    }

}