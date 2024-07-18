package com.property.crawler.pdf;

import com.property.crawler.pdf.reader.PdfReaderService;
import com.property.crawler.pdf.writer.PdfWriterService;
import com.property.crawler.property.PropertyDto;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class PdfService {

    @Autowired
    PdfReaderService pdfReaderService;

    @Autowired
    PdfWriterService pdfWriterService;

    public byte[] getPdfWithFindPropertyData(MultipartFile multipartFile) throws IOException {
        List<PropertyDto> propertyList = pdfReaderService.getPropertiesBySearchCriteria(multipartFile);
        List<PropertyDto> foundProperties = new ArrayList<>();
        PropertyDto searchProperty = new PropertyDto();
        for (int i = 0; i < propertyList.size(); i++) {
            if (i != 3) {
                foundProperties.add(propertyList.get(i));
            }
            searchProperty = propertyList.get(i);
        }
        byte[] newUpdatedPdf = new byte[0];
        try {
            newUpdatedPdf = pdfWriterService.generatePdf(propertyList, searchProperty);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return newUpdatedPdf;
    }
}
