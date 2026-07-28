package com.olist.dashboard.service;

import java.util.LinkedHashMap;
import java.util.List;

import org.springframework.stereotype.Service;

import com.olist.dashboard.dto.ColumnarResponse;
import com.olist.dashboard.repository.OrdersRepository;

/** Shapes source-order rows into pandas {@code orient="list"} response documents. */
@Service
public class OrdersService {

    private final OrdersRepository ordersRepository;

    public OrdersService(OrdersRepository ordersRepository) {
        this.ordersRepository = ordersRepository;
    }

    public ColumnarResponse dailyOrderCounts() {
        var rows = ordersRepository.findDailyOrderCounts();
        var columns = new LinkedHashMap<String, List<?>>();
        columns.put("day", rows.stream().map(row -> row.day()).toList());
        columns.put("order_count", rows.stream().map(row -> row.orderCount()).toList());
        return new ColumnarResponse(columns);
    }

    public ColumnarResponse deliveredOrderCosts() {
        var rows = ordersRepository.findDeliveredOrderCosts();
        var columns = new LinkedHashMap<String, List<?>>();
        columns.put("order_id", rows.stream().map(row -> row.orderId()).toList());
        columns.put("product_cost", rows.stream().map(row -> row.productCost()).toList());
        columns.put("shipping_cost", rows.stream().map(row -> row.shippingCost()).toList());
        return new ColumnarResponse(columns);
    }
}
