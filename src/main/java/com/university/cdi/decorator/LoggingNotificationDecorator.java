package com.university.cdi.decorator;

import jakarta.annotation.Priority;
import jakarta.decorator.Decorator;
import jakarta.decorator.Delegate;
import jakarta.enterprise.inject.Any;
import jakarta.inject.Inject;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.Logger;

@Decorator
@Priority(10)
public class LoggingNotificationDecorator implements NotificationService {

    private static final Logger LOGGER = Logger.getLogger(LoggingNotificationDecorator.class.getName());

    @Inject
    @Delegate
    @Any
    private NotificationService notificationService;

    @Override
    public void sendNotification(String recipient, String subject, String message) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        LOGGER.info("[" + timestamp + "] Notification being processed: " +
                "recipient=" + recipient + ", subject=" + subject);

        // Call the decorated service
        notificationService.sendNotification(recipient, subject, message);

        LOGGER.info("[" + timestamp + "] Notification processed successfully");
    }

    @Override
    public void sendSystemAlert(String message) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        LOGGER.info("[" + timestamp + "] System alert being processed: " + message);

        // Call the decorated service
        notificationService.sendSystemAlert(message);

        LOGGER.info("[" + timestamp + "] System alert processed successfully");
    }
}