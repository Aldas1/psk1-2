package com.university.cdi.interceptor;

import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.Interceptor;
import jakarta.interceptor.InvocationContext;

import java.util.Arrays;
import java.util.logging.Logger;

@Logged
@Interceptor
public class LoggingInterceptor {

    private static final Logger LOGGER = Logger.getLogger(LoggingInterceptor.class.getName());

    @AroundInvoke
    public Object logMethodCall(InvocationContext context) throws Exception {
        long startTime = System.currentTimeMillis();

        LOGGER.info("Entering method: " + context.getMethod().getDeclaringClass().getName() + "." +
                context.getMethod().getName() + " with parameters: " +
                Arrays.toString(context.getParameters()));

        try {
            Object result = context.proceed();
            long endTime = System.currentTimeMillis();

            LOGGER.info("Exiting method: " + context.getMethod().getDeclaringClass().getName() + "." +
                    context.getMethod().getName() + " - execution time: " + (endTime - startTime) + " ms");

            return result;
        } catch (Exception e) {
            LOGGER.severe("Exception in method: " + context.getMethod().getDeclaringClass().getName() + "." +
                    context.getMethod().getName() + " - " + e.getMessage());
            throw e;
        }
    }
}