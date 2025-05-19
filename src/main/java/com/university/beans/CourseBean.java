package com.university.beans;

import com.university.entity.Course;
import com.university.entity.Faculty;
import com.university.service.CourseService;
import com.university.service.FacultyService;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.Serializable;
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
        courses = courseService.getAllCoursesJpa();
        newCourse = new Course();
        selectedCourse = new Course();
    }

    public String saveCourse() {
        // If a faculty is selected, set it for the new course
        if (selectedFacultyId != null) {
            Faculty faculty = facultyService.getFacultyByIdJpa(selectedFacultyId);
            newCourse.setFaculty(faculty);
        }
        courseService.saveCourseJpa(newCourse);
        init(); // Refresh the list
        return "courses?faces-redirect=true";
    }

    public String deleteCourse(Long id) {
        courseService.deleteCourseJpa(id);
        init(); // Refresh the list
        return "courses?faces-redirect=true";
    }

    public String editCourse(Course course) {
        // Load the fresh course with all associations
        this.selectedCourse = courseService.getCourseByIdJpa(course.getId());
        // Set the selected faculty ID
        if (this.selectedCourse.getFaculty() != null) {
            this.selectedFacultyId = this.selectedCourse.getFaculty().getId();
        }
        return "editCourse?faces-redirect=true";
    }

    public String updateCourse() {
        // If a faculty is selected, set it for the course
        if (selectedFacultyId != null) {
            Faculty faculty = facultyService.getFacultyByIdJpa(selectedFacultyId);
            selectedCourse.setFaculty(faculty);
        }
        courseService.saveCourseJpa(selectedCourse);
        return "courses?faces-redirect=true";
    }

    public List<Faculty> getAvailableFaculties() {
        return facultyService.getAllFacultiesJpa();
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