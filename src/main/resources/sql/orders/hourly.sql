WITH OrderDayHour AS (
    SELECT
        CASE STRFTIME('%w', order_purchase_timestamp)
            WHEN '1' THEN 'Mon'
            WHEN '2' THEN 'Tue'
            WHEN '3' THEN 'Wed'
            WHEN '4' THEN 'Thu'
            WHEN '5' THEN 'Fri'
            WHEN '6' THEN 'Sat'
            WHEN '0' THEN 'Sun'
        END AS day_of_week_name,
        CAST(STRFTIME('%w', order_purchase_timestamp) AS INTEGER) AS day_of_week_int,
        CAST(STRFTIME("%H", order_purchase_timestamp) AS INTEGER) AS hour
    FROM orders
)
SELECT
    day_of_week_name,
    COUNT(CASE WHEN hour = 0 THEN 1 END) AS "0",
    COUNT(CASE WHEN hour = 1 THEN 1 END) AS "1",
    COUNT(CASE WHEN hour = 2 THEN 1 END) AS "2",
    COUNT(CASE WHEN hour = 3 THEN 1 END) AS "3",
    COUNT(CASE WHEN hour = 4 THEN 1 END) AS "4",
    COUNT(CASE WHEN hour = 5 THEN 1 END) AS "5",
    COUNT(CASE WHEN hour = 6 THEN 1 END) AS "6",
    COUNT(CASE WHEN hour = 7 THEN 1 END) AS "7",
    COUNT(CASE WHEN hour = 8 THEN 1 END) AS "8",
    COUNT(CASE WHEN hour = 9 THEN 1 END) AS "9",
    COUNT(CASE WHEN hour = 10 THEN 1 END) AS "10",
    COUNT(CASE WHEN hour = 11 THEN 1 END) AS "11",
    COUNT(CASE WHEN hour = 12 THEN 1 END) AS "12",
    COUNT(CASE WHEN hour = 13 THEN 1 END) AS "13",
    COUNT(CASE WHEN hour = 14 THEN 1 END) AS "14",
    COUNT(CASE WHEN hour = 15 THEN 1 END) AS "15",
    COUNT(CASE WHEN hour = 16 THEN 1 END) AS "16",
    COUNT(CASE WHEN hour = 17 THEN 1 END) AS "17",
    COUNT(CASE WHEN hour = 18 THEN 1 END) AS "18",
    COUNT(CASE WHEN hour = 19 THEN 1 END) AS "19",
    COUNT(CASE WHEN hour = 20 THEN 1 END) AS "20",
    COUNT(CASE WHEN hour = 21 THEN 1 END) AS "21",
    COUNT(CASE WHEN hour = 22 THEN 1 END) AS "22",
    COUNT(CASE WHEN hour = 23 THEN 1 END) AS "23"
FROM OrderDayHour
GROUP BY day_of_week_int
ORDER BY day_of_week_int
