package org.example.utils;

import java.math.BigDecimal;
import java.text.Normalizer;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public final class StringUtils {

    private StringUtils() {
    }

    public static boolean isEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }

    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }

    public static String nullToEmpty(String str) {
        return str == null ? "" : str;
    }

    public static String emptyToNull(String str) {
        return isEmpty(str) ? null : str;
    }

    public static String trimAndUppercase(String str) {
        return isEmpty(str) ? "" : str.trim().toUpperCase();
    }

    public static String trimAndLowercase(String str) {
        return isEmpty(str) ? "" : str.trim().toLowerCase();
    }

    public static String trimAndCapitalize(String str) {
        if (isEmpty(str)) {
            return "";
        }

        String trimmed = str.trim();
        if (trimmed.length() == 1) {
            return trimmed.toUpperCase();
        }

        return trimmed.substring(0, 1).toUpperCase() + trimmed.substring(1).toLowerCase();
    }

    public static String cleanSpecialCharacters(String str) {
        if (isEmpty(str)) {
            return "";
        }
        return str.replaceAll("[^a-zA-ZΑ-Ωα-ωάέήίόύώΆΈΉΊΌΎΏ0-9\\s]", "").trim();
    }

    public static String keepOnlyNumbers(String str) {
        if (isEmpty(str)) {
            return "";
        }
        return str.replaceAll("[^0-9]", "");
    }

    public static String keepOnlyLetters(String str) {
        if (isEmpty(str)) {
            return "";
        }
        return str.replaceAll("[^a-zA-ZΑ-Ωα-ωάέήίόύώΆΈΉΊΌΎΏ\\s]", "").trim();
    }

    public static String removeGreekAccents(String str) {
        if (isEmpty(str)) {
            return "";
        }

        String normalized = Normalizer.normalize(str, Normalizer.Form.NFD);
        return normalized.replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
    }

    public static String truncate(String str, int maxLength) {
        if (isEmpty(str) || maxLength <= 0) {
            return "";
        }

        if (str.length() <= maxLength) {
            return str;
        }

        return str.substring(0, Math.max(0, maxLength - 3)) + "...";
    }

    public static String padLeft(String str, int totalLength) {
        return padLeft(str, totalLength, ' ');
    }

    public static String padLeft(String str, int totalLength, char paddingChar) {
        if (str == null) {
            str = "";
        }

        if (str.length() >= totalLength) {
            return str;
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < totalLength - str.length(); i++) {
            sb.append(paddingChar);
        }
        sb.append(str);

        return sb.toString();
    }

    public static String padRight(String str, int totalLength) {
        return padRight(str, totalLength, ' ');
    }

    public static String padRight(String str, int totalLength, char paddingChar) {
        if (str == null) {
            str = "";
        }

        if (str.length() >= totalLength) {
            return str;
        }

        StringBuilder sb = new StringBuilder(str);
        for (int i = 0; i < totalLength - str.length(); i++) {
            sb.append(paddingChar);
        }

        return sb.toString();
    }

    public static String repeat(String str, int times) {
        if (isEmpty(str) || times <= 0) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < times; i++) {
            sb.append(str);
        }

        return sb.toString();
    }

    public static String join(String[] array, String delimiter) {
        if (array == null || array.length == 0) {
            return "";
        }

        return Arrays.stream(array)
                .filter(StringUtils::isNotEmpty)
                .collect(Collectors.joining(delimiter));
    }

    public static String join(List<String> list, String delimiter) {
        if (list == null || list.isEmpty()) {
            return "";
        }

        return list.stream()
                .filter(StringUtils::isNotEmpty)
                .collect(Collectors.joining(delimiter));
    }

    public static String[] splitAndTrim(String str, String delimiter) {
        if (isEmpty(str)) {
            return new String[0];
        }

        return Arrays.stream(str.split(delimiter))
                .map(String::trim)
                .filter(StringUtils::isNotEmpty)
                .toArray(String[]::new);
    }

    public static BigDecimal toBigDecimal(String str) {
        if (isEmpty(str)) {
            return BigDecimal.ZERO;
        }

        try {
            String cleanStr = str.trim().replace(",", ".");
            cleanStr = cleanStr.replaceAll("[€$£¥]", "").trim();

            return new BigDecimal(cleanStr);
        } catch (NumberFormatException e) {
            return BigDecimal.ZERO;
        }
    }

    public static Integer toInteger(String str) {
        if (isEmpty(str)) {
            return null;
        }

        try {
            return Integer.parseInt(str.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public static Double toDouble(String str) {
        if (isEmpty(str)) {
            return null;
        }

        try {
            String cleanStr = str.trim().replace(",", ".");
            return Double.parseDouble(cleanStr);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public static boolean isNumeric(String str) {
        if (isEmpty(str)) {
            return false;
        }

        try {
            Double.parseDouble(str.trim().replace(",", "."));
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isInteger(String str) {
        if (isEmpty(str)) {
            return false;
        }

        try {
            Integer.parseInt(str.trim());
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static String mask(String str, char maskChar, int visibleChars) {
        if (isEmpty(str)) {
            return "";
        }

        if (str.length() <= visibleChars) {
            return str;
        }

        StringBuilder masked = new StringBuilder();

        int showStart = Math.min(visibleChars / 2, str.length());
        masked.append(str, 0, showStart);

        int maskLength = str.length() - visibleChars;
        masked.append(repeat(String.valueOf(maskChar), maskLength));

        int showEnd = visibleChars - showStart;
        if (showEnd > 0) {
            masked.append(str.substring(str.length() - showEnd));
        }

        return masked.toString();
    }

    public static String cleanCSVField(String field) {
        if (isEmpty(field)) {
            return "";
        }

        String cleaned = field.trim();

        if (cleaned.startsWith("\"") && cleaned.endsWith("\"")) {
            cleaned = cleaned.substring(1, cleaned.length() - 1);
        }

        cleaned = cleaned.replace("\"\"", "\"");

        return cleaned;
    }

    public static String formatForCSV(String field) {
        if (isEmpty(field)) {
            return "";
        }

        if (field.contains(",") || field.contains("\"") || field.contains("\n") || field.contains("\r")) {
            return "\"" + field.replace("\"", "\"\"") + "\"";
        }

        return field;
    }
}