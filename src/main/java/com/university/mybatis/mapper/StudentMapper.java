package com.university.mybatis.mapper;

import com.university.mybatis.entity.StudentMB;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.mybatis.cdi.Transactional;

import java.util.List;

@Mapper
@Transactional
public interface StudentMapper {
    List<StudentMB> getAllStudents();
    StudentMB getStudentById(@Param("id") Long id);
    void insertStudent(StudentMB student);
    void updateStudent(StudentMB student);
    void deleteStudent(@Param("id") Long id);
    void enrollStudentInCourse(@Param("studentId") Long studentId, @Param("courseId") Long courseId);
    void removeStudentFromCourse(@Param("studentId") Long studentId, @Param("courseId") Long courseId);
    List<StudentMB> getStudentsByCourseId(@Param("courseId") Long courseId);
}
