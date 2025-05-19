package com.university.service;

import com.university.dao.jpa.StudentJpaDao;
import com.university.dao.mybatis.StudentMyBatisDao;
import com.university.entity.Student;
import com.university.mybatis.entity.StudentMB;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.List;

@ApplicationScoped
public class StudentService {

    @Inject
    private StudentJpaDao studentJpaDao;

    @Inject
    private StudentMyBatisDao studentMyBatisDao;

    // JPA methods
    public List<Student> getAllStudentsJpa() {
        return studentJpaDao.getAllStudents();
    }

    public Student getStudentByIdJpa(Long id) {
        return studentJpaDao.getStudentById(id);
    }

    @Transactional
    public void saveStudentJpa(Student student) {
        studentJpaDao.saveStudent(student);
    }

    @Transactional
    public void deleteStudentJpa(Long id) {
        studentJpaDao.deleteStudent(id);
    }

    @Transactional
    public void enrollStudentInCourseJpa(Long studentId, Long courseId) {
        studentJpaDao.enrollStudentInCourse(studentId, courseId);
    }

    @Transactional
    public void removeStudentFromCourseJpa(Long studentId, Long courseId) {
        studentJpaDao.removeStudentFromCourse(studentId, courseId);
    }

    public List<Student> getStudentsByCourseIdJpa(Long courseId) {
        return studentJpaDao.getStudentsByCourseId(courseId);
    }

    // MyBatis methods
    public List<StudentMB> getAllStudentsMyBatis() {
        return studentMyBatisDao.getAllStudents();
    }

    public StudentMB getStudentByIdMyBatis(Long id) {
        return studentMyBatisDao.getStudentById(id);
    }

    @Transactional
    public void saveStudentMyBatis(StudentMB student) {
        studentMyBatisDao.saveStudent(student);
    }

    @Transactional
    public void deleteStudentMyBatis(Long id) {
        studentMyBatisDao.deleteStudent(id);
    }

    @Transactional
    public void enrollStudentInCourseMyBatis(Long studentId, Long courseId) {
        studentMyBatisDao.enrollStudentInCourse(studentId, courseId);
    }

    @Transactional
    public void removeStudentFromCourseMyBatis(Long studentId, Long courseId) {
        studentMyBatisDao.removeStudentFromCourse(studentId, courseId);
    }

    public List<StudentMB> getStudentsByCourseIdMyBatis(Long courseId) {
        return studentMyBatisDao.getStudentsByCourseId(courseId);
    }
}
