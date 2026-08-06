-- ============================================================
-- COMPUTER STORE - POSTGRESQL FULL DATABASE
-- Generated from project: ComputerStore_Full
-- Compatible with Spring Boot entities in Backend/model
-- Target database: ComputerStore
-- Image paths already fixed to match files in Backend/uploads/
-- ============================================================

BEGIN;

-- Re-run safely: remove dependent tables first
DROP TABLE IF EXISTS order_items CASCADE;
DROP TABLE IF EXISTS orders CASCADE;
DROP TABLE IF EXISTS products CASCADE;
DROP TABLE IF EXISTS users CASCADE;
DROP TABLE IF EXISTS rams CASCADE;
DROP TABLE IF EXISTS cpus CASCADE;
DROP TABLE IF EXISTS brands CASCADE;
DROP TABLE IF EXISTS categories CASCADE;

-- ============================================================
-- 1. TABLES
-- ============================================================

CREATE TABLE categories (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    img VARCHAR(500),
    status VARCHAR(50) NOT NULL DEFAULT 'ACTIVE'
);

CREATE TABLE brands (
    id BIGSERIAL PRIMARY KEY,
    brand_name VARCHAR(255) NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'ACTIVE'
);

CREATE TABLE cpus (
    id BIGSERIAL PRIMARY KEY,
    cpu_name VARCHAR(255) NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'ACTIVE'
);

CREATE TABLE rams (
    id BIGSERIAL PRIMARY KEY,
    ram_name VARCHAR(255) NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'ACTIVE'
);

CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    fullname VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    phone VARCHAR(20),
    password VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL DEFAULT 'USER',
    status VARCHAR(50) NOT NULL DEFAULT 'ACTIVE'
);

CREATE TABLE products (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    price DOUBLE PRECISION NOT NULL,
    stock INTEGER NOT NULL,
    image VARCHAR(500),
    description TEXT,
    status VARCHAR(50) NOT NULL DEFAULT 'ACTIVE',
    ssd VARCHAR(255),
    gpu VARCHAR(255),
    screen VARCHAR(255),
    os VARCHAR(255),
    category_id BIGINT,
    brand_id BIGINT,
    cpu_id BIGINT,
    ram_id BIGINT,
    CONSTRAINT fk_products_category FOREIGN KEY (category_id) REFERENCES categories(id),
    CONSTRAINT fk_products_brand FOREIGN KEY (brand_id) REFERENCES brands(id),
    CONSTRAINT fk_products_cpu FOREIGN KEY (cpu_id) REFERENCES cpus(id),
    CONSTRAINT fk_products_ram FOREIGN KEY (ram_id) REFERENCES rams(id)
);

