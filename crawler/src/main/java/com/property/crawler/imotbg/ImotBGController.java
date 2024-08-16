package com.property.crawler.imotbg;


import com.property.crawler.enums.Neighborhood;
import com.property.crawler.pdf.PdfService;
import com.property.crawler.property.PropertyDtoFormVersion;
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

    private static final Logger logger = LoggerFactory.getLogger(ImotBGController.class);

    @Autowired
    PdfService pdfService;

    @GetMapping
    public String getForm() {
        return "index";
    }

    @PostMapping("/uploadPdf")
    @ResponseBody
    public ResponseEntity<byte[]> handleFileUpload(@RequestParam("file") MultipartFile file) {
        try {
            byte[] updatedPdf = pdfService.getPdfWithFoundProperties(file);

            if (updatedPdf == null || updatedPdf.length == 0) {
                logger.warn("No properties found or failed to generate PDF.");
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
            }

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "Filled-property-template.pdf");

            return new ResponseEntity<>(updatedPdf, headers, HttpStatus.OK);
        } catch (TemplateProcessingException e) {
            logger.error("Template processing error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        } catch (IOException e) {
            logger.error("I/O error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        } catch (Exception e) {
            logger.error("Unexpected error: {}", e.getMessage());
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
                logger.warn("No properties found or failed to generate PDF.");
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
            }

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "Filled-property-template.pdf");

            return new ResponseEntity<>(pdf, headers, HttpStatus.OK);
        } catch (TemplateProcessingException e) {
            logger.error("Template processing error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        } catch (Exception e) {
            logger.error("Unexpected error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // - - - END FORM - - -

}
