package com.olist.dashboard.repository;

/** One source-query row for the customer lifetime value geographic map. */
public record CustomerClvMapRow(
        Integer zipPrefix,
        Double averageClv,
        Long customerCount,
        Double latitude,
        Double longitude) {
}
