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
    COUNT(seller_id) AS seller_count
FROM BucketedSellers
GROUP BY bucket
-- The FastAPI query had no ORDER BY. Preserve the captured bucket sequence deliberately.
ORDER BY CASE bucket
    WHEN '1-9 orders' THEN 1
    WHEN '10-99 orders' THEN 2
    WHEN '100-999 orders' THEN 3
    WHEN '1000+ orders' THEN 4
END
