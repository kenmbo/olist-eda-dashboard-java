SELECT
    UPPER(customer_city)
        AS city,
    AVG(JULIANDAY(order_approved_at) - JULIANDAY(order_purchase_timestamp))
        AS approved,
    AVG(JULIANDAY(order_delivered_carrier_date) - JULIANDAY(order_approved_at))
        AS delivered_to_carrier,
    AVG(JULIANDAY(order_delivered_customer_date) - JULIANDAY(order_delivered_carrier_date))
        AS delivered_to_customer,
    AVG(JULIANDAY(order_estimated_delivery_date) - JULIANDAY(order_delivered_customer_date))
        AS estimated_delivery
FROM orders
    JOIN customers USING (customer_id)
WHERE customer_city IN (
    'sao paulo',
    'rio de janeiro',
    'belo horizonte',
    'brasilia',
    'curitiba',
    'campinas',
    'porto alegre',
    'salvador',
    'guarulhos',
    'sao bernardo do campo'
)
GROUP BY customer_city
ORDER BY approved + delivered_to_carrier + delivered_to_customer DESC
