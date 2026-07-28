package com.olist.dashboard.service;

import org.springframework.stereotype.Service;

import com.olist.dashboard.dto.LeadConversionResponse;
import com.olist.dashboard.dto.LeadOriginResponse;
import com.olist.dashboard.repository.LeadAnalyticsRepository;

/** Shapes lead rows into source-compatible custom arrays and cleaned labels. */
@Service
public class LeadAnalyticsService {

    private final LeadAnalyticsRepository leadAnalyticsRepository;
    private final PandasFormattingService pandasFormattingService;

    public LeadAnalyticsService(
            LeadAnalyticsRepository leadAnalyticsRepository,
            PandasFormattingService pandasFormattingService) {
        this.leadAnalyticsRepository = leadAnalyticsRepository;
        this.pandasFormattingService = pandasFormattingService;
    }

    public LeadConversionResponse conversions() {
        var rows = leadAnalyticsRepository.findConversions();
        return new LeadConversionResponse(
                rows.stream().map(row -> pandasFormattingService.underscoreToTitleCase(row.origin())).toList(),
                rows.stream().map(row -> row.qualifiedLeads()).toList(),
                rows.stream().map(row -> row.closedLeads()).toList(),
                rows.stream().map(row -> row.conversionRate()).toList());
    }

    public LeadOriginResponse origins() {
        var rows = leadAnalyticsRepository.findOrigins();
        return new LeadOriginResponse(
                rows.stream().map(row -> pandasFormattingService.underscoreToTitleCase(row.origin())).toList(),
                rows.stream().map(row -> row.totalLeads()).toList());
    }
}
