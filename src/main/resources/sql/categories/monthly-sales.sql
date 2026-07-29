SELECT
    strftime('%Y-%m', o.order_purchase_timestamp) AS order_month,
    p.product_category_name AS category,
    SUM(oi.price) AS total_sales
FROM orders o
JOIN order_items oi ON o.order_id = oi.order_id
JOIN products p ON oi.product_id = p.product_id
WHERE o.order_status = 'delivered'
  AND p.product_category_name IS NOT NULL
GROUP BY order_month, category
ORDER BY order_month ASC
