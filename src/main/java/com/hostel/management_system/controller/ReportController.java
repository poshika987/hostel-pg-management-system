package com.hostel.management_system.controller;

import com.hostel.management_system.service.ReportService;
import com.hostel.management_system.service.report.ReportData;
import com.hostel.management_system.service.report.export.ReportExport;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/reports")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping
    public String reports(@RequestParam(defaultValue = "payment") String type,
                          @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                          @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
                          Model model,
                          RedirectAttributes ra) {
        try {
            model.addAttribute("report", reportService.generate(type, startDate, endDate));
        } catch (IllegalArgumentException ex) {
            ra.addFlashAttribute("errorMessage", ex.getMessage());
            return "redirect:/reports";
        }
        addCommonAttributes(model, type, startDate, endDate);
        return "reports";
    }

    @GetMapping("/export/{format}")
    public ResponseEntity<byte[]> export(@PathVariable String format,
                                         @RequestParam(defaultValue = "payment") String type,
                                         @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                         @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        ReportData report = reportService.generate(type, startDate, endDate);
        ReportExport export = reportService.export(format, report);

        String filename = type.toLowerCase() + "-report." + export.extension();
        return ResponseEntity.ok()
                .contentType(export.mediaType())
                .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.attachment().filename(filename).build().toString())
                .body(export.content());
    }

    private void addCommonAttributes(Model model, String type, LocalDate startDate, LocalDate endDate) {
        model.addAttribute("types", List.of("payment", "complaint", "occupancy", "review"));
        model.addAttribute("selectedType", type);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
    }
}