CREATE TABLE orders (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT,
    customer_name VARCHAR(255) NOT NULL,
    phone VARCHAR(20),
    address TEXT,
    payment_method VARCHAR(100),
    subtotal DOUBLE PRECISION,
    vat DOUBLE PRECISION,
    total DOUBLE PRECISION,
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    order_date TIMESTAMP,
    cancellation_reason TEXT,
    CONSTRAINT fk_orders_user FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE order_items (
    id BIGSERIAL PRIMARY KEY,
    order_id BIGINT NOT NULL,
    product_id BIGINT,
    product_name VARCHAR(255),
    quantity INTEGER NOT NULL,
    price DOUBLE PRECISION,
    image VARCHAR(500),
    CONSTRAINT fk_order_items_order FOREIGN KEY (order_id) REFERENCES orders(id),
    CONSTRAINT fk_order_items_product FOREIGN KEY (product_id) REFERENCES products(id)
);

-- ============================================================
-- 2. SAMPLE DATA
-- ============================================================

INSERT INTO categories (name, description, img, status) VALUES
('Laptop Gaming', 'High-performance gaming laptops', 'http://localhost:8080/uploads/ASUS ROG Zephyrus G14.png', 'ACTIVE'),
('Laptop Văn Phòng', 'Office laptops for work', 'http://localhost:8080/uploads/HP Pavilion 15.png', 'ACTIVE'),
('Desktop Gaming', 'Gaming desktop computers', 'http://localhost:8080/uploads/MSI GE76 Raider.png', 'ACTIVE'),
('Desktop Văn Phòng', 'Office desktop computers', 'http://localhost:8080/uploads/Dell Inspiron 16.png', 'ACTIVE'),
('Workstation', 'Professional workstations', 'http://localhost:8080/uploads/Lenovo ThinkPad X1.png', 'ACTIVE'),
('Laptop Mỏng Nhẹ', 'Ultrabook and thin laptops', 'http://localhost:8080/uploads/Laptop Acer Swift 5.png', 'ACTIVE'),
('Máy tính bảng', 'Tablets and portable devices', 'http://localhost:8080/uploads/Samsung Galaxy Book4.png', 'ACTIVE'),
('PC Custom', 'Custom-built computers', 'http://localhost:8080/uploads/ASUS VivoBook 15.png', 'ACTIVE'),
('Server', 'Server computers', 'http://localhost:8080/uploads/LG UltraPC.png', 'ACTIVE'),
('Chromebook', 'Chrome OS laptops', 'http://localhost:8080/uploads/MacBook Pro 14.png', 'ACTIVE');

INSERT INTO brands (brand_name, status) VALUES
('ASUS', 'ACTIVE'),
('Dell', 'ACTIVE'),
('HP', 'ACTIVE'),
('Lenovo', 'ACTIVE'),
('Acer', 'ACTIVE'),
('MSI', 'ACTIVE'),
('Razer', 'ACTIVE'),
('Apple', 'ACTIVE'),
('Samsung', 'ACTIVE'),
('LG', 'ACTIVE');

INSERT INTO cpus (cpu_name, status) VALUES
('Intel Core i9-13900K', 'ACTIVE'),
('Intel Core i7-13700K', 'ACTIVE'),
('Intel Core i5-13600K', 'ACTIVE'),
('AMD Ryzen 9 7950X', 'ACTIVE'),
('AMD Ryzen 7 7700X', 'ACTIVE'),
('Intel Core i9-12900K', 'ACTIVE'),
('AMD Ryzen 5 7600X', 'ACTIVE'),
('Intel Core i3-13100', 'ACTIVE'),
('AMD Ryzen 3 7100', 'ACTIVE'),
('Intel Xeon Platinum', 'ACTIVE');

INSERT INTO rams (ram_name, status) VALUES
('DDR5 32GB 6000MHz', 'ACTIVE'),
('DDR5 16GB 6000MHz', 'ACTIVE'),
('DDR5 8GB 6000MHz', 'ACTIVE'),
('DDR4 32GB 3600MHz', 'ACTIVE'),
('DDR4 16GB 3600MHz', 'ACTIVE'),
('DDR4 8GB 3600MHz', 'ACTIVE'),
('DDR5 64GB 6000MHz', 'ACTIVE'),
('DDR5 24GB 6000MHz', 'ACTIVE'),
('DDR4 48GB 3600MHz', 'ACTIVE'),
('LPDDR5 12GB 6400MHz', 'ACTIVE');

-- Passwords are plain text because the current AuthService compares strings directly.
INSERT INTO users (fullname, email, phone, password, role, status) VALUES
('Nguyễn Văn A', 'admin@computerstore.com', '0901234567', 'admin123', 'ADMIN', 'ACTIVE'),
('Trần Thị B', 'user1@computerstore.com', '0902345678', 'user123', 'USER', 'ACTIVE'),
('Phạm Văn C', 'user2@computerstore.com', '0903456789', 'user123', 'USER', 'ACTIVE'),
('Đỗ Thị D', 'user3@computerstore.com', '0904567890', 'user123', 'USER', 'ACTIVE'),
('Bùi Văn E', 'user4@computerstore.com', '0905678901', 'user123', 'USER', 'ACTIVE');

INSERT INTO products
(name, price, stock, image, description, status, ssd, gpu, screen, os, category_id, brand_id, cpu_id, ram_id)
VALUES
('ASUS ROG Zephyrus G14', 25000000, 15, 'http://localhost:8080/uploads/ASUS ROG Zephyrus G14.png', 'Gaming laptop mạnh mẽ', 'ACTIVE', '1TB NVMe', 'NVIDIA RTX 4090', '14 inch QHD+', 'Windows 11', 1, 1, 1, 1),
('Dell XPS 15', 22000000, 12, 'http://localhost:8080/uploads/Dell XPS 15.png', 'Laptop cao cấp cho designer', 'ACTIVE', '512GB NVMe', 'NVIDIA RTX 4080', '15.6 inch OLED', 'Windows 11', 1, 2, 2, 2),
('HP Pavilion 15', 12000000, 25, 'http://localhost:8080/uploads/HP Pavilion 15.png', 'Laptop văn phòng thông dụng', 'ACTIVE', '256GB SSD', 'Intel UHD', '15.6 inch FHD', 'Windows 11', 2, 3, 3, 3),
('Lenovo ThinkPad X1', 18000000, 20, 'http://localhost:8080/uploads/Lenovo ThinkPad X1.png', 'Laptop doanh nhân đỉnh cao', 'ACTIVE', '512GB SSD', 'Intel Iris', '14 inch 2K', 'Windows 11', 2, 4, 2, 2),
('Acer Swift 5', 15000000, 18, 'http://localhost:8080/uploads/Laptop Acer Swift 5.png', 'Ultrabook nhẹ và mạnh', 'ACTIVE', '512GB SSD', 'Intel Iris', '14 inch FHD', 'Windows 11', 6, 5, 3, 2),
('MSI GE76 Raider', 24000000, 10, 'http://localhost:8080/uploads/MSI GE76 Raider.png', 'Gaming laptop pro', 'ACTIVE', '1TB NVMe', 'NVIDIA RTX 4080 Ti', '17 inch FHD 240Hz', 'Windows 11', 1, 6, 1, 1),
('Razer Blade 15', 26000000, 8, 'http://localhost:8080/uploads/Razer Blade 15.png', 'Gaming laptop siêu mỏng', 'ACTIVE', '1TB SSD', 'NVIDIA RTX 4090', '15.6 inch QHD', 'Windows 11', 1, 7, 1, 1),
('MacBook Pro 14', 28000000, 5, 'http://localhost:8080/uploads/MacBook Pro 14.png', 'Laptop sáng tạo chuyên nghiệp', 'ACTIVE', '512GB SSD', 'Apple M3 Pro', '14 inch Liquid Retina', 'macOS', 6, 8, 1, 2),
('Samsung Galaxy Book4', 16000000, 14, 'http://localhost:8080/uploads/Samsung Galaxy Book4.png', 'AMOLED laptop từ Samsung', 'ACTIVE', '256GB SSD', 'Intel Arc', '14 inch AMOLED', 'Windows 11', 2, 9, 3, 3),
('LG UltraPC', 17000000, 11, 'http://localhost:8080/uploads/LG UltraPC.png', 'Laptop với màn hình OLED', 'ACTIVE', '512GB SSD', 'Intel Iris', '14 inch OLED', 'Windows 11', 6, 10, 2, 2),
('ASUS VivoBook 15', 11000000, 30, 'http://localhost:8080/uploads/ASUS VivoBook 15.png', 'Laptop nhập môn giá tốt', 'ACTIVE', '256GB SSD', 'AMD Radeon', '15.6 inch FHD', 'Windows 11', 2, 1, 7, 3),
('Dell Inspiron 16', 13000000, 22, 'http://localhost:8080/uploads/Dell Inspiron 16.png', 'Laptop 16 inch cho công việc', 'ACTIVE', '512GB SSD', 'Intel UHD', '16 inch FHD', 'Windows 11', 2, 2, 3, 2),
('HP Spectre x360', 20000000, 9, 'http://localhost:8080/uploads/HP Spectre x360.png', 'Convertible laptop cao cấp', 'ACTIVE', '512GB SSD', 'Intel Iris', '13.5 inch 3K OLED', 'Windows 11', 6, 3, 2, 2),
('Lenovo IdeaPad Gaming 3', 14000000, 19, 'http://localhost:8080/uploads/Lenovo IdeaPad Gaming 3.png', 'Gaming laptop tầm trung', 'ACTIVE', '512GB SSD', 'NVIDIA RTX 4050', '15.6 inch 144Hz', 'Windows 11', 1, 4, 5, 2),
('Acer Nitro 5', 16000000, 16, 'http://localhost:8080/uploads/Acer Nitro 5.png', 'Gaming laptop giá rẻ', 'ACTIVE', '512GB SSD', 'NVIDIA RTX 4060', '15.6 inch 144Hz', 'Windows 11', 1, 5, 5, 2),
('ASUS TUF Gaming A16', 23000000, 7, 'http://localhost:8080/uploads/ASUS TUF Gaming A16.png', 'Gaming laptop bền bỉ', 'ACTIVE', '1TB SSD', 'NVIDIA RTX 4070', '16 inch 165Hz', 'Windows 11', 1, 1, 4, 1),
('Dell G15', 18000000, 13, 'http://localhost:8080/uploads/Dell G15.png', 'Gaming laptop cân bằng', 'ACTIVE', '512GB SSD', 'NVIDIA RTX 4050 Ti', '15.6 inch 165Hz', 'Windows 11', 1, 2, 4, 2),
('HP Victus 15', 17000000, 15, 'http://localhost:8080/uploads/HP Victus 15.png', 'Gaming laptop HP', 'ACTIVE', '512GB SSD', 'NVIDIA RTX 4050', '15.6 inch 144Hz', 'Windows 11', 1, 3, 5, 2),
('Lenovo Legion 7i', 24000000, 6, 'http://localhost:8080/uploads/Lenovo Legion 7i.png', 'Gaming laptop flagship Lenovo', 'ACTIVE', '1TB SSD', 'NVIDIA RTX 4090', '16 inch QHD+ 240Hz', 'Windows 11', 1, 4, 1, 1),
('MSI Stealth 17', 25000000, 8, 'http://localhost:8080/uploads/MSI Stealth 17.png', 'Gaming laptop mỏng nhẹ MSI', 'ACTIVE', '1TB SSD', 'NVIDIA RTX 4080', '17 inch FHD 240Hz', 'Windows 11', 1, 6, 1, 1);

INSERT INTO orders
(user_id, customer_name, phone, address, payment_method, subtotal, vat, total, status, order_date, cancellation_reason)
VALUES
(2, 'Trần Thị B', '0902345678', '123 Nguyễn Huệ, TP HCM', 'Credit Card', 25000000, 2500000, 27500000, 'PENDING', CURRENT_TIMESTAMP, NULL),
(3, 'Phạm Văn C', '0903456789', '456 Lê Lợi, Hà Nội', 'Bank Transfer', 22000000, 2200000, 24200000, 'CONFIRMED', CURRENT_TIMESTAMP, NULL),
(4, 'Đỗ Thị D', '0904567890', '789 Trần Hưng Đạo, Đà Nẵng', 'E-Wallet', 12000000, 1200000, 13200000, 'SHIPPED', CURRENT_TIMESTAMP, NULL),
(5, 'Bùi Văn E', '0905678901', '321 Nguyễn Trãi, Cần Thơ', 'Credit Card', 18000000, 1800000, 19800000, 'DELIVERED', CURRENT_TIMESTAMP, NULL),
(2, 'Trần Thị B', '0902345678', '123 Nguyễn Huệ, TP HCM', 'Bank Transfer', 15000000, 1500000, 16500000, 'PENDING', CURRENT_TIMESTAMP, NULL);

INSERT INTO order_items
(order_id, product_id, product_name, quantity, price, image)
VALUES
(1, 1, 'ASUS ROG Zephyrus G14', 1, 25000000, 'http://localhost:8080/uploads/ASUS ROG Zephyrus G14.png'),
(2, 2, 'Dell XPS 15', 1, 22000000, 'http://localhost:8080/uploads/Dell XPS 15.png'),
(3, 3, 'HP Pavilion 15', 1, 12000000, 'http://localhost:8080/uploads/HP Pavilion 15.png'),
(4, 4, 'Lenovo ThinkPad X1', 1, 18000000, 'http://localhost:8080/uploads/Lenovo ThinkPad X1.png'),
(5, 5, 'Acer Swift 5', 1, 15000000, 'http://localhost:8080/uploads/Laptop Acer Swift 5.png');

-- ============================================================
-- 3. INDEXES
-- ============================================================

CREATE INDEX idx_products_category ON products(category_id);
CREATE INDEX idx_products_brand ON products(brand_id);
CREATE INDEX idx_products_cpu ON products(cpu_id);
CREATE INDEX idx_products_ram ON products(ram_id);
CREATE INDEX idx_products_status ON products(status);
CREATE INDEX idx_orders_user ON orders(user_id);
CREATE INDEX idx_orders_status ON orders(status);
CREATE INDEX idx_order_items_order ON order_items(order_id);
CREATE INDEX idx_order_items_product ON order_items(product_id);
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_status ON users(status);

COMMIT;

-- ============================================================
-- QUICK CHECKS
-- ============================================================
-- SELECT current_database();
-- SELECT COUNT(*) AS categories FROM categories;
-- SELECT COUNT(*) AS brands FROM brands;
-- SELECT COUNT(*) AS cpus FROM cpus;
-- SELECT COUNT(*) AS rams FROM rams;
-- SELECT COUNT(*) AS users FROM users;
-- SELECT COUNT(*) AS products FROM products;
-- SELECT COUNT(*) AS orders FROM orders;
-- SELECT COUNT(*) AS order_items FROM order_items;
-- ============================================================
