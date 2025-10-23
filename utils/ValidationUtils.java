package org.pms.utils;

/**
 * Utility κλάση για validations - συγκεντρώνει όλες τις validations σε ένα μέρος
 */
public final class ValidationUtils {

    // REGEX PATTERNS
    private static final String EMAIL_PATTERN = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
    private static final String PHONE_PATTERN = "\\d{10}";
    private static final String AFM_PATTERN = "\\d{9}";
    private static final String POSTAL_CODE_PATTERN = "\\d{5}";

    private ValidationUtils() {
        // Prevent instantiation
    }

    /**
     * Έλεγχος εγκυρότητας ΑΦΜ (9 ψηφία)
     */
    public static boolean isValidAFM(String afm) {
        return afm != null && afm.trim().matches(AFM_PATTERN);
    }

    /**
     * Έλεγχος εγκυρότητας τηλεφώνου (10 ψηφία) - προαιρετικό πεδίο
     */
    public static boolean isValidPhone(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            return true; // Empty is valid (optional field)
        }
        return phone.trim().matches(PHONE_PATTERN);
    }

    /**
     * Έλεγχος εγκυρότητας email - προαιρετικό πεδίο
     */
    public static boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return true; // Empty is valid (optional field)
        }
        return email.trim().matches(EMAIL_PATTERN);
    }

    /**
     * Έλεγχος εγκυρότητας ταχυδρομικού κώδικα (5 ψηφία) - προαιρετικό πεδίο
     */
    public static boolean isValidPostalCode(String postalCode) {
        if (postalCode == null || postalCode.trim().isEmpty()) {
            return true; // Empty is valid (optional field)
        }
        return postalCode.trim().matches(POSTAL_CODE_PATTERN);
    }

    /**
     * Έλεγχος αν ένα string είναι κενό ή null
     */
    public static boolean isEmpty(String text) {
        return text == null || text.trim().isEmpty();
    }

    /**
     * Έλεγχος αν ένα string δεν είναι κενό ή null
     */
    public static boolean isNotEmpty(String text) {
        return !isEmpty(text);
    }

    /**
     * Έλεγχος μήκους τηλεφώνου (μέχρι 20 χαρακτήρες)
     */
    public static boolean isPhoneTooLong(String phone) {
        return phone != null && phone.trim().length() > 20;
    }

    /**
     * Έλεγχος αν ένα string περιέχει μόνο αριθμούς
     */
    public static boolean isNumeric(String text) {
        if (isEmpty(text)) {
            return false;
        }
        return text.trim().matches("\\d+");
    }

    /**
     * Έλεγχος αν ένα string περιέχει μόνο γράμματα και κενά
     */
    public static boolean isAlphabetic(String text) {
        if (isEmpty(text)) {
            return false;
        }
        return text.trim().matches("[a-zA-ZΑ-Ωα-ωάέήίόύώΆΈΉΊΌΎΏ\\s]+");
    }

    /**
     * Καθαρισμός και τυποποίηση ΑΦΜ (αφαίρεση κενών, μη αριθμητικών χαρακτήρων)
     */
    public static String cleanAFM(String afm) {
        if (isEmpty(afm)) {
            return "";
        }
        return afm.trim().replaceAll("[^\\d]", "");
    }

    /**
     * Καθαρισμός και τυποποίηση τηλεφώνου (αφαίρεση κενών, παύλων, παρενθέσεων)
     */
    public static String cleanPhone(String phone) {
        if (isEmpty(phone)) {
            return "";
        }
        return phone.trim().replaceAll("[^\\d]", "");
    }

    /**
     * Καθαρισμός email (trim και lowercase)
     */
    public static String cleanEmail(String email) {
        if (isEmpty(email)) {
            return "";
        }
        return email.trim().toLowerCase();
    }

    /**
     * Έλεγχος αν ένα BigDecimal είναι θετικό
     */
    public static boolean isPositiveAmount(java.math.BigDecimal amount) {
        return amount != null && amount.compareTo(java.math.BigDecimal.ZERO) > 0;
    }
}