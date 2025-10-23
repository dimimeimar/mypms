package org.pms.antikatavoles.service;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.pms.antikatavoles.model.AntikatavoliFullView;

import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class AntikatavoliExcelExporter {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public void exportToExcel(List<AntikatavoliFullView> data, String filePath) throws Exception {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Αντικαταβολές");

        createHeaderRow(workbook, sheet);
        fillDataRows(sheet, data);
        autoSizeColumns(sheet);

        try (FileOutputStream fileOut = new FileOutputStream(filePath)) {
            workbook.write(fileOut);
        }

        workbook.close();
    }

    private void createHeaderRow(Workbook workbook, Sheet sheet) {
        Row headerRow = sheet.createRow(0);

        CellStyle headerStyle = workbook.createCellStyle();
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setFontHeightInPoints((short) 12);
        headerStyle.setFont(headerFont);
        headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headerStyle.setBorderBottom(BorderStyle.THIN);
        headerStyle.setBorderTop(BorderStyle.THIN);
        headerStyle.setBorderLeft(BorderStyle.THIN);
        headerStyle.setBorderRight(BorderStyle.THIN);
        headerStyle.setAlignment(HorizontalAlignment.CENTER);
        headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        String[] headers = {
                "Α/Α",
                "Ημ/νία Παραλαβής",
                "Ημ/νία Παράδοσης",
                "Courier",
                "Αρ. Αποστολής",
                "Αρ. Παραγγελίας",
                "Αντικαταβολή",
                "Παραλήπτης"
        };

        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }
    }

    private void fillDataRows(Sheet sheet, List<AntikatavoliFullView> data) {
        int rowNum = 1;

        for (AntikatavoliFullView view : data) {
            Row row = sheet.createRow(rowNum);

            row.createCell(0).setCellValue(rowNum);

            row.createCell(1).setCellValue(view.getImerominiaParalabis() != null ?
                    view.getImerominiaParalabis().format(DATE_FORMATTER) : "");

            row.createCell(2).setCellValue(view.getImerominiaParadosis() != null ?
                    view.getImerominiaParadosis().format(DATE_FORMATTER) : "");

            row.createCell(3).setCellValue(view.getCourier() != null ? view.getCourier() : "");

            row.createCell(4).setCellValue(view.getArithmosApostolis() != null ?
                    view.getArithmosApostolis() : "");

            row.createCell(5).setCellValue(view.getArithmosParaggelias() != null ?
                    view.getArithmosParaggelias() : "");

            BigDecimal antikatavoli = view.getAntikatavoli();
            if (antikatavoli != null) {
                row.createCell(6).setCellValue(antikatavoli.doubleValue());
            } else {
                row.createCell(6).setCellValue(0.0);
            }

            row.createCell(7).setCellValue(view.getParaliptis() != null ? view.getParaliptis() : "");

            rowNum++;
        }

        addTotalRow(sheet, rowNum, data);
    }

    private void addTotalRow(Sheet sheet, int rowNum, List<AntikatavoliFullView> data) {
        Row totalRow = sheet.createRow(rowNum + 1);

        Cell labelCell = totalRow.createCell(5);
        labelCell.setCellValue("ΣΥΝΟΛΟ:");

        BigDecimal total = BigDecimal.ZERO;
        for (AntikatavoliFullView view : data) {
            if (view.getAntikatavoli() != null) {
                total = total.add(view.getAntikatavoli());
            }
        }

        Cell totalCell = totalRow.createCell(6);
        totalCell.setCellValue(total.doubleValue());

        Workbook workbook = sheet.getWorkbook();
        CellStyle boldStyle = workbook.createCellStyle();
        Font boldFont = workbook.createFont();
        boldFont.setBold(true);
        boldStyle.setFont(boldFont);

        labelCell.setCellStyle(boldStyle);
        totalCell.setCellStyle(boldStyle);
    }

    private void autoSizeColumns(Sheet sheet) {
        for (int i = 0; i < 8; i++) {
            sheet.autoSizeColumn(i);
            sheet.setColumnWidth(i, sheet.getColumnWidth(i) + 500);
        }
    }
}