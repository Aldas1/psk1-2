package com.university.cdi.decorator;

public interface NotificationService {
    void sendNotification(String recipient, String subject, String message);
    void sendSystemAlert(String message);
}