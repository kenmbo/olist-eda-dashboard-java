SELECT
    sellers.seller_id,
    AVG(order_reviews.review_score) AS avg_review_score,
    SUM(order_items.price) AS total_sales,
    COUNT(orders.order_id) AS num_orders
FROM sellers
LEFT JOIN order_items ON sellers.seller_id = order_items.seller_id
LEFT JOIN orders ON order_items.order_id = orders.order_id
LEFT JOIN order_reviews ON orders.order_id = order_reviews.order_id
GROUP BY sellers.seller_id
HAVING COUNT(orders.order_id) > 10
-- The FastAPI query had no ORDER BY. Its frozen successful contract is seller_id ascending.
ORDER BY sellers.seller_id ASC
