package com.university.cdi.decorator;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;

import java.util.logging.Logger;

@Named
@ApplicationScoped
public class DefaultNotificationService implements NotificationService {

    private static final Logger LOGGER = Logger.getLogger(DefaultNotificationService.class.getName());

    @Override
    public void sendNotification(String recipient, String subject, String message) {
        LOGGER.info("Sending notification to " + recipient + " with subject: " + subject);
        // Actual implementation would send an email or other notification
    }

    @Override
    public void sendSystemAlert(String message) {
        LOGGER.info("System alert: " + message);
        // Actual implementation would log to system monitoring
    }
}