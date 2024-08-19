package com.property.crawler.pdf.writer;

import com.property.crawler.property.PropertyDto;
import java.util.List;

public interface PdfWriterService {

    byte[] generatePdf(List<PropertyDto> property, PropertyDto searchProperty) throws Exception;

}
