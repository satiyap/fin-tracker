package com.fintracker.util;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

@Component
public class CurrencyFormatter {

    private static final Locale INDIA_LOCALE = new Locale("en", "IN");
    
    /**
     * Format a BigDecimal amount to Indian Rupee format with the ₹ symbol
     * 
     * @param amount The amount to format
     * @return Formatted string in Indian Rupee format (e.g., ₹1,00,000.00)
     */
    public String formatIndianRupee(BigDecimal amount) {
        if (amount == null) {
            return "₹0.00";
        }
        
        NumberFormat indianFormat = NumberFormat.getCurrencyInstance(INDIA_LOCALE);
        return indianFormat.format(amount);
    }
    
    /**
     * Format a BigDecimal amount to Indian number format without currency symbol
     * 
     * @param amount The amount to format
     * @return Formatted string in Indian number format (e.g., 1,00,000.00)
     */
    public String formatIndianNumber(BigDecimal amount) {
        if (amount == null) {
            return "0.00";
        }
        
        DecimalFormat indianFormat = new DecimalFormat("##,##,##0.00");
        return indianFormat.format(amount);
    }
}