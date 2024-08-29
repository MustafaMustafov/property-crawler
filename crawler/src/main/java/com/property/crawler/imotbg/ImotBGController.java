package com.property.crawler.imotbg;


import com.property.crawler.enums.Neighborhood;
import com.property.crawler.pdf.PdfService;
import com.property.crawler.property.PropertyDtoFormVersion;
import com.property.crawler.word.WordService;
import java.io.IOException;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.exceptions.TemplateProcessingException;

@Controller
public class ImotBGController {

    public static final String TEMPLATE_PROCESSING_ERROR = "Template processing error: {}";
    private static final Logger logger = LoggerFactory.getLogger(ImotBGController.class);
    public static final String NO_PROPERTIES_FOUND_OR_FAILED_TO_GENERATE_PDF = "No properties found or failed to generate PDF.";
    public static final String ATTACHMENT = "attachment";
    public static final String UNEXPECTED_ERROR = "Unexpected error: {}";
    public static final String I_O_ERROR = "I/O error: {}";

    @Autowired
    WordService wordService;
    @Autowired
    PdfService pdfService;

    @GetMapping
    public String getIndexPage() {
        return "index";
    }

    @GetMapping("/download-template")
    @ResponseBody
    public ResponseEntity<byte[]> getWordTemplate() throws IOException {
        byte[] wordDoc = wordService.getPropertySearchTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData(ATTACHMENT, "property-search-template.docx");

        return new ResponseEntity<>(wordDoc, headers, HttpStatus.OK);
    }

    @PostMapping("/uploadPdf")
    @ResponseBody
    public ResponseEntity<byte[]> handleFileUpload(@RequestParam("file") MultipartFile file) {
        try {
            byte[] updatedPdf = pdfService.getPdfWithFoundProperties(file);

            if (updatedPdf == null || updatedPdf.length == 0) {
                logger.warn(NO_PROPERTIES_FOUND_OR_FAILED_TO_GENERATE_PDF);
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
            }

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData(ATTACHMENT, "Filled-property-template.pdf");

            return new ResponseEntity<>(updatedPdf, headers, HttpStatus.OK);
        } catch (TemplateProcessingException e) {
            logger.error(TEMPLATE_PROCESSING_ERROR, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        } catch (IOException e) {
            logger.error(I_O_ERROR, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        } catch (Exception e) {
            logger.error(UNEXPECTED_ERROR, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // - - - FORM - - -
    @GetMapping("/form")
    public String getSearchingForm(Model model) {
        model.addAttribute("dto", new PropertyDtoFormVersion());
        model.addAttribute("neighborhoods", Neighborhood.values());

        return "form";
    }

    @GetMapping("/pdf")
    public String getAttachPdfVariant() {
        return "attach-pdf";
    }


    @GetMapping("/{townName}")
    @ResponseBody
    public ResponseEntity<List<String>> getNeighborhoodsByTownName(@PathVariable String townName) {
        return ResponseEntity.ok(Neighborhood.getNeighborhoodsByTown(townName));
    }


    @PostMapping("/generate-pdf")
    @ResponseBody
    public ResponseEntity<byte[]> generatePdf(@ModelAttribute PropertyDtoFormVersion dto) {
        try {
            byte[] pdf = pdfService.createPdfFromForm(dto);

            if (pdf == null || pdf.length == 0) {
                logger.warn(NO_PROPERTIES_FOUND_OR_FAILED_TO_GENERATE_PDF);
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
            }

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData(ATTACHMENT, "Filled-property-template.pdf");

            return new ResponseEntity<>(pdf, headers, HttpStatus.OK);
        } catch (TemplateProcessingException e) {
            logger.error(TEMPLATE_PROCESSING_ERROR, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        } catch (Exception e) {
            logger.error(UNEXPECTED_ERROR, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PostMapping("/generate-word-form")
    @ResponseBody
    public ResponseEntity<byte[]> generateWordFromForm(@ModelAttribute PropertyDtoFormVersion dto) {
        try {
            byte[] wordDoc = wordService.createWordFileWithFoundPropertiesFromForm(dto);

            if (wordDoc == null || wordDoc.length == 0) {
                logger.warn(NO_PROPERTIES_FOUND_OR_FAILED_TO_GENERATE_PDF);
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
            }

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData(ATTACHMENT, "document.docx");

            return new ResponseEntity<>(wordDoc, headers, HttpStatus.OK);
        } catch (TemplateProcessingException e) {
            logger.error(TEMPLATE_PROCESSING_ERROR, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        } catch (Exception e) {
            logger.error(UNEXPECTED_ERROR, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // - - - END FORM - - -

    @PostMapping("/generate-word")
    @ResponseBody
    public ResponseEntity<byte[]> handleFileUploadReturnWord(@RequestParam("file") MultipartFile file) {
        try {
            byte[] wordDoc = wordService.createWordFileWithFoundPropertiesFromPdf(file);

            if (wordDoc == null || wordDoc.length == 0) {
                logger.warn(NO_PROPERTIES_FOUND_OR_FAILED_TO_GENERATE_PDF);
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
            }

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData(ATTACHMENT, "document.docx");

            return new ResponseEntity<>(wordDoc, headers, HttpStatus.OK);
        } catch (TemplateProcessingException e) {
            logger.error(TEMPLATE_PROCESSING_ERROR, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        } catch (IOException e) {
            logger.error(I_O_ERROR, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        } catch (Exception e) {
            logger.error(UNEXPECTED_ERROR, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }


}
