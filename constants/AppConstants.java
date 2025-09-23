package org.example.constants;

/**
 * Γενικές σταθερές της εφαρμογής
 */
public final class AppConstants {

    // Τίτλοι Εφαρμογών
    public static final String CUSTOMER_MANAGEMENT_TITLE = "Διαχείριση Πελατών";
    public static final String SHIPMENT_MANAGEMENT_TITLE = "Διαχείριση Αποστολών";

    // Έκδοση Εφαρμογής
    public static final String APP_VERSION = "1.0.0";
    public static final String APP_NAME = "Customer & Shipment Management System";

    // Default Values
    public static final String DEFAULT_COUNTRY = "Ελλάδα";
    public static final String DEFAULT_COURIER = "ACS";
    public static final int AFM_LENGTH = 9;
    public static final int MAX_PHONE_LENGTH = 20;

    // File Extensions
    public static final String CSV_EXTENSION = ".csv";
    public static final String EXCEL_EXTENSION = ".xlsx";

    // Keyboard Shortcuts
    public static final String SHORTCUT_NEW = "ctrl N";
    public static final String SHORTCUT_EXIT = "ctrl Q";
    public static final String SHORTCUT_REFRESH = "F5";
    public static final String SHORTCUT_HELP = "F1";

    private AppConstants() {
        // Prevent instantiation
    }
}