package com.university.mybatis.entity;

import lombok.Data;

/**
 * MyBatis Course entity
 */
@Data
public class CourseMB {
    private Long id;
    private String courseCode;
    private String title;
    private Integer credits;
    private Long facultyId;
    private String facultyName; // For joining with faculty table
}
