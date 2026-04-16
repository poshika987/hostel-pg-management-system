package com.hostel.management_system.service.report;

import java.util.List;
import java.util.Map;

public record ReportData(String title,
                         List<String> headers,
                         List<List<String>> rows,
                         Map<String, String> summary) {
    public boolean hasRows() {
        return !rows.isEmpty();
    }
}
