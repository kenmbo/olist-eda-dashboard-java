package com.olist.dashboard.repository;

import java.util.List;

/** One source-query weekday row with exactly 24 hour-count cells. */
public record HourlyOrderRow(String dayOfWeekName, List<Long> hourlyCounts) {
}
