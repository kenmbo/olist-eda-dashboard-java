package com.olist.dashboard.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.olist.dashboard.dto.SellerDistributionResponse;
import com.olist.dashboard.repository.SellerDistributionRepository;
import com.olist.dashboard.repository.SellerDistributionRow;

/** Shapes seller order-volume buckets into the frozen column-oriented API response. */
@Service
public class SellerDistributionService {

    private final SellerDistributionRepository sellerDistributionRepository;

    public SellerDistributionService(SellerDistributionRepository sellerDistributionRepository) {
        this.sellerDistributionRepository = sellerDistributionRepository;
    }

    public SellerDistributionResponse getDistribution() {
        List<SellerDistributionRow> rows = sellerDistributionRepository.findAll();
        return new SellerDistributionResponse(
                rows.stream().map(SellerDistributionRow::bucket).toList(),
                rows.stream().map(SellerDistributionRow::sellerCount).toList());
    }
}
