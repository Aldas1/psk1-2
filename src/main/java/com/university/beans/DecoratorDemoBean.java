package com.university.beans;

import com.university.cdi.decorator.NotificationService;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

@Named
@RequestScoped
public class DecoratorDemoBean {

    @Inject
    private NotificationService notificationService;

    private String recipient;
    private String subject;
    private String message;
    private String result;

    public void sendNotification() {
        if (recipient == null || recipient.isEmpty() ||
                subject == null || subject.isEmpty() ||
                message == null || message.isEmpty()) {
            result = "Please fill all fields";
            return;
        }

        notificationService.sendNotification(recipient, subject, message);
        result = "Notification sent to " + recipient;
    }

    public void sendSystemAlert() {
        if (message == null || message.isEmpty()) {
            result = "Please enter a message";
            return;
        }

        notificationService.sendSystemAlert(message);
        result = "System alert sent";
    }

    // Getters and setters
    public String getRecipient() { return recipient; }
    public void setRecipient(String recipient) { this.recipient = recipient; }
    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public String getResult() { return result; }
}