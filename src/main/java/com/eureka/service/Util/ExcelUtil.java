package com.eureka.service.Util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.eureka.service.Entity.BaseEntity;
import com.eureka.service.Repository.BaseRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

public class ExcelUtil {

    @SuppressWarnings("unchecked")
    public static <T extends BaseEntity> List<T> fromExcel(final String filePath,
            final BaseRepository<T> baseRepository)
            throws org.apache.poi.openxml4j.exceptions.InvalidFormatException, IOException {
        List<T> list = new ArrayList<>();
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, String> fieldMapper = baseRepository.getFieldMapper();
        Class<?> dataType = baseRepository.getTableType();
        try {
            Workbook workbook = WorkbookFactory.create(new File(filePath));
            DataFormatter dataFormatter = new DataFormatter();
            Iterator<Sheet> sheetIterator = workbook.sheetIterator();
            while (sheetIterator.hasNext()) {
                Sheet sheet = sheetIterator.next();
                Iterator<Row> rowIterator = sheet.rowIterator();
                Map<Integer, String> headerMapper = new HashMap<>();
                Integer rowIndex = 0;
                while (rowIterator.hasNext()) {
                    Row row = rowIterator.next();
                    Iterator<Cell> cellIterator = row.cellIterator();
                    // Read header map
                    if (rowIndex == 0) {
                        while (cellIterator.hasNext()) {
                            Cell cell = cellIterator.next();
                            String headerValue = dataFormatter.formatCellValue(cell);
                            if (!fieldMapper.containsKey(headerValue)) {
                                System.out
                                        .println("The column headers are not valid. Please update your template file");
                                return null;
                            }
                            headerMapper.put(cell.getColumnIndex(), fieldMapper.get(headerValue));
                        }
                    } else {
                        Map<String, String> object = new HashMap<>();
                        while (cellIterator.hasNext()) {
                            Cell cell = cellIterator.next();
                            String cellValue = dataFormatter.formatCellValue(cell);
                            if (!headerMapper.containsKey(cell.getColumnIndex())) {
                                System.out.println("Can not parse this file (" + filePath + ")");
                                return null;
                            }
                            object.put(headerMapper.get(cell.getColumnIndex()), cellValue);
                        }
                        list.add((T) objectMapper.convertValue(object, dataType));
                    }
                    rowIndex += 1;
                }

            }
        } catch (EncryptedDocumentException | InvalidFormatException e) {
            System.out.println("Can not parse this file (" + filePath + "): " + e.getMessage());
        }
        return list;
    }

}