package com.crm.backend.dashboard;

public class DashboardStats {
    private long totalCustomers;
    private long totalDeals;
    private long openTasks;
    private long totalEmails;
    private double monthlyRevenue;
    private double conversionRate;

    public DashboardStats() {}

    public DashboardStats(long totalCustomers, long totalDeals, long openTasks, long totalEmails, double monthlyRevenue, double conversionRate) {
        this.totalCustomers = totalCustomers;
        this.totalDeals = totalDeals;
        this.openTasks = openTasks;
        this.totalEmails = totalEmails;
        this.monthlyRevenue = monthlyRevenue;
        this.conversionRate = conversionRate;
    }

    // Getters & Setters
    public long getTotalCustomers() {
        return totalCustomers;
    }

    public void setTotalCustomers(long totalCustomers) {
        this.totalCustomers = totalCustomers;
    }

    public long getTotalDeals() {
        return totalDeals;
    }

    public void setTotalDeals(long totalDeals) {
        this.totalDeals = totalDeals;
    }

    public long getOpenTasks() {
        return openTasks;
    }

    public void setOpenTasks(long openTasks) {
        this.openTasks = openTasks;
    }

    public long getTotalEmails() {
        return totalEmails;
    }

    public void setTotalEmails(long totalEmails) {
        this.totalEmails = totalEmails;
    }

    public double getMonthlyRevenue() {
        return monthlyRevenue;
    }

    public void setMonthlyRevenue(double monthlyRevenue) {
        this.monthlyRevenue = monthlyRevenue;
    }

    public double getConversionRate() {
        return conversionRate;
    }

    public void setConversionRate(double conversionRate) {
        this.conversionRate = conversionRate;
    }
}
