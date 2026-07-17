# Hướng dẫn Triển khai Hệ thống Bằng Ansible

Tài liệu này hướng dẫn cách build và chạy Ansible playbook để deploy tự động dự án **SBA301 - Small Clothing E-commerce** lên VPS Kamatera (`74.113.235.181`).

---

## 1. Chuẩn bị trước khi chạy Ansible (Prerequisites)

### A. Trên máy cá nhân (Control Machine) của bạn
1. **Cài đặt các công cụ build**:
   - **Java 21 JDK** và **Maven 3.9.x** (để build Backend).
   - **Node.js 20 LTS** và **npm** (để build Frontend).
   - **Ansible** (thường chạy trên Linux hoặc Windows WSL).

2. **Cấu hình SSH không mật khẩu**:
   - Nếu bạn chưa có SSH key, sinh một cặp key trên máy local bằng lệnh:
     ```bash
     ssh-keygen -t rsa -b 4096
     ```
   - Copy nội dung trong file public key vừa tạo (ở Windows thường là `C:\Users\<Tên_User>\.ssh\id_rsa.pub`, ở Linux/macOS là `~/.ssh/id_rsa.pub`).
   - Đăng nhập SSH bằng mật khẩu vào VPS (`ssh root@74.113.235.181`), mở file `/root/.ssh/authorized_keys` và dán dòng public key đó vào dòng mới. Lưu lại.
   - Thử kết nối lại bằng lệnh: `ssh root@74.113.235.181`. Nếu đăng nhập thẳng vào shell mà không hỏi mật khẩu là thành công!

---

## 2. Các bước triển khai (Deployment Workflow)

### Bước 1: Build mã nguồn ở máy local
Trước khi chạy Ansible, ta cần build cả 2 phần dự án để sinh ra file nén/sản phẩm tĩnh:

1. **Build Frontend (React)**:
   - Truy cập vào thư mục `frontend/`.
   - Tạo file `.env` (hoặc sửa file hiện tại) với nội dung:
     ```env
     VITE_API_BASE_URL=https://api.smiledev.id.vn/api/v1
     ```
   - Cài đặt dependency và build:
     ```bash
     npm install
     npm run build
     ```
     *Lưu ý*: Lệnh này sẽ sinh ra thư mục `frontend/dist/` chứa toàn bộ code tĩnh.

2. **Build Backend (Spring Boot)**:
   - Truy cập vào thư mục `backend/`.
   - Tiến hành đóng gói file JAR (bỏ qua chạy test để tiết kiệm thời gian):
     ```bash
     mvn clean package -DskipTests
     ```
     *Lưu ý*: Lệnh này sẽ sinh ra file `backend/target/ecommerce.jar`.

### Bước 2: Chạy Ansible Playbook
1. Truy cập vào thư mục `ansible/` từ terminal.
2. Kiểm tra và đảm bảo các thông tin biến môi trường trong file `group_vars/all.yml` là chính xác.
3. Chạy lệnh deploy:
   ```bash
   ansible-playbook playbook.yml
   ```
4. Ansible sẽ tuần tự:
   - Cài đặt Docker, Docker Compose, Nginx, Certbot trên VPS.
   - Đồng bộ code tĩnh frontend lên thư mục `/var/www/sba301/frontend`.
   - Đóng gói container Docker (MariaDB & Spring Boot) và khởi chạy ngầm.
   - Cấu hình server blocks cho Nginx và lấy chứng chỉ HTTPS miễn phí tự động.

---

## 3. Các lệnh hữu ích trên VPS (Troubleshooting)

Nếu gặp sự cố hoặc cần kiểm tra trạng thái trên VPS, hãy SSH vào VPS (`ssh root@74.113.235.181`) và dùng các lệnh sau:

### A. Quản lý Docker Containers
- **Xem trạng thái các container**:
  ```bash
  docker ps -a
  ```
- **Xem log của backend Spring Boot để check kết nối DB**:
  ```bash
  docker logs -f sba301-app
  ```
- **Truy cập vào shell của database MariaDB**:
  ```bash
  docker exec -it sba301-db mysql -u smiledev_user -p
  # Nhập password: Longdeptrai99 (hoặc mật khẩu cấu hình trong all.yml)
  ```

### B. Quản lý Web Server Nginx
- **Kiểm tra trạng thái Nginx**:
  ```bash
  systemctl status nginx
  ```
- **Đọc log lỗi của Nginx (nếu trang web lỗi 502/504)**:
  ```bash
  tail -n 100 /var/log/nginx/error.log
  ```
- **Kiểm tra thời hạn hoặc gia hạn chứng chỉ SSL Let's Encrypt**:
  ```bash
  certbot renew --dry-run
  ```
