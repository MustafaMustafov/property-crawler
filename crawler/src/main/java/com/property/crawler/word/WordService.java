package com.property.crawler.word;

import com.property.crawler.enums.PropertyType;
import com.property.crawler.pdf.reader.PdfReaderService;
import com.property.crawler.property.PropertyDto;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTShd;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STMerge;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class WordService {

    @Autowired
    PdfReaderService pdfReaderService;

    public byte[] createWordFileWithFoundProperties(MultipartFile multipartFile) throws IOException {
        // [foundProperties.. , searchProperty]
        List<PropertyDto> propertyList = pdfReaderService.getPropertiesBySearchCriteria(multipartFile);
        return generateWordDocument(propertyList);
    }

    public byte[] generateWordDocument(List<PropertyDto> propertyDtos) throws IOException {
        PropertyDto searchProperty = propertyDtos.get(propertyDtos.size() - 1);
        List<PropertyDto> foundProperties = new ArrayList<>();

        for (int k = 0; k < propertyDtos.size() - 1; k++) {
            foundProperties.add(propertyDtos.get(k));
        }
        // Create a new Word document
        XWPFDocument document = new XWPFDocument();

        // Create a table with 1 row and the specified number of columns
        XWPFTable table = document.createTable(17, 10);

        int[] rowIndexToFillBackgroundColor = {0, 3, 8};
        Arrays.stream(rowIndexToFillBackgroundColor).forEach(rowIndex -> {
            mergeCellsHorizontally(table, rowIndex, 0, 9);
            setBackgroundColorForACell(table, rowIndex, 0, "D3D3D3");
        });

        mergeCellsHorizontally(table, 1, 8, 9);
        mergeCellsHorizontally(table, 2, 8, 9);
        // Define headers
        String[] headersSearchProperty = {
            "Град",
            "Квартал",
            "Тип имот",
            "Чиста площ",
            "Обща площ",
            "Тухла/Панел",
            "Гараж/Паркомясто",
            "Етаж/Етажност",
            "Предложена цена"
        };

        String[] headersFoundProperties = {
            "Град",
            "Квартал",
            "Тип имот",
            "Чиста площ",
            "Обща площ",
            "Тухла/Панел",
            "Гараж/Паркомясто",
            "Етаж/Етажност",
            "Срок за продажба",
            "Продажна цена"
        };

        // Fill in the header row
        setRowHeaders(table, headersSearchProperty, 1);
        setRowHeaders(table, headersFoundProperties, 4);
        setRowHeaders(table, headersFoundProperties, 9);

        // Add an empty row for data entry
        XWPFTableRow dataRow2 = table.getRow(2);
        dataRow2.getCell(0).setText(searchProperty.getCity());
        dataRow2.getCell(1).setText(searchProperty.getLocation());
        dataRow2.getCell(2).setText(PropertyType.getValueById(searchProperty.getPropertyType()));
        dataRow2.getCell(3).setText(String.valueOf(searchProperty.getPropertySizeClean()));
        dataRow2.getCell(4).setText(String.valueOf(searchProperty.getPropertySize()));
        dataRow2.getCell(5).setText(searchProperty.getConstructionType());
        dataRow2.getCell(6).setText(searchProperty.getHasGarage());
        dataRow2.getCell(7).setText(searchProperty.getFloorInfo());
        dataRow2.getCell(8).setText(searchProperty.getPrice());

        // Optional: set column widths if needed
        //10,11,12,13,14,15,16
        AtomicInteger i = new AtomicInteger(10);
        foundProperties.forEach(propertyDto -> {
            XWPFTableRow dataRow = table.getRow(i.get());
            dataRow.getCell(0).setText(propertyDto.getCity());
            dataRow.getCell(1).setText(propertyDto.getLocation());
            dataRow.getCell(2).setText(PropertyType.getValueById(propertyDto.getPropertyType()));
            dataRow.getCell(3).setText(String.valueOf(propertyDto.getPropertySizeClean()));
            dataRow.getCell(4).setText(String.valueOf(propertyDto.getPropertySize()));
            dataRow.getCell(5).setText(propertyDto.getConstructionType());
            dataRow.getCell(6).setText(propertyDto.getHasGarage());
            dataRow.getCell(7).setText(propertyDto.getFloorInfo());
            dataRow.getCell(8).setText(
                propertyDto.getPublicationDateAndTime() != null ? propertyDto.getPublicationDateAndTime().toString()
                    : "");
            dataRow.getCell(9).setText(propertyDto.getPrice());
            i.getAndIncrement();
        });

        // Prepare the document for download
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        document.write(outputStream);
        document.close();

        // Convert the document to a byte array
        byte[] documentBytes = outputStream.toByteArray();

        return documentBytes;
    }

    private static void setBackgroundColorForACell(XWPFTable table, int rowNumber, int cellNumber, String colorCode) {
        CTShd ctShd = table.getRow(rowNumber).getCell(cellNumber).getCTTc().addNewTcPr().addNewShd();
        ctShd.setVal(org.openxmlformats.schemas.wordprocessingml.x2006.main.STShd.CLEAR);
        ctShd.setFill(colorCode);
    }

    private static void mergeCellsHorizontally(XWPFTable table, int row, int fromCol, int toCol) {
        XWPFTableRow tableRow = table.getRow(row);
        for (int colIndex = fromCol; colIndex <= toCol; colIndex++) {
            XWPFTableCell cell = tableRow.getCell(colIndex);
            if (colIndex == fromCol) {
                cell.getCTTc().addNewTcPr().addNewHMerge().setVal(STMerge.RESTART);
            } else {
                cell.getCTTc().addNewTcPr().addNewHMerge().setVal(STMerge.CONTINUE);
            }
        }
    }

    private static void setRowHeaders(XWPFTable table, String[] headers, int rowNumber) {
        if (rowNumber == 1) {
        }
        XWPFTableRow headerRow = table.getRow(rowNumber);
        for (int i = 0; i < headers.length; i++) {
            headerRow.getCell(i).setText(headers[i]);
        }
    }
}
