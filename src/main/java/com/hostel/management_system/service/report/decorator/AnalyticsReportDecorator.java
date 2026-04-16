package com.hostel.management_system.service.report.decorator;

import com.hostel.management_system.service.report.Report;
import com.hostel.management_system.service.report.ReportData;

import java.time.LocalDate;
import java.util.LinkedHashMap;

public class AnalyticsReportDecorator extends ReportDecorator {

    public AnalyticsReportDecorator(Report delegate) {
        super(delegate);
    }

    @Override
    public ReportData generate(LocalDate startDate, LocalDate endDate) {
        ReportData report = super.generate(startDate, endDate);
        var summary = new LinkedHashMap<>(report.summary());
        summary.put("Dashboard Analytics", "Enabled");
        return new ReportData(report.title(), report.headers(), report.rows(), summary);
    }
}
