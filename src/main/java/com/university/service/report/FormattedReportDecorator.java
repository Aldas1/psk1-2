package com.university.service.report;

import jakarta.decorator.Decorator;
import jakarta.decorator.Delegate;
import jakarta.enterprise.inject.Any;
import jakarta.inject.Inject;
import java.util.List;
import java.util.Map;

@Decorator
public abstract class FormattedReportDecorator implements ReportGenerator {

    @Inject
    @Delegate
    @Any
    private ReportGenerator reportGenerator;

    @Override
    public String generateReport(String title, List<Map<String, Object>> data) {
        
        String baseReport = reportGenerator.generateReport(title, data);

        
        StringBuilder enhancedReport = new StringBuilder();
        enhancedReport.append("*** FORMATTED REPORT ***\n\n");
        enhancedReport.append("Report generated on: ").append(java.time.LocalDateTime.now()).append("\n");
        enhancedReport.append("Format: ").append(reportGenerator.getReportFormat()).append(" (Enhanced)\n");
        enhancedReport.append("Total records: ").append(data.size()).append("\n\n");
        enhancedReport.append("=".repeat(50)).append("\n\n");
        enhancedReport.append(baseReport);
        enhancedReport.append("\n").append("=".repeat(50)).append("\n");
        enhancedReport.append("*** END OF REPORT ***");

        return enhancedReport.toString();
    }

    @Override
    public String getReportFormat() {
        return reportGenerator.getReportFormat() + " (Enhanced with Decorator)";
    }
}