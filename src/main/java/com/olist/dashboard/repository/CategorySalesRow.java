package com.olist.dashboard.repository;

/** One translated product category and its delivered-order sales total. */
public record CategorySalesRow(String category, double sales) {
}
