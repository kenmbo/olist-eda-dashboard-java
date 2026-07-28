WITH CityVolumes AS (
    -- Get the Top 10 cities by order volume first
    SELECT
        c.customer_city AS city,
        COUNT(o.order_id) AS order_count
    FROM orders o
    JOIN customers c ON o.customer_id = c.customer_id
    WHERE o.order_status = 'delivered'
    GROUP BY c.customer_city
    ORDER BY order_count DESC
    LIMIT 10
)
SELECT
    c.customer_city AS city,
    AVG(julianday(o.order_approved_at) - julianday(o.order_purchase_timestamp)) AS approval_days,
    AVG(julianday(o.order_delivered_carrier_date) - julianday(o.order_approved_at)) AS carrier_days,
    AVG(julianday(o.order_delivered_customer_date) - julianday(o.order_delivered_carrier_date)) AS transit_days
FROM orders o
JOIN customers c ON o.customer_id = c.customer_id
JOIN CityVolumes cv ON c.customer_city = cv.city
WHERE o.order_status = 'delivered'
  AND o.order_approved_at IS NOT NULL
  AND o.order_delivered_carrier_date IS NOT NULL
  AND o.order_delivered_customer_date IS NOT NULL
GROUP BY c.customer_city
ORDER BY cv.order_count DESC
