package com.university.service.report;

import java.util.List;
import java.util.Map;

public interface ReportGenerator {
    String generateReport(String title, List<Map<String, Object>> data);
    String getReportFormat();
}