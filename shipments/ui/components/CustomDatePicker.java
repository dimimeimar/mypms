package org.pms.shipments.ui.components;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Custom DatePicker component για εύκολη επιλογή ημερομηνιών
 */
public class CustomDatePicker extends JPanel {

    private LocalDate selectedDate;
    private JTextField dateField;
    private JButton calendarButton;
    private JPopupMenu calendarPopup;
    private JPanel calendarPanel;
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private List<ChangeListener> changeListeners = new ArrayList<>();

    // Calendar components
    private JLabel monthYearLabel;
    private JButton prevMonthBtn;
    private JButton nextMonthBtn;
    private JPanel daysPanel;
    private JButton selectedDayButton;

    // Current viewing date (might be different from selected date)
    private LocalDate viewingDate;

    public CustomDatePicker() {
        this(LocalDate.now());
    }

    public CustomDatePicker(LocalDate initialDate) {
        this.selectedDate = initialDate;
        this.viewingDate = initialDate != null ? initialDate : LocalDate.now();
        initComponents();
        setupLayout();
        setupEvents();
        updateDisplay();
    }

    private void initComponents() {
        dateField = new JTextField(10);
        dateField.setPreferredSize(new Dimension(100, 25));
        dateField.setToolTipText("Εισάγετε ημερομηνία (dd/MM/yyyy) ή κάντε κλικ στο ημερολόγιο");

        calendarButton = new JButton("Pick");
        calendarButton.setPreferredSize(new Dimension(65, 35));
        calendarButton.setToolTipText("Ανοίξτε το ημερολόγιο");
        calendarButton.setFocusable(false);

        createCalendarPopup();
    }

    private void createCalendarPopup() {
        calendarPopup = new JPopupMenu();
        calendarPanel = new JPanel(new BorderLayout());
        calendarPanel.setPreferredSize(new Dimension(320, 250));
        calendarPanel.setBorder(BorderFactory.createRaisedBevelBorder());

        // Header with month/year navigation
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        prevMonthBtn = new JButton("<");
        prevMonthBtn.setPreferredSize(new Dimension(45, 35));
        prevMonthBtn.setFocusable(false);

        nextMonthBtn = new JButton(">");
        nextMonthBtn.setPreferredSize(new Dimension(45, 35));
        nextMonthBtn.setFocusable(false);

        monthYearLabel = new JLabel("", SwingConstants.CENTER);
        monthYearLabel.setFont(monthYearLabel.getFont().deriveFont(Font.BOLD, 14f));

        headerPanel.add(prevMonthBtn, BorderLayout.WEST);
        headerPanel.add(monthYearLabel, BorderLayout.CENTER);
        headerPanel.add(nextMonthBtn, BorderLayout.EAST);

        // Days panel
        // Days panel με σταθερό μέγεθος
        daysPanel = new JPanel();
        daysPanel.setLayout(new GridBagLayout());
        daysPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        daysPanel.setPreferredSize(new Dimension(290, 180));
        daysPanel.setMinimumSize(new Dimension(290, 180));

        // Footer with today and clear buttons
        JPanel footerPanel = new JPanel(new FlowLayout());
        JButton todayBtn = new JButton("Σήμερα");
        JButton clearBtn = new JButton("Καθαρισμός");

        todayBtn.addActionListener(e -> {
            setSelectedDate(LocalDate.now());
            calendarPopup.setVisible(false);
        });

        clearBtn.addActionListener(e -> {
            setSelectedDate(null);
            calendarPopup.setVisible(false);
        });

        footerPanel.add(todayBtn);
        footerPanel.add(clearBtn);

        calendarPanel.add(headerPanel, BorderLayout.NORTH);
        calendarPanel.add(daysPanel, BorderLayout.CENTER);
        calendarPanel.add(footerPanel, BorderLayout.SOUTH);

        calendarPopup.add(calendarPanel);
    }

    private void setupLayout() {
        setLayout(new BorderLayout(2, 0));
        add(dateField, BorderLayout.CENTER);
        add(calendarButton, BorderLayout.EAST);
    }

    private void setupEvents() {
        // Calendar button click
        calendarButton.addActionListener(e -> {
            if (calendarPopup.isVisible()) {
                calendarPopup.setVisible(false);
            } else {
                updateCalendar();
                calendarPopup.show(this, 0, getHeight());
            }
        });

        // Text field validation
        dateField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                validateAndParseDate();
            }
        });

        dateField.addActionListener(e -> validateAndParseDate());

        // Month navigation
        prevMonthBtn.addActionListener(e -> {
            viewingDate = viewingDate.minusMonths(1);
            updateCalendar();
        });

        nextMonthBtn.addActionListener(e -> {
            viewingDate = viewingDate.plusMonths(1);
            updateCalendar();
        });
    }

    private void validateAndParseDate() {
        String text = dateField.getText().trim();
        if (text.isEmpty()) {
            setSelectedDate(null);
            return;
        }

        try {
            LocalDate date = LocalDate.parse(text, formatter);
            setSelectedDate(date);
        } catch (DateTimeParseException e) {
            // Restore previous valid value
            updateDisplay();
            JOptionPane.showMessageDialog(this,
                    "Μη έγκυρη ημερομηνία! Χρησιμοποιήστε τη μορφή dd/MM/yyyy",
                    "Σφάλμα",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateCalendar() {
        // Update month/year label
        String monthYear = viewingDate.getMonth().name() + " " + viewingDate.getYear();
        monthYearLabel.setText(monthYear);

        // Clear days panel
        daysPanel.removeAll();

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(1, 1, 1, 1);

// Add day headers
        String[] dayHeaders = {"Δ", "Τ", "Τ", "Π", "Π", "Σ", "Κ"};
        for (int col = 0; col < 7; col++) {
            gbc.gridx = col;
            gbc.gridy = 0;
            gbc.weightx = 1.0;
            gbc.weighty = 0.3;

            JLabel dayLabel = new JLabel(dayHeaders[col], SwingConstants.CENTER);
            dayLabel.setFont(dayLabel.getFont().deriveFont(Font.BOLD));
            dayLabel.setPreferredSize(new Dimension(40, 25));
            dayLabel.setMinimumSize(new Dimension(40, 25));
            dayLabel.setOpaque(true);
            dayLabel.setBackground(Color.LIGHT_GRAY);
            dayLabel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
            daysPanel.add(dayLabel, gbc);
        }

// Get first day of month and calculate offset
        LocalDate firstDay = viewingDate.withDayOfMonth(1);
        int dayOfWeek = firstDay.getDayOfWeek().getValue(); // Monday = 1, Sunday = 7
        int offset = dayOfWeek - 1; // Make Monday = 0

// Add days of month
        int daysInMonth = viewingDate.lengthOfMonth();
        int currentDay = 1;
        gbc.weighty = 1.0;

        for (int row = 1; row <= 6; row++) {
            for (int col = 0; col < 7; col++) {
                gbc.gridx = col;
                gbc.gridy = row;

                // Calculate which day this cell represents
                int cellNumber = (row - 1) * 7 + col;

                if (cellNumber < offset || currentDay > daysInMonth) {
                    // Empty cell
                    JLabel emptyLabel = new JLabel("");
                    emptyLabel.setPreferredSize(new Dimension(40, 30));
                    emptyLabel.setMinimumSize(new Dimension(40, 30));
                    daysPanel.add(emptyLabel, gbc);
                } else {
                    // Day button
                    LocalDate currentDate = viewingDate.withDayOfMonth(currentDay);
                    JButton dayButton = new JButton(String.valueOf(currentDay));
                    dayButton.setPreferredSize(new Dimension(40, 30));
                    dayButton.setMinimumSize(new Dimension(40, 30));
                    dayButton.setMaximumSize(new Dimension(40, 30));
                    dayButton.setMargin(new Insets(2, 2, 2, 2));
                    dayButton.setFocusable(false);

                    // Highlight selected date
                    if (selectedDate != null && currentDate.equals(selectedDate)) {
                        dayButton.setBackground(new Color(100, 149, 237));
                        dayButton.setForeground(Color.WHITE);
                        dayButton.setOpaque(true);
                        selectedDayButton = dayButton;
                    } else if (currentDate.equals(LocalDate.now())) {
                        // Highlight today
                        dayButton.setBackground(Color.YELLOW);
                        dayButton.setOpaque(true);
                    }

                    dayButton.addActionListener(e -> {
                        setSelectedDate(currentDate);
                        calendarPopup.setVisible(false);
                    });

                    daysPanel.add(dayButton, gbc);
                    currentDay++;
                }
            }

            if (currentDay > daysInMonth) break;
        }

        daysPanel.revalidate();
        daysPanel.repaint();
    }

    private void updateDisplay() {
        if (selectedDate != null) {
            dateField.setText(selectedDate.format(formatter));
        } else {
            dateField.setText("");
        }
    }

    // Public methods
    public LocalDate getSelectedDate() {
        return selectedDate;
    }

    public void setSelectedDate(LocalDate date) {
        LocalDate oldDate = this.selectedDate;
        this.selectedDate = date;

        if (date != null) {
            this.viewingDate = date;
        }

        updateDisplay();

        // Fire change event if date actually changed
        if ((oldDate == null && date != null) ||
                (oldDate != null && !oldDate.equals(date))) {
            fireChangeEvent();
        }
    }

    public void clear() {
        setSelectedDate(null);
    }

    public boolean isEmpty() {
        return selectedDate == null;
    }

    public String getDateAsString() {
        return selectedDate != null ? selectedDate.format(formatter) : "";
    }

    // Event handling
    public void addChangeListener(ChangeListener listener) {
        changeListeners.add(listener);
    }

    public void removeChangeListener(ChangeListener listener) {
        changeListeners.remove(listener);
    }

    private void fireChangeEvent() {
        ChangeEvent event = new ChangeEvent(this);
        for (ChangeListener listener : changeListeners) {
            listener.stateChanged(event);
        }
    }

    // Utility methods
    public void setEditable(boolean editable) {
        dateField.setEditable(editable);
        calendarButton.setEnabled(editable);
    }

    public void setToolTip(String tooltip) {
        dateField.setToolTipText(tooltip);
        setToolTipText(tooltip);
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        dateField.setEnabled(enabled);
        calendarButton.setEnabled(enabled);
    }

    public void setDateFormat(String pattern) {
        this.formatter = DateTimeFormatter.ofPattern(pattern);
        updateDisplay();
    }
}