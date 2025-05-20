package com.university.beans;

import com.university.service.AsyncCalculationService;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.Serializable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@Named
@SessionScoped
public class AsyncDemoBean implements Serializable {

    @Inject
    private AsyncCalculationService asyncCalculationService;

    private String calculationInput;
    private String calculationResult;
    private boolean calculationInProgress = false;
    private Future<String> futureResult;
    private Future<Integer> studentCountFuture;
    private Integer studentCount;

    @PostConstruct
    public void init() {
        calculationInput = "Sample input";
        calculationResult = "";
    }

    public String startAsyncCalculation() {
        calculationInProgress = true;
        calculationResult = "Calculation in progress...";

        futureResult = asyncCalculationService.performLongCalculation(calculationInput);

        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO,
                        "Async calculation started", "You can continue using the application"));

        return null;
    }

    public String checkCalculationResult() {
        if (futureResult == null) {
            calculationResult = "No calculation has been started";
            return null;
        }

        if (futureResult.isDone()) {
            try {
                calculationResult = futureResult.get();
                calculationInProgress = false;
            } catch (InterruptedException | ExecutionException e) {
                calculationResult = "Error retrieving result: " + e.getMessage();
                calculationInProgress = false;
            }
        } else {
            calculationResult = "Calculation still in progress...";
        }

        return null;
    }

    public String countStudentsAsync() {
        studentCountFuture = asyncCalculationService.countStudents();
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO,
                        "Async student counting started", null));
        return null;
    }

    public String checkStudentCount() {
        if (studentCountFuture == null) {
            studentCount = null;
            return null;
        }

        if (studentCountFuture.isDone()) {
            try {
                studentCount = studentCountFuture.get();
            } catch (InterruptedException | ExecutionException e) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                "Error getting student count", e.getMessage()));
            }
        } else {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO,
                            "Student counting still in progress", null));
        }

        return null;
    }

    public String getCalculationInput() {
        return calculationInput;
    }

    public void setCalculationInput(String calculationInput) {
        this.calculationInput = calculationInput;
    }

    public String getCalculationResult() {
        return calculationResult;
    }

    public boolean isCalculationInProgress() {
        return calculationInProgress;
    }

    public Integer getStudentCount() {
        return studentCount;
    }
}