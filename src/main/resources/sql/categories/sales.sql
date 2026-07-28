WITH RankedCategories AS (
    SELECT
        product_category_name_english AS category,
        SUM(price) AS sales,
        RANK() OVER (ORDER BY SUM(price) DESC) AS rank
    FROM order_items
        JOIN orders USING (order_id)
        JOIN products USING (product_id)
        JOIN product_category_name_translation USING (product_category_name)
    WHERE order_status = 'delivered'
    GROUP BY product_category_name_english
)
SELECT
    category,
    sales
FROM RankedCategories
WHERE rank <= 18
-- Other categories, aggregated
UNION ALL
SELECT
    'Other categories' AS category,
    SUM(sales) AS sales
FROM RankedCategories
WHERE rank > 18
