package com.university.beans;

import com.university.service.LoggedStudentService;
import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.Serializable;

@Named
@RequestScoped
public class InterceptorDemoBean implements Serializable {

    @Inject
    private LoggedStudentService loggedStudentService;

    public String callInterceptedMethod() {
        try {
            // This method call will be intercepted by LoggingInterceptor
            loggedStudentService.getStudentsWithGradeAverage();

            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO,
                            "Intercepted method called successfully",
                            "Check server logs for detailed interceptor output"));
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            "Error calling intercepted method", e.getMessage()));
        }

        return null;
    }
}