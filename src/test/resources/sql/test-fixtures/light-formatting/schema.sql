CREATE TABLE order_items (
    order_id TEXT NOT NULL,
    seller_id TEXT NOT NULL,
    price REAL NOT NULL
);

CREATE TABLE order_reviews (
    review_id TEXT NOT NULL,
    order_id TEXT NOT NULL,
    review_score INTEGER
);

CREATE TABLE leads_qualified (
    mql_id TEXT NOT NULL,
    origin TEXT
);

CREATE TABLE leads_closed (
    mql_id TEXT NOT NULL
);

CREATE TABLE customers (
    customer_id TEXT NOT NULL,
    customer_city TEXT NOT NULL
);

CREATE TABLE orders (
    order_id TEXT NOT NULL,
    customer_id TEXT NOT NULL,
    order_status TEXT NOT NULL,
    order_purchase_timestamp TEXT,
    order_approved_at TEXT,
    order_delivered_carrier_date TEXT,
    order_delivered_customer_date TEXT
);
