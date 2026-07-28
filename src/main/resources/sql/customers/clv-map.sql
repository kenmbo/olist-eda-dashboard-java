WITH CLV AS (
    WITH CustomerData AS (
        SELECT
            customer_unique_id,
            customer_zip_code_prefix AS zip_code_prefix,
            COUNT(DISTINCT orders.order_id) AS order_count,
            SUM(payment_value) AS total_payment,
            JULIANDAY(MIN(order_purchase_timestamp)) AS first_order_day,
            JULIANDAY(MAX(order_purchase_timestamp)) AS last_order_day
        FROM customers
            JOIN orders USING (customer_id)
            JOIN order_payments USING (order_id)
        GROUP BY customer_unique_id
    )
    SELECT
        customer_unique_id,
        zip_code_prefix,
        order_count AS PF,
        total_payment / order_count AS AOV,
        CASE
            WHEN (last_order_day - first_order_day) < 7 THEN
                1
            ELSE
                (last_order_day - first_order_day) / 7
        END AS ACL
    FROM CustomerData
),
-- Aggregate the coordinates before joining to prevent a Cartesian row explosion.
UniqueGeo AS (
    SELECT
        geolocation_zip_code_prefix AS zip_prefix,
        AVG(geolocation_lat) AS latitude,
        AVG(geolocation_lng) AS longitude
    FROM geolocation
    GROUP BY geolocation_zip_code_prefix
)
SELECT
    CLV.zip_code_prefix AS zip_prefix,
    AVG(PF * AOV * ACL) AS avg_CLV,
    COUNT(CLV.customer_unique_id) AS customer_count,
    UniqueGeo.latitude,
    UniqueGeo.longitude
FROM CLV
    JOIN UniqueGeo ON CLV.zip_code_prefix = UniqueGeo.zip_prefix
GROUP BY CLV.zip_code_prefix
