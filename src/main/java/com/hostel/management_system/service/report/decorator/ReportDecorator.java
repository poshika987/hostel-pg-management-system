package com.hostel.management_system.service.report.decorator;

import com.hostel.management_system.service.report.Report;
import com.hostel.management_system.service.report.ReportData;

import java.time.LocalDate;

public abstract class ReportDecorator implements Report {

    private final Report delegate;

    protected ReportDecorator(Report delegate) {
        this.delegate = delegate;
    }

    @Override
    public boolean supports(String reportType) {
        return delegate.supports(reportType);
    }

    @Override
    public ReportData generate(LocalDate startDate, LocalDate endDate) {
        return delegate.generate(startDate, endDate);
    }
}
