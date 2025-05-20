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

    
    @PersistenceContext
    private EntityManager em;

    @Asynchronous
    public Future<String> performLongCalculation(String input) {
        logger.info("Starting long calculation for input: " + input);

        try {
            
            Thread.sleep(5000);

            
            String result = "Calculated result for " + input + ": " + (System.currentTimeMillis() % 1000);

            logger.info("Calculation completed: " + result);
            return new AsyncResult<>(result);
        } catch (InterruptedException e) {
            logger.severe("Calculation was interrupted: " + e.getMessage());
            return new AsyncResult<>("Error in calculation: " + e.getMessage());
        }
    }

    @Asynchronous
    public Future<Integer> countStudents() {
        logger.info("Starting async counting of students");

        try {
            
            Thread.sleep(3000);

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