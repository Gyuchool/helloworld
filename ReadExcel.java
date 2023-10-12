package org.example;

/**
 *  implementation 'org.apache.poi:poi:5.2.2'
 *  implementation 'org.apache.poi:poi-ooxml:5.2.2'
 */
import org.apache.poi.ss.usermodel.*;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

public class ReadExcel {
    private static final int SHEET_INDEX = 0;
    private static final String QUERY = "UPDATE place SET id = %d WHERE place.id = %d and type = 'store';";

    public static void main(String[] args) {

        final List<Response> RESPONSES = new ArrayList<>();
        try {
            FileInputStream file = new FileInputStream("/Users/gyucheol/Downloads/202309.xlsx");
            Workbook workbook = WorkbookFactory.create(file);

            Sheet sheet = workbook.getSheetAt(SHEET_INDEX);
            //행의 수
            int rows = sheet.getPhysicalNumberOfRows();

            for (int rowindex = 1; rowindex < rows; rowindex++) {
                //행을읽는다
                Row row = sheet.getRow(rowindex);
                if (row != null) {
                    //셀의 수
                    Cell first = row.getCell(0);
                    Cell second = row.getCell(2);
                    if (first == null || second == null) {
                        continue;
                    }

                    double id = first.getNumericCellValue();
                    double level = second.getNumericCellValue();
                    RESPONSES.add(new Response(id, level));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


        StringBuilder sb = new StringBuilder();
        for (var info: RESPONSES) {
            sb.append(QUERY.formatted(info.id(), info.level()));
        }
        String result = sb.toString();
        System.out.println(result);
    }
}
