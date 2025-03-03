package com.fintracker.util;

import lombok.experimental.UtilityClass;
import org.slf4j.Logger;
import org.slf4j.MDC;

import java.util.function.Supplier;

/**
 * Utility class for standardized logging patterns and helper methods
 */
@UtilityClass
public class LoggingUtils {

    private static final String ERROR_CODE = "errorCode";

    /**
     * Log the start of a service operation
     * 
     * @param logger The SLF4J logger instance
     * @param operationName The name of the operation
     * @param args Operation arguments (parameters) to log
     */
    public void logOperationStart(Logger logger, String operationName, Object... args) {
        if (logger.isDebugEnabled()) {
            StringBuilder message = new StringBuilder("Starting operation: ").append(operationName);
            if (args != null && args.length > 0) {
                message.append(" with parameters: ");
                for (int i = 0; i < args.length; i++) {
                    if (i > 0) {
                        message.append(", ");
                    }
                    // Avoid toString on null values
                    message.append(args[i] != null ? args[i].toString() : "null");
                }
            }
            logger.debug(message.toString());
        }
    }

    /**
     * Log the completion of a service operation
     * 
     * @param logger The SLF4J logger instance
     * @param operationName The name of the operation
     * @param result The operation result
     * @param startTime The operation start time in milliseconds
     */
    public void logOperationSuccess(Logger logger, String operationName, Object result, long startTime) {
        if (logger.isDebugEnabled()) {
            long duration = System.currentTimeMillis() - startTime;
            String resultStr = result != null ? result.toString() : "null";
            // Truncate very long results
            if (resultStr.length() > 500) {
                resultStr = resultStr.substring(0, 500) + "... [truncated]";
            }
            logger.debug("Completed operation: {} in {} ms with result: {}", operationName, duration, resultStr);
        }
    }

    /**
     * Log a failed operation with error details
     * 
     * @param logger The SLF4J logger instance
     * @param operationName The name of the operation
     * @param error The exception that occurred
     * @param startTime The operation start time in milliseconds
     */
    public void logOperationError(Logger logger, String operationName, Throwable error, long startTime) {
        if (logger.isErrorEnabled()) {
            long duration = System.currentTimeMillis() - startTime;
            logger.error("Failed operation: {} after {} ms: {}", operationName, duration, error.getMessage(), error);
        }
    }

    /**
     * Execute an operation with standardized logging of start, success, and errors
     * 
     * @param <T> The return type of the operation
     * @param logger The SLF4J logger instance
     * @param operationName The name of the operation
     * @param operation The operation to execute (as a Supplier)
     * @return The result of the operation
     * @throws Exception If the operation throws an exception
     */
    public <T> T executeWithLogging(Logger logger, String operationName, Supplier<T> operation) throws Exception {
        long startTime = System.currentTimeMillis();
        logOperationStart(logger, operationName);
        
        try {
            T result = operation.get();
            logOperationSuccess(logger, operationName, result, startTime);
            return result;
        } catch (Exception e) {
            logOperationError(logger, operationName, e, startTime);
            throw e;
        }
    }

    /**
     * Set an error code in the MDC context
     * 
     * @param code The error code to set
     */
    public void setErrorCode(String code) {
        if (code != null) {
            MDC.put(ERROR_CODE, code);
        }
    }

    /**
     * Clear the error code from MDC context
     */
    public void clearErrorCode() {
        MDC.remove(ERROR_CODE);
    }
}