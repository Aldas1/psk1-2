package com.university.beans;

import com.university.entity.Faculty;
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
public class FacultyBean implements Serializable {

    @Inject
    private FacultyService facultyService;

    private List<Faculty> faculties;
    private Faculty newFaculty;
    private Faculty selectedFaculty;
    private String persistenceType = "jpa"; // Default to JPA

    @PostConstruct
    public void init() {
        try {
            faculties = facultyService.getAllFacultiesJpa();
            newFaculty = new Faculty();
            selectedFaculty = new Faculty();
        } catch (Exception e) {
            handleException("Error initializing faculties", e);
            // Initialize with empty lists to prevent further errors
            faculties = new ArrayList<>();
            newFaculty = new Faculty();
            selectedFaculty = new Faculty();
        }
    }

    @Transactional
    public String saveFaculty() {
        try {
            facultyService.saveFacultyJpa(newFaculty);
            init(); // Refresh the list
            return "faculties?faces-redirect=true";
        } catch (Exception e) {
            handleException("Error saving faculty", e);
            return null;
        }
    }

    @Transactional
    public String deleteFaculty(Long id) {
        try {
            facultyService.deleteFacultyJpa(id);
            init(); // Refresh the list
            return "faculties?faces-redirect=true";
        } catch (Exception e) {
            handleException("Error deleting faculty", e);
            return null;
        }
    }

    @Transactional
    public String editFaculty(Faculty faculty) {
        try {
            // Load the fresh faculty with all associations
            this.selectedFaculty = facultyService.getFacultyByIdJpa(faculty.getId());
            return "editFaculty?faces-redirect=true";
        } catch (Exception e) {
            handleException("Error loading faculty for editing", e);
            return null;
        }
    }

    @Transactional
    public String updateFaculty() {
        try {
            facultyService.saveFacultyJpa(selectedFaculty);
            return "faculties?faces-redirect=true";
        } catch (Exception e) {
            handleException("Error updating faculty", e);
            return null;
        }
    }

    private void handleException(String message, Exception e) {
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        message, e.getMessage()));
        e.printStackTrace();
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