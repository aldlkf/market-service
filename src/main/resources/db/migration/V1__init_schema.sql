CREATE TABLE users(
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(250) NOT NULL,
    balance DECIMAL(19, 2) NOT NULL
);

CREATE TABLE products(
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(100) NOT NULL,
    price DECIMAL(19, 2) NOT NULL,
    stock_quantity INT NOT NULL
);

CREATE TABLE orders(
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    quantity INT NOT NULL,
    total_price DECIMAL(19, 2) NOT NULL,
    status VARCHAR(50) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_orders_user FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT fk_orders_product FOREIGN KEY (product_id) REFERENCES products (id)

);