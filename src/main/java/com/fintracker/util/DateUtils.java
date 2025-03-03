package com.fintracker.util;

import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@Component
public class DateUtils {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
    
    /**
     * Get the start of day for a given date
     * 
     * @param date The date
     * @return LocalDateTime representing the start of the day (00:00:00)
     */
    public LocalDateTime getStartOfDay(LocalDate date) {
        return date.atStartOfDay();
    }
    
    /**
     * Get the end of day for a given date
     * 
     * @param date The date
     * @return LocalDateTime representing the end of the day (23:59:59.999999999)
     */
    public LocalDateTime getEndOfDay(LocalDate date) {
        return date.atTime(LocalTime.MAX);
    }
    
    /**
     * Get the start of month for a given date
     * 
     * @param date The date
     * @return LocalDateTime representing the start of the month
     */
    public LocalDateTime getStartOfMonth(LocalDate date) {
        return date.withDayOfMonth(1).atStartOfDay();
    }
    
    /**
     * Get the end of month for a given date
     * 
     * @param date The date
     * @return LocalDateTime representing the end of the month
     */
    public LocalDateTime getEndOfMonth(LocalDate date) {
        return date.withDayOfMonth(date.lengthOfMonth()).atTime(LocalTime.MAX);
    }
    
    /**
     * Format a LocalDate to a string in the format dd-MM-yyyy
     * 
     * @param date The date to format
     * @return Formatted date string
     */
    public String formatDate(LocalDate date) {
        if (date == null) {
            return null;
        }
        return date.format(DATE_FORMATTER);
    }
    
    /**
     * Format a LocalDateTime to a string in the format dd-MM-yyyy HH:mm:ss
     * 
     * @param dateTime The date time to format
     * @return Formatted date time string
     */
    public String formatDateTime(LocalDateTime dateTime) {
        if (dateTime == null) {
            return null;
        }
        return dateTime.format(DATE_TIME_FORMATTER);
    }
    
    /**
     * Parse a date string in the format dd-MM-yyyy to a LocalDate
     * 
     * @param dateString The date string to parse
     * @return Parsed LocalDate
     */
    public LocalDate parseDate(String dateString) {
        if (dateString == null || dateString.isEmpty()) {
            return null;
        }
        return LocalDate.parse(dateString, DATE_FORMATTER);
    }
    
    /**
     * Parse a date time string in the format dd-MM-yyyy HH:mm:ss to a LocalDateTime
     * 
     * @param dateTimeString The date time string to parse
     * @return Parsed LocalDateTime
     */
    public LocalDateTime parseDateTime(String dateTimeString) {
        if (dateTimeString == null || dateTimeString.isEmpty()) {
            return null;
        }
        return LocalDateTime.parse(dateTimeString, DATE_TIME_FORMATTER);
    }
}