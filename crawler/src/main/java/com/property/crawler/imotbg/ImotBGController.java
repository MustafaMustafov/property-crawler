package com.property.crawler.imotbg;


import com.property.crawler.enums.Neighborhood;
import com.property.crawler.pdf.PdfService;
import com.property.crawler.property.PropertyDtoFormVersion;
import java.util.List;
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

@Controller
public class ImotBGController {

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
            byte[] updatedPdf = pdfService.getPdfWithFindPropertyData(file);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "Filled-property-template.pdf");

            return new ResponseEntity<>(updatedPdf, headers, HttpStatus.OK);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

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
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "Filled-property-template.pdf");

            return new ResponseEntity<>(pdf, headers, HttpStatus.OK);
        } catch (Exception e) {
            throw new RuntimeException(e);

        }
    }
}
