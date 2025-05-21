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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

@Named
@RequestScoped
public class StudentBean implements Serializable {
    private static final Logger logger = Logger.getLogger(StudentBean.class.getName());

    @Inject
    private StudentService studentService;

    @Inject
    private CourseService courseService;

    private Student courseViewStudent;
    private List<Student> students;
    private Student newStudent;
    private Student selectedStudent;
    private Long selectedCourseId;
    private String persistenceType = "jpa";

    // For the multi-select course checkboxes
    private List<Long> selectedCourseIds;
    @PostConstruct
    public void init() {
        try {
            students = studentService.getAllStudentsJpa();
            newStudent = createNewStudent();
            selectedStudent = createNewStudent();
            selectedCourseIds = new ArrayList<>();
        } catch (Exception e) {
            handleException("Error initializing data", e);

            students = new ArrayList<>();
            newStudent = createNewStudent();
            selectedStudent = createNewStudent();
            selectedCourseIds = new ArrayList<>();
        }
    }

    @Transactional
    public String saveStudent() {
        try {
            logger.info("Attempting to save new student: " + newStudent.getFirstName() + " " + newStudent.getLastName());
            logger.info("Selected course IDs: " + selectedCourseIds);

            // Make sure the version is null for new entities
            newStudent.setVersion(null);

            // First save the student without any courses
            studentService.saveStudentJpa(newStudent);

            // Now add courses if selected
            if (selectedCourseIds != null && !selectedCourseIds.isEmpty()) {
                for (Long courseId : selectedCourseIds) {
                    studentService.enrollStudentInCourseJpa(newStudent.getId(), courseId);
                }

                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_INFO,
                                "Success", "Student created successfully with " + selectedCourseIds.size() + " course enrollments"));
            } else {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_INFO,
                                "Success", "Student created successfully"));
            }

            // Reset the form and refresh data
            init();
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
            init(); 
            return "students?faces-redirect=true";
        } catch (Exception e) {
            handleException("Error deleting student", e);
            return null;
        }
    }

    @Transactional
    public String editStudent(Student student) {
        try {
            
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
            
            Student student = studentService.getStudentByIdJpa(id);
            

            
            studentService.simulateConcurrentModification(id);

            
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

    public List<Course> getAllCoursesForSelection() {
        try {
            return courseService.getAllCoursesJpa();
        } catch (Exception e) {
            handleException("Error loading courses for selection", e);
            return new ArrayList<>();
        }
    }

    public List<Student> getStudents() {
        return students;
    }

    public Student getNewStudent() {
        return newStudent;
    }

    public void setNewStudent(Student newStudent) {
        this.newStudent = newStudent;
    }

    private Student createNewStudent() {
        Student student = new Student();
        student.setId(null);
        student.setVersion(null);
        student.setCourses(new HashSet<>());
        return student;
    }

    public List<Long> getSelectedCourseIds() {
        return selectedCourseIds;
    }

    public void setSelectedCourseIds(List<Long> selectedCourseIds) {
        this.selectedCourseIds = selectedCourseIds;
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

    public Student getCourseViewStudent() {
        return courseViewStudent;
    }

    public void setCourseViewStudent(Student courseViewStudent) {
        this.courseViewStudent = courseViewStudent;
    }

    public void showCourses(Student student) {
        this.courseViewStudent = student;
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