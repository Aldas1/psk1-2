package com.university.beans;

import com.university.entity.Student;
import com.university.service.StudentService;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.persistence.EntityManager;
import jakarta.persistence.OptimisticLockException;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

import java.io.Serializable;
import java.util.logging.Logger;

@Named
@RequestScoped
public class OptLockDemoBean implements Serializable {
    private static final Logger logger = Logger.getLogger(OptLockDemoBean.class.getName());

    @PersistenceContext
    private EntityManager em;

    @Inject
    private StudentService studentService;

    private Student selectedStudent;
    private String newFirstName;
    private String newLastName;
    private String result;
    private boolean conflictOccurred = false;

    @PostConstruct
    public void init() {
        selectedStudent = new Student();
    }

    @Transactional
    public void loadStudent(Long id) {
        try {
            selectedStudent = studentService.getStudentByIdJpa(id);
            if (selectedStudent != null) {
                newFirstName = selectedStudent.getFirstName();
                newLastName = selectedStudent.getLastName();
                conflictOccurred = false;
                result = null;

                logger.info("Loaded student: " + selectedStudent.getFirstName() +
                        " " + selectedStudent.getLastName() +
                        ", version: " + selectedStudent.getVersion());
            }
        } catch (Exception e) {
            logger.severe("Error loading student: " + e.getMessage());
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            "Error loading student", e.getMessage()));
        }
    }

    /**
     * Direct approach to demonstrate optimistic locking
     */
    @Transactional
    public String simulateConcurrentUpdate() {
        if (selectedStudent == null || selectedStudent.getId() == null) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_WARN,
                            "No student selected", "Please select a student first"));
            return null;
        }

        try {
            logger.info("Starting optimistic locking simulation for student ID: " + selectedStudent.getId());

            // Step 1: Force a version increment in a separate transaction
            // Use direct SQL to avoid entity cache
            int updated = em.createNativeQuery(
                            "UPDATE student SET first_name = ?, version = version + 1 WHERE id = ?")
                    .setParameter(1, selectedStudent.getFirstName() + " [Modified]")
                    .setParameter(2, selectedStudent.getId())
                    .executeUpdate();

            if (updated > 0) {
                logger.info("Successfully updated student directly in the database");
            } else {
                logger.warning("Failed to update student directly in the database");
            }

            // Step 2: Try to update the student with our changes
            selectedStudent.setFirstName(newFirstName);
            selectedStudent.setLastName(newLastName);

            // Step 3: This should throw OptimisticLockException
            em.flush();

            // If we get here, no exception was thrown
            result = "The update succeeded without conflict. This is unusual - the optimistic locking " +
                    "mechanism did not detect the version change. Check your entity configuration.";
            conflictOccurred = false;

            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_WARN,
                            "No Conflict Detected", result));

            return null;

        } catch (OptimisticLockException e) {
            // This is what we expect to happen
            result = "OptimisticLockException detected! This demonstrates that JPA's optimistic locking " +
                    "mechanism works correctly. In a real application, you would need to refresh " +
                    "the entity and reapply your changes. Click 'Recover from Conflict' to see how this works.";
            conflictOccurred = true;

            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO,
                            "Optimistic Lock Exception",
                            "Conflict detected as expected! This demonstrates JPA's optimistic locking."));

            logger.info("OptimisticLockException demonstrated successfully: " + e.getMessage());
            return null;

        } catch (Exception e) {
            result = "Error: " + e.getMessage();
            conflictOccurred = false;

            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            "Error", result));

            logger.severe("Error in optimistic lock demo: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Demonstrates recovery from OptimisticLockException
     */
    @Transactional
    public String recoverFromConflict() {
        if (selectedStudent == null || selectedStudent.getId() == null) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_WARN,
                            "No student selected", "Please select a student first"));
            return null;
        }

        try {
            logger.info("Demonstrating recovery from optimistic lock conflict");

            // Step 1: Refresh the entity from the database
            // We need to get a completely fresh instance to avoid cache issues
            Student freshStudent = em.createQuery(
                            "SELECT s FROM Student s WHERE s.id = :id", Student.class)
                    .setParameter("id", selectedStudent.getId())
                    .getSingleResult();

            logger.info("Retrieved fresh student with version: " + freshStudent.getVersion());

            // Step 2: Apply our changes to the fresh entity
            freshStudent.setFirstName(newFirstName);
            freshStudent.setLastName(newLastName);

            // Step 3: Save the changes
            em.merge(freshStudent);
            em.flush();

            result = "Successfully recovered from the optimistic lock exception by refreshing the entity " +
                    "and reapplying our changes. The student is now updated with the version number " +
                    freshStudent.getVersion() + ".";

            // Update our reference
            selectedStudent = freshStudent;

            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO,
                            "Recovery Successful",
                            "Successfully recovered by refreshing the entity and reapplying changes."));

            // Reset the conflict flag
            conflictOccurred = false;

            return null;

        } catch (Exception e) {
            result = "Error during recovery: " + e.getMessage();

            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            "Recovery Failed", result));

            logger.severe("Error during conflict recovery: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    // Getters and setters
    public Student getSelectedStudent() {
        return selectedStudent;
    }

    public void setSelectedStudent(Student selectedStudent) {
        this.selectedStudent = selectedStudent;
    }

    public String getNewFirstName() {
        return newFirstName;
    }

    public void setNewFirstName(String newFirstName) {
        this.newFirstName = newFirstName;
    }

    public String getNewLastName() {
        return newLastName;
    }

    public void setNewLastName(String newLastName) {
        this.newLastName = newLastName;
    }

    public String getResult() {
        return result;
    }

    public boolean isConflictOccurred() {
        return conflictOccurred;
    }
}