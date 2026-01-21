package com.app.Hyperion.utils;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import technology.tabula.*;
import technology.tabula.extractors.SpreadsheetExtractionAlgorithm;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class PDFReader {

    public static File processPDF(File pdfFile) throws IOException {
        String extractedText = extractTextFromPDF(pdfFile);

        String fecha = "";
        Map<Double, Double> deduccionesIVA = new HashMap<>();
        double totalDeduccionesIVA = 0;
        double totalOtrasRetenciones = 0;
        String razonSocialComprador = "";
        String cuitComprador = "";
        String coe = "";

        // Lógica para extraer los valores desde el texto
        deduccionesIVA = extractDeduccionesIVA(pdfFile);
        totalDeduccionesIVA = deduccionesIVA.values().stream().mapToDouble(Double::doubleValue).sum();
        totalOtrasRetenciones = extractTotalRetenciones(pdfFile);
        Map<String, Double> valores = extractOperacionValues(pdfFile);
        double subtotalOperacion = valores.get("Subtotal");
        double operacionConIVA = valores.get("OperacionConIVA");
        razonSocialComprador = extractRazonSocialComprador(extractedText);
        cuitComprador = extractCuitComprador(extractedText);
        coe = extractCOE(extractedText);
        fecha = extractFecha(extractedText);

        double totalRetenciones = totalDeduccionesIVA + totalOtrasRetenciones;

        // Crear un directorio temporal para nombres de archivos determinísticos
        File tempDir = Files.createTempDirectory("LIBRO_IVA_DIGITAL_").toFile();

        List<File> outputFiles = new ArrayList<>();

        // Ventas CBTE (1 línea)
        StringBuilder ventas = generateIVAVentas(fecha, coe, cuitComprador, razonSocialComprador, operacionConIVA, subtotalOperacion);
        ventas.append("\r\n");
        outputFiles.add(writeToFile(tempDir, "LIBRO_IVA_DIGITAL_VENTAS_CBTE.txt", ventas));

        // Compras CBTE (1 línea)
        StringBuilder compras = generateIVACompras(fecha, coe, cuitComprador, razonSocialComprador, totalRetenciones, totalOtrasRetenciones);
        compras.append("\r\n");
        outputFiles.add(writeToFile(tempDir, "LIBRO_IVA_DIGITAL_COMPRAS_CBTE.txt", compras));

        // Compras Alicuotas (N líneas en UN solo archivo)
        if (!deduccionesIVA.isEmpty()) {
            StringBuilder alicuotas = new StringBuilder();
            for (Map.Entry<Double, Double> entry : deduccionesIVA.entrySet()) {
                StringBuilder linea = generateIVAComprasAlicuotas(fecha, coe, cuitComprador, entry.getKey(), entry.getValue());
                alicuotas.append(linea).append("\r\n");
            }
            outputFiles.add(writeToFile(tempDir, "LIBRO_IVA_DIGITAL_COMPRAS_ALICUOTAS.txt", alicuotas));
        }

        return createZipFile(outputFiles);
    }

    private static File createZipFile(List<File> files) throws IOException {
        File zipFile = File.createTempFile("LIBRO_IVA_DIGITAL", ".zip");
        try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipFile))) {
            for (File file : files) {
                try (FileInputStream fis = new FileInputStream(file)) {
                    ZipEntry zipEntry = new ZipEntry(file.getName());
                    zos.putNextEntry(zipEntry);
                    Files.copy(file.toPath(), zos);
                    zos.closeEntry();
                }
            }
        }
        return zipFile;
    }

    private static File writeToFile(File directory, String desiredFileName, StringBuilder content) throws IOException {
        if (!directory.exists() && !directory.mkdirs()) {
            throw new IOException("No se pudo crear el directorio temporal para salida TXT");
        }
        File file = new File(directory, desiredFileName);
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "Windows-1252"))) {
            writer.write(content.toString());
        }
        return file;
    }

    private static StringBuilder generateIVAVentas(String fecha, String coe, String cuit, String razonSocial, double operacionConIVA, double subtotal) {
        return new StringBuilder()
                .append(String.format("%-8s", fecha))
                .append(String.format("%-3s", "033"))
                .append(String.format("%-5s", "00000"))
                .append(String.format("%-20s", coe))
                .append(String.format("%-20s", ""))
                .append(String.format("%-2s", "80"))
                .append(String.format("%-20s", cuit))
                .append(String.format("%-30s", razonSocial))
                .append(formatAmount(operacionConIVA))
                .append(formatAmount(subtotal))
                .append("000000000000000")
                .append("000000000000000")
                .append("000000000000000")
                .append("000000000000000")
                .append("000000000000000")
                .append("000000000000000")
                .append(String.format("%-3s", "PES"))
                .append(String.format("%-10s", "0000000001"))
                .append(String.format("%-1s", "1"))
                .append(String.format("%-1s", " "))
                .append("000000000000000")
                .append(String.format("%-8s", fecha));
    }

    private static StringBuilder generateIVACompras(String fecha, String coe, String cuit, String razonSocial, double totalRetenciones, double totalOtrasRetenciones) {
        return new StringBuilder()
                .append(String.format("%-8s", fecha))
                .append(String.format("%-3s", "033"))
                .append(String.format("%-5s", "00000"))
                .append(String.format("%-20s", coe))
                .append(String.format("%-16s", " "))
                .append(String.format("%-2s", "80"))
                .append(String.format("%-20s", cuit))
                .append(String.format("%-30s", razonSocial))
                .append(formatAmount(totalRetenciones))
                .append(formatAmount(totalOtrasRetenciones))
                .append("000000000000000")
                .append("000000000000000")
                .append("000000000000000")
                .append("000000000000000")
                .append("000000000000000")
                .append("000000000000000")
                .append(String.format("%-3s", "PES"))
                .append(String.format("%-10s", "0000000001"))
                .append(String.format("%-1s", "1"))
                .append(String.format("%-1s", " "))
                .append(String.format("%-15s", " "))
                .append(String.format("%-15s", " "))
                .append(String.format("%-11s", " "))
                .append(String.format("%-30s", " "))
                .append(String.format("%-15s", " "));
    }

    private static StringBuilder generateIVAComprasAlicuotas(String fecha, String coe, String cuit, double baseCalculo, double alicuota) {
        double impuesto = (baseCalculo * alicuota) / 100.0;
        return new StringBuilder()
                .append(String.format("%-3s", "033"))
                .append(String.format("%-5s", "00000"))
                .append(String.format("%-20s", coe))
                .append(String.format("%-2s", "80"))
                .append(String.format("%-20s", cuit))
                .append(formatAmount(baseCalculo))
                .append(String.format("%-4s", getAlicuotaCode(alicuota)))
                .append(formatAmount(impuesto));
    }

    private static String formatAmount(double amount) {
        long cents = Math.round(amount * 100.0);
        return String.format("%015d", cents);
    }

    private static String getAlicuotaCode(double porcentaje) {
        Map<Double, String> alicuotaMap = Map.of(
                0.00, "0003",
                10.50, "0004",
                21.00, "0005",
                27.00, "0006",
                5.00, "0008",
                2.50, "0009"
        );
        return alicuotaMap.getOrDefault(porcentaje, "0003");
    }


    private static String extractTextFromPDF(File pdfFile) throws IOException {
        try (PDDocument document = PDDocument.load(pdfFile)) {
            PDFTextStripper pdfStripper = new PDFTextStripper();
            return pdfStripper.getText(document);
        }
    }

    public static Map<Double, Double> extractDeduccionesIVA(File pdfFile) throws IOException {
        Map<Double, Double> deducciones = new HashMap<>();
        try (PDDocument document = PDDocument.load(pdfFile)) {
            ObjectExtractor extractor = new ObjectExtractor(document);
            PageIterator pageIterator = extractor.extract();
            SpreadsheetExtractionAlgorithm sea = new SpreadsheetExtractionAlgorithm();

            boolean insideDeduccionesTable = false;

            while (pageIterator.hasNext()) {
                Page page = pageIterator.next();
                List<Table> tables = sea.extract(page);

                for (Table table : tables) {
                    List<List<RectangularTextContainer>> rows = table.getRows();
                    boolean headersFound = false;
                    int baseCalculoIndex = -1;
                    int alicuotaIndex = -1;

                    for (List<RectangularTextContainer> row : rows) {
                        String rowText = row.stream().map(RectangularTextContainer::getText).reduce("", (a, b) -> a + " " + b).trim();

                        // Si encontramos "RETENCIONES", terminamos la búsqueda
                        if (insideDeduccionesTable && rowText.contains("RETENCIONES")) {
                            return deducciones; // Salimos inmediatamente
                        }

                        // Detectar el inicio de la tabla de deducciones
                        if (rowText.contains("DEDUCCIONES")) {
                            insideDeduccionesTable = true;
                            continue;
                        }

                        if (insideDeduccionesTable && !headersFound) {
                            // Buscar los índices de "Base Cálculo" y "% Alícuota"
                            for (int i = 0; i < row.size(); i++) {
                                String text = row.get(i).getText().toLowerCase();
                                if (text.contains("base cálculo") || text.contains("base calculo")) {
                                    baseCalculoIndex = i;
                                }
                                if (text.contains("alícuota")) {
                                    alicuotaIndex = i;
                                }
                            }
                            if (baseCalculoIndex != -1 && alicuotaIndex != -1) {
                                headersFound = true;
                            }
                            continue;
                        }

                        // Extraer valores de las filas después de encontrar los encabezados
                        if (insideDeduccionesTable && headersFound && row.size() > Math.max(baseCalculoIndex, alicuotaIndex)) {
                            try {
                                String baseCalculoStr = row.get(baseCalculoIndex).getText().trim();
                                String alicuotaStr = row.get(alicuotaIndex).getText().replace("%", "").replace(",", ".").trim();

                                if (!baseCalculoStr.isEmpty() && !alicuotaStr.isEmpty()) {
                                    double baseCalculo = parseAmountValue(baseCalculoStr);
                                    double alicuota = Double.parseDouble(alicuotaStr);

                                    if (alicuota > 0) {
                                        deducciones.put(baseCalculo, alicuota);
                                    }
                                }
                            } catch (NumberFormatException e) {
                                System.out.println("No se pudo convertir la fila: " + row);
                            }
                        }
                    }
                }
            }
        }
        return deducciones;
    }

    private static double parseAmountValue(String value) {
        // Normaliza distintos formatos: "$ 1.234,56" o "1,234.56" o "1.234,56" o "1234,56"
        String cleaned = value
                .replace("$", "")
                .replace(" ", "")
                .trim();

        // Si contiene coma y punto, asumimos que el separador decimal es la coma cuando está al final
        if (cleaned.matches(".*\\.\\d{3,}.*") && cleaned.matches(".*\\,\\d{1,2}$")) {
            cleaned = cleaned.replace(".", "").replace(",", ".");
        } else if (cleaned.contains(".")) {
            // Si hay puntos y no hay coma al final, quitar separadores de miles y dejar posible decimal
            // Casos: "1.234" (miles) o "1234.56" (decimal con punto)
            if (cleaned.matches(".*\\.\\d{3}$")) {
                cleaned = cleaned.replace(".", "");
            }
        } else if (cleaned.contains(",")) {
            // Si solo hay coma, tomarla como decimal
            cleaned = cleaned.replace(".", "").replace(",", ".");
        }

        return Double.parseDouble(cleaned);
    }


    public static double extractTotalRetenciones(File pdfFile) throws IOException {
        double totalRetenciones = 0;
        try (PDDocument document = PDDocument.load(pdfFile)) {
            ObjectExtractor extractor = new ObjectExtractor(document);
            PageIterator pageIterator = extractor.extract();
            SpreadsheetExtractionAlgorithm sea = new SpreadsheetExtractionAlgorithm();

            boolean insideDeduccionesTable = false;

            while (pageIterator.hasNext()) {
                Page page = pageIterator.next();
                List<Table> tables = sea.extract(page);

                for (Table table : tables) {
                    List<List<RectangularTextContainer>> rows = table.getRows();
                    boolean headersFound = false;
                    int baseCalculoIndex = -1;
                    int conceptoIndex = -1;

                    for (List<RectangularTextContainer> row : rows) {
                        String rowText = row.stream().map(RectangularTextContainer::getText).reduce("", (a, b) -> a + " " + b).trim();

                        // Si encontramos "RETENCIONES", terminamos la búsqueda
                        if (insideDeduccionesTable && rowText.contains("RETENCIONES")) {
                            return totalRetenciones; // Salimos inmediatamente
                        }

                        // Detectar el inicio de la tabla de deducciones
                        if (rowText.contains("DEDUCCIONES")) {
                            insideDeduccionesTable = true;
                            continue;
                        }

                        if (insideDeduccionesTable && !headersFound) {
                            // Buscar los índices de "Concepto" y "Base Cálculo"
                            for (int i = 0; i < row.size(); i++) {
                                String text = row.get(i).getText().toLowerCase();
                                if (text.contains("concepto")) {
                                    conceptoIndex = i;
                                }
                                if (text.contains("base cálculo") || text.contains("base calculo")) {
                                    baseCalculoIndex = i;
                                }
                            }
                            if (baseCalculoIndex != -1 && conceptoIndex != -1) {
                                headersFound = true;
                            }
                            continue;
                        }

                        // Extraer valores de las filas con "Otras Deducciones"
                        if (insideDeduccionesTable && headersFound && row.size() > Math.max(baseCalculoIndex, conceptoIndex)) {
                            try {
                                String conceptoStr = row.get(conceptoIndex).getText().toLowerCase();
                                String baseCalculoStr = row.get(baseCalculoIndex).getText().trim();

                                if (conceptoStr.contains("otras deducciones") && !baseCalculoStr.isEmpty()) {
                                    totalRetenciones += parseAmountValue(baseCalculoStr);
                                }
                            } catch (NumberFormatException e) {
                                System.out.println("No se pudo convertir la fila: " + row);
                            }
                        }
                    }
                }
            }
        }
        return totalRetenciones;
    }

    public static Map<String, Double> extractOperacionValues(File pdfFile) throws IOException {
        Map<String, Double> valoresOperacion = new HashMap<>();
        valoresOperacion.put("Subtotal", 0.0);
        valoresOperacion.put("OperacionConIVA", 0.0);

        try (PDDocument document = PDDocument.load(pdfFile)) {
            ObjectExtractor extractor = new ObjectExtractor(document);
            PageIterator pageIterator = extractor.extract();
            SpreadsheetExtractionAlgorithm sea = new SpreadsheetExtractionAlgorithm();

            boolean insideOperacionTable = false;

            while (pageIterator.hasNext()) {
                Page page = pageIterator.next();
                List<Table> tables = sea.extract(page);

                for (Table table : tables) {
                    List<List<RectangularTextContainer>> rows = table.getRows();
                    int subtotalIndex = -1;
                    int operacionConIVAIndex = -1;
                    boolean headersFound = false;

                    for (List<RectangularTextContainer> row : rows) {
                        String rowText = row.stream().map(RectangularTextContainer::getText).reduce("", (a, b) -> a + " " + b).trim();

                        // Detectar la tabla de operación
                        if (rowText.contains("OPERACIÓN")) {
                            insideOperacionTable = true;
                            continue;
                        }

                        if (insideOperacionTable && !headersFound) {
                            // Buscar los índices de las columnas "Subtotal" y "Operación c/IVA"
                            for (int i = 0; i < row.size(); i++) {
                                String text = row.get(i).getText().toLowerCase();
                                if (text.contains("subtotal")) {
                                    subtotalIndex = i;
                                }
                                if (text.contains("operación c/iva")) {
                                    operacionConIVAIndex = i;
                                }
                            }
                            if (subtotalIndex != -1 && operacionConIVAIndex != -1) {
                                headersFound = true;
                            }
                            continue;
                        }

                        // Extraer los valores de la única fila en la tabla "OPERACIÓN"
                        if (insideOperacionTable && headersFound && row.size() > Math.max(subtotalIndex, operacionConIVAIndex)) {
                            try {
                                if (subtotalIndex != -1) {
                                    String subtotalStr = row.get(subtotalIndex).getText().replace("$", "").replace(",", "").trim();
                                    if (!subtotalStr.isEmpty()) {
                                        valoresOperacion.put("Subtotal", parseAmountValue(subtotalStr));
                                    }
                                }

                                if (operacionConIVAIndex != -1) {
                                    String operacionConIVAStr = row.get(operacionConIVAIndex).getText().replace("$", "").replace(",", "").trim();
                                    if (!operacionConIVAStr.isEmpty()) {
                                        valoresOperacion.put("OperacionConIVA", parseAmountValue(operacionConIVAStr));
                                    }
                                }
                            } catch (NumberFormatException e) {
                                System.out.println("No se pudo convertir un valor de la tabla OPERACIÓN: " + row);
                            }
                            return valoresOperacion; // Salir inmediatamente después de encontrar los valores
                        }
                    }
                }
            }
        }
        return valoresOperacion;
    }



    private static String extractRazonSocialComprador(String text) {
        Pattern pattern = Pattern.compile("Razón Social:\s*(.*)");
        Matcher matcher = pattern.matcher(text);
        return matcher.find() ? matcher.group(1).trim() : "";
    }

    private static String extractCuitComprador(String text) {
        Pattern pattern = Pattern.compile("C\\.U\\.I\\.T\\.:\s*(\\d+)");
        Matcher matcher = pattern.matcher(text);
        return matcher.find() ? matcher.group(1).trim() : "";
    }

    private static String extractCOE(String text) {
        Pattern pattern = Pattern.compile("C\\.O\\.E\\.:\s*(\\d+)");
        Matcher matcher = pattern.matcher(text);
        return matcher.find() ? matcher.group(1).substring(Math.max(0, matcher.group(1).length() - 7)) : "";
    }

    private static String extractFecha(String text) {
        Pattern fechaPattern = Pattern.compile("\\d{2}/\\d{2}/\\d{4}");
        Matcher fechaMatcher = fechaPattern.matcher(text);
        return fechaMatcher.find() ? fechaMatcher.group().replace("/", "") : "00000000";
    }

}