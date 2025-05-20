package com.university.service.notification;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Specializes;
import java.util.logging.Logger;

@Specializes
@ApplicationScoped
public class EnhancedEmailNotificationService extends EmailNotificationService {
    private static final Logger logger = Logger.getLogger(EnhancedEmailNotificationService.class.getName());

    @Override
    public void sendNotification(String recipient, String subject, String message) {
        logger.info("Sending ENHANCED EMAIL notification to " + recipient);
        logger.info("Subject: " + subject);
        logger.info("Message: " + message);
        logger.info("Adding delivery tracking and read receipts");
        
    }

    @Override
    public String getProviderName() {
        return "Enhanced Email Notification Service with Tracking";
    }
}