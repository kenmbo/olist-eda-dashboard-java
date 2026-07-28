package com.olist.dashboard.service;

import org.springframework.stereotype.Service;

import com.olist.dashboard.dto.CustomersClvMapResponse;
import com.olist.dashboard.repository.CustomerAnalyticsRepository;

/** Shapes source-query CLV rows into their frozen column-oriented HTTP contract. */
@Service
public class CustomerAnalyticsService {

    private final CustomerAnalyticsRepository customerAnalyticsRepository;

    public CustomerAnalyticsService(CustomerAnalyticsRepository customerAnalyticsRepository) {
        this.customerAnalyticsRepository = customerAnalyticsRepository;
    }

    public CustomersClvMapResponse clvMap() {
        var rows = customerAnalyticsRepository.findClvMapRows();
        return new CustomersClvMapResponse(
                rows.stream().map(row -> row.zipPrefix()).toList(),
                rows.stream().map(row -> row.averageClv()).toList(),
                rows.stream().map(row -> row.customerCount()).toList(),
                rows.stream().map(row -> row.latitude()).toList(),
                rows.stream().map(row -> row.longitude()).toList());
    }
}
