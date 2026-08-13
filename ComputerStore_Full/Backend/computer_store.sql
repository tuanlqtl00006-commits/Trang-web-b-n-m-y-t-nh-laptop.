-- ============================================================
-- COMPUTER STORE - SQL SERVER (T-SQL) FULL DATABASE
-- Generated from project: ComputerStore_Full
-- Compatible with Spring Boot entities in Backend/model
-- Target database: ComputerStore
-- Run with sqlcmd, Azure Data Studio, or SSMS (NOT MySQL client)
-- ============================================================

IF DB_ID(N'ComputerStore') IS NULL
BEGIN
    CREATE DATABASE ComputerStore;
END
GO

USE ComputerStore;
GO

-- Re-run safely: remove dependent tables first (order matters for FKs)
DROP TABLE IF EXISTS order_items;
DROP TABLE IF EXISTS orders;
DROP TABLE IF EXISTS products;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS rams;
DROP TABLE IF EXISTS cpus;
DROP TABLE IF EXISTS brands;
DROP TABLE IF EXISTS categories;
GO

-- ============================================================
-- 1. TABLES
-- ============================================================

CREATE TABLE categories (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    name NVARCHAR(255) NOT NULL,
    description NVARCHAR(MAX),
    img NVARCHAR(500),
    status NVARCHAR(50) NOT NULL DEFAULT N'ACTIVE'
);

CREATE TABLE brands (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    brand_name NVARCHAR(255) NOT NULL,
    status NVARCHAR(50) NOT NULL DEFAULT N'ACTIVE'
);

CREATE TABLE cpus (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    cpu_name NVARCHAR(255) NOT NULL,
    status NVARCHAR(50) NOT NULL DEFAULT N'ACTIVE'
);

CREATE TABLE rams (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    ram_name NVARCHAR(255) NOT NULL,
    status NVARCHAR(50) NOT NULL DEFAULT N'ACTIVE'
);

CREATE TABLE users (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    fullname NVARCHAR(255) NOT NULL,
    email NVARCHAR(255) NOT NULL UNIQUE,
    phone NVARCHAR(20),
    password NVARCHAR(255) NOT NULL,
    role NVARCHAR(50) NOT NULL DEFAULT N'USER',
    status NVARCHAR(50) NOT NULL DEFAULT N'ACTIVE'
);

