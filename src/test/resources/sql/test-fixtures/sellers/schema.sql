CREATE TABLE sellers (
    seller_id TEXT PRIMARY KEY
);

CREATE TABLE orders (
    order_id TEXT PRIMARY KEY
);

CREATE TABLE order_items (
    order_id TEXT NOT NULL,
    seller_id TEXT NOT NULL,
    price REAL
);

CREATE TABLE order_reviews (
    review_id TEXT PRIMARY KEY,
    order_id TEXT NOT NULL,
    review_score INTEGER
);
