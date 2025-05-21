package com.university.service.report;

import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.Map;

@ApplicationScoped
public class PlainTextReportGenerator implements ReportGenerator {

    @Override
    public String generateReport(String title, List<Map<String, Object>> data) {
        StringBuilder report = new StringBuilder();
        report.append(title).append("\n");
        report.append("=".repeat(title.length())).append("\n\n");

        for (Map<String, Object> row : data) {
            for (Map.Entry<String, Object> entry : row.entrySet()) {
                report.append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
            }
            report.append("\n");
        }

        return report.toString();
    }

    @Override
    public String getReportFormat() {
        return "Plain Text";
    }
}