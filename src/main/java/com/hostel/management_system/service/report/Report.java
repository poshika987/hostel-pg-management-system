package com.hostel.management_system.service.report;

import java.time.LocalDate;

public interface Report {
    boolean supports(String reportType);

    ReportData generate(LocalDate startDate, LocalDate endDate);
}
