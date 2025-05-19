package com.university.cdi.alternative;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;

import java.util.logging.Level;
import java.util.logging.Logger;

@Named
@ApplicationScoped
public class DefaultLoggingService implements LoggingService {

    private static final Logger LOGGER = Logger.getLogger(DefaultLoggingService.class.getName());

    @Override
    public void logInfo(String message) {
        LOGGER.info("DEFAULT LOGGER: " + message);
    }

    @Override
    public void logWarning(String message) {
        LOGGER.warning("DEFAULT LOGGER: " + message);
    }

    @Override
    public void logError(String message, Throwable throwable) {
        LOGGER.log(Level.SEVERE, "DEFAULT LOGGER: " + message, throwable);
    }
}