package com.university.optimistic;

import com.university.entity.Faculty;
import com.university.service.FacultyService;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.OptimisticLockException;
import jakarta.persistence.PersistenceUnit;
import jakarta.transaction.Transactional;

@Named
@RequestScoped
public class OptimisticLockingDemo {

    @Inject
    private FacultyService facultyService;

    @PersistenceUnit
    private EntityManagerFactory emf;

    private Long facultyId;
    private String result;

    @PostConstruct
    public void init() {
        // Default to first faculty if available
        try {
            facultyId = facultyService.getAllFacultiesJpa().get(0).getId();
        } catch (Exception e) {
            // No faculties yet
        }
    }

    public String demonstrateOptimisticLocking() {
        if (facultyId == null) {
            result = "No faculty selected";
            return null;
        }

        try {
            // Start two parallel transactions
            simulateConcurrentEdit();
            result = "Optimistic locking demonstration completed successfully!";
        } catch (OptimisticLockException e) {
            result = "OptimisticLockException occurred: " + e.getMessage();

            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            "Optimistic Locking Error",
                            "The faculty was modified by another user. Please refresh and try again."));
        } catch (Exception e) {
            result = "Error: " + e.getMessage();
        }

        return null;
    }

    @Transactional
    public void simulateConcurrentEdit() throws Exception {
        // First transaction - get faculty
        EntityManager em1 = emf.createEntityManager();
        em1.getTransaction().begin();
        Faculty faculty1 = em1.find(Faculty.class, facultyId);

        // Second transaction - also get faculty and update it
        EntityManager em2 = emf.createEntityManager();
        em2.getTransaction().begin();
        Faculty faculty2 = em2.find(Faculty.class, facultyId);

        // Update in second transaction
        String originalName = faculty2.getName();
        faculty2.setName(originalName + " - Updated by user 2");
        em2.merge(faculty2);
        em2.getTransaction().commit();
        em2.close();

        // Update in first transaction (will fail due to optimistic locking)
        faculty1.setName(originalName + " - Updated by user 1");
        em1.merge(faculty1); // This will throw OptimisticLockException
        em1.getTransaction().commit(); // Won't reach here
        em1.close();
    }

    // Recovery method after OptimisticLockException
    @Transactional
    public void recoverFromOptimisticLock(Long facultyId, String newName) {
        try {
            EntityManager em = emf.createEntityManager();
            em.getTransaction().begin();

            // Reload the entity with fresh data
            Faculty faculty = em.find(Faculty.class, facultyId);
            faculty.setName(newName);

            em.merge(faculty);
            em.getTransaction().commit();
            em.close();

            result = "Recovery successful";
        } catch (Exception e) {
            result = "Recovery failed: " + e.getMessage();
        }
    }

    // Getters and setters
    public Long getFacultyId() {
        return facultyId;
    }

    public void setFacultyId(Long facultyId) {
        this.facultyId = facultyId;
    }

    public String getResult() {
        return result;
    }
}