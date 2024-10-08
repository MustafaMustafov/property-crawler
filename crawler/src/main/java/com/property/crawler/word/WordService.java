package com.property.crawler.word;

import com.property.crawler.entity.SearchRecord;
import com.property.crawler.enums.PropertyType;
import com.property.crawler.imotbg.ImotBGService;
import com.property.crawler.pdf.reader.PdfReaderService;
import com.property.crawler.property.PropertyDto;
import com.property.crawler.property.PropertyDtoFormVersion;
import com.property.crawler.property.mapper.PropertyDtoFormVersionToPropertyDto;
import com.property.crawler.repository.SearchRecordRepository;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.util.Units;
import org.apache.poi.xwpf.usermodel.Document;
import org.apache.poi.xwpf.usermodel.UnderlinePatterns;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFHyperlinkRun;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRelation;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTHyperlink;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTShd;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STMerge;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class WordService {

    private static final String[] HEADERS_SEARCH_PROPERTY = {
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

    private static final String[] HEADERS_FOUND_PROPERTIES = {
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
    @Autowired
    PdfReaderService pdfReaderService;

    @Autowired
    ImotBGService imotBGService;

    @Autowired
    ResourceLoader resourceLoader;

    @Autowired
    SearchRecordRepository searchRecordRepository;

    public byte[] createWordFileWithFoundPropertiesFromForm(PropertyDtoFormVersion dto)
        throws IOException, InvalidFormatException {
        List<PropertyDto> propertyList;

        propertyList = getPropertiesByLocations(dto);

        SearchRecord searchRecord = new SearchRecord();
        PropertyDto searchProperty = PropertyDtoFormVersionToPropertyDto.toPropertyDto(dto);
        if (!propertyList.isEmpty()) {

            propertyList.add(searchProperty);
            byte[] generatedWord = generateWordDocument(propertyList);

            searchRecord.setSearchObject(searchProperty.toString());
            searchRecord.setFile(null);
            searchRecord.setTimestamp(LocalDateTime.now());
            searchRecordRepository.save(searchRecord);

            return generatedWord;
        }

        searchRecord.setSearchObject("Nothing found for - " + searchProperty.toString());
        searchRecord.setFile(null);
        searchRecord.setTimestamp(LocalDateTime.now());
        searchRecordRepository.save(searchRecord);

        return new byte[0];
    }

    private List<PropertyDto> getPropertiesByLocations(PropertyDtoFormVersion dto) {
        // Executor for parallel requests
        ExecutorService executorService = Executors.newFixedThreadPool(dto.getNeighbourLocations().size() + 1);

        // List to hold futures for async property retrieval from neighbor locations
        List<CompletableFuture<List<PropertyDto>>> futures = new ArrayList<>();
        List<PropertyDto> finalPropertyList = new ArrayList<>();

        try {
            // First, search for properties in the main location
            List<PropertyDto> mainLocationProperties = imotBGService.getProperty(1, dto.getPropertyType(),
                dto.getCity(), dto.getMainLocation(), dto.getPropertySize());
            finalPropertyList.addAll(mainLocationProperties);

            // If we already have 5 or more properties, return them
            if (finalPropertyList.size() >= 5) {
                return getOnlyFiveProperties(finalPropertyList);
            }

            // If fewer than 5 properties are found, start searching in neighboring locations
            for (String location : dto.getNeighbourLocations()) {
                CompletableFuture<List<PropertyDto>> future = CompletableFuture.supplyAsync(
                    () -> imotBGService.getProperty(1, dto.getPropertyType(), dto.getCity(), location,
                        dto.getPropertySize()), executorService);
                futures.add(future);
            }

            // Wait for neighbor location futures to complete and aggregate results
            for (CompletableFuture<List<PropertyDto>> future : futures) {
                List<PropertyDto> result = future.get(); // Block until each future completes
                finalPropertyList.addAll(result);

                // Stop as soon as we have 5 or more properties
                if (finalPropertyList.size() >= 5) {
                    break;
                }
            }

        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace(); // Replace with appropriate logging or error handling
        } finally {
            // Ensure the executor is shut down after use
            executorService.shutdown();
        }

        // Return only the first 5 properties
        return getOnlyFiveProperties(finalPropertyList);
    }


    private static List<PropertyDto> getOnlyFiveProperties(List<PropertyDto> propertyList) {
        if (propertyList.size() > 5) {
            List<PropertyDto> propertyDtoListOfSize5 = new ArrayList<>();
            for (int i = 0; i < 5; i++) {
                propertyDtoListOfSize5.add(propertyList.get(i));
            }
            return propertyDtoListOfSize5;
        } else {
            return propertyList;
        }
    }

    public byte[] createWordFileWithFoundPropertiesFromPdf(MultipartFile multipartFile)
        throws IOException, InvalidFormatException {
        // [foundProperties.. , searchProperty]

        List<PropertyDto> propertyList = pdfReaderService.getPropertiesBySearchCriteria(multipartFile);
        SearchRecord searchRecord = new SearchRecord();
        if (!propertyList.isEmpty() && propertyList.size() > 1) {
            byte[] generatedWord = generateWordDocument(propertyList);

            searchRecord.setSearchObject(propertyList.get(propertyList.size() - 1).toString());
            searchRecord.setFile(null);
            searchRecord.setTimestamp(LocalDateTime.now());
            searchRecordRepository.save(searchRecord);

            return generatedWord;
        }

        searchRecord.setSearchObject("Nothing found for - " + propertyList.get(propertyList.size() - 1).toString());
        searchRecord.setTimestamp(LocalDateTime.now());
        searchRecord.setFile(new byte[0]);
        searchRecordRepository.save(searchRecord);

        return new byte[0];
    }

    public byte[] generateWordDocument(List<PropertyDto> propertyDtos) throws IOException, InvalidFormatException {
        PropertyDto searchProperty = propertyDtos.get(propertyDtos.size() - 1);
        List<PropertyDto> foundProperties = new ArrayList<>();

        for (int k = 0; k < propertyDtos.size() - 1; k++) {
            foundProperties.add(propertyDtos.get(k));
        }

        XWPFDocument document = new XWPFDocument();

        setLogoAsHeaderLine(document);
        setCreatedByAndCreatedForLine(document);

        XWPFTable table = document.createTable(17, 10);
        table.setWidth("100%");

        int[] rowIndexToFillBackgroundColor = {0, 3, 8};
        Arrays.stream(rowIndexToFillBackgroundColor).forEach(rowIndex -> {
            mergeCellsHorizontally(table, rowIndex, 0, 9);
            setBackgroundColorForACell(table, rowIndex, 0, "D3D3D3");
            setTextToMergedCellsRow(rowIndex, table);
        });

        mergeCellsHorizontally(table, 1, 8, 9);
        mergeCellsHorizontally(table, 2, 8, 9);

        setRowHeaders(table, HEADERS_SEARCH_PROPERTY, 1);
        setRowHeaders(table, HEADERS_FOUND_PROPERTIES, 4);
        setRowHeaders(table, HEADERS_FOUND_PROPERTIES, 9);

        setSecondRowForSearchProperty(table, searchProperty);
        setFoundPropertiesIntoTableRows(foundProperties, table);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        document.write(outputStream);
        document.close();

        return outputStream.toByteArray();
    }

    private static void setTextToMergedCellsRow(int rowIndex, XWPFTable table) {
        XWPFTableRow tableRow = table.getRow(rowIndex);
        XWPFTableCell cell = tableRow.getCell(0);

        XWPFParagraph paragraph = cell.getParagraphs().get(0);
        paragraph.setAlignment(org.apache.poi.xwpf.usermodel.ParagraphAlignment.CENTER);

        XWPFRun run = paragraph.createRun();
        run.setBold(true);
        if (rowIndex == 0) {
            run.setText("Вашият имот");
        } else if (rowIndex == 3) {
            run.setText("Продадени имоти");
        } else {
            run.setText("Имоти в продажба");
        }
    }

    private static void setFoundPropertiesIntoTableRows(List<PropertyDto> foundProperties, XWPFTable table) {
        // Optional: set column widths if needed
        //10,11,12,13,14,15,16
        AtomicInteger i = new AtomicInteger(10);
        foundProperties.forEach(propertyDto -> {
            XWPFTableRow dataRow = table.getRow(i.get());
            addHyperlinkToCityCell(propertyDto, dataRow);
            dataRow.getCell(1).setText(propertyDto.getMainLocation());
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
    }

    private static void addHyperlinkToCityCell(PropertyDto propertyDto, XWPFTableRow dataRow) {
        XWPFParagraph cityParagraph = dataRow.getCell(0).getParagraphs().get(0);
        XWPFHyperlinkRun hyperlinkrun = createHyperlinkRun(cityParagraph, propertyDto.getPropertyUrl());
        hyperlinkrun.setText(propertyDto.getCity());
        hyperlinkrun.setColor("0000FF");
        hyperlinkrun.setUnderline(UnderlinePatterns.SINGLE);
    }

    private static void setSecondRowForSearchProperty(XWPFTable table, PropertyDto searchProperty) {
        XWPFTableRow dataRow2 = table.getRow(2);
        dataRow2.getCell(0).setText(searchProperty.getCity());
        dataRow2.getCell(1).setText(searchProperty.getMainLocation());
        dataRow2.getCell(2).setText(PropertyType.getValueById(searchProperty.getPropertyType()));
        dataRow2.getCell(3).setText(String.valueOf(searchProperty.getPropertySizeClean()));
        dataRow2.getCell(4).setText(String.valueOf(searchProperty.getPropertySize()));
        dataRow2.getCell(5).setText(searchProperty.getConstructionType());
        dataRow2.getCell(6).setText(searchProperty.getHasGarage());
        dataRow2.getCell(7).setText(searchProperty.getFloorInfo());
        dataRow2.getCell(8).setText(searchProperty.getPrice());
    }

    private static XWPFHyperlinkRun createHyperlinkRun(XWPFParagraph paragraph, String uri) {
        String rId = paragraph.getDocument().getPackagePart().addExternalRelationship(
            uri,
            XWPFRelation.HYPERLINK.getRelation()
        ).getId();

        CTHyperlink cthyperLink = paragraph.getCTP().addNewHyperlink();
        cthyperLink.setId(rId);
        cthyperLink.addNewR();

        return new XWPFHyperlinkRun(
            cthyperLink,
            cthyperLink.getRArray(0),
            paragraph
        );
    }

    private static void setCreatedByAndCreatedForLine(XWPFDocument document) {
        XWPFParagraph textParagraph = document.createParagraph();
        XWPFRun textRun = textParagraph.createRun();
        textRun.setText(
            "За: .......................................................................................................................................................................................................................");
        textRun.setText(
            " Съставил: ......................................................................................................................................................");
    }

    private void setLogoAsHeaderLine(XWPFDocument document) throws IOException, InvalidFormatException {
        Resource resource = new ClassPathResource("era-logo.png");
        try (InputStream logoStream = resource.getInputStream()) {
            XWPFParagraph logoParagraph = document.createParagraph();
            XWPFRun logoRun = logoParagraph.createRun();
            logoRun.addPicture(logoStream, Document.PICTURE_TYPE_PNG, "era-logo.png",
                Units.toEMU(100), Units.toEMU(50));
            logoRun.setFontSize(38);
            logoRun.setText("Сравнителна оценка на пазара");
        }
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

    public byte[] getPropertySearchTemplate() throws IOException {
        Resource resource = new ClassPathResource("property-search-template.docx");
        try (InputStream templateStream = resource.getInputStream()) {
            return templateStream.readAllBytes();
        }
    }
}
