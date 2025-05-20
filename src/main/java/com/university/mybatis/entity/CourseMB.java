package com.university.mybatis.entity;

import lombok.Data;

@Data
public class CourseMB {
    private Long id;
    private String courseCode;
    private String title;
    private Integer credits;
    private Long facultyId;
    private String facultyName; 
}
