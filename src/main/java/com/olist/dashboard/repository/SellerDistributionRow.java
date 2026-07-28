package com.olist.dashboard.repository;

/** One source-query row for a seller order-volume bucket. */
public record SellerDistributionRow(String bucket, long sellerCount) {
}
