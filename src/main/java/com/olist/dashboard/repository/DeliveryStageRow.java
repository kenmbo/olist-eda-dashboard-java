package com.olist.dashboard.repository;

/** Raw city delivery-stage aggregate row before city title-casing. */
public record DeliveryStageRow(String city, Double approvalDays, Double carrierDays, Double transitDays) {
}
