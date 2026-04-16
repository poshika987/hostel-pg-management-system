package com.hostel.management_system.service.report;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

public abstract class AbstractReport<T> implements Report {

    private static final DateTimeFormatter DATE_TIME = DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm");

    @Override
    public final ReportData generate(LocalDate startDate, LocalDate endDate) {
        var records = retrieveData(startDate, endDate);
        return new ReportData(title(), headers(), rows(records), summary(records));
    }

    protected abstract String title();

    protected abstract java.util.List<String> headers();

    protected abstract java.util.List<T> retrieveData(LocalDate startDate, LocalDate endDate);

    protected abstract java.util.List<java.util.List<String>> rows(java.util.List<T> records);

    protected abstract Map<String, String> summary(java.util.List<T> records);

    protected boolean inRange(LocalDateTime value, LocalDate startDate, LocalDate endDate) {
        if (value == null) {
            return startDate == null && endDate == null;
        }
        LocalDateTime start = startDate == null ? LocalDateTime.MIN : startDate.atStartOfDay();
        LocalDateTime end = endDate == null ? LocalDateTime.MAX : endDate.atTime(LocalTime.MAX);
        return !value.isBefore(start) && !value.isAfter(end);
    }

    protected Map<String, String> orderedSummary(String... values) {
        Map<String, String> summary = new LinkedHashMap<>();
        for (int i = 0; i + 1 < values.length; i += 2) {
            summary.put(values[i], values[i + 1]);
        }
        return summary;
    }

    protected String money(double amount) {
        return String.format(Locale.US, "%.2f", amount);
    }

    protected String format(LocalDateTime value) {
        return value == null ? "" : DATE_TIME.format(value);
    }

    protected String nullSafe(Object value) {
        return value == null ? "" : String.valueOf(value);
    }
}
