package com.hostel.management_system.service.report.export;

import com.hostel.management_system.service.report.ReportData;

public interface ReportExporter {
    boolean supports(String format);

    ReportExport export(ReportData report);
}
