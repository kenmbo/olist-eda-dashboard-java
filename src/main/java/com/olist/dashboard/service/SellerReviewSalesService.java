package com.olist.dashboard.service;

import org.springframework.stereotype.Service;

import com.olist.dashboard.dto.SellerReviewSalesResponse;
import com.olist.dashboard.repository.SellerReviewSalesRepository;

/** Shapes seller review-sales rows into the rounded custom chart arrays. */
@Service
public class SellerReviewSalesService {

    private final SellerReviewSalesRepository sellerReviewSalesRepository;
    private final PandasFormattingService pandasFormattingService;

    public SellerReviewSalesService(
            SellerReviewSalesRepository sellerReviewSalesRepository,
            PandasFormattingService pandasFormattingService) {
        this.sellerReviewSalesRepository = sellerReviewSalesRepository;
        this.pandasFormattingService = pandasFormattingService;
    }

    public SellerReviewSalesResponse reviewSales() {
        var rows = sellerReviewSalesRepository.findReviewSales();
        return new SellerReviewSalesResponse(
                rows.stream().map(row -> row.sellerId()).toList(),
                rows.stream().map(row -> pandasFormattingService.roundToTwoDecimals(row.totalSales())).toList(),
                rows.stream().map(row -> pandasFormattingService.roundToTwoDecimals(row.averageScore())).toList(),
                rows.stream().map(row -> row.orderCount()).toList());
    }
}
