package com.hostel.management_system.service.report.export;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Locale;

@Component
public class ReportExporterFactory {

    private final List<ReportExporter> exporters;

    public ReportExporterFactory(List<ReportExporter> exporters) {
        this.exporters = exporters;
    }

    public ReportExporter create(String format) {
        String normalized = format == null ? "" : format.trim().toLowerCase(Locale.ROOT);
        return exporters.stream()
                .filter(exporter -> exporter.supports(normalized))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Choose PDF or Excel export."));
    }
}
