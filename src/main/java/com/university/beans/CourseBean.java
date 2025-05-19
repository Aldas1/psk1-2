package com.university.beans;

import com.university.entity.Course;
import com.university.entity.Faculty;
import com.university.service.CourseService;
import com.university.service.FacultyService;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.transaction.Transactional;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Named
@RequestScoped
public class CourseBean implements Serializable {

    @Inject
    private CourseService courseService;

    @Inject
    private FacultyService facultyService;

    private List<Course> courses;
    private Course newCourse;
    private Course selectedCourse;
    private Long selectedFacultyId;
    private String persistenceType = "jpa"; // Default to JPA

    @PostConstruct
    public void init() {
        try {
            courses = courseService.getAllCoursesJpa();
            newCourse = new Course();
            selectedCourse = new Course();
        } catch (Exception e) {
            handleException("Error initializing courses", e);
            // Initialize with empty lists to prevent further errors
            courses = new ArrayList<>();
            newCourse = new Course();
            selectedCourse = new Course();
        }
    }

    @Transactional
    public String saveCourse() {
        try {
            // If a faculty is selected, set it for the new course
            if (selectedFacultyId != null) {
                Faculty faculty = facultyService.getFacultyByIdJpa(selectedFacultyId);
                newCourse.setFaculty(faculty);
            }
            courseService.saveCourseJpa(newCourse);
            init(); // Refresh the list
            return "courses?faces-redirect=true";
        } catch (Exception e) {
            handleException("Error saving course", e);
            return null;
        }
    }

    @Transactional
    public String deleteCourse(Long id) {
        try {
            courseService.deleteCourseJpa(id);
            init(); // Refresh the list
            return "courses?faces-redirect=true";
        } catch (Exception e) {
            handleException("Error deleting course", e);
            return null;
        }
    }

    @Transactional
    public String editCourse(Course course) {
        try {
            // Load the fresh course with all associations
            this.selectedCourse = courseService.getCourseByIdJpa(course.getId());
            // Set the selected faculty ID
            if (this.selectedCourse.getFaculty() != null) {
                this.selectedFacultyId = this.selectedCourse.getFaculty().getId();
            }
            return "editCourse?faces-redirect=true";
        } catch (Exception e) {
            handleException("Error loading course for editing", e);
            return null;
        }
    }

    @Transactional
    public String updateCourse() {
        try {
            // If a faculty is selected, set it for the course
            if (selectedFacultyId != null) {
                Faculty faculty = facultyService.getFacultyByIdJpa(selectedFacultyId);
                selectedCourse.setFaculty(faculty);
            }
            courseService.saveCourseJpa(selectedCourse);
            return "courses?faces-redirect=true";
        } catch (Exception e) {
            handleException("Error updating course", e);
            return null;
        }
    }

    @Transactional
    public List<Faculty> getAvailableFaculties() {
        try {
            return facultyService.getAllFacultiesJpa();
        } catch (Exception e) {
            handleException("Error loading faculties", e);
            return new ArrayList<>();
        }
    }

    private void handleException(String message, Exception e) {
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        message, e.getMessage()));
        e.printStackTrace();
    }

    // Getters and setters
    public List<Course> getCourses() {
        return courses;
    }

    public Course getNewCourse() {
        return newCourse;
    }

    public void setNewCourse(Course newCourse) {
        this.newCourse = newCourse;
    }

    public Course getSelectedCourse() {
        return selectedCourse;
    }

    public void setSelectedCourse(Course selectedCourse) {
        this.selectedCourse = selectedCourse;
    }

    public Long getSelectedFacultyId() {
        return selectedFacultyId;
    }

    public void setSelectedFacultyId(Long selectedFacultyId) {
        this.selectedFacultyId = selectedFacultyId;
    }

    public String getPersistenceType() {
        return persistenceType;
    }

    public void setPersistenceType(String persistenceType) {
        this.persistenceType = persistenceType;
    }
}