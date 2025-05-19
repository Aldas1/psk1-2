package com.university.service;

import com.university.entity.Student;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.persistence.OptimisticLockException;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

import java.util.logging.Level;
import java.util.logging.Logger;

@Stateless
public class OptimisticLockingDemoService {
    private static final Logger logger = Logger.getLogger(OptimisticLockingDemoService.class.getName());

    @PersistenceContext
    private EntityManager em;

    @Inject
    private StudentService studentService;

    /**
     * Demonstrates optimistic locking with proper exception handling
     */
    @Transactional
    public String demonstrateOptimisticLocking(Long studentId) {
        String result;

        try {
            logger.info("Starting optimistic locking demonstration for student ID: " + studentId);

            // Step 1: Load the student with optimistic locking
            Student student = em.find(Student.class, studentId, LockModeType.OPTIMISTIC);
            if (student == null) {
                return "Student not found";
            }

            String originalName = student.getFirstName();
            logger.info("Original student name: " + originalName);

            // Step 2: Simulate concurrent modification - this runs in a separate transaction
            studentService.simulateConcurrentModification(studentId);
            logger.info("Concurrent modification simulated");

            // Step 3: Modify the student in our transaction
            student.setFirstName(originalName + " - Updated in Demo");
            logger.info("Student updated in demo transaction: " + student.getFirstName());

            // Step 4: Flush changes - this should trigger OptimisticLockException
            em.flush();

            // If we get here, no OptimisticLockException occurred (unusual but possible)
            result = "No optimistic lock exception occurred. The demo still worked because the concurrent " +
                    "modification was applied, but no exception was thrown. Check the student list to see changes.";
            logger.info(result);

        } catch (OptimisticLockException e) {
            // This is expected - handle it properly
            logger.log(Level.INFO, "OptimisticLockException caught as expected: {0}", e.getMessage());
            result = handleLockingConflict(studentId);
        } catch (Exception e) {
            // Unexpected exception
            logger.log(Level.SEVERE, "Unexpected error in optimistic locking demo: {0}", e.getMessage());
            result = "Unexpected error: " + e.getMessage();
        }

        return result;
    }

    /**
     * Handles the optimistic locking conflict by refreshing the entity
     */
    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public String handleLockingConflict(Long studentId) {
        try {
            // Get a fresh copy of the student in a new transaction
            Student refreshedStudent = em.find(Student.class, studentId);

            if (refreshedStudent == null) {
                return "Student no longer exists";
            }

            // Apply our changes to the fresh entity
            String currentName = refreshedStudent.getFirstName();
            refreshedStudent.setFirstName(currentName + " - Recovered after conflict");

            // Save the changes
            em.merge(refreshedStudent);
            em.flush();

            return "Successfully handled OptimisticLockException: " +
                    "1) The original transaction was rolled back automatically. " +
                    "2) The EntityManager needed a fresh entity. " +
                    "3) We applied our changes to the fresh entity and saved it successfully.";

        } catch (Exception e) {
            logger.severe("Error in conflict resolution: " + e.getMessage());
            return "Error during conflict resolution: " + e.getMessage();
        }
    }
}