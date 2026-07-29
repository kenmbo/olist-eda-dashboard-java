package com.olist.dashboard.repository;

/** Raw lead-origin aggregation row before origin formatting. */
public record LeadOriginRow(String origin, Long totalLeads) {
}
