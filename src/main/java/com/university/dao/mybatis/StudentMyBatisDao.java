package com.university.dao.mybatis;

import com.university.mybatis.entity.StudentMB;
import com.university.mybatis.mapper.StudentMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.List;

@ApplicationScoped
public class StudentMyBatisDao {

    @Inject
    private StudentMapper studentMapper;

    public List<StudentMB> getAllStudents() {
        return studentMapper.getAllStudents();
    }

    public StudentMB getStudentById(Long id) {
        return studentMapper.getStudentById(id);
    }

    @Transactional
    public void saveStudent(StudentMB student) {
        if (student.getId() == null) {
            studentMapper.insertStudent(student);
        } else {
            studentMapper.updateStudent(student);
        }
    }

    @Transactional
    public void deleteStudent(Long id) {
        studentMapper.deleteStudent(id);
    }

    @Transactional
    public void enrollStudentInCourse(Long studentId, Long courseId) {
        studentMapper.enrollStudentInCourse(studentId, courseId);
    }

    @Transactional
    public void removeStudentFromCourse(Long studentId, Long courseId) {
        studentMapper.removeStudentFromCourse(studentId, courseId);
    }

    public List<StudentMB> getStudentsByCourseId(Long courseId) {
        return studentMapper.getStudentsByCourseId(courseId);
    }
}
