package com.olist.dashboard.repository;

/** One delivered raw-category monthly sales aggregate before pandas-style pivoting. */
public record MonthlyCategorySalesRow(String orderMonth, String category, Double totalSales) {
}
