# 🛒 SBA301 E-Commerce & CI/CD Automated Deployment System

[![Build Status](https://img.shields.io/badge/CI%2FCD-GitHub%20Actions-blue.svg)](https://github.com/longmoon2k4/SBA301-CICD)
[![Backend](https://img.shields.io/badge/Backend-Spring%20Boot%203.4-green.svg)](https://spring.io/projects/spring-boot)
[![Frontend](https://img.shields.io/badge/Frontend-React%2018%20%2B%20Vite-blueviolet.svg)](https://vitejs.dev/)
[![License](https://img.shields.io/badge/License-MIT-brightgreen.svg)](LICENSE)

Dự án Hệ thống Thương mại Điện tử Đa nền tảng (SBA301) tích hợp Quy trình Tự động hóa Đóng gói và Triển khai Đột phá (CI/CD Pipeline) trên hạ tầng VPS Cloud.

---

## 🌐 Live System URLs (Hệ thống đang hoạt động)

* 🎨 **Frontend Web Application**: [https://shop.smiledev.id.vn](https://shop.smiledev.id.vn)
* ⚡ **Backend RESTful API**: [https://api.smiledev.id.vn](https://api.smiledev.id.vn)
* 📚 **Swagger UI Documentation**: [https://api.smiledev.id.vn/api/v1/swagger-ui/index.html](https://api.smiledev.id.vn/api/v1/swagger-ui/index.html) *(Tự động chuyển hướng từ `https://api.smiledev.id.vn/`)*

---

## 🛠️ Công nghệ Sử dụng (Tech Stack)

### 1. Backend API (Spring Boot & Java)
* **Ngôn ngữ & Framework**: Java 21, Spring Boot 3.4.1 (Spring Framework 6.2).
* **Bảo mật & Xác thực**: Spring Security, JWT (JJWT 0.12.6).
* **Tương tác CSDL**: Spring Data JPA, Hibernate 6, MapStruct 1.6.0.
* **Tài liệu API**: Springdoc OpenAPI 2.7.0 (Swagger UI v5).
* **Cổng thanh toán**: Tích hợp VNPay Sandbox & Production API.

### 2. Frontend Web App (React & Vite)
* **Framework**: React 18, Vite build tool.
* **Quản lý State & Query**: TanStack Query (React Query v5) với cơ chế Polling đồng bộ tồn kho 5 giây/lần.
* **Giao diện & Style**: React Bootstrap, Bootstrap Icons, Custom Space Grotesk / Neo-brutalist Design.
* **Routing**: React Router v6.

### 3. Cơ sở dữ liệu (Database)
* **Môi trường Local**: Microsoft SQL Server 2022 (`mssql-jdbc` driver).
* **Môi trường VPS Production**: MariaDB 10.11 Container (`SPRING_JPA_HIBERNATE_DDL_AUTO=update` bảo vệ dữ liệu sản xuất).

### 4. Hạ tầng & CI/CD Pipeline
* **Tự động hóa CI/CD**: GitHub Actions workflows.
* **Đóng gói & Quản lý Container**: Docker, Docker Compose v2.
* **Quản lý Cấu hình VPS**: Ansible Playbooks.
* **Web Server & SSL**: Nginx Reverse Proxy với chứng chỉ mã hóa SSL Let's Encrypt (Certbot).

---

## ✨ Tính năng Nổi bật (Key Features)

### 🛍️ Dành cho Khách hàng (Customer Experience)
1. **Duyệt & Tìm kiếm Sản phẩm**: Xem thông tin chi tiết, chọn kích cỡ (Size), màu sắc (Color) và số lượng tồn kho theo thời gian thực.
2. **Giỏ hàng Thông minh**:
   * Chặn khách vãng lai chưa đăng nhập thêm sản phẩm vào giỏ.
   * Tự động đồng bộ số lượng sản phẩm hiển thị trên Header (Cart Badge) không cần F5.
   * Cơ chế Polling ngầm (5s/lần) cảnh báo biến động tồn kho khi có người khác mua mất sản phẩm.
3. **Giữ chỗ Thanh toán (Inventory Reservation)**:
   * Giữ chỗ sản phẩm 10 phút khi tiến hành đặt hàng để tránh tranh chấp tồn kho.
   * Chuẩn hóa thời gian ISO-8601 (`OffsetDateTime`) xử lý chính xác lệch múi giờ giữa VPS (UTC) và Trình duyệt (GMT+7).
4. **Cổng thanh toán VNPay**: Tự động chuyển hướng và xử lý phản hồi giao dịch VNPay linh hoạt theo từng tên miền.
5. **Sổ địa chỉ cá nhân (`/account/addresses`)**:
   * Thêm, chọn địa chỉ giao hàng mặc định.
   * Xóa địa chỉ cũ với cơ sở dữ liệu ràng buộc khóa ngoại (Foreign Key Integrity Check) và tự động gán địa chỉ mặc định mới.
   * Giao diện Pop-up xác nhận xóa chuẩn Neo-brutalist design (không sử dụng `alert` mặc định của trình duyệt).
6. **Lịch sử đơn hàng (`/my-orders`)**: Xem danh sách đơn hàng đã mua, theo dõi trạng thái đơn và chi tiết từng đơn hàng.

### ⚙️ Dành cho Quản trị viên (Admin & Staff)
1. **Quản lý Đơn hàng (POS & Online Order Management)**: Cập nhật trạng thái đơn hàng (PENDING, PROCESSING, SHIPPED, DELIVERED, CANCELLED).
2. **Hệ thống Thu ngân Bán tại cửa hàng (POS)**: Đặt hàng nhanh cho khách mua trực tiếp tại quầy.
3. **Quản lý Sản phẩm & Biến thể**: Thêm, sửa, xóa sản phẩm, hình ảnh và tồn kho.
4. **Nhật ký thao tác (Audit Log)**: Ghi lại toàn bộ lịch sử thay đổi quan trọng trên hệ thống.

---

## 📂 Cấu trúc Thư mục Dự án (Project Structure)

```text
E:\cicid
├── backend/                      # Mã nguồn Backend Spring Boot
│   ├── src/main/java/com/sba301/ecommerce/
│   │   ├── config/               # Cấu hình Security, CORS, OpenAPI Swagger, VNPay
│   │   ├── exception/            # Global Exception Handler (400, 404, 500)
│   │   └── features/             # Các Feature Modules (address, auth, cart, order, product, pos, review...)
│   └── pom.xml                   # Cấu hình Maven dependencies
│
├── frontend/                     # Mã nguồn Frontend React + Vite
│   ├── src/
│   │   ├── app/router/           # Cấu hình React Router (/account/addresses, /my-orders...)
│   │   ├── features/             # Component theo tính năng (account, cart, checkout, products...)
│   │   └── shared/               # Axios instance, Header, Components dùng chung
│   └── package.json
│
├── ansible/                      # kịch bản Ansible triển khai tự động lên VPS
│   ├── roles/                    # Các role backend_docker, nginx, database...
│   └── playbook.yml
│
└── .github/workflows/            # GitHub Actions CI/CD Pipeline
    └── deploy.yml
```

---

## 🚀 Hướng dẫn Cài đặt & Khởi chạy ở Local

### 1. Khởi chạy Backend (Spring Boot)
Yêu cầu: Java 21+, Microsoft SQL Server.

```bash
cd backend

# Biên dịch dự án
./mvnw clean package -DskipTests

# Chạy ứng dụng
./mvnw spring-boot:run
```
Backend API sẽ khởi chạy tại: `http://localhost:8080/api/v1`  
Swagger UI tại: `http://localhost:8080/api/v1/swagger-ui/index.html`

### 2. Khởi chạy Frontend (React + Vite)
Yêu cầu: Node.js 18+.

```bash
cd frontend

# Cài đặt thư viện
npm install

# Chạy server phát triển (Dev Server)
npm run dev
```
Ứng dụng Frontend sẽ khởi chạy tại: `http://localhost:5173`

---

## 🔄 Quy trình Triển khai Tự động (CI/CD Workflow)

Mỗi khi có thay đổi mã nguồn được push lên branch `smile-dev` hoặc `main`:

```bash
git add .
git commit -m "feat: your new feature"
git push origin smile-dev
```

1. **GitHub Actions** sẽ tự động chạy pipeline build & test mã nguồn.
2. Trigger **Ansible Playbook** tự động kết nối VPS qua SSH.
3. Tự động đóng gói Container Backend Spring Boot và MariaDB Database qua **Docker Compose**.
4. Cập nhật file tĩnh Frontend vào Nginx và tự động `reload nginx` bảo đảm **Zero-Downtime**.

---

## 📝 License

Dự án thuộc bản quyền môn học SBA301 - Phát triển Hệ thống Thương mại Điện tử và CI/CD.
