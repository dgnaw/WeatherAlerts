# 🌩️ Weather Alert System

Ứng dụng **cảnh báo thời tiết khẩn cấp** được phát triển bằng **Java Spring Boot**.  
Mục tiêu của hệ thống là cung cấp cảnh báo mưa, bão, gió mạnh, lũ lụt... **ngay lập tức** đến người dùng thông qua **push notification**.

---

## 🚀 Giới thiệu sản phẩm
- Ứng dụng giúp người dùng **chủ động phòng tránh thiên tai** bằng cách nhận cảnh báo nhanh chóng.
- Dữ liệu lấy từ các nguồn thời tiết **real-time API** (ví dụ: OpenWeatherMap).
- Người dùng có thể chọn **khu vực quan tâm** (ví dụ: Hà Nội, TP.HCM, Đà Nẵng).
- Khi phát hiện thời tiết xấu hoặc cảnh báo chính thức từ nhà cung cấp, hệ thống sẽ:
    1. Phân tích dữ liệu.
    2. Kích hoạt cơ chế cảnh báo.
    3. Gửi thông báo qua **Firebase Cloud Messaging (FCM)** đến điện thoại.

---

## 🎯 Tính năng chính
- 📡 Lấy dữ liệu dự báo thời tiết và cảnh báo theo khu vực.
- 🔔 Gửi thông báo đẩy (push notification) khi có mưa, bão, sấm sét, lũ lụt...
- 🗺️ Người dùng chọn nhiều vùng quan tâm khác nhau.
- 📜 Lưu lịch sử cảnh báo để theo dõi lại.
- ⚙️ Tùy chỉnh mức độ cảnh báo (ví dụ: chỉ nhận khi bão lớn, gió mạnh cấp 8+).

---

## 🛠️ Công nghệ sử dụng
- **Backend**: Java 17, Spring Boot 3.x
- **Database**: PostgreSQL (hoặc MySQL)
- **Push Notification**: Firebase Cloud Messaging (FCM)
- **Scheduler**: Spring Scheduler (cron job kiểm tra định kỳ)
- **API Provider**: OpenWeatherMap API (hoặc nguồn chính phủ)

---

## 🏗️ Kiến trúc hệ thống
1. **Spring Boot Server**
    - Gọi Weather API định kỳ.
    - Phân tích dữ liệu (Alert Engine).
    - Lưu cảnh báo vào cơ sở dữ liệu.
    - Gửi cảnh báo đến các thiết bị người dùng.

2. **Client (Android/iOS/Web)**
    - Đăng ký thiết bị (FCM token).
    - Nhận thông báo và hiển thị chuông cảnh báo.

---

## 📦 Cách chạy dự án
### Yêu cầu:
- JDK 17+
- Maven 3+
- PostgreSQL/MySQL
- Firebase service account (JSON file)

### Các bước:
```bash
# Clone repo
git clone https://github.com/your-username/weather-alert-system.git
cd weather-alert-system

# Cấu hình application.yml (DB, API key, Firebase)
# Chạy ứng dụng
mvn spring-boot:run
