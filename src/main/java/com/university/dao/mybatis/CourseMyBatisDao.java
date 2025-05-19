package com.university.dao.mybatis;

import com.university.mybatis.entity.CourseMB;
import com.university.mybatis.mapper.CourseMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.List;

@ApplicationScoped
public class CourseMyBatisDao {

    @Inject
    private CourseMapper courseMapper;

    public List<CourseMB> getAllCourses() {
        return courseMapper.getAllCourses();
    }

    public CourseMB getCourseById(Long id) {
        return courseMapper.getCourseById(id);
    }

    @Transactional
    public void saveCourse(CourseMB course) {
        if (course.getId() == null) {
            courseMapper.insertCourse(course);
        } else {
            courseMapper.updateCourse(course);
        }
    }

    @Transactional
    public void deleteCourse(Long id) {
        courseMapper.deleteCourse(id);
    }

    public List<CourseMB> getCoursesByFacultyId(Long facultyId) {
        return courseMapper.getCoursesByFacultyId(facultyId);
    }
}
