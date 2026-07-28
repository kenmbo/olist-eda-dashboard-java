package com.olist.dashboard.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.olist.dashboard.dto.LeadConversionResponse;
import com.olist.dashboard.dto.LeadOriginResponse;
import com.olist.dashboard.service.LeadAnalyticsService;

/** HTTP routes for the migrated lead-origin analytics charts. */
@RestController
@RequestMapping("/api/leads")
public class LeadsController {

    private final LeadAnalyticsService leadAnalyticsService;

    public LeadsController(LeadAnalyticsService leadAnalyticsService) {
        this.leadAnalyticsService = leadAnalyticsService;
    }

    @GetMapping(value = "/conversion", produces = MediaType.APPLICATION_JSON_VALUE)
    public LeadConversionResponse conversions() {
        return leadAnalyticsService.conversions();
    }

    @GetMapping(value = "/origin", produces = MediaType.APPLICATION_JSON_VALUE)
    public LeadOriginResponse origins() {
        return leadAnalyticsService.origins();
    }
}
