package com.university.service;

import com.university.entity.Student;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.OptimisticLockException;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Stateless
public class OptimisticLockDemoService {
    private static final Logger logger = Logger.getLogger(OptimisticLockDemoService.class.getName());

    @PersistenceContext
    private EntityManager em;

    /**
     * Gets a student by ID
     */
    public Student getStudent(Long id) {
        logger.info("Finding student with ID: " + id);
        return em.find(Student.class, id);
    }

    /**
     * Updates a student normally (step 1 of the demo)
     */
    @Transactional
    public Student updateStudentFirstTime(Student student, String newName) {
        logger.info("Updating student first time. Original version: " + student.getVersion());

        student.setFirstName(newName);
        Student merged = em.merge(student);
        em.flush();

        logger.info("Student updated successfully. New version: " + merged.getVersion());
        return merged;
    }

    /**
     * FORCE updates the version number in the database directly (step 2 of the demo)
     * This guarantees a version conflict when the original entity is saved
     */
    @Transactional
    public boolean forceDirectDatabaseUpdate(Long studentId, String nameChange) {
        logger.info("Forcing direct database update for student ID: " + studentId);

        try {
            
            
            int rowsUpdated = em.createNativeQuery(
                            "UPDATE student SET first_name = first_name || ? , version = version + 1 WHERE id = ?")
                    .setParameter(1, nameChange)
                    .setParameter(2, studentId)
                    .executeUpdate();

            logger.info("Direct database update completed. Rows affected: " + rowsUpdated);
            return rowsUpdated > 0;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error in direct database update", e);
            return false;
        }
    }

    /**
     * Attempts to update a student with stale version (step 3 of the demo)
     * This should throw OptimisticLockException
     */
    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public String tryUpdateWithStaleVersion(Student student, String newName) {
        logger.info("Attempting to update student with potentially stale version: " + student.getVersion());

        try {
            student.setFirstName(newName);
            em.merge(student);
            em.flush();

            
            logger.warning("No OptimisticLockException was thrown! This is unexpected.");
            return "UPDATE SUCCEEDED - Optimistic locking appears to be NOT working properly! " +
                    "The update should have failed due to version conflict.";
        } catch (OptimisticLockException e) {
            
            logger.info("OptimisticLockException caught as expected: " + e.getMessage());
            return "OPTIMISTIC LOCK EXCEPTION - This is the expected behavior! " +
                    "The update failed because the entity was modified by another transaction.";
        } catch (Exception e) {
            
            logger.log(Level.SEVERE, "Unexpected error in update with stale version", e);
            return "ERROR: " + e.getMessage();
        }
    }

    /**
     * Demonstrates recovery from OptimisticLockException (step 4 of the demo)
     */
    @Transactional
    public String recoverFromOptimisticLock(Long studentId, String newName) {
        logger.info("Demonstrating recovery from OptimisticLockException for student ID: " + studentId);

        try {
            
            Student freshStudent = em.find(Student.class, studentId);

            if (freshStudent == null) {
                return "ERROR: Student not found with ID " + studentId;
            }

            Long currentVersion = freshStudent.getVersion();

            
            freshStudent.setFirstName(newName);
            em.merge(freshStudent);
            em.flush();

            
            return "RECOVERY SUCCESSFUL - Retrieved fresh entity with current version (" +
                    currentVersion + "), applied changes, and saved successfully. " +
                    "New version: " + freshStudent.getVersion();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error in recovery demonstration", e);
            return "ERROR IN RECOVERY: " + e.getMessage();
        }
    }

    @Transactional
    public void resetStudentName(Long studentId) {
        logger.info("Resetting student name for ID: " + studentId);

        try {
            
            Student student = em.find(Student.class, studentId);
            if (student == null) {
                logger.warning("Cannot reset student name - student not found with ID: " + studentId);
                return;
            }

            
            String currentName = student.getFirstName();
            String cleanName = currentName
                    .replace(" [Updated]", "")
                    .replace(" [Modified Directly]", "")
                    .replace(" [Should Fail]", "")
                    .replace(" [Recovered]", "")
                    .replace(" - Modified 1", "")
                    .replace(" - Modified 2", "")
                    .replace(" - Recovered", "");

            
            if (!currentName.equals(cleanName)) {
                student.setFirstName(cleanName);
                em.merge(student);
                em.flush();
                logger.info("Student name reset from '" + currentName + "' to '" + cleanName + "'");
            } else {
                logger.info("No need to reset student name - already clean");
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error resetting student name", e);
        }
    }

    /**
     * Resets all student names in the database
     */
    @Transactional
    public void resetAllStudentNames() {
        logger.info("Resetting all student names");

        try {
            
            List<Student> modifiedStudents = em.createQuery(
                    "SELECT s FROM Student s WHERE " +
                            "s.firstName LIKE '% [Updated]%' OR " +
                            "s.firstName LIKE '% [Modified Directly]%' OR " +
                            "s.firstName LIKE '% [Should Fail]%' OR " +
                            "s.firstName LIKE '% [Recovered]%' OR " +
                            "s.firstName LIKE '% - Modified%' OR " +
                            "s.firstName LIKE '% - Recovered%'",
                    Student.class).getResultList();

            logger.info("Found " + modifiedStudents.size() + " students with modified names");

            
            for (Student student : modifiedStudents) {
                resetStudentName(student.getId());
            }

            logger.info("All student names reset successfully");
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error resetting all student names", e);
        }
    }
}