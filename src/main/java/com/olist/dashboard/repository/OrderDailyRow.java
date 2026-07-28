package com.olist.dashboard.repository;

/** One row from the source orders-per-day query. */
public record OrderDailyRow(String day, long orderCount) {
}
