package com.hostel.management_system.service.report.export;

import org.springframework.http.MediaType;

public record ReportExport(byte[] content, MediaType mediaType, String extension) {
}
