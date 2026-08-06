# Computer Store Management System

Hệ thống quản lý bán máy tính/laptop xây dựng bằng Spring Boot 3, PostgreSQL và Docker.

## Yêu Cầu

- Java 17+
- Maven 3.9+
- PostgreSQL 13+
- Docker & Docker Compose (tùy chọn)

## Cấu Trúc Dự Án

```
ComputerStore/
├── src/
│   ├── main/
│   │   ├── java/com/example/computerstore/
│   │   │   ├── config/          # Configuration classes
│   │   │   ├── controller/      # REST Controllers
│   │   │   ├── dto/            # Data Transfer Objects
│   │   │   ├── model/          # Entity models
│   │   │   ├── repository/     # JPA Repositories
│   │   │   ├── service/        # Business logic
│   │   │   └── ComputerStoreApplication.java
│   │   └── resources/
│   │       └── application.properties
│   └── test/
├── pom.xml
├── Dockerfile
├── docker-compose.yml
├── computer_store.sql
├── mvnw / mvnw.cmd
└── README.md
```

## Thiết Lập

### 1. Cách 1: Chạy với Docker Compose (Khuyến nghị)

```bash
# Sao chép project
cd ComputerStore

# Chạy với Docker Compose
docker-compose up -d

# Ứng dụng sẽ chạy tại: http://localhost:8080
```

### 2. Cách 2: Chạy Cục Bộ

```bash
# Cài đặt PostgreSQL
# Tạo database: ComputerStore
# Chạy SQL script: computer_store.sql

# Cấu hình application.properties
# spring.datasource.url=jdbc:postgresql://localhost:5432/ComputerStore
# spring.datasource.username=postgres
# spring.datasource.password=12345

# Chạy Maven
mvn clean install
mvn spring-boot:run

# Ứng dụng sẽ chạy tại: http://localhost:8080
```

## API Endpoints

### Authentication
- `POST /api/auth/login` - Đăng nhập
- `POST /api/auth/register` - Đăng ký tài khoản

### Products
- `GET /api/products` - Danh sách tất cả sản phẩm
- `GET /api/products/active` - Danh sách sản phẩm hoạt động
- `GET /api/products/{id}` - Chi tiết sản phẩm
- `GET /api/products/category/{categoryId}` - Sản phẩm theo danh mục
- `POST /api/products` - Tạo sản phẩm
- `POST /api/products/upload` - Tạo sản phẩm với upload ảnh
- `PUT /api/products/{id}` - Cập nhật sản phẩm
- `DELETE /api/products/{id}` - Xóa sản phẩm (soft delete)

### Categories
- `GET /api/categories` - Danh sách danh mục
- `GET /api/categories/active` - Danh mục hoạt động
- `GET /api/categories/{id}` - Chi tiết danh mục
- `POST /api/categories` - Tạo danh mục
- `PUT /api/categories/{id}` - Cập nhật danh mục
- `DELETE /api/categories/{id}` - Xóa danh mục

### Brands
- `GET /api/brands` - Danh sách thương hiệu
- `GET /api/brands/active` - Thương hiệu hoạt động
- `GET /api/brands/{id}` - Chi tiết thương hiệu
- `POST /api/brands` - Tạo thương hiệu
- `PUT /api/brands/{id}` - Cập nhật thương hiệu
- `DELETE /api/brands/{id}` - Xóa thương hiệu

### CPUs
- `GET /api/cpus` - Danh sách CPU
- `GET /api/cpus/active` - CPU hoạt động
- `GET /api/cpus/{id}` - Chi tiết CPU
- `POST /api/cpus` - Tạo CPU
- `PUT /api/cpus/{id}` - Cập nhật CPU
- `DELETE /api/cpus/{id}` - Xóa CPU

### RAMs
- `GET /api/rams` - Danh sách RAM
- `GET /api/rams/active` - RAM hoạt động
- `GET /api/rams/{id}` - Chi tiết RAM
- `POST /api/rams` - Tạo RAM
- `PUT /api/rams/{id}` - Cập nhật RAM
- `DELETE /api/rams/{id}` - Xóa RAM

### Orders
- `GET /api/orders` - Danh sách đơn hàng
- `GET /api/orders/{id}` - Chi tiết đơn hàng
- `GET /api/orders/user/{userId}` - Đơn hàng của người dùng
- `POST /api/orders` - Tạo đơn hàng
- `PUT /api/orders/{id}` - Cập nhật đơn hàng
- `DELETE /api/orders/{id}` - Xóa đơn hàng

### Users
- `GET /api/users` - Danh sách người dùng
- `GET /api/users/{id}` - Chi tiết người dùng
- `POST /api/users` - Tạo người dùng
- `PUT /api/users/{id}` - Cập nhật người dùng
- `DELETE /api/users/{id}` - Xóa người dùng

## Cơ Sở Dữ Liệu

### Bảng Chính
- **categories** - Danh mục sản phẩm (10 dữ liệu mẫu)
- **brands** - Thương hiệu (10 dữ liệu mẫu)
- **cpus** - Bộ xử lý (10 dữ liệu mẫu)
- **rams** - Bộ nhớ (10 dữ liệu mẫu)
- **users** - Người dùng (5 dữ liệu mẫu)
- **products** - Sản phẩm (20 dữ liệu mẫu)
- **orders** - Đơn hàng (5 dữ liệu mẫu)
- **order_items** - Chi tiết đơn hàng

## Tính Năng

✅ CRUD đầy đủ cho tất cả entities
✅ Soft delete (status = INACTIVE)
✅ Upload ảnh sản phẩm
✅ Quản lý đơn hàng
✅ Quản lý người dùng
✅ Xác thực cơ bản (email/password)
✅ CORS hỗ trợ tất cả origins
✅ PostgreSQL database
✅ Docker deployment
✅ REST API tiêu chuẩn

## Credentials Mặc Định

**Database:**
- Username: postgres
- Password: 12345
- Database: ComputerStore

**Admin Account:**
- Email: admin@computerstore.com
- Password: admin123

## Triển Khai

### Docker
```bash
# Build image
docker build -t computerstore:latest .

# Run with docker-compose
docker-compose up -d

# View logs
docker-compose logs -f backend

# Stop services
docker-compose down
```

### Maven
```bash
# Clean build
mvn clean package

# Run
java -jar target/computerstore-0.0.1-SNAPSHOT.jar
```

## Cấu Hình

Chỉnh sửa `src/main/resources/application.properties`:
- Thay đổi database URL, username, password
- Thay đổi server port (mặc định: 8080)
- Cấu hình upload directory

## Lưu Ý

- Tất cả request không cần xác thực (CSRF disabled)
- Hình ảnh lưu trong thư mục `uploads/`
- Soft delete: set status = 'INACTIVE'
- Hard delete: xóa trực tiếp từ database

## Hỗ Trợ

Liên hệ: support@computerstore.com

---

**Version:** 1.0.0
**Last Updated:** 2026-07-05
