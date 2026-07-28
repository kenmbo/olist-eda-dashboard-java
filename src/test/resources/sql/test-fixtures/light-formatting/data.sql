INSERT INTO order_items (order_id, seller_id, price) VALUES
    ('seller-a-1', 'seller-alpha', 10.005),
    ('seller-a-2', 'seller-alpha', 10.005),
    ('seller-a-3', 'seller-alpha', 10.005),
    ('seller-a-4', 'seller-alpha', 10.005),
    ('seller-a-5', 'seller-alpha', 10.005),
    ('seller-a-6', 'seller-alpha', 10.005),
    ('seller-b-1', 'seller-beta', 1.0),
    ('seller-b-2', 'seller-beta', 1.0),
    ('seller-b-3', 'seller-beta', 1.0),
    ('seller-b-4', 'seller-beta', 1.0),
    ('seller-b-5', 'seller-beta', 1.0),
    ('seller-b-6', 'seller-beta', 1.0),
    ('seller-small-1', 'seller-small', 9.0),
    ('seller-small-2', 'seller-small', 9.0),
    ('seller-small-3', 'seller-small', 9.0),
    ('seller-small-4', 'seller-small', 9.0),
    ('seller-small-5', 'seller-small', 9.0);

INSERT INTO order_reviews (review_id, order_id, review_score) VALUES
    ('review-a-1', 'seller-a-1', 4),
    ('review-a-2', 'seller-a-2', 4),
    ('review-a-3', 'seller-a-3', 4),
    ('review-a-4', 'seller-a-4', 4),
    ('review-a-5', 'seller-a-5', 4),
    ('review-a-6', 'seller-a-6', 4),
    ('review-b-1', 'seller-b-1', 5),
    ('review-b-2', 'seller-b-2', 5),
    ('review-b-3', 'seller-b-3', 5),
    ('review-b-4', 'seller-b-4', 5),
    ('review-b-5', 'seller-b-5', 5),
    ('review-b-6', 'seller-b-6', 5),
    ('review-small-1', 'seller-small-1', 1),
    ('review-small-2', 'seller-small-2', 1),
    ('review-small-3', 'seller-small-3', 1),
    ('review-small-4', 'seller-small-4', 1),
    ('review-small-5', 'seller-small-5', 1),
    ('review-extra-1', 'review-only-1', 1),
    ('review-extra-2', 'review-only-2', 1),
    ('review-extra-3', 'review-only-3', 2),
    ('review-extra-4', 'review-only-4', 3),
    ('review-extra-5', 'review-only-5', 3),
    ('review-extra-6', 'review-only-6', 3);

INSERT INTO leads_qualified (mql_id, origin) VALUES
    ('organic-1', 'organic_search'),
    ('organic-2', 'organic_search'),
    ('organic-3', 'organic_search'),
    ('organic-4', 'organic_search'),
    ('paid-1', 'paid_search'),
    ('paid-2', 'paid_search'),
    ('paid-3', 'paid_search'),
    ('unknown-1', NULL),
    ('unknown-2', NULL),
    ('other-1', 'other');

INSERT INTO leads_closed (mql_id) VALUES
    ('organic-1'),
    ('organic-1'),
    ('organic-2'),
    ('paid-1'),
    ('unknown-1'),
    ('other-1');

INSERT INTO customers (customer_id, customer_city) VALUES
    ('customer-sao', 'sao paulo'),
    ('customer-rio', 'rio de janeiro'),
    ('customer-incomplete', 'incomplete city');

INSERT INTO orders (
    order_id,
    customer_id,
    order_status,
    order_purchase_timestamp,
    order_approved_at,
    order_delivered_carrier_date,
    order_delivered_customer_date
) VALUES
    ('delivery-sao-1', 'customer-sao', 'delivered', '2018-01-01 00:00:00', '2018-01-02 00:00:00', '2018-01-04 00:00:00', '2018-01-08 00:00:00'),
    ('delivery-sao-2', 'customer-sao', 'delivered', '2018-01-02 00:00:00', '2018-01-02 12:00:00', '2018-01-04 12:00:00', '2018-01-07 12:00:00'),
    ('delivery-sao-3', 'customer-sao', 'delivered', '2018-01-03 00:00:00', '2018-01-04 00:00:00', '2018-01-06 00:00:00', '2018-01-10 00:00:00'),
    ('delivery-rio-1', 'customer-rio', 'delivered', '2018-01-01 00:00:00', '2018-01-02 00:00:00', '2018-01-03 00:00:00', '2018-01-05 00:00:00'),
    ('delivery-rio-2', 'customer-rio', 'delivered', '2018-01-02 00:00:00', '2018-01-02 00:00:00', '2018-01-03 00:00:00', '2018-01-06 00:00:00'),
    ('delivery-incomplete', 'customer-incomplete', 'delivered', '2018-01-01 00:00:00', NULL, NULL, NULL);
