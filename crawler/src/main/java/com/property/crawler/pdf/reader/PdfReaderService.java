package com.property.crawler.pdf.reader;


import com.property.crawler.property.PropertyDto;
import java.io.IOException;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public interface PdfReaderService {

    List<PropertyDto> getPropertiesBySearchCriteria(MultipartFile pdfFile) throws IOException;

}
