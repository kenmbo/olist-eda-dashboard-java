package com.olist.dashboard.repository;

/** One seller/order-item duration before source-equivalent grouped IQR filtering. */
public record SellerShippingTimeRow(String bucket, String sellerId, Double deliveryTime) {
}
