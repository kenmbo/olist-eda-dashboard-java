SELECT
    p.product_category_name AS category,
    p.product_weight_g AS weight
FROM order_items oi
JOIN products p ON oi.product_id = p.product_id
WHERE p.product_category_name IS NOT NULL
  AND p.product_weight_g IS NOT NULL
