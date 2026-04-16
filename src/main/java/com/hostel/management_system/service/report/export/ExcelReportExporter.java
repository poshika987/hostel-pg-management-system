package com.hostel.management_system.service.report.export;

import com.hostel.management_system.service.report.ReportData;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Component
public class ExcelReportExporter implements ReportExporter {

    @Override
    public boolean supports(String format) {
        return "excel".equals(format) || "xls".equals(format);
    }

    @Override
    public ReportExport export(ReportData report) {
        StringBuilder html = new StringBuilder();
        html.append("<html><head><meta charset=\"UTF-8\"></head><body>");
        html.append("<h1>").append(escapeHtml(report.title())).append("</h1>");
        html.append("<table border=\"1\"><tbody>");
        report.summary().forEach((key, value) -> html.append("<tr><th>")
                .append(escapeHtml(key))
                .append("</th><td>")
                .append(escapeHtml(value))
                .append("</td></tr>"));
        html.append("</tbody></table><br/>");
        html.append("<table border=\"1\"><thead><tr>");
        report.headers().forEach(header -> html.append("<th>").append(escapeHtml(header)).append("</th>"));
        html.append("</tr></thead><tbody>");
        for (var row : report.rows()) {
            html.append("<tr>");
            row.forEach(cell -> html.append("<td>").append(escapeHtml(cell)).append("</td>"));
            html.append("</tr>");
        }
        html.append("</tbody></table></body></html>");
        return new ReportExport(
                html.toString().getBytes(StandardCharsets.UTF_8),
                MediaType.parseMediaType("application/vnd.ms-excel"),
                "xls"
        );
    }

    private String escapeHtml(String value) {
        return (value == null ? "" : value)
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;");
    }
}
