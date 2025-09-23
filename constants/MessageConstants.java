package org.example.constants;

/**
 * Όλα τα μηνύματα της εφαρμογής (σφάλματα, επιτυχίες, προειδοποιήσεις)
 */
public final class MessageConstants {

    // VALIDATION MESSAGES
    public static final String REQUIRED_FIELD = "Αυτό το πεδίο είναι υποχρεωτικό!";
    public static final String INVALID_AFM = "Μη έγκυρος ΑΦΜ!";
    public static final String INVALID_AFM_COMPANY = "Μη έγκυρος ΑΦΜ εταιρίας!";
    public static final String INVALID_AFM_REPRESENTATIVE = "Μη έγκυρος ΑΦΜ νόμιμου εκπροσώπου!";
    public static final String REQUIRED_CUSTOMER_CODE = "Ο κωδικός πελάτη είναι υποχρεωτικός!";
    public static final String REQUIRED_COMPANY_NAME = "Η επωνυμία εταιρίας είναι υποχρεωτική!";

    // PHONE VALIDATION MESSAGES
    public static final String PHONE_TOO_LONG = "Τα τηλέφωνα δεν μπορούν να ξεπερνούν τους 20 χαρακτήρες!";
    public static final String PHONE_LENGTH_ERROR_TITLE = "Σφάλμα Μήκους";

    // SUCCESS MESSAGES
    public static final String SUCCESS_SAVE = "Τα δεδομένα αποθηκεύτηκαν επιτυχώς!";
    public static final String SUCCESS_DELETE = "Η διαγραφή ολοκληρώθηκε επιτυχώς!";
    public static final String SUCCESS_UPDATE = "Η ενημέρωση ολοκληρώθηκε επιτυχώς!";
    public static final String SUCCESS_IMPORT = "Η εισαγωγή ολοκληρώθηκε επιτυχώς!";
    public static final String SUCCESS_TITLE = "Επιτυχία";

    // ERROR MESSAGES
    public static final String ERROR_TITLE = "Σφάλμα";
    public static final String ERROR_SAVE = "Σφάλμα κατά την αποθήκευση!";
    public static final String ERROR_DELETE = "Σφάλμα κατά τη διαγραφή!";
    public static final String ERROR_UPDATE = "Σφάλμα κατά την ενημέρωση!";
    public static final String ERROR_DATABASE = "Σφάλμα βάσης δεδομένων!";
    public static final String ERROR_SHIPMENT_NOT_FOUND = "Δεν βρέθηκε η αποστολή!";
    public static final String ERROR_CUSTOMER_NOT_FOUND = "Δεν βρέθηκε ο πελάτης!";
    public static final String ERROR_INDEX = "Σφάλμα ευρετηρίου - δοκιμάστε ξανά";
    public static final String ERROR_PARSING = "Σφάλμα ανάλυσης δεδομένων";
    public static final String ERROR_DATE_FORMAT = "Λάθος μορφή ημερομηνίας";
    public static final String ERROR_COMMENT_DELETE = "Σφάλμα κατά τη διαγραφή του σχολίου!";

    // WARNING MESSAGES
    public static final String WARNING_TITLE = "Προειδοποίηση";
    public static final String WARNING_NO_SELECTION = "Παρακαλώ επιλέξτε μία ή περισσότερες αποστολές!";
    public static final String WARNING_NO_CUSTOMER_SELECTED = "Παρακαλώ επιλέξτε πελάτη";

    // CONFIRMATION MESSAGES
    public static final String CONFIRM_DELETE_TITLE = "Επιβεβαίωση Διαγραφής";
    public static final String CONFIRM_DELETE_SINGLE = "Είστε σίγουροι ότι θέλετε να διαγράψετε αυτή την εγγραφή;";
    public static final String CONFIRM_DELETE_MULTIPLE_PREFIX = "Είστε σίγουροι ότι θέλετε να διαγράψετε τις παρακάτω ";
    public static final String CONFIRM_DELETE_MULTIPLE_SUFFIX = " αποστολές?";
    public static final String CONFIRM_IMPORT_PREFIX = "Θα ανέβουν ";
    public static final String CONFIRM_IMPORT_SUFFIX = " εγγραφές στη βάση.\nΣυνέχεια;";
    public static final String CONFIRM_TITLE = "Επιβεβαίωση";
    public static final String DELETE_WARNING = "⚠️ ΠΡΟΣΟΧΗ: Αυτή η ενέργεια δεν μπορεί να αναιρεθεί!";

    // DEVELOPMENT MESSAGES
    public static final String FEATURE_IN_DEVELOPMENT = "Σε ανάπτυξη";
    public static final String EXPORT_FEATURE = "Δυνατότητα εξαγωγής - Σε ανάπτυξη";
    public static final String IMPORT_FEATURE = "Δυνατότητα εισαγωγής - Σε ανάπτυξη";
    public static final String BACKUP_FEATURE = "Δυνατότητα Backup - Σε ανάπτυξη";
    public static final String SETTINGS_FEATURE = "Ρυθμίσεις - Σε ανάπτυξη";
    public static final String API_SETTINGS_FEATURE = "Ρυθμίσεις API - Σε ανάπτυξη";
    public static final String API_IMPORT_FEATURE = "Εισαγωγή από API - Σε ανάπτυξη";

    // MENU ITEMS
    public static final String MENU_FILE = "Αρχείο";
    public static final String MENU_VIEW = "Προβολή";
    public static final String MENU_TOOLS = "Εργαλεία";
    public static final String MENU_HELP = "Βοήθεια";

    // MENU ITEM LABELS
    public static final String MENU_NEW_CUSTOMER = "Νέος Πελάτης";
    public static final String MENU_EXIT = "Έξοδος";
    public static final String MENU_REFRESH = "Ανανέωση";
    public static final String MENU_EXPORT_DATA = "Εξαγωγή Δεδομένων";
    public static final String MENU_IMPORT_DATA = "Εισαγωγή Δεδομένων";
    public static final String MENU_IMPORT_API = "Εισαγωγή από API";
    public static final String MENU_BACKUP = "Δημιουργία Backup";
    public static final String MENU_SETTINGS = "Ρυθμίσεις";
    public static final String MENU_API_SETTINGS = "Ρυθμίσεις API";
    public static final String MENU_HELP_USAGE = "Οδηγίες Χρήσης";
    public static final String MENU_ABOUT = "Σχετικά";
    public static final String MENU_ACS_TRACKING_AUTO = "ACS Αυτοματισμός Tracking";
    public static final String MENU_ACS_DATE_AUTO = "ACS Αυτοματισμός Ημερομηνιών";
    public static final String MENU_ACS_TRACKING_DETAILS = "ACS Tracking Details Συγχρονισμός";

    // FORM LABELS
    public static final String LABEL_CUSTOMER_CODE = "Κωδικός Πελάτη";
    public static final String LABEL_CATEGORY = "Κατηγορία";
    public static final String LABEL_COMPANY_NAME = "Επωνυμία Εταιρίας";
    public static final String LABEL_AFM_COMPANY = "ΑΦΜ Εταιρίας";
    public static final String LABEL_DOY_COMPANY = "ΔΟΥ Εταιρίας";
    public static final String LABEL_LEGAL_REPRESENTATIVE = "Νόμιμος Εκπρόσωπος";
    public static final String LABEL_AFM_REPRESENTATIVE = "ΑΦΜ Νόμιμου Εκπροσώπου";

    private MessageConstants() {
        // Prevent instantiation
    }
}