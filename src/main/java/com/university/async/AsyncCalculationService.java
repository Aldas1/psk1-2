package com.university.async;

import jakarta.ejb.AsyncResult;
import jakarta.ejb.Asynchronous;
import jakarta.ejb.Stateless;
import jakarta.inject.Named;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.concurrent.Future;
import java.util.logging.Logger;

@Named
@Stateless
public class AsyncCalculationService {

    private static final Logger LOGGER = Logger.getLogger(AsyncCalculationService.class.getName());

    @PersistenceContext
    private EntityManager em;

    @Asynchronous
    public Future<Integer> performLongCalculation(int input) {
        try {
            LOGGER.info("Starting long calculation with input: " + input);

            // Simulate long-running calculation
            Thread.sleep(5000);

            // Simple calculation for demonstration
            int result = input * input;

            LOGGER.info("Calculation completed. Result: " + result);
            return new AsyncResult<>(result);
        } catch (InterruptedException e) {
            LOGGER.severe("Calculation was interrupted: " + e.getMessage());
            return new AsyncResult<>(-1);
        }
    }

    // This method demonstrates why @RequestScoped EntityManager wouldn't work
    @Asynchronous
    public Future<Boolean> tryToUseEntityManager() {
        try {
            // Simulate delay
            Thread.sleep(1000);

            // Try to use EntityManager
            try {
                // This would fail if the EntityManager was @RequestScoped
                em.createQuery("SELECT f FROM Faculty f").getResultList();
                return new AsyncResult<>(true);
            } catch (Exception e) {
                LOGGER.severe("EntityManager operation failed: " + e.getMessage());
                return new AsyncResult<>(false);
            }
        } catch (InterruptedException e) {
            return new AsyncResult<>(false);
        }
    }
}