INSERT INTO customers (customer_id, customer_unique_id, customer_zip_code_prefix, customer_city, customer_state) VALUES
    ('shipping-salvador', 'shipping-salvador-unique', 1111, 'salvador', 'BA'),
    ('shipping-sao-paulo', 'shipping-sao-paulo-unique', 2222, 'sao paulo', 'SP'),
    ('shipping-excluded', 'shipping-excluded-unique', 3333, 'fortaleza', 'CE'),
    ('clv-a', 'clv-unique-a', 1003, 'sao paulo', 'SP'),
    ('clv-b', 'clv-unique-b', 1003, 'sao paulo', 'SP'),
    ('clv-c', 'clv-unique-c', 99990, 'salvador', 'BA'),
    ('clv-no-geolocation', 'clv-no-geolocation-unique', 5555, 'curitiba', 'PR');

INSERT INTO orders (
    order_id,
    customer_id,
    order_status,
    order_purchase_timestamp,
    order_approved_at,
    order_delivered_carrier_date,
    order_delivered_customer_date,
    order_estimated_delivery_date
) VALUES
    ('shipping-salvador-1', 'shipping-salvador', 'delivered', '2020-01-01 00:00:00', '2020-01-02 00:00:00', '2020-01-04 00:00:00', '2020-01-07 00:00:00', '2020-01-09 00:00:00'),
    ('shipping-salvador-2', 'shipping-salvador', 'delivered', '2020-01-01 00:00:00', '2020-01-03 00:00:00', '2020-01-06 00:00:00', '2020-01-12 00:00:00', '2020-01-15 00:00:00'),
    ('shipping-sao-paulo-1', 'shipping-sao-paulo', 'delivered', '2020-01-01 00:00:00', '2020-01-02 00:00:00', '2020-01-03 00:00:00', '2020-01-05 00:00:00', '2020-01-07 00:00:00'),
    ('shipping-fortaleza-1', 'shipping-excluded', 'delivered', '2020-01-01 00:00:00', '2020-01-11 00:00:00', '2020-01-21 00:00:00', '2020-01-31 00:00:00', '2020-02-10 00:00:00'),
    ('clv-a-1', 'clv-a', 'delivered', '2020-01-01 00:00:00', NULL, NULL, NULL, NULL),
    ('clv-a-2', 'clv-a', 'delivered', '2020-01-15 00:00:00', NULL, NULL, NULL, NULL),
    ('clv-b-1', 'clv-b', 'delivered', '2020-01-05 00:00:00', NULL, NULL, NULL, NULL),
    ('clv-c-1', 'clv-c', 'delivered', '2020-01-01 00:00:00', NULL, NULL, NULL, NULL),
    ('clv-c-2', 'clv-c', 'delivered', '2020-01-04 00:00:00', NULL, NULL, NULL, NULL),
    ('clv-no-geo-1', 'clv-no-geolocation', 'delivered', '2020-01-01 00:00:00', NULL, NULL, NULL, NULL);

INSERT INTO order_payments (order_id, payment_sequential, payment_type, payment_installments, payment_value) VALUES
    ('clv-a-1', 1, 'credit_card', 1, 100.0),
    ('clv-a-2', 1, 'credit_card', 1, 300.0),
    ('clv-b-1', 1, 'boleto', 1, 50.0),
    ('clv-c-1', 1, 'credit_card', 1, 200.0),
    ('clv-c-2', 1, 'credit_card', 1, 100.0),
    ('clv-no-geo-1', 1, 'credit_card', 1, 900.0);

INSERT INTO geolocation (
    geolocation_zip_code_prefix,
    geolocation_lat,
    geolocation_lng,
    geolocation_city,
    geolocation_state
) VALUES
    (1003, 1.0, 10.0, 'sao paulo', 'SP'),
    (1003, 3.0, 14.0, 'sao paulo', 'SP'),
    (99990, 5.0, 15.0, 'salvador', 'BA'),
    (99990, 7.0, 17.0, 'salvador', 'BA');
