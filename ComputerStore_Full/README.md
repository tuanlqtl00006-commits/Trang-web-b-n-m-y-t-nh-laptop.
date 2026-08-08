# ComputerStore - Full Stack (Backend + Frontend)

Dự án quản lý bán máy tính, gồm:
- **Backend**: Spring Boot 3 + Java 17 + SQL Server (REST API)
- **FrontEnd**: Vue 3 + Vite + Bootstrap 5 (trang khách hàng + trang quản trị)

## Cấu trúc thư mục

```
ComputerStore_Full/
├── Backend/          # Spring Boot API (port 8080)
├── FrontEnd/         # Vue 3 SPA (port 5173 khi chạy Docker, 5174 khi chạy npm dev)
└── docker-compose.yml
```

## Cách chạy nhanh nhất: Docker Compose

Yêu cầu: đã cài Docker Desktop.

```bash
cd ComputerStore_Full
docker compose up --build
```

Sau khi khởi động xong:
- Frontend (trang bán hàng): http://localhost:5173
- Backend API: http://localhost:8080/api
- SQL Server: localhost:1433 (user: sa / pass: YourStrong@Passw0rd)

## Chạy thủ công (không dùng Docker)

### 1. Backend
```bash
cd Backend
# Cấu hình SQL Server trong application.properties (script computer_store.sql tự tạo database "ComputerStore" nếu chưa có)
./mvnw spring-boot:run
```
Backend chạy ở `http://localhost:8080`.

### 2. Frontend
```bash
cd FrontEnd
npm install
npm run dev
```
Frontend chạy ở `http://localhost:5173` (hoặc cổng Vite báo ra).

## Tài khoản mẫu

Sau khi seed dữ liệu (computer_store.sql) hoặc đăng ký mới:
- Đăng ký tài khoản khách hàng qua trang `/register`
- Để có tài khoản `admin`, cập nhật trực tiếp cột `role = 'admin'` cho user trong bảng `users`

## Chức năng chính

### Trang khách hàng (client)
- Trang chủ: danh sách sản phẩm, lọc theo danh mục/thương hiệu, sắp xếp giá, tìm kiếm
- Xem sản phẩm theo danh mục
- Chi tiết sản phẩm (CPU, RAM, SSD, GPU, màn hình, OS...)
- Giỏ hàng (localStorage), thanh toán, tạo đơn hàng
- Xem đơn hàng của tôi, chi tiết đơn hàng
- Thông tin tài khoản cá nhân

### Trang quản trị (admin) - `/admin`
- Dashboard: thống kê tổng quan
- Quản lý sản phẩm (CRUD, upload ảnh)
- Quản lý danh mục / thương hiệu / CPU / RAM
- Quản lý đơn hàng (đổi trạng thái, xem chi tiết)
- Quản lý khách hàng

## Ghi chú kỹ thuật

- Backend đã được bổ sung `OrderItemController` + `OrderItemService` (trước đó thiếu) để lưu chi tiết sản phẩm trong từng đơn hàng khi khách thanh toán.
- Khi tạo `OrderItem`, hệ thống tự động trừ tồn kho (`stock`) của sản phẩm tương ứng.
- Toàn bộ API base URL được cấu hình tại `FrontEnd/src/api/axios.js` (`http://localhost:8080/api`). Nếu đổi cổng/host backend, sửa tại đây.
