package com.property.crawler.pdf.reader;

import com.property.crawler.enums.ConstructionType;
import com.property.crawler.enums.PropertyType;
import com.property.crawler.imotbg.ImotBGService;
import com.property.crawler.property.PropertyDto;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import technology.tabula.ObjectExtractor;
import technology.tabula.Page;
import technology.tabula.PageIterator;
import technology.tabula.RectangularTextContainer;
import technology.tabula.Table;
import technology.tabula.extractors.SpreadsheetExtractionAlgorithm;

@Service
public class PdfReaderServiceImpl implements PdfReaderService {

    @Autowired
    ImotBGService imotBGService;

    public List<PropertyDto> getPropertiesBySearchCriteria(MultipartFile pdfFile) throws IOException {
        byte[] pdfContent = pdfFile.getBytes();
        try (PDDocument document = PDDocument.load(new ByteArrayInputStream(pdfContent))) {
            PropertyDto propertyData = extractPropertyInformationToSearch(document);
            if (propertyData != null) {
                List<PropertyDto> propertyList = imotBGService.getProperty(1, propertyData.getPropertyType(),
                    propertyData.getCity(), "Център",
                    propertyData.getPropertySize());
                propertyList.add(propertyData);
                return propertyList;
            } else {
                throw new RuntimeException("No property data found or could not generate PDF.");
            }
        } catch (Exception e) {
            throw new RuntimeException("Error processing PDF: " + e.getMessage(), e);
        }
    }

    private static PropertyDto extractPropertyInformationToSearch(PDDocument document) throws IOException {
        PropertyDto propertyData = new PropertyDto();
        SpreadsheetExtractionAlgorithm sea = new SpreadsheetExtractionAlgorithm();
        PageIterator pi = new ObjectExtractor(document).extract();
        while (pi.hasNext()) {
            Page page = pi.next();
            List<? extends Table> tables = sea.extract(page);
            for (Table table : tables) {
                List<List<RectangularTextContainer>> rows = table.getRows();
                for (int i = 0; i < rows.size(); i++) {
                    List<RectangularTextContainer> cells = rows.get(i);
                    if (i == 2) { // Extract data from row 3 (index 2)
                        String rowText = cells.stream()
                            .map(RectangularTextContainer::getText)
                            .map(text -> text.replace("\r", " "))
                            .reduce((cell1, cell2) -> cell1 + "|" + cell2)
                            .orElse("");

                        String[] cellTexts = rowText.split("\\|");

                        if (cellTexts.length >= 8) {
                            propertyData.setCity(cellTexts[0].split(",")[0].trim().split(" ")[1]);
                            propertyData.setLocation(cellTexts[0].split(",")[1].trim());
                            propertyData.setPropertyType(PropertyType.getIdByValue(cellTexts[1].trim()));
                            propertyData.setPropertySizeClean(Integer.parseInt(cellTexts[2].trim()));
                            propertyData.setPropertySize(Integer.parseInt(cellTexts[3].trim()));
                            propertyData.setConstructionType(
                                ConstructionType.getConstructionValueByType(cellTexts[4].trim()));
                            propertyData.setHasGarage(cellTexts[5].trim());
                            propertyData.setFloorInfo(cellTexts[6].trim());
                            propertyData.setPrice(cellTexts[7].trim());
                        }
                        return propertyData;
                    }
                }
            }
        }
        return null;
    }
}
