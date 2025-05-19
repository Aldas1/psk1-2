package com.university.service.notification;

import jakarta.enterprise.context.ApplicationScoped;
import java.util.logging.Logger;

@ApplicationScoped
public class EmailNotificationService implements NotificationService {
    private static final Logger logger = Logger.getLogger(EmailNotificationService.class.getName());

    @Override
    public void sendNotification(String recipient, String subject, String message) {
        logger.info("Sending EMAIL notification to " + recipient);
        logger.info("Subject: " + subject);
        logger.info("Message: " + message);
        // Implementation would go here
    }

    @Override
    public String getProviderName() {
        return "Email Notification Service";
    }
}