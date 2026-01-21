package com.app.Hyperion.utils;

import java.util.*;
import java.util.regex.*;
import java.text.*;

public class TicketExtractor {
    public static Map<String, String> extractTicketFields(String ocrText) {
        Map<String, String> fields = new HashMap<>();

        // Fecha (formato típico argentino dd/mm/yyyy)
        Matcher m = Pattern.compile("([ \t]|^)\\d{2}/\\d{2}/\\d{4}([ \t]|$)").matcher(ocrText);
        if (m.find()) fields.put("fecha", m.group().trim());
        else fields.put("fecha", "");

        // CUIT Emisor
        m = Pattern.compile("CUIT[ \t:]*([0-9]{2}-[0-9]{8}-[0-9])").matcher(ocrText);
        if (m.find()) fields.put("cuit", m.group(1));
        else fields.put("cuit", "");

        // Razón Social: cualquier línea que inicia con Razon Social, RS ó similar
        m = Pattern.compile("(?i)raz[oó]n social[: ]*(.*)").matcher(ocrText);
        if (m.find()) fields.put("razonSocial", m.group(1).trim());
        else fields.put("razonSocial", "");

        // Punto de venta
        m = Pattern.compile("Pto[ \\t\\-]*Vta\\.?[ \\t]*([0-9]{4})").matcher(ocrText);

        if (m.find()) fields.put("ptoVenta", m.group(1));
        else fields.put("ptoVenta", "");

        // Número comprobante / Tique
        m = Pattern.compile("Comp\\.?[ \t]*N?ro?\\.?[ \t]*([0-9]{8})").matcher(ocrText);
        if (m.find()) fields.put("nroComprobante", m.group(1));
        else fields.put("nroComprobante", "");

        // Total (última línea con TOTAL o primero grande)
        m = Pattern.compile("(?i)TOTAL[\\s\\$]*([0-9.,]+)\n").matcher(ocrText);
        if (m.find()) fields.put("total", m.group(1).replace(",", "."));
        else fields.put("total", "");

        // IVA discr. (puede ser 21%, 10.5%, etc.)
        m = Pattern.compile("IVA[ \t:]*([0-9.,]+)").matcher(ocrText);
        if (m.find()) fields.put("iva", m.group(1).replace(",", "."));
        else fields.put("iva", "");

        return fields;
    }

    public static String generateCsvFromTicket(Map<String, String> extracted) {
        // Encabezado típico para libro IVA compras (ajustar según tu necesidad exacta)
        String[] columns = {"fecha", "cuit", "razonSocial", "ptoVenta", "nroComprobante", "total", "iva"};
        StringBuilder sb = new StringBuilder();
        sb.append(String.join(",", columns)).append("\r\n");
        for (String col : columns) {
            sb.append(extracted.getOrDefault(col, "")).append(",");
        }
        sb.deleteCharAt(sb.length() - 1); // eliminar última coma
        sb.append("\r\n");
        return sb.toString();
    }
}
