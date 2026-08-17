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

INSERT INTO categories (name, description, img, status) VALUES
(N'Laptop Gaming', N'High-performance gaming laptops', N'http://localhost:8080/uploads/ASUS ROG Zephyrus G14.png', N'ACTIVE'),
(N'Laptop Văn Phòng', N'Office laptops for work', N'http://localhost:8080/uploads/HP Pavilion 15.png', N'ACTIVE'),
(N'Desktop Gaming', N'Gaming desktop computers', N'http://localhost:8080/uploads/Lenovo Legion 7i.png', N'ACTIVE'),
(N'Desktop Văn Phòng', N'Office desktop computers', N'http://localhost:8080/uploads/Dell Inspiron 16.png', N'ACTIVE'),
(N'Workstation', N'Professional workstations', N'http://localhost:8080/uploads/Lenovo ThinkPad X1.png', N'ACTIVE'),
(N'Laptop Mỏng Nhẹ', N'Ultrabook and thin laptops', N'http://localhost:8080/uploads/Laptop Acer Swift 5.png', N'ACTIVE'),
(N'Máy tính bảng', N'Tablets and portable devices', N'http://localhost:8080/uploads/Samsung Galaxy Book4.png', N'ACTIVE'),
(N'PC Custom', N'Custom-built computers', N'http://localhost:8080/uploads/Acer Nitro 5.png', N'ACTIVE'),
(N'Server', N'Server computers', N'http://localhost:8080/uploads/LG UltraPC.png', N'ACTIVE'),
(N'Chromebook', N'Chrome OS laptops', N'http://localhost:8080/uploads/MacBook Pro 14.png', N'ACTIVE');

INSERT INTO brands (brand_name, status) VALUES
(N'ASUS', N'ACTIVE'),
(N'Dell', N'ACTIVE'),
(N'HP', N'ACTIVE'),
(N'Lenovo', N'ACTIVE'),
(N'Acer', N'ACTIVE'),
(N'MSI', N'ACTIVE'),
(N'Razer', N'ACTIVE'),
(N'Apple', N'ACTIVE'),
(N'Samsung', N'ACTIVE'),
(N'LG', N'ACTIVE');

INSERT INTO cpus (cpu_name, status) VALUES
(N'Intel Core i9-13900K', N'ACTIVE'),
(N'Intel Core i7-13700K', N'ACTIVE'),
(N'Intel Core i5-13600K', N'ACTIVE'),
(N'AMD Ryzen 9 7950X', N'ACTIVE'),
(N'AMD Ryzen 7 7700X', N'ACTIVE'),
(N'Intel Core i9-12900K', N'ACTIVE'),
(N'AMD Ryzen 5 7600X', N'ACTIVE'),
(N'Intel Core i3-13100', N'ACTIVE'),
(N'AMD Ryzen 3 7100', N'ACTIVE'),
(N'Intel Xeon Platinum', N'ACTIVE');

INSERT INTO rams (ram_name, status) VALUES
(N'DDR5 32GB 6000MHz', N'ACTIVE'),
(N'DDR5 16GB 6000MHz', N'ACTIVE'),
(N'DDR5 8GB 6000MHz', N'ACTIVE'),
(N'DDR4 32GB 3600MHz', N'ACTIVE'),
(N'DDR4 16GB 3600MHz', N'ACTIVE'),
(N'DDR4 8GB 3600MHz', N'ACTIVE'),
(N'DDR5 64GB 6000MHz', N'ACTIVE'),
(N'DDR5 24GB 6000MHz', N'ACTIVE'),
(N'DDR4 48GB 3600MHz', N'ACTIVE'),
(N'LPDDR5 12GB 6400MHz', N'ACTIVE');

-- Passwords are plain text because the current AuthService compares strings directly.
INSERT INTO users (fullname, email, phone, password, role, status) VALUES
(N'Nguyễn Văn A', N'admin@computerstore.com', N'0901234567', N'admin123', N'ADMIN', N'ACTIVE'),
(N'Lê Thị Nhân Viên', N'staff@computerstore.com', N'0909876543', N'staff123', N'STAFF', N'ACTIVE'),
(N'Trần Thị B', N'user1@computerstore.com', N'0902345678', N'user123', N'USER', N'ACTIVE'),
(N'Phạm Văn C', N'user2@computerstore.com', N'0903456789', N'user123', N'USER', N'ACTIVE'),
(N'Đỗ Thị D', N'user3@computerstore.com', N'0904567890', N'user123', N'USER', N'ACTIVE'),
(N'Bùi Văn E', N'user4@computerstore.com', N'0905678901', N'user123', N'USER', N'ACTIVE');

