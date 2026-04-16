package com.hostel.management_system.service;

import com.hostel.management_system.service.report.Report;
import com.hostel.management_system.service.report.ReportData;
import com.hostel.management_system.service.report.decorator.AnalyticsReportDecorator;
import com.hostel.management_system.service.report.export.ReportExport;
import com.hostel.management_system.service.report.export.ReportExporterFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Locale;

@Service
public class ReportService {

    private final List<Report> reports;
    private final ReportExporterFactory exporterFactory;

    public ReportService(List<Report> reports, ReportExporterFactory exporterFactory) {
        this.reports = reports;
        this.exporterFactory = exporterFactory;
    }

    public ReportData generate(String reportType, LocalDate startDate, LocalDate endDate) {
        validateRange(startDate, endDate);
        Report report = findReport(reportType);
        if (report.supports("occupancy")) {
            report = new AnalyticsReportDecorator(report);
        }
        return report.generate(startDate, endDate);
    }

    public ReportExport export(String format, ReportData report) {
        return exporterFactory.create(format).export(report);
    }

    private Report findReport(String reportType) {
        String normalized = reportType == null ? "" : reportType.trim().toLowerCase(Locale.ROOT);
        return reports.stream()
                .filter(report -> report.supports(normalized))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Choose a valid report type."));
    }

    private void validateRange(LocalDate startDate, LocalDate endDate) {
        if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Start date cannot be after end date.");
        }
    }
}
