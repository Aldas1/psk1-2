package com.university.beans;

import com.university.entity.Faculty;
import com.university.service.FacultyService;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.Serializable;
import java.util.List;

@Named
@RequestScoped
public class FacultyBean implements Serializable {

    @Inject
    private FacultyService facultyService;

    private List<Faculty> faculties;
    private Faculty newFaculty;
    private Faculty selectedFaculty;
    private String persistenceType = "jpa"; // Default to JPA

    @PostConstruct
    public void init() {
        faculties = facultyService.getAllFacultiesJpa();
        newFaculty = new Faculty();
        selectedFaculty = new Faculty();
    }

    public String saveFaculty() {
        facultyService.saveFacultyJpa(newFaculty);
        init(); // Refresh the list
        return "faculties?faces-redirect=true";
    }

    public String deleteFaculty(Long id) {
        facultyService.deleteFacultyJpa(id);
        init(); // Refresh the list
        return "faculties?faces-redirect=true";
    }

    public String editFaculty(Faculty faculty) {
        // Load the fresh faculty with all associations
        this.selectedFaculty = facultyService.getFacultyByIdJpa(faculty.getId());
        return "editFaculty?faces-redirect=true";
    }

    public String updateFaculty() {
        facultyService.saveFacultyJpa(selectedFaculty);
        return "faculties?faces-redirect=true";
    }

    // Getters and setters
    public List<Faculty> getFaculties() {
        return faculties;
    }

    public Faculty getNewFaculty() {
        return newFaculty;
    }

    public void setNewFaculty(Faculty newFaculty) {
        this.newFaculty = newFaculty;
    }

    public Faculty getSelectedFaculty() {
        return selectedFaculty;
    }

    public void setSelectedFaculty(Faculty selectedFaculty) {
        this.selectedFaculty = selectedFaculty;
    }

    public String getPersistenceType() {
        return persistenceType;
    }

    public void setPersistenceType(String persistenceType) {
        this.persistenceType = persistenceType;
    }
}
