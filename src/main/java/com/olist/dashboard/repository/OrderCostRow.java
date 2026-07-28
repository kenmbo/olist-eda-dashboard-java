package com.olist.dashboard.repository;

/** One delivered order with its source-query product and freight sums. */
public record OrderCostRow(String orderId, double productCost, double shippingCost) {
}
