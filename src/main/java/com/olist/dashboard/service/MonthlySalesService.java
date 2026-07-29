package com.olist.dashboard.service;

import org.springframework.stereotype.Service;

import com.olist.dashboard.dto.MonthlySalesResponse;
import com.olist.dashboard.repository.MonthlySalesRepository;
import com.olist.dashboard.repository.MonthlySalesRow;

/** Shapes fixed category monthly rows into the source's pandas timestamp columnar contract. */
@Service
public class MonthlySalesService {

    private final MonthlySalesRepository monthlySalesRepository;

    public MonthlySalesService(MonthlySalesRepository monthlySalesRepository) {
        this.monthlySalesRepository = monthlySalesRepository;
    }

    public MonthlySalesResponse monthlySales() {
        var rows = monthlySalesRepository.findMonthlySales();
        if (rows.stream().anyMatch(MonthlySalesService::hasMissingSelectedCategory)) {
            /*
             * Source pandas leaves this as NaN and FastAPI rejects the response during JSON
             * serialization. Do not turn that non-successful source edge into JSON null.
             */
            throw new IllegalStateException("A selected monthly category aggregate is missing");
        }
        return new MonthlySalesResponse(
                rows.stream().map(row -> pandasMonthTimestamp(row.yearMonth())).toList(),
                rows.stream().map(row -> row.healthBeauty()).toList(),
                rows.stream().map(row -> row.auto()).toList(),
                rows.stream().map(row -> row.toys()).toList(),
                rows.stream().map(row -> row.electronics()).toList(),
                rows.stream().map(row -> row.fashionShoes()).toList());
    }

    private static boolean hasMissingSelectedCategory(MonthlySalesRow row) {
        return row.healthBeauty() == null
                || row.auto() == null
                || row.toys() == null
                || row.electronics() == null
                || row.fashionShoes() == null;
    }

    /** Equivalent to source {@code pd.to_datetime("YYYY-MM")} serialization through FastAPI. */
    static String pandasMonthTimestamp(String yearMonth) {
        return yearMonth == null ? null : yearMonth + "-01T00:00:00";
    }
}
