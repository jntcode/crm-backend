package com.crm.backend.lead;

import java.math.BigDecimal;

/**
 * Aggregate row: total value of leads grouped by creation year/month.
 */
public record LeadMonthlyValue(Integer year, Integer month, BigDecimal total) {
}
