package com.university.interceptor;

import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.Interceptor;
import jakarta.interceptor.InvocationContext;
import java.util.Arrays;
import java.util.logging.Logger;

@Interceptor
@Logged
public class LoggingInterceptor {
    private static final Logger logger = Logger.getLogger(LoggingInterceptor.class.getName());

    @AroundInvoke
    public Object logMethodCall(InvocationContext context) throws Exception {
        String className = context.getTarget().getClass().getName();
        String methodName = context.getMethod().getName();

        logger.info("Entering method: " + className + "." + methodName);
        logger.info("Parameters: " + Arrays.toString(context.getParameters()));

        long startTime = System.currentTimeMillis();
        try {
            return context.proceed();
        } finally {
            long endTime = System.currentTimeMillis();
            logger.info("Exiting method: " + className + "." + methodName);
            logger.info("Execution time: " + (endTime - startTime) + " ms");
        }
    }
}