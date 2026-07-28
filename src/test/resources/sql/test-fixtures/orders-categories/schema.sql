CREATE TABLE orders (
    order_id TEXT PRIMARY KEY,
    order_status TEXT NOT NULL,
    order_purchase_timestamp TEXT
);

CREATE TABLE order_items (
    order_id TEXT NOT NULL,
    product_id TEXT NOT NULL,
    price REAL NOT NULL,
    freight_value REAL NOT NULL
);

CREATE TABLE products (
    product_id TEXT PRIMARY KEY,
    product_category_name TEXT
);

CREATE TABLE product_category_name_translation (
    product_category_name TEXT PRIMARY KEY,
    product_category_name_english TEXT NOT NULL
);
