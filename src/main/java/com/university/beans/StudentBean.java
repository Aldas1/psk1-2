package com.university.beans;

import com.university.entity.Course;
import com.university.entity.Student;
import com.university.service.CourseService;
import com.university.service.StudentService;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.Serializable;
import java.util.List;

@Named
@RequestScoped
public class StudentBean implements Serializable {

    @Inject
    private StudentService studentService;

    @Inject
    private CourseService courseService;

    private List<Student> students;
    private Student newStudent;
    private Student selectedStudent;
    private Long selectedCourseId;
    private String persistenceType = "jpa"; // Default to JPA

    @PostConstruct
    public void init() {
        students = studentService.getAllStudentsJpa();
        newStudent = new Student();
        selectedStudent = new Student();
    }

    public String saveStudent() {
        studentService.saveStudentJpa(newStudent);
        init(); // Refresh the list
        return "students?faces-redirect=true";
    }

    public String deleteStudent(Long id) {
        studentService.deleteStudentJpa(id);
        init(); // Refresh the list
        return "students?faces-redirect=true";
    }

    public String editStudent(Student student) {
        this.selectedStudent = student;
        return "editStudent?faces-redirect=true";
    }

    public String updateStudent() {
        studentService.saveStudentJpa(selectedStudent);
        return "students?faces-redirect=true";
    }

    public String enrollInCourse() {
        if (selectedStudent != null && selectedCourseId != null) {
            studentService.enrollStudentInCourseJpa(selectedStudent.getId(), selectedCourseId);
            // Refresh the selected student to show updated courses
            selectedStudent = studentService.getStudentByIdJpa(selectedStudent.getId());
        }
        return "editStudent?faces-redirect=true";
    }

    public String removeFromCourse(Long courseId) {
        if (selectedStudent != null) {
            studentService.removeStudentFromCourseJpa(selectedStudent.getId(), courseId);
            // Refresh the selected student to show updated courses
            selectedStudent = studentService.getStudentByIdJpa(selectedStudent.getId());
        }
        return "editStudent?faces-redirect=true";
    }

    public List<Course> getAvailableCourses() {
        // Get all courses that the student is not already enrolled in
        List<Course> allCourses = courseService.getAllCoursesJpa();
        if (selectedStudent != null && selectedStudent.getCourses() != null) {
            allCourses.removeAll(selectedStudent.getCourses());
        }
        return allCourses;
    }

    // Getters and setters
    public List<Student> getStudents() {
        return students;
    }

    public Student getNewStudent() {
        return newStudent;
    }

    public void setNewStudent(Student newStudent) {
        this.newStudent = newStudent;
    }

    public Student getSelectedStudent() {
        return selectedStudent;
    }

    public void setSelectedStudent(Student selectedStudent) {
        this.selectedStudent = selectedStudent;
    }

    public Long getSelectedCourseId() {
        return selectedCourseId;
    }

    public void setSelectedCourseId(Long selectedCourseId) {
        this.selectedCourseId = selectedCourseId;
    }

    public String getPersistenceType() {
        return persistenceType;
    }

    public void setPersistenceType(String persistenceType) {
        this.persistenceType = persistenceType;
    }
}