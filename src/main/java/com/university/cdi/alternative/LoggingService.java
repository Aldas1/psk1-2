package com.university.cdi.alternative;

public interface LoggingService {
    void logInfo(String message);
    void logWarning(String message);
    void logError(String message, Throwable throwable);
}