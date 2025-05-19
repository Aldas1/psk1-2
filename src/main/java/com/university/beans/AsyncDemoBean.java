package com.university.beans;

import com.university.async.AsyncCalculationService;
import jakarta.annotation.Resource;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.transaction.Status;
import jakarta.transaction.UserTransaction;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Named
@SessionScoped
public class AsyncDemoBean implements Serializable {

    @Inject
    private AsyncCalculationService asyncService;

    @Resource
    private UserTransaction userTransaction;

    private Integer input;
    private List<AsyncTaskStatus> taskStatuses = new ArrayList<>();
    private String transactionTestResult;
    private String entityManagerTestResult;

    public void startCalculation() {
        if (input == null || input <= 0) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            "Invalid input", "Please enter a positive number"));
            return;
        }

        Future<Integer> future = asyncService.performLongCalculation(input);
        AsyncTaskStatus status = new AsyncTaskStatus(input, future);
        taskStatuses.add(status);

        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO,
                        "Task Started", "Calculation started with input: " + input));
    }

    public void checkResults() {
        boolean updated = false;

        for (AsyncTaskStatus status : taskStatuses) {
            if (!status.isComplete() && status.getFuture().isDone()) {
                try {
                    status.setResult(status.getFuture().get());
                    status.setComplete(true);
                    updated = true;
                } catch (InterruptedException | ExecutionException e) {
                    status.setError("Error: " + e.getMessage());
                    status.setComplete(true);
                    updated = true;
                }
            }
        }

        if (updated) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO,
                            "Tasks Updated", "Task results have been updated"));
        }
    }

    public void clearCompletedTasks() {
        taskStatuses.removeIf(AsyncTaskStatus::isComplete);
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO,
                        "Tasks Cleared", "Completed tasks have been cleared"));
    }

    public void testTransaction() {
        try {
            // Start a transaction
            userTransaction.begin();

            // Start async operation - it won't join this transaction
            Future<Integer> future = asyncService.performLongCalculation(99);

            // Try to get result with timeout
            try {
                Integer result = future.get(1, TimeUnit.SECONDS);
                transactionTestResult = "Task completed immediately with result: " + result;
            } catch (TimeoutException e) {
                // This is expected since the task takes 5 seconds
                transactionTestResult = "Async task is running in its own transaction context. " +
                        "Current transaction status: " + getTransactionStatusName(userTransaction.getStatus());
            } catch (Exception e) {
                transactionTestResult = "Error: " + e.getMessage();
            }

            // Complete our transaction
            userTransaction.commit();

        } catch (Exception e) {
            transactionTestResult = "Transaction error: " + e.getMessage();
        }
    }

    public void testEntityManager() {
        Future<Boolean> future = asyncService.tryToUseEntityManager();
        try {
            Boolean result = future.get();
            if (result) {
                entityManagerTestResult = "EntityManager successfully used in async method. " +
                        "This works because the EntityManager is container-managed (PersistenceContext), not RequestScoped.";
            } else {
                entityManagerTestResult = "EntityManager failed in async method.";
            }
        } catch (Exception e) {
            entityManagerTestResult = "Error: " + e.getMessage();
        }
    }

    private String getTransactionStatusName(int status) {
        switch (status) {
            case Status.STATUS_ACTIVE: return "ACTIVE";
            case Status.STATUS_COMMITTED: return "COMMITTED";
            case Status.STATUS_COMMITTING: return "COMMITTING";
            case Status.STATUS_MARKED_ROLLBACK: return "MARKED_ROLLBACK";
            case Status.STATUS_NO_TRANSACTION: return "NO_TRANSACTION";
            case Status.STATUS_PREPARED: return "PREPARED";
            case Status.STATUS_PREPARING: return "PREPARING";
            case Status.STATUS_ROLLEDBACK: return "ROLLEDBACK";
            case Status.STATUS_ROLLING_BACK: return "ROLLING_BACK";
            case Status.STATUS_UNKNOWN: return "UNKNOWN";
            default: return "UNDEFINED";
        }
    }

    // Inner class to track async task status
    public static class AsyncTaskStatus implements Serializable {
        private Integer input;
        private Future<Integer> future;
        private Integer result;
        private boolean complete;
        private String error;

        public AsyncTaskStatus(Integer input, Future<Integer> future) {
            this.input = input;
            this.future = future;
            this.complete = false;
        }

        // Getters and setters
        public Integer getInput() { return input; }
        public Future<Integer> getFuture() { return future; }
        public Integer getResult() { return result; }
        public void setResult(Integer result) { this.result = result; }
        public boolean isComplete() { return complete; }
        public void setComplete(boolean complete) { this.complete = complete; }
        public String getError() { return error; }
        public void setError(String error) { this.error = error; }
    }

    // Getters and setters
    public Integer getInput() { return input; }
    public void setInput(Integer input) { this.input = input; }
    public List<AsyncTaskStatus> getTaskStatuses() { return taskStatuses; }
    public String getTransactionTestResult() { return transactionTestResult; }
    public String getEntityManagerTestResult() { return entityManagerTestResult; }
}