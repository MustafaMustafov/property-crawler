package com.property.crawler.pdf;

import com.property.crawler.imotbg.ImotBGService;
import com.property.crawler.pdf.reader.PdfReaderService;
import com.property.crawler.pdf.writer.PdfWriterService;
import com.property.crawler.property.PropertyDto;
import com.property.crawler.property.PropertyDtoFormVersion;
import com.property.crawler.property.mapper.PropertyDtoFormVersionToPropertyDto;
import java.io.IOException;
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

    @Autowired
    ImotBGService imotBGService;

    public byte[] getPdfWithFoundProperties(MultipartFile multipartFile) throws IOException {
        // [foundProperties.. , searchProperty]
        List<PropertyDto> propertyList = pdfReaderService.getPropertiesBySearchCriteria(multipartFile);
        PropertyDto searchProperty = propertyList.get(propertyList.size() - 1);
        byte[] newUpdatedPdf;
        try {
            newUpdatedPdf = pdfWriterService.generatePdf(propertyList, searchProperty);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return newUpdatedPdf;
    }

    public byte[] createPdfFromForm(PropertyDtoFormVersion dto) {

        List<PropertyDto> propertyList = imotBGService.getProperty(dto.getActionType(), dto.getPropertyType(),
            dto.getCity(),
            dto.getLocation(), dto.getPropertySize());

        PropertyDto searchDto = PropertyDtoFormVersionToPropertyDto.toPropertyDto(dto);

        byte[] newUpdatedPdf;
        try {
            newUpdatedPdf = pdfWriterService.generatePdf(propertyList, searchDto);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return newUpdatedPdf;
    }
}
