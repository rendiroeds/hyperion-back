package com.app.Hyperion.controller;

import com.app.Hyperion.utils.PDFReader;
import com.google.cloud.vision.v1.*;
import com.google.protobuf.ByteString;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/images")
public class ImageController {

    @PostMapping("/process")
    public ResponseEntity<Map<String, Object>> processTicket(@RequestParam("image") MultipartFile file) throws Exception {
        ByteString imgBytes = ByteString.copyFrom(file.getBytes());
        Image img = Image.newBuilder().setContent(imgBytes).build();
        Feature feat = Feature.newBuilder().setType(Feature.Type.DOCUMENT_TEXT_DETECTION).build();
        AnnotateImageRequest request = AnnotateImageRequest.newBuilder().addFeatures(feat).setImage(img).build();

        try (ImageAnnotatorClient vision = ImageAnnotatorClient.create()) {
            List<AnnotateImageResponse> responses = vision.batchAnnotateImages(List.of(request)).getResponsesList();
            String text = responses.get(0).getFullTextAnnotation().getText();

            Map<String, String> extractedData = extractTicketDetails(text);

            return ResponseEntity.ok(Map.of("ticketData", extractedData));
        }
    }

    @PostMapping("/procesar")
    public ResponseEntity<byte[]> procesarPDF(@RequestParam("file") MultipartFile file) {
        try {
            File tempFile = File.createTempFile("temp", ".pdf");
            file.transferTo(tempFile);

            // Procesa el PDF y devuelve un ZIP
            File zipFile = PDFReader.processPDF(tempFile);

            byte[] fileContent = Files.readAllBytes(zipFile.toPath());
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", "libro_iva_digital.zip");

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(fileContent);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }


    private Map<String, String> extractTicketDetails(String text) {
        Map<String, String> data = new HashMap<>();

        Pattern datePattern = Pattern.compile("\\d{2}/\\d{2}/\\d{4}");
        Pattern totalPattern = Pattern.compile("TOTAL\\s*\\$?(\\d+[.,]?\\d*)");
        Pattern ticketNumberPattern = Pattern.compile("TICKET\\s*N?°?\\s*(\\d+)");

        Matcher dateMatcher = datePattern.matcher(text);
        Matcher totalMatcher = totalPattern.matcher(text);
        Matcher ticketMatcher = ticketNumberPattern.matcher(text);

        if (dateMatcher.find()) data.put("fecha", dateMatcher.group());
        if (totalMatcher.find()) data.put("total", "$" + totalMatcher.group(1));
        if (ticketMatcher.find()) data.put("ticketNumber", ticketMatcher.group(1));

        return data;
    }
}