CREATE TABLE products (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    name NVARCHAR(255) NOT NULL,
    price FLOAT NOT NULL,
    stock INT NOT NULL,
    image NVARCHAR(500),
    description NVARCHAR(MAX),
    status NVARCHAR(50) NOT NULL DEFAULT N'ACTIVE',
    ssd NVARCHAR(255),
    gpu NVARCHAR(255),
    screen NVARCHAR(255),
    os NVARCHAR(255),
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
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    user_id BIGINT,
    customer_name NVARCHAR(255) NOT NULL,
    phone NVARCHAR(20),
    address NVARCHAR(MAX),
    payment_method NVARCHAR(100),
    subtotal FLOAT,
    vat FLOAT,
    total FLOAT,
    status NVARCHAR(50) NOT NULL DEFAULT N'PENDING',
    order_date DATETIME2,
    cancellation_reason NVARCHAR(MAX),
    CONSTRAINT fk_orders_user FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE order_items (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    order_id BIGINT NOT NULL,
    product_id BIGINT,
    product_name NVARCHAR(255),
    quantity INT NOT NULL,
    price FLOAT,
    image NVARCHAR(500),
    CONSTRAINT fk_order_items_order FOREIGN KEY (order_id) REFERENCES orders(id),
    CONSTRAINT fk_order_items_product FOREIGN KEY (product_id) REFERENCES products(id)
);

-- ============================================================
-- 2. SAMPLE DATA
-- ============================================================

-- 4 danh mục sát với sản phẩm thực tế đang bán (loại bỏ các danh mục không có
-- sản phẩm nào và ảnh minh họa không khớp như trước: Máy tính bảng, Chromebook,
-- PC Custom, Server, Desktop... khi cửa hàng chỉ đang bán laptop).
INSERT INTO categories (name, description, img, status) VALUES
(N'Laptop Gaming', N'Laptop cấu hình mạnh cho chơi game, đồ họa nặng', N'http://localhost:8080/uploads/ASUS ROG Zephyrus G14.png', N'ACTIVE'),
(N'Laptop Văn Phòng', N'Laptop phổ thông cho học tập, văn phòng', N'http://localhost:8080/uploads/HP Pavilion 15.png', N'ACTIVE'),
(N'Laptop Mỏng Nhẹ', N'Ultrabook mỏng nhẹ, thời lượng pin tốt', N'http://localhost:8080/uploads/Laptop Acer Swift 5.png', N'ACTIVE'),
(N'Laptop Doanh Nhân & Đồ Họa', N'Laptop cao cấp cho doanh nhân và dân thiết kế', N'http://localhost:8080/uploads/Dell XPS 15.png', N'ACTIVE');

-- Bỏ Apple: toàn bộ máy trong cửa hàng chạy Windows 10/11 (xem ràng buộc OS bên dưới).
INSERT INTO brands (brand_name, status) VALUES
(N'ASUS', N'ACTIVE'),
(N'Dell', N'ACTIVE'),
(N'HP', N'ACTIVE'),
(N'Lenovo', N'ACTIVE'),
(N'Acer', N'ACTIVE'),
(N'MSI', N'ACTIVE'),
(N'Razer', N'ACTIVE'),
(N'Samsung', N'ACTIVE'),
(N'LG', N'ACTIVE');

-- Dùng đúng dòng chip cho laptop (hậu tố H/HX/HS/U) thay vì chip desktop
-- không dùng trong laptop (K-series) như dữ liệu mẫu cũ.
INSERT INTO cpus (cpu_name, status) VALUES
(N'Intel Core i9-13900HX', N'ACTIVE'),
(N'Intel Core i7-13700H', N'ACTIVE'),
(N'Intel Core i5-13500H', N'ACTIVE'),
(N'Intel Core i3-1315U', N'ACTIVE'),
(N'AMD Ryzen 9 7945HX', N'ACTIVE'),
(N'AMD Ryzen 7 7735HS', N'ACTIVE'),
(N'AMD Ryzen 5 7535HS', N'ACTIVE');

INSERT INTO rams (ram_name, status) VALUES
(N'8GB DDR4 3200MHz', N'ACTIVE'),
(N'16GB DDR4 3200MHz', N'ACTIVE'),
(N'16GB DDR5 4800MHz', N'ACTIVE'),
(N'32GB DDR5 4800MHz', N'ACTIVE'),
(N'32GB DDR4 3200MHz', N'ACTIVE');

-- Passwords are plain text because the current AuthService compares strings directly.
INSERT INTO users (fullname, email, phone, password, role, status) VALUES
(N'Nguyễn Văn A', N'admin@computerstore.com', N'0901234567', N'admin123', N'ADMIN', N'ACTIVE'),
(N'Lê Thị Nhân Viên', N'staff@computerstore.com', N'0909876543', N'staff123', N'STAFF', N'ACTIVE'),
(N'Trần Thị B', N'user1@computerstore.com', N'0902345678', N'user123', N'USER', N'ACTIVE'),
(N'Phạm Văn C', N'user2@computerstore.com', N'0903456789', N'user123', N'USER', N'ACTIVE'),
(N'Đỗ Thị D', N'user3@computerstore.com', N'0904567890', N'user123', N'USER', N'ACTIVE'),
(N'Bùi Văn E', N'user4@computerstore.com', N'0905678901', N'user123', N'USER', N'ACTIVE');

-- Danh sách sản phẩm rút gọn (tồn kho vừa phải, không nhập quá nhiều), giá
-- trong khoảng 9.000.000đ - 300.000.000đ, hệ điều hành chỉ Windows 10/11, và
-- CPU/RAM/danh mục/thương hiệu khớp với các bảng danh mục đã làm gọn ở trên.
-- 5 sản phẩm đầu giữ nguyên tên/thứ tự (id 1-5) vì order_items bên dưới đang
-- tham chiếu tới các id này.
INSERT INTO products
(name, price, stock, image, description, status, ssd, gpu, screen, os, category_id, brand_id, cpu_id, ram_id)
VALUES
(N'ASUS ROG Zephyrus G14', 25000000, 15, N'http://localhost:8080/uploads/ASUS ROG Zephyrus G14.png', N'Gaming laptop mạnh mẽ', N'ACTIVE', N'1TB NVMe', N'NVIDIA RTX 4090', N'14 inch QHD+', N'Windows 11', 1, 1, 5, 4),
(N'Dell XPS 15', 22000000, 12, N'http://localhost:8080/uploads/Dell XPS 15.png', N'Laptop cao cấp cho designer', N'ACTIVE', N'512GB NVMe', N'NVIDIA RTX 4050', N'15.6 inch OLED', N'Windows 11', 4, 2, 2, 3),
(N'HP Pavilion 15', 12000000, 20, N'http://localhost:8080/uploads/HP Pavilion 15.png', N'Laptop văn phòng thông dụng', N'ACTIVE', N'256GB SSD', N'Intel UHD', N'15.6 inch FHD', N'Windows 11', 2, 3, 4, 1),
(N'Lenovo ThinkPad X1', 30000000, 10, N'http://localhost:8080/uploads/Lenovo ThinkPad X1.png', N'Laptop doanh nhân đỉnh cao', N'ACTIVE', N'512GB SSD', N'Intel Iris Xe', N'14 inch 2K', N'Windows 11', 4, 4, 2, 3),
(N'Acer Swift 5', 15000000, 18, N'http://localhost:8080/uploads/Laptop Acer Swift 5.png', N'Ultrabook nhẹ và mạnh', N'ACTIVE', N'512GB SSD', N'Intel Iris Xe', N'14 inch FHD', N'Windows 11', 3, 5, 3, 2),
(N'MSI GE76 Raider', 24000000, 8, N'http://localhost:8080/uploads/MSI GE76 Raider.png', N'Gaming laptop pro', N'ACTIVE', N'1TB NVMe', N'NVIDIA RTX 4080', N'17 inch FHD 240Hz', N'Windows 11', 1, 6, 1, 4),
(N'Razer Blade 15', 26000000, 6, N'http://localhost:8080/uploads/Razer Blade 15.png', N'Gaming laptop siêu mỏng', N'ACTIVE', N'1TB SSD', N'NVIDIA RTX 4090', N'15.6 inch QHD', N'Windows 11', 1, 7, 1, 4),
(N'Samsung Galaxy Book4', 16000000, 14, N'http://localhost:8080/uploads/Samsung Galaxy Book4.png', N'AMOLED laptop từ Samsung', N'ACTIVE', N'256GB SSD', N'Intel Arc', N'14 inch AMOLED', N'Windows 10', 2, 8, 4, 1),
(N'LG UltraPC', 17000000, 11, N'http://localhost:8080/uploads/LG UltraPC.png', N'Laptop với màn hình OLED', N'ACTIVE', N'512GB SSD', N'Intel Iris Xe', N'14 inch OLED', N'Windows 11', 3, 9, 2, 3),
(N'ASUS VivoBook 15', 11000000, 20, N'http://localhost:8080/uploads/ASUS VivoBook 15.png', N'Laptop nhập môn giá tốt', N'ACTIVE', N'256GB SSD', N'AMD Radeon', N'15.6 inch FHD', N'Windows 10', 2, 1, 7, 2);

INSERT INTO orders
(user_id, customer_name, phone, address, payment_method, subtotal, vat, total, status, order_date, cancellation_reason)
VALUES
(3, N'Trần Thị B', N'0902345678', N'123 Nguyễn Huệ, TP HCM', N'Credit Card', 25000000, 2500000, 27500000, N'PENDING', SYSDATETIME(), NULL),
(4, N'Phạm Văn C', N'0903456789', N'456 Lê Lợi, Hà Nội', N'Bank Transfer', 22000000, 2200000, 24200000, N'CONFIRMED', SYSDATETIME(), NULL),
(5, N'Đỗ Thị D', N'0904567890', N'789 Trần Hưng Đạo, Đà Nẵng', N'E-Wallet', 12000000, 1200000, 13200000, N'SHIPPING', SYSDATETIME(), NULL),
(6, N'Bùi Văn E', N'0905678901', N'321 Nguyễn Trãi, Cần Thơ', N'Credit Card', 18000000, 1800000, 19800000, N'COMPLETED', SYSDATETIME(), NULL),
(3, N'Trần Thị B', N'0902345678', N'123 Nguyễn Huệ, TP HCM', N'Bank Transfer', 15000000, 1500000, 16500000, N'PENDING', SYSDATETIME(), NULL);

INSERT INTO order_items
(order_id, product_id, product_name, quantity, price, image)
VALUES
(1, 1, N'ASUS ROG Zephyrus G14', 1, 25000000, N'http://localhost:8080/uploads/ASUS ROG Zephyrus G14.png'),
(2, 2, N'Dell XPS 15', 1, 22000000, N'http://localhost:8080/uploads/Dell XPS 15.png'),
(3, 3, N'HP Pavilion 15', 1, 12000000, N'http://localhost:8080/uploads/HP Pavilion 15.png'),
(4, 4, N'Lenovo ThinkPad X1', 1, 18000000, N'http://localhost:8080/uploads/Lenovo ThinkPad X1.png'),
(5, 5, N'Acer Swift 5', 1, 15000000, N'http://localhost:8080/uploads/Laptop Acer Swift 5.png');

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


-- ============================================================
-- QUICK CHECKS
-- ============================================================
-- SELECT DB_NAME();
-- SELECT COUNT(*) AS categories FROM categories;
-- SELECT COUNT(*) AS brands FROM brands;
-- SELECT COUNT(*) AS cpus FROM cpus;
-- SELECT COUNT(*) AS rams FROM rams;
-- SELECT COUNT(*) AS users FROM users;
-- SELECT COUNT(*) AS products FROM products;
-- SELECT COUNT(*) AS orders FROM orders;
-- SELECT COUNT(*) AS order_items FROM order_items;
-- ============================================================
