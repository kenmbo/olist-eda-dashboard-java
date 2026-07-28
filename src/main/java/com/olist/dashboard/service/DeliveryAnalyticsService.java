package com.olist.dashboard.service;

import org.springframework.stereotype.Service;

import com.olist.dashboard.dto.DeliveryStagesResponse;
import com.olist.dashboard.repository.DeliveryAnalyticsRepository;

/** Shapes raw city stage-duration rows into title-cased custom chart arrays. */
@Service
public class DeliveryAnalyticsService {

    private final DeliveryAnalyticsRepository deliveryAnalyticsRepository;
    private final PandasFormattingService pandasFormattingService;

    public DeliveryAnalyticsService(
            DeliveryAnalyticsRepository deliveryAnalyticsRepository,
            PandasFormattingService pandasFormattingService) {
        this.deliveryAnalyticsRepository = deliveryAnalyticsRepository;
        this.pandasFormattingService = pandasFormattingService;
    }

    public DeliveryStagesResponse stages() {
        var rows = deliveryAnalyticsRepository.findStages();
        return new DeliveryStagesResponse(
                rows.stream().map(row -> pandasFormattingService.underscoreToTitleCase(row.city())).toList(),
                rows.stream().map(row -> row.approvalDays()).toList(),
                rows.stream().map(row -> row.carrierDays()).toList(),
                rows.stream().map(row -> row.transitDays()).toList());
    }
}
