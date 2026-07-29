package com.olist.dashboard.repository;

/** Raw lead conversion aggregation row before origin formatting. */
public record LeadConversionRow(String origin, Long qualifiedLeads, Long closedLeads, Double conversionRate) {
}
