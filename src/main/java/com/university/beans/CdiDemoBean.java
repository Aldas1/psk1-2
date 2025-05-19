package com.university.beans;

import com.university.cdi.alternative.LoggingService;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

@Named
@RequestScoped
public class CdiDemoBean {

    @Inject
    private LoggingService loggingService;

    private String message;
    private String result;

    public void logMessage() {
        if (message == null || message.isEmpty()) {
            result = "Please enter a message to log";
            return;
        }

        loggingService.logInfo(message);
        result = "Message logged: " + message;
    }

    public void generateWarning() {
        loggingService.logWarning("Warning demonstration");
        result = "Warning logged";
    }

    public void generateError() {
        try {
            // Deliberately cause an exception
            int a = 1/0;
        } catch (Exception e) {
            loggingService.logError("Error demonstration", e);
            result = "Error logged";
        }
    }

    // Getters and setters
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public String getResult() { return result; }
}