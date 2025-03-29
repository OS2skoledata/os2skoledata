package dk.digitalidentity.os2skoledata.controller.mvc.xlsview;

import dk.digitalidentity.os2skoledata.service.model.PrintStudentDTO;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.web.servlet.view.document.AbstractXlsxStreamingView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class StudentListXlsxView extends AbstractXlsxStreamingView {

    @Override
    protected void buildExcelDocument(Map<String, Object> model, Workbook workbook, HttpServletRequest request, HttpServletResponse response) throws Exception {
        List<PrintStudentDTO> students = (List<PrintStudentDTO>) model.get("students");
        boolean withPassword = (boolean) model.get("withPassword");
        String groupName = (String) model.get("groupName");
        ResourceBundleMessageSource messageSource = (ResourceBundleMessageSource) model.get("messagesBundle");
        Locale locale = (Locale) model.get("locale");

        // Setup shared resources
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);

        CellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setFont(headerFont);

        Sheet sheet = workbook.createSheet(messageSource.getMessage("html.students.xlsx.title", null, locale) + " " + groupName);

        ArrayList<String> headers = new ArrayList<>();
        headers.add("html.students.table.headers.person");
        headers.add("html.students.table.headers.username");
        headers.add("html.students.table.headers.unilogin");

        if (withPassword) {
            headers.add("html.students.table.headers.password");
        }

        createHeaderRow(messageSource, locale, sheet, headers, headerStyle);

        int row = 1;
        for (PrintStudentDTO student : students) {
            Row dataRow = sheet.createRow(row++);

            createCell(dataRow, 0, student.getName(), null);
            createCell(dataRow, 1, student.getUsername(), null);
            createCell(dataRow, 2, student.getUniId(), null);

            if (withPassword) {
                createCell(dataRow, 3, student.getPassword(), null);
            }
        }
    }

    private void createHeaderRow(ResourceBundleMessageSource messageSource, Locale locale, Sheet sheet, List<String> headers, CellStyle headerStyle) {
        Row headerRow = sheet.createRow(0);

        int column = 0;
        for (String header : headers) {
            String localeSpecificHeader = messageSource.getMessage(header, null, locale);
            createCell(headerRow, column++, localeSpecificHeader, headerStyle);
        }
    }

    private static void createCell(Row header, int column, String value, CellStyle style) {
        if (value != null && value.length() > 32767) {
            value = value.substring(0, 32767 - 3) + "...";
        }

        Cell cell = header.createCell(column);
        cell.setCellValue(value);

        if (style != null) {
            cell.setCellStyle(style);
        }
    }
}
