package com.olist.dashboard.repository;

/** One deliberately repeated product-weight observation from the source order-items join. */
public record CategoryWeightRow(String category, Double weight) {
}
