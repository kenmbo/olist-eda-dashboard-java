WITH BucketedSellers AS (
    SELECT
        seller_id,
        CASE
            WHEN COUNT(order_id) BETWEEN 1 AND 9 THEN '1-9 orders'
            WHEN COUNT(order_id) BETWEEN 10 AND 99 THEN '10-99 orders'
            WHEN COUNT(order_id) BETWEEN 100 AND 999 THEN '100-999 orders'
            ELSE '1000+ orders'
        END AS bucket
    FROM order_items
    GROUP BY seller_id
)
SELECT
    bucket,
    BucketedSellers.seller_id,
    JULIANDAY(order_delivered_customer_date) - JULIANDAY(order_purchase_timestamp)
        AS delivery_time
FROM orders
    JOIN order_items USING (order_id)
    JOIN BucketedSellers USING (seller_id)
WHERE order_status = 'delivered'
