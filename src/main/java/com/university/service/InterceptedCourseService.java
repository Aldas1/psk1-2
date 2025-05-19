package com.university.service;

import com.university.cdi.interceptor.Logged;
import com.university.dao.jpa.CourseJpaDao;
import com.university.dao.mybatis.CourseMyBatisDao;
import com.university.entity.Course;
import com.university.mybatis.entity.CourseMB;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.List;

@ApplicationScoped
@Logged // Apply the interceptor to the whole class
public class InterceptedCourseService {

    @Inject
    private CourseJpaDao courseJpaDao;

    // JPA methods
    public List<Course> getAllCourses() {
        return courseJpaDao.getAllCourses();
    }

    public Course getCourseById(Long id) {
        return courseJpaDao.getCourseById(id);
    }

    @Transactional
    public void saveCourse(Course course) {
        courseJpaDao.saveCourse(course);
    }

    @Transactional
    public void deleteCourse(Long id) {
        courseJpaDao.deleteCourse(id);
    }

    public List<Course> getCoursesByFacultyId(Long facultyId) {
        return courseJpaDao.getCoursesByFacultyId(facultyId);
    }
}