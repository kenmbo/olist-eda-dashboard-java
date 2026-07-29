package com.olist.dashboard.service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.springframework.stereotype.Service;

import com.olist.dashboard.dto.SellerShippingTimesResponse;
import com.olist.dashboard.repository.SellerShippingTimeRow;
import com.olist.dashboard.repository.SellerShippingTimesRepository;

/** Applies the source's per-bucket pandas IQR filtering before columnar serialization. */
@Service
public class SellerShippingTimesService {

    private final SellerShippingTimesRepository sellerShippingTimesRepository;

    public SellerShippingTimesService(SellerShippingTimesRepository sellerShippingTimesRepository) {
        this.sellerShippingTimesRepository = sellerShippingTimesRepository;
    }

    public SellerShippingTimesResponse shippingTimes() {
        var rows = filterIqrOutliers(sellerShippingTimesRepository.findSellerShippingTimes());
        return new SellerShippingTimesResponse(
                rows.stream().map(SellerShippingTimeRow::bucket).toList(),
                rows.stream().map(SellerShippingTimeRow::sellerId).toList(),
                rows.stream().map(SellerShippingTimeRow::deliveryTime).toList());
    }

    /**
     * Mirrors {@code remove_outliers_iqr}: groups occur in first-seen order, rows stay in source
     * SQL order within a group, and bounds are inclusive.
     */
    static List<SellerShippingTimeRow> filterIqrOutliers(List<SellerShippingTimeRow> rows) {
        var groups = new LinkedHashMap<String, List<SellerShippingTimeRow>>();
        for (var row : rows) {
            groups.computeIfAbsent(row.bucket(), ignored -> new ArrayList<>()).add(row);
        }

        var filtered = new ArrayList<SellerShippingTimeRow>();
        for (var group : groups.values()) {
            var values = group.stream().map(SellerShippingTimeRow::deliveryTime).toList();
            Double firstQuartile = PandasStatistics.linearQuantile(values, 0.25);
            Double thirdQuartile = PandasStatistics.linearQuantile(values, 0.75);
            if (firstQuartile == null || thirdQuartile == null) {
                continue;
            }
            double iqr = thirdQuartile - firstQuartile;
            double lowerBound = firstQuartile - 1.5 * iqr;
            double upperBound = thirdQuartile + 1.5 * iqr;
            for (var row : group) {
                Double value = row.deliveryTime();
                if (value != null && Double.isFinite(value) && value >= lowerBound && value <= upperBound) {
                    filtered.add(row);
                }
            }
        }
        return List.copyOf(filtered);
    }
}
