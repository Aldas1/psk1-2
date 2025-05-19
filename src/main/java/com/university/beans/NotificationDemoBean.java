package com.university.beans;

import com.university.service.notification.NotificationService;
import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.Serializable;

@Named
@RequestScoped
public class NotificationDemoBean implements Serializable {

    @Inject
    private NotificationService notificationService;

    private String recipient;
    private String subject;
    private String message;

    public String sendNotification() {
        notificationService.sendNotification(recipient, subject, message);

        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO,
                        "Notification sent using " + notificationService.getProviderName(),
                        "Sent to: " + recipient));

        return null;
    }

    // Getters and setters
    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getProviderName() {
        return notificationService.getProviderName();
    }
}