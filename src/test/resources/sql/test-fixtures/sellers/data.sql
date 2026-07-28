INSERT INTO sellers (seller_id) VALUES
    ('seller-alpha'),
    ('seller-beta'),
    ('seller-excluded'),
    ('seller-no-items');

WITH RECURSIVE sequence(number) AS (
    SELECT 1
    UNION ALL
    SELECT number + 1 FROM sequence WHERE number < 11
)
INSERT INTO orders (order_id)
SELECT printf('alpha-%02d', number) FROM sequence;

WITH RECURSIVE sequence(number) AS (
    SELECT 1
    UNION ALL
    SELECT number + 1 FROM sequence WHERE number < 11
)
INSERT INTO order_items (order_id, seller_id, price)
SELECT printf('alpha-%02d', number), 'seller-alpha', 10.0 FROM sequence;

WITH RECURSIVE sequence(number) AS (
    SELECT 1
    UNION ALL
    SELECT number + 1 FROM sequence WHERE number < 11
)
INSERT INTO order_reviews (review_id, order_id, review_score)
SELECT printf('alpha-review-%02d', number), printf('alpha-%02d', number), 4 FROM sequence;

-- The extra review intentionally duplicates alpha-01 in the source query's LEFT JOIN.
INSERT INTO order_reviews (review_id, order_id, review_score)
VALUES ('alpha-review-extra', 'alpha-01', 2);

WITH RECURSIVE sequence(number) AS (
    SELECT 1
    UNION ALL
    SELECT number + 1 FROM sequence WHERE number < 11
)
INSERT INTO orders (order_id)
SELECT printf('beta-%02d', number) FROM sequence;

WITH RECURSIVE sequence(number) AS (
    SELECT 1
    UNION ALL
    SELECT number + 1 FROM sequence WHERE number < 11
)
INSERT INTO order_items (order_id, seller_id, price)
SELECT printf('beta-%02d', number), 'seller-beta', 5.5 FROM sequence;

WITH RECURSIVE sequence(number) AS (
    SELECT 1
    UNION ALL
    SELECT number + 1 FROM sequence WHERE number < 11
)
INSERT INTO order_reviews (review_id, order_id, review_score)
SELECT printf('beta-review-%02d', number), printf('beta-%02d', number), 5 FROM sequence;

WITH RECURSIVE sequence(number) AS (
    SELECT 1
    UNION ALL
    SELECT number + 1 FROM sequence WHERE number < 10
)
INSERT INTO orders (order_id)
SELECT printf('excluded-%02d', number) FROM sequence;

WITH RECURSIVE sequence(number) AS (
    SELECT 1
    UNION ALL
    SELECT number + 1 FROM sequence WHERE number < 10
)
INSERT INTO order_items (order_id, seller_id, price)
SELECT printf('excluded-%02d', number), 'seller-excluded', 7.0 FROM sequence;

WITH RECURSIVE sequence(number) AS (
    SELECT 1
    UNION ALL
    SELECT number + 1 FROM sequence WHERE number < 10
)
INSERT INTO order_reviews (review_id, order_id, review_score)
SELECT printf('excluded-review-%02d', number), printf('excluded-%02d', number), 3 FROM sequence;

INSERT INTO order_items (order_id, seller_id, price)
VALUES ('one-01', 'seller-one', 1.0);

WITH RECURSIVE sequence(number) AS (
    SELECT 1
    UNION ALL
    SELECT number + 1 FROM sequence WHERE number < 9
)
INSERT INTO order_items (order_id, seller_id, price)
SELECT printf('nine-%02d', number), 'seller-nine', 1.0 FROM sequence;

WITH RECURSIVE sequence(number) AS (
    SELECT 1
    UNION ALL
    SELECT number + 1 FROM sequence WHERE number < 10
)
INSERT INTO order_items (order_id, seller_id, price)
SELECT printf('ten-%02d', number), 'seller-ten', 1.0 FROM sequence;

WITH RECURSIVE sequence(number) AS (
    SELECT 1
    UNION ALL
    SELECT number + 1 FROM sequence WHERE number < 99
)
INSERT INTO order_items (order_id, seller_id, price)
SELECT printf('ninety-nine-%03d', number), 'seller-ninety-nine', 1.0 FROM sequence;

WITH RECURSIVE sequence(number) AS (
    SELECT 1
    UNION ALL
    SELECT number + 1 FROM sequence WHERE number < 100
)
INSERT INTO order_items (order_id, seller_id, price)
SELECT printf('hundred-%03d', number), 'seller-hundred', 1.0 FROM sequence;

WITH RECURSIVE sequence(number) AS (
    SELECT 1
    UNION ALL
    SELECT number + 1 FROM sequence WHERE number < 1000
)
INSERT INTO order_items (order_id, seller_id, price)
SELECT printf('thousand-%04d', number), 'seller-thousand', 1.0 FROM sequence;
