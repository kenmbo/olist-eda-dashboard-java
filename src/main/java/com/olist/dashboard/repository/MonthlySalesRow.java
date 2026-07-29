package com.olist.dashboard.repository;

/** One source-query month and its nullable selected-category sales aggregates. */
public record MonthlySalesRow(
        String yearMonth,
        Double healthBeauty,
        Double auto,
        Double toys,
        Double electronics,
        Double fashionShoes) {
}
