package com.olist.dashboard.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.olist.dashboard.dto.SellerPerformanceResponse;
import com.olist.dashboard.repository.SellerPerformanceRepository;
import com.olist.dashboard.repository.SellerPerformanceRow;

/** Shapes seller performance rows into the frozen column-oriented API response. */
@Service
public class SellerPerformanceService {

    private final SellerPerformanceRepository sellerPerformanceRepository;

    public SellerPerformanceService(SellerPerformanceRepository sellerPerformanceRepository) {
        this.sellerPerformanceRepository = sellerPerformanceRepository;
    }

    public SellerPerformanceResponse getPerformance() {
        List<SellerPerformanceRow> rows = sellerPerformanceRepository.findAll();
        return new SellerPerformanceResponse(
                rows.stream().map(SellerPerformanceRow::sellerId).toList(),
                rows.stream().map(SellerPerformanceRow::avgReviewScore).toList(),
                rows.stream().map(SellerPerformanceRow::totalSales).toList(),
                rows.stream().map(SellerPerformanceRow::numOrders).toList());
    }
}
