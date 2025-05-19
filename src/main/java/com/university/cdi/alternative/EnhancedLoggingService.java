package com.university.cdi.alternative;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Alternative;
import jakarta.inject.Named;

import java.util.logging.Level;
import java.util.logging.Logger;

@Named
@ApplicationScoped
@Alternative
public class EnhancedLoggingService implements LoggingService {

    private static final Logger LOGGER = Logger.getLogger(EnhancedLoggingService.class.getName());

    @Override
    public void logInfo(String message) {
        LOGGER.info("ENHANCED LOGGER [" + System.currentTimeMillis() + "]: " + message);
    }

    @Override
    public void logWarning(String message) {
        LOGGER.warning("ENHANCED LOGGER [" + System.currentTimeMillis() + "]: " + message);
    }

    @Override
    public void logError(String message, Throwable throwable) {
        LOGGER.log(Level.SEVERE, "ENHANCED LOGGER [" + System.currentTimeMillis() + "]: " + message, throwable);
    }
}