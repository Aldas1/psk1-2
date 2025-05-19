package com.university.service;

import jakarta.ejb.AsyncResult;
import jakarta.ejb.Asynchronous;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.concurrent.Future;
import java.util.logging.Logger;

@Stateless
public class AsyncCalculationService {
    private static final Logger logger = Logger.getLogger(AsyncCalculationService.class.getName());

    // Cannot use request-scoped EntityManager in async methods
    @PersistenceContext
    private EntityManager em;

    @Asynchronous
    public Future<String> performLongCalculation(String input) {
        logger.info("Starting long calculation for input: " + input);

        try {
            // Simulate a long-running calculation
            Thread.sleep(5000);

            // Example calculation result
            String result = "Calculated result for " + input + ": " + (System.currentTimeMillis() % 1000);

            logger.info("Calculation completed: " + result);
            return new AsyncResult<>(result);
        } catch (InterruptedException e) {
            logger.severe("Calculation was interrupted: " + e.getMessage());
            return new AsyncResult<>("Error in calculation: " + e.getMessage());
        }
    }

    /**
     * Note: This method cannot join the caller's transaction because it's async
     * Async methods must use their own transaction scope
     */
    @Asynchronous
    public Future<Integer> countStudents() {
        logger.info("Starting async counting of students");

        try {
            // Simulate delay
            Thread.sleep(3000);

            // This will use a new transaction, not the caller's transaction
            int count = em.createQuery("SELECT COUNT(s) FROM Student s", Long.class)
                    .getSingleResult().intValue();

            logger.info("Async student count completed: " + count);
            return new AsyncResult<>(count);
        } catch (Exception e) {
            logger.severe("Error in async counting: " + e.getMessage());
            return new AsyncResult<>(-1);
        }
    }
}