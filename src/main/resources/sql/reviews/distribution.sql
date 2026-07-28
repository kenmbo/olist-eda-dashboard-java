SELECT
    review_score,
    COUNT(review_id) AS total_reviews
FROM order_reviews
WHERE review_score IS NOT NULL
GROUP BY review_score
ORDER BY review_score ASC