INSERT INTO products
(name, price, stock, image, description, status, ssd, gpu, screen, os, category_id, brand_id, cpu_id, ram_id)
VALUES
-- Category 1: Laptop Gaming
(N'ASUS ROG Zephyrus G14', 25000000, 15, N'http://localhost:8080/uploads/ASUS ROG Zephyrus G14.png', N'Gaming laptop mạnh mẽ nhỏ gọn', N'ACTIVE', N'1TB NVMe', N'NVIDIA RTX 4090', N'14 inch QHD+', N'Windows 11', 1, 1, 1, 1),
-- Category 5: Workstation
(N'Dell XPS 15', 22000000, 12, N'http://localhost:8080/uploads/Dell XPS 15.png', N'Laptop đồ họa cao cấp cho designer', N'ACTIVE', N'512GB NVMe', N'NVIDIA RTX 4080', N'15.6 inch OLED', N'Windows 11', 5, 2, 2, 2),
-- Category 2: Laptop Văn Phòng
(N'HP Pavilion 15', 12000000, 25, N'http://localhost:8080/uploads/HP Pavilion 15.png', N'Laptop văn phòng thông dụng, bền bỉ', N'ACTIVE', N'256GB SSD', N'Intel UHD', N'15.6 inch FHD', N'Windows 11', 2, 3, 3, 5),
-- Category 5: Workstation
(N'Lenovo ThinkPad X1', 18000000, 20, N'http://localhost:8080/uploads/Lenovo ThinkPad X1.png', N'Workstation doanh nhân đỉnh cao độ bền', N'ACTIVE', N'512GB SSD', N'Intel Iris', N'14 inch 2K', N'Windows 11', 5, 4, 2, 1),
-- Category 6: Laptop Mỏng Nhẹ
(N'Acer Swift 5', 15000000, 18, N'http://localhost:8080/uploads/Laptop Acer Swift 5.png', N'Ultrabook siêu mỏng nhẹ dưới 1kg', N'ACTIVE', N'512GB SSD', N'Intel Iris', N'14 inch FHD', N'Windows 11', 6, 5, 3, 5),
-- Category 1: Laptop Gaming
(N'MSI GE76 Raider', 24000000, 10, N'http://localhost:8080/uploads/MSI GE76 Raider.png', N'Gaming laptop pro tản nhiệt vượt trội', N'ACTIVE', N'1TB NVMe', N'NVIDIA RTX 4080 Ti', N'17 inch FHD 240Hz', N'Windows 11', 1, 6, 1, 2),
-- Category 10: Chromebook
(N'Razer Blade 15', 26000000, 8, N'http://localhost:8080/uploads/Razer Blade 15.png', N'Laptop vỏ nhôm nguyên khối siêu mỏng', N'ACTIVE', N'1TB SSD', N'NVIDIA RTX 4090', N'15.6 inch QHD', N'Windows 11', 10, 7, 1, 1),
-- Category 10: Chromebook
(N'MacBook Pro 14', 28000000, 5, N'http://localhost:8080/uploads/MacBook Pro 14.png', N'Laptop sáng tạo chuyên nghiệp', N'ACTIVE', N'512GB SSD', N'Apple M3 Pro', N'14 inch Liquid Retina', N'macOS', 10, 8, 1, 2),
-- Category 7: Máy tính bảng
(N'Samsung Galaxy Book4', 16000000, 14, N'http://localhost:8080/uploads/Samsung Galaxy Book4.png', N'Tablet 2-in-1 màn hình AMOLED rực rỡ', N'ACTIVE', N'256GB SSD', N'Intel Arc', N'14 inch AMOLED Touch', N'Windows 11', 7, 9, 3, 10),
-- Category 9: Server
(N'LG UltraPC Enterprise', 17000000, 11, N'http://localhost:8080/uploads/LG UltraPC.png', N'Máy chủ server mini cho doanh nghiệp vừa và nhỏ', N'ACTIVE', N'512GB SSD', N'Intel Iris', N'14 inch OLED', N'Windows Server', 9, 10, 10, 7),
-- Category 2: Laptop Văn Phòng
(N'ASUS VivoBook 15', 11000000, 30, N'http://localhost:8080/uploads/ASUS VivoBook 15.png', N'Laptop nhập môn học tập giá tốt', N'ACTIVE', N'256GB SSD', N'AMD Radeon', N'15.6 inch FHD', N'Windows 11', 2, 1, 7, 6),
-- Category 4: Desktop Văn Phòng
(N'Dell Inspiron 16 Desktop', 13000000, 22, N'http://localhost:8080/uploads/Dell Inspiron 16.png', N'PC đồng bộ văn phòng xử lý tác vụ mượt mà', N'ACTIVE', N'512GB SSD', N'Intel UHD', N'16 inch FHD', N'Windows 11', 4, 2, 8, 5),
-- Category 6: Laptop Mỏng Nhẹ
(N'HP Spectre x360', 20000000, 9, N'http://localhost:8080/uploads/HP Spectre x360.png', N'Convertible xoay gập 360 cao cấp mỏng nhẹ', N'ACTIVE', N'512GB SSD', N'Intel Iris', N'13.5 inch 3K OLED', N'Windows 11', 6, 3, 2, 2),
-- Category 3: Desktop Gaming
(N'Lenovo IdeaPad Gaming 3 PC', 14000000, 19, N'http://localhost:8080/uploads/Lenovo IdeaPad Gaming 3.png', N'PC Gaming tầm trung cấu hình cân game', N'ACTIVE', N'512GB SSD', N'NVIDIA RTX 4050', N'15.6 inch 144Hz', N'Windows 11', 3, 4, 5, 5),
-- Category 8: PC Custom
(N'Acer Nitro 5 Custom PC', 16000000, 16, N'http://localhost:8080/uploads/Acer Nitro 5.png', N'PC Custom dựng sẵn hiệu năng đỉnh cao', N'ACTIVE', N'512GB SSD', N'NVIDIA RTX 4060', N'15.6 inch 144Hz', N'Windows 11', 8, 5, 5, 2),
-- Category 3: Desktop Gaming
(N'ASUS TUF Gaming A16 PC', 23000000, 7, N'http://localhost:8080/uploads/ASUS TUF Gaming A16.png', N'PC Gaming cao cấp bền bỉ tiêu chuẩn quân đội', N'ACTIVE', N'1TB SSD', N'NVIDIA RTX 4070', N'16 inch 165Hz', N'Windows 11', 3, 1, 4, 2),
-- Category 8: PC Custom
(N'Dell G15 Custom', 18000000, 13, N'http://localhost:8080/uploads/Dell G15.png', N'Dàn PC Custom hiệu năng mạnh mẽ', N'ACTIVE', N'512GB SSD', N'NVIDIA RTX 4050 Ti', N'15.6 inch 165Hz', N'Windows 11', 8, 2, 4, 2),
-- Category 4: Desktop Văn Phòng
(N'HP Victus 15 Desktop', 17000000, 15, N'http://localhost:8080/uploads/HP Victus 15.png', N'Bộ máy tính để bàn đa nhiệm tốc độ cao', N'ACTIVE', N'512GB SSD', N'NVIDIA RTX 4050', N'15.6 inch 144Hz', N'Windows 11', 4, 3, 5, 5),
-- Category 1: Laptop Gaming
(N'Lenovo Legion 7i', 24000000, 6, N'http://localhost:8080/uploads/Lenovo Legion 7i.png', N'Gaming laptop flagship Lenovo hiệu năng khủng', N'ACTIVE', N'1TB SSD', N'NVIDIA RTX 4090', N'16 inch QHD+ 240Hz', N'Windows 11', 1, 4, 1, 1),
-- Category 9: Server
(N'MSI Stealth 17 Server Workstation', 25000000, 8, N'http://localhost:8080/uploads/MSI Stealth 17.png', N'Máy chủ trạm cao cấp chịu tải lớn', N'ACTIVE', N'1TB SSD', N'NVIDIA RTX 4080', N'17 inch FHD 240Hz', N'Windows Server', 9, 6, 10, 7);

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
GO
