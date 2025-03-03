package com.fintracker.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.util.UUID;

/**
 * Filter that logs request and response information and populates MDC context
 * for structured logging.
 */
@Component
@Slf4j
public class RequestLoggingFilter extends OncePerRequestFilter {

    private static final String REQUEST_ID = "requestId";
    private static final String USER_ID = "userId";
    private static final String CLIENT_IP = "clientIp";
    private static final String USER_AGENT = "userAgent";
    private static final String HTTP_METHOD = "httpMethod";
    private static final String REQUEST_URI = "requestURI";
    private static final String HTTP_STATUS = "httpStatus";
    private static final String RESPONSE_TIME = "responseTime";
    private static final String SESSION_ID = "sessionId";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(response);
        
        long startTime = System.currentTimeMillis();
        
        try {
            // Generate unique request ID for tracking
            String requestId = UUID.randomUUID().toString().replace("-", "");
            MDC.put(REQUEST_ID, requestId);
            
            // Add request ID to response headers for client-side tracking
            response.addHeader("X-Request-ID", requestId);
            
            // Capture authenticated user if available
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated() && !"anonymousUser".equals(authentication.getPrincipal())) {
                MDC.put(USER_ID, authentication.getName());
            }
            
            // Capture request metadata
            MDC.put(CLIENT_IP, getClientIp(request));
            MDC.put(USER_AGENT, request.getHeader("User-Agent"));
            MDC.put(HTTP_METHOD, request.getMethod());
            MDC.put(REQUEST_URI, request.getRequestURI());
            MDC.put(SESSION_ID, request.getSession(false) != null ? request.getSession().getId() : "none");
            
            // Log incoming request
            if (log.isInfoEnabled()) {
                log.info("Incoming request: {} {} from {}", 
                        request.getMethod(), 
                        request.getRequestURI(),
                        getClientIp(request));
            }
            
            // Continue with filter chain
            filterChain.doFilter(requestWrapper, responseWrapper);
            
            // Capture response status
            int status = responseWrapper.getStatus();
            MDC.put(HTTP_STATUS, String.valueOf(status));
            
            // Calculate response time
            long duration = System.currentTimeMillis() - startTime;
            MDC.put(RESPONSE_TIME, String.valueOf(duration));
            
            // Log response details
            if (log.isInfoEnabled()) {
                log.info("Request completed: {} {} - {} in {} ms", 
                        request.getMethod(),
                        request.getRequestURI(), 
                        status,
                        duration);
            }
            
            // Debug level logging for detailed data
            if (log.isDebugEnabled() && shouldLogBody(request.getRequestURI())) {
                logRequestBody(requestWrapper);
            }
            
        } catch (Exception e) {
            log.error("Error during request processing", e);
            throw e;
        } finally {
            // Copy content to the response body
            responseWrapper.copyBodyToResponse();
            
            // Clear MDC context to prevent memory leaks
            MDC.clear();
        }
    }
    
    private String getClientIp(HttpServletRequest request) {
        String forwardedFor = request.getHeader("X-Forwarded-For");
        if (forwardedFor != null && !forwardedFor.isEmpty()) {
            // Get the first IP which is the client's IP
            return forwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
    
    private void logRequestBody(ContentCachingRequestWrapper request) {
        // Don't log binary content or form data
        String contentType = request.getContentType();
        if (contentType != null && 
            (contentType.startsWith("application/json") || 
             contentType.startsWith("application/xml") || 
             contentType.startsWith("text/"))) {
            
            byte[] content = request.getContentAsByteArray();
            if (content.length > 0) {
                String bodyContent = new String(content);
                // Avoid logging sensitive data by redacting known sensitive fields
                String redactedContent = redactSensitiveData(bodyContent);
                log.debug("Request body: {}", redactedContent);
            }
        }
    }
    
    private String redactSensitiveData(String content) {
        // Redact common sensitive fields
        return content
            .replaceAll("\"password\"\\s*:\\s*\"[^\"]*\"", "\"password\":\"[REDACTED]\"")
            .replaceAll("\"creditCard\"\\s*:\\s*\"[^\"]*\"", "\"creditCard\":\"[REDACTED]\"")
            .replaceAll("\"ssn\"\\s*:\\s*\"[^\"]*\"", "\"ssn\":\"[REDACTED]\"")
            .replaceAll("\"token\"\\s*:\\s*\"[^\"]*\"", "\"token\":\"[REDACTED]\"");
    }
    
    private boolean shouldLogBody(String requestUri) {
        // Skip logging for specific endpoints
        return !requestUri.contains("/health") && 
               !requestUri.contains("/metrics") && 
               !requestUri.contains("/actuator");
    }
}