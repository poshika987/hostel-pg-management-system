package com.hostel.management_system.service.report.export;

import com.hostel.management_system.service.report.ReportData;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Component
public class PdfReportExporter implements ReportExporter {

    @Override
    public boolean supports(String format) {
        return "pdf".equals(format);
    }

    @Override
    public ReportExport export(ReportData report) {
        List<String> lines = new ArrayList<>();
        lines.add(report.title());
        report.summary().forEach((key, value) -> lines.add(key + ": " + value));
        lines.add("");
        lines.add(String.join(" | ", report.headers()));
        report.rows().forEach(row -> lines.add(String.join(" | ", row)));

        StringBuilder textCommands = new StringBuilder("BT /F1 10 Tf 40 780 Td 14 TL\n");
        int written = 0;
        for (String line : lines) {
            for (String part : wrap(line, 105)) {
                if (written >= 52) {
                    break;
                }
                textCommands.append('(').append(escapePdf(part)).append(") Tj T*\n");
                written++;
            }
            if (written >= 52) {
                break;
            }
        }
        textCommands.append("ET");

        byte[] stream = textCommands.toString().getBytes(StandardCharsets.UTF_8);
        List<byte[]> objects = List.of(
                "<< /Type /Catalog /Pages 2 0 R >>".getBytes(StandardCharsets.UTF_8),
                "<< /Type /Pages /Kids [3 0 R] /Count 1 >>".getBytes(StandardCharsets.UTF_8),
                "<< /Type /Page /Parent 2 0 R /MediaBox [0 0 595 842] /Resources << /Font << /F1 4 0 R >> >> /Contents 5 0 R >>".getBytes(StandardCharsets.UTF_8),
                "<< /Type /Font /Subtype /Type1 /BaseFont /Helvetica >>".getBytes(StandardCharsets.UTF_8),
                ("<< /Length " + stream.length + " >>\nstream\n" + textCommands + "\nendstream").getBytes(StandardCharsets.UTF_8)
        );

        ByteArrayOutputStream pdf = new ByteArrayOutputStream();
        write(pdf, "%PDF-1.4\n");
        List<Integer> offsets = new ArrayList<>();
        for (int i = 0; i < objects.size(); i++) {
            offsets.add(pdf.size());
            write(pdf, (i + 1) + " 0 obj\n");
            pdf.writeBytes(objects.get(i));
            write(pdf, "\nendobj\n");
        }
        int xref = pdf.size();
        write(pdf, "xref\n0 6\n0000000000 65535 f \n");
        offsets.forEach(offset -> write(pdf, String.format("%010d 00000 n \n", offset)));
        write(pdf, "trailer << /Size 6 /Root 1 0 R >>\nstartxref\n" + xref + "\n%%EOF");
        return new ReportExport(pdf.toByteArray(), MediaType.APPLICATION_PDF, "pdf");
    }

    private List<String> wrap(String text, int width) {
        if (text.length() <= width) {
            return List.of(text);
        }
        List<String> wrapped = new ArrayList<>();
        for (int i = 0; i < text.length(); i += width) {
            wrapped.add(text.substring(i, Math.min(text.length(), i + width)));
        }
        return wrapped;
    }

    private String escapePdf(String value) {
        return (value == null ? "" : value).replace("\\", "\\\\").replace("(", "\\(").replace(")", "\\)");
    }

    private void write(ByteArrayOutputStream output, String value) {
        output.writeBytes(value.getBytes(StandardCharsets.UTF_8));
    }
}
