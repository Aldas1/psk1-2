package com.university.service.notification;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Alternative;
import java.util.logging.Logger;

@Alternative
@ApplicationScoped
public class SMSNotificationService implements NotificationService {
    private static final Logger logger = Logger.getLogger(SMSNotificationService.class.getName());

    @Override
    public void sendNotification(String recipient, String subject, String message) {
        logger.info("Sending SMS notification to " + recipient);
        logger.info("Message: " + subject + " - " + message);
        
    }

    @Override
    public String getProviderName() {
        return "SMS Notification Service";
    }
}