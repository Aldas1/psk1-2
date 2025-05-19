package com.university.service;

import com.university.dao.jpa.CourseJpaDao;
import com.university.dao.mybatis.CourseMyBatisDao;
import com.university.entity.Course;
import com.university.mybatis.entity.CourseMB;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.List;

@ApplicationScoped
public class CourseService {

    @Inject
    private CourseJpaDao courseJpaDao;

    @Inject
    private CourseMyBatisDao courseMyBatisDao;

    // JPA methods
    public List<Course> getAllCoursesJpa() {
        return courseJpaDao.getAllCourses();
    }

    public Course getCourseByIdJpa(Long id) {
        return courseJpaDao.getCourseById(id);
    }

    @Transactional
    public void saveCourseJpa(Course course) {
        courseJpaDao.saveCourse(course);
    }

    @Transactional
    public void deleteCourseJpa(Long id) {
        courseJpaDao.deleteCourse(id);
    }

    public List<Course> getCoursesByFacultyIdJpa(Long facultyId) {
        return courseJpaDao.getCoursesByFacultyId(facultyId);
    }

    // MyBatis methods
    public List<CourseMB> getAllCoursesMyBatis() {
        return courseMyBatisDao.getAllCourses();
    }

    public CourseMB getCourseByIdMyBatis(Long id) {
        return courseMyBatisDao.getCourseById(id);
    }

    @Transactional
    public void saveCourseMyBatis(CourseMB course) {
        courseMyBatisDao.saveCourse(course);
    }

    @Transactional
    public void deleteCourseMyBatis(Long id) {
        courseMyBatisDao.deleteCourse(id);
    }

    public List<CourseMB> getCoursesByFacultyIdMyBatis(Long facultyId) {
        return courseMyBatisDao.getCoursesByFacultyId(facultyId);
    }
}
