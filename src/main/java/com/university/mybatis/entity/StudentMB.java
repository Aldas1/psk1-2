package com.university.mybatis.entity;

import lombok.Data;

import java.util.List;

@Data
public class StudentMB {
    private Long id;
    private String studentId;
    private String firstName;
    private String lastName;
    private String email;
    private List<CourseMB> courses; 
}
