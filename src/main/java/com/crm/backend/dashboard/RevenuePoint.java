package com.crm.backend.dashboard;

/**
 * Revenue for one calendar month. {@code month} is the ISO key (yyyy-MM);
 * {@code label} is a short display name; {@code revenue} is the total value of
 * leads marked WON whose creation date falls in that month.
 */
public record RevenuePoint(String month, String label, double revenue) {
}
