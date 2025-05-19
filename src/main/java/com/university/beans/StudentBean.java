package com.university.beans;

import com.university.entity.Course;
import com.university.entity.Student;
import com.university.service.CourseService;
import com.university.service.StudentService;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;

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
        try {
            studentService.saveStudentJpa(newStudent);
            init(); // Refresh the list
            return "students?faces-redirect=true";
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            "Error saving student", e.getMessage()));
            return null;
        }
    }

    public String deleteStudent(Long id) {
        try {
            studentService.deleteStudentJpa(id);
            init(); // Refresh the list
            return "students?faces-redirect=true";
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            "Error deleting student", e.getMessage()));
            return null;
        }
    }

    public String editStudent(Student student) {
        // Reload the student to ensure all associations are loaded
        this.selectedStudent = studentService.getStudentByIdJpa(student.getId());
        return "editStudent?faces-redirect=true";
    }

    public String updateStudent() {
        try {
            studentService.saveStudentJpa(selectedStudent);
            return "students?faces-redirect=true";
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            "Error updating student", e.getMessage()));
            return null;
        }
    }

    public String enrollInCourse() {
        try {
            if (selectedStudent != null && selectedCourseId != null) {
                studentService.enrollStudentInCourseJpa(selectedStudent.getId(), selectedCourseId);
                // Refresh the selected student to show updated courses
                selectedStudent = studentService.getStudentByIdJpa(selectedStudent.getId());
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_INFO,
                                "Enrollment successful", "Student has been enrolled in the course."));
            }
            return "editStudent?faces-redirect=true";
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            "Error enrolling student", e.getMessage()));
            return null;
        }
    }

    public String removeFromCourse(Long courseId) {
        try {
            if (selectedStudent != null) {
                studentService.removeStudentFromCourseJpa(selectedStudent.getId(), courseId);
                // Refresh the selected student to show updated courses
                selectedStudent = studentService.getStudentByIdJpa(selectedStudent.getId());
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_INFO,
                                "Course removed", "Student has been removed from the course."));
            }
            return "editStudent?faces-redirect=true";
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            "Error removing course", e.getMessage()));
            return null;
        }
    }

    public List<Course> getAvailableCourses() {
        // Get all courses that the student is not already enrolled in
        List<Course> allCourses = courseService.getAllCoursesJpa();
        if (selectedStudent != null && selectedStudent.getCourses() != null) {
            List<Course> availableCourses = new ArrayList<>(allCourses);
            availableCourses.removeAll(selectedStudent.getCourses());
            return availableCourses;
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