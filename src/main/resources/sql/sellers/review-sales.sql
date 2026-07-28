SELECT
    oi.seller_id,
    SUM(oi.price) AS total_sales,
    AVG(r.review_score) AS avg_score,
    COUNT(DISTINCT oi.order_id) AS order_count
FROM order_items oi
JOIN order_reviews r ON oi.order_id = r.order_id
GROUP BY oi.seller_id
HAVING order_count > 5
