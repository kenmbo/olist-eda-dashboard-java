SELECT metric_id, category, amount
FROM fixture_metrics
WHERE amount >= :minimumAmount
ORDER BY metric_id ASC;
