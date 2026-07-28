package com.olist.dashboard.repository;

/** One source-query row for the shipping stage timing chart. */
public record ShippingStageByCityRow(
        String city,
        Double approved,
        Double deliveredToCarrier,
        Double deliveredToCustomer,
        Double estimatedDelivery) {
}
