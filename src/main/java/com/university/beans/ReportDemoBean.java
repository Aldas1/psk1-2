package com.university.beans;

import com.university.entity.Student;
import com.university.service.StudentService;
import com.university.service.report.ReportGenerator;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Named
@RequestScoped
public class ReportDemoBean implements Serializable {

    @Inject
    private ReportGenerator reportGenerator;

    @Inject
    private StudentService studentService;

    private String generatedReport;

    public String generateStudentReport() {
        List<Student> students = studentService.getAllStudentsJpa();
        List<Map<String, Object>> reportData = new ArrayList<>();

        for (Student student : students) {
            Map<String, Object> row = new HashMap<>();
            row.put("ID", student.getId());
            row.put("Student ID", student.getStudentId());
            row.put("Name", student.getFirstName() + " " + student.getLastName());
            row.put("Email", student.getEmail());
            row.put("Courses", student.getCourses().size());
            reportData.add(row);
        }

        generatedReport = reportGenerator.generateReport("Student Summary Report", reportData);
        return null;
    }

    public String getReportFormat() {
        return reportGenerator.getReportFormat();
    }

    public String getGeneratedReport() {
        return generatedReport;
    }
}