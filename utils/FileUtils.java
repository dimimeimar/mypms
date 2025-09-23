package org.example.utils;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public final class FileUtils {

    private FileUtils() {
    }

    public static File chooseExcelFileForReading() {
        return chooseFile("Επιλέξτε Excel αρχείο", JFileChooser.OPEN_DIALOG, "xlsx", "Excel αρχεία (*.xlsx)");
    }

    public static File chooseExcelFileForSaving() {
        return chooseFile("Αποθήκευση ως Excel", JFileChooser.SAVE_DIALOG, "xlsx", "Excel αρχεία (*.xlsx)");
    }

    private static File chooseFile(String title, int dialogType, String extension, String description) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle(title);
        fileChooser.setDialogType(dialogType);

        FileNameExtensionFilter filter = new FileNameExtensionFilter(description, extension);
        fileChooser.setFileFilter(filter);

        fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));

        int result = (dialogType == JFileChooser.OPEN_DIALOG)
                ? fileChooser.showOpenDialog(null)
                : fileChooser.showSaveDialog(null);

        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();

            if (dialogType == JFileChooser.SAVE_DIALOG && !selectedFile.getName().toLowerCase().endsWith("." + extension)) {
                selectedFile = new File(selectedFile.getAbsolutePath() + "." + extension);
            }

            return selectedFile;
        }

        return null;
    }

    public static boolean fileExists(String filePath) {
        return Files.exists(Paths.get(filePath));
    }

    public static boolean isReadable(File file) {
        return file != null && file.exists() && file.canRead();
    }

    public static boolean isWritable(File file) {
        if (file == null) return false;

        if (file.exists()) {
            return file.canWrite();
        }

        File parent = file.getParentFile();
        return parent != null && parent.exists() && parent.canWrite();
    }

    public static File createBackup(File originalFile) throws IOException {
        if (!isReadable(originalFile)) {
            throw new IOException("Το αρχείο δεν είναι προσβάσιμο: " + originalFile.getAbsolutePath());
        }

        String originalName = originalFile.getName();
        String extension = "";
        String nameWithoutExt = originalName;

        int lastDot = originalName.lastIndexOf('.');
        if (lastDot > 0) {
            extension = originalName.substring(lastDot);
            nameWithoutExt = originalName.substring(0, lastDot);
        }

        String timestamp = DateUtils.getNowFormatted().replace("/", "-").replace(":", "-");
        String backupName = nameWithoutExt + "_backup_" + timestamp + extension;

        File backupFile = new File(originalFile.getParent(), backupName);
        Files.copy(originalFile.toPath(), backupFile.toPath());

        return backupFile;
    }

    public static String readFileAsString(File file) throws IOException {
        return Files.readString(file.toPath(), StandardCharsets.UTF_8);
    }

    public static void writeStringToFile(File file, String content) throws IOException {
        Files.writeString(file.toPath(), content, StandardCharsets.UTF_8);
    }

    public static String getFileExtension(File file) {
        if (file == null) return "";

        String name = file.getName();
        int lastDot = name.lastIndexOf('.');

        return lastDot > 0 ? name.substring(lastDot + 1).toLowerCase() : "";
    }

    public static boolean isExcelFile(File file) {
        String ext = getFileExtension(file);
        return "xlsx".equals(ext) || "xls".equals(ext);
    }
}