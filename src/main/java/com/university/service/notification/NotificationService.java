package com.university.service.notification;

public interface NotificationService {
    void sendNotification(String recipient, String subject, String message);
    String getProviderName();
}