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
import jakarta.transaction.Transactional;

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
        try {
            students = studentService.getAllStudentsJpa();
            newStudent = new Student();
            selectedStudent = new Student();
        } catch (Exception e) {
            handleException("Error initializing data", e);
            // Initialize with empty lists to prevent further errors
            students = new ArrayList<>();
            newStudent = new Student();
            selectedStudent = new Student();
        }
    }

    @Transactional
    public String saveStudent() {
        try {
            studentService.saveStudentJpa(newStudent);
            init(); // Refresh the list
            return "students?faces-redirect=true";
        } catch (Exception e) {
            handleException("Error saving student", e);
            return null;
        }
    }

    @Transactional
    public String deleteStudent(Long id) {
        try {
            studentService.deleteStudentJpa(id);
            init(); // Refresh the list
            return "students?faces-redirect=true";
        } catch (Exception e) {
            handleException("Error deleting student", e);
            return null;
        }
    }

    @Transactional
    public String editStudent(Student student) {
        try {
            // Reload the student to ensure all associations are loaded
            this.selectedStudent = studentService.getStudentByIdJpa(student.getId());
            return "editStudent?faces-redirect=true";
        } catch (Exception e) {
            handleException("Error loading student for editing", e);
            return null;
        }
    }

    @Transactional
    public String updateStudent() {
        try {
            studentService.saveStudentJpa(selectedStudent);
            return "students?faces-redirect=true";
        } catch (Exception e) {
            handleException("Error updating student", e);
            return null;
        }
    }

    @Transactional
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
            handleException("Error enrolling student", e);
            return null;
        }
    }

    @Transactional
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
            handleException("Error removing course", e);
            return null;
        }
    }

    @Transactional
    public List<Course> getAvailableCourses() {
        try {
            // Get all courses that the student is not already enrolled in
            List<Course> allCourses = courseService.getAllCoursesJpa();
            if (selectedStudent != null && selectedStudent.getCourses() != null) {
                List<Course> availableCourses = new ArrayList<>(allCourses);
                availableCourses.removeAll(selectedStudent.getCourses());
                return availableCourses;
            }
            return allCourses;
        } catch (Exception e) {
            handleException("Error loading available courses", e);
            return new ArrayList<>();
        }
    }

    @Transactional
    public String demonstrateOptimisticLocking(Long id) {
        try {
            // This will now be implemented more safely
            Student student = studentService.getStudentByIdJpa(id);
            // Perform operations that might cause OptimisticLockException

            // For demo purposes, we'll simulate a concurrent modification
            studentService.simulateConcurrentModification(id);

            // Now try to update the same student
            student.setFirstName(student.getFirstName() + " - Updated");
            studentService.saveStudentJpa(student);

            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO,
                            "Optimistic locking demonstration completed", null));
        } catch (Exception e) {
            handleException("Optimistic Lock Exception Demo", e);
        }
        return null;
    }

    private void handleException(String message, Exception e) {
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        message, e.getMessage()));
        e.printStackTrace();
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