package com.university.mybatis.mapper;

import com.university.mybatis.entity.CourseMB;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CourseMapper {
    List<CourseMB> getAllCourses();
    CourseMB getCourseById(@Param("id") Long id);
    void insertCourse(CourseMB course);
    void updateCourse(CourseMB course);
    void deleteCourse(@Param("id") Long id);
    List<CourseMB> getCoursesByFacultyId(@Param("facultyId") Long facultyId);
}
