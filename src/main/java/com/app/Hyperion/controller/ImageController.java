package com.app.Hyperion.controller;

import com.app.Hyperion.utils.PDFReader;
import com.app.Hyperion.utils.TicketExtractor;
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
            headers.setContentType(MediaType.parseMediaType("application/zip"));
            headers.setContentDispositionFormData("attachment", "libro_iva_digital.zip");

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(fileContent);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/tickets")
    public ResponseEntity<byte[]> procesarTicketFiscal(@RequestParam("image") MultipartFile file) {
        try {
            // 1. Obtener texto del ticket usando Google Vision OCR
            ByteString imgBytes = ByteString.copyFrom(file.getBytes());
            Image img = Image.newBuilder().setContent(imgBytes).build();
            Feature feat = Feature.newBuilder().setType(Feature.Type.DOCUMENT_TEXT_DETECTION).build();
            AnnotateImageRequest request = AnnotateImageRequest.newBuilder().addFeatures(feat).setImage(img).build();

            String ticketText = "Detalles\n" +
                    "72.142\n" +
                    "X\n" +
                    "Valores Netos de Impuestos\n" +
                    "$ 83.0386 [14.54]\n" +
                    "IDC 1.1211 $/t. - ICL 18.3022 $/t.\n" +
                    "Shell V-Power Nafta\n" +
                    "(21.00) 5990.57\n" +
                    "Formas de Pago\n" +
                    "Efectivo\n" +
                    "Gravado : $\n" +
                    "Exento : $\n" +
                    "Perc.IB.CF.GRAL\n" +
                    "$ 9,346.97\n" +
                    "5990.57\n" +
                    "0.00\n" +
                    "221.75\n" +
                    "Perc.I.Brutos Bs.As.\n" +
                    "295.67\n" +
                    "Percepcion IVA\n" +
                    "179.72\n" +
                    "Impuesto DC Nafta V-Power\n" +
                    "80.88\n" +
                    "Impuesto Combu. Liquidos\n" +
                    "1,320.36\n" +
                    "I.V.A. Argentina 21 %\n" +
                    "TOTAL: $\n" +
                    "1,258.02\n" +
                    "9346.97\n" +
                    "C.A.E. :\n" +
                    "Fecha Vto:\n" +
                    "72110835345374\n" +
                    "22/03/2022\n" +
                    "CABA-Defensa y Protec Consum.TE Grat: 147\n" +
                    "Los cambios se efectuan en los mismos\n" +
                    "dias y horarios de venta al publico\n" +
                    "QR Factura Electrónica.";
           /* try (ImageAnnotatorClient vision = ImageAnnotatorClient.create()) {
                List<AnnotateImageResponse> responses = vision.batchAnnotateImages(List.of(request)).getResponsesList();
                ticketText = responses.get(0).getFullTextAnnotation().getText();
            }*/

            // 2. Extraer campos clave del texto del ticket
            Map<String, String> extracted = TicketExtractor.extractTicketFields(ticketText);

            // 3. Generar CSV para libro IVA compras (AFIP)
            String csv = TicketExtractor.generateCsvFromTicket(extracted);
            byte[] csvBytes = csv.getBytes("Windows-1252");

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType("text/csv"));
            headers.setContentDispositionFormData("attachment", "libro_iva_compras_ticket.csv");
            return ResponseEntity.ok().headers(headers).body(csvBytes);
        } catch (Exception e) {
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
