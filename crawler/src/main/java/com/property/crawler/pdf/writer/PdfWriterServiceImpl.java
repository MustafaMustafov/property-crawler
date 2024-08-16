package com.property.crawler.pdf.writer;

import com.lowagie.text.pdf.BaseFont;
import com.property.crawler.property.PropertyDto;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.xhtmlrenderer.pdf.ITextFontResolver;
import org.xhtmlrenderer.pdf.ITextRenderer;

@Service
public class PdfWriterServiceImpl implements PdfWriterService {

    @Autowired
    ResourceLoader resourceLoader;
    @Autowired
    TemplateEngine templateEngine;

    @Override
    public byte[] generatePdf(List<PropertyDto> properties, PropertyDto searchProperty) throws Exception {
        if (!properties.isEmpty()) {
            Context context = new Context();

            context.setVariable("searchProperty", searchProperty);

            PropertyDto[] foundProperties = new PropertyDto[5];
            for (int i = 0; i < properties.size() - 1; i++) {
                    foundProperties[i] = properties.get(i);
            }

            context.setVariable("properties", foundProperties);
            String htmlContent = templateEngine.process("property", context);

            try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
                ITextRenderer renderer = new ITextRenderer();

                setupFont(renderer);

                renderer.setDocumentFromString(htmlContent);
                renderer.layout();
                renderer.createPDF(outputStream);
                return outputStream.toByteArray();
            }
        }
        return new byte[0];
    }

    private void setupFont(ITextRenderer renderer) throws IOException, com.lowagie.text.DocumentException {
        ITextFontResolver fontResolver = renderer.getFontResolver();
        Resource resource = resourceLoader.getResource("classpath:fonts/verdana.ttf");

        if (!resource.exists()) {
            throw new RuntimeException("Font file not found: " + resource.getFilename());
        }

        try (InputStream inputStream = resource.getInputStream()) {
            fontResolver.addFont("fonts/verdana.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
        }
    }
}
