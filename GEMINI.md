# 5THEWAY® OUTLET™ - Tổng quan dự án

Đây là ứng dụng web Spring Boot cho **5THEWAY® OUTLET™**, một nền tảng thương mại điện tử chuyên về thời trang streetwear. Dự án tập trung vào việc cung cấp trải nghiệm mua sắm trực quan và phản hồi nhanh bằng cách sử dụng Java hiện đại và các công nghệ web.

# Mã nguồn được tạo
Mọi chỉnh sửa, thêm mới, xóa đi đều phải comment lại bằng tiếng Việt ở phía trên.

## Công nghệ sử dụng

- **Backend:** Java 17 (LTS), Spring Boot 3.5.12
- **Cơ sở dữ liệu:** MySQL (Laragon)
- **Bảo mật:** Spring Security (Mã hóa mật khẩu BCrypt, Phân quyền ROLE_ADMIN/ROLE_USER)
- **Templating:** Thymeleaf với `thymeleaf-layout-dialect` và `thymeleaf-extras-springsecurity6`.
- **Frontend:**
    - **Framework:** Bootstrap 4 (via CDN)
    - **Tương tác:** Swiper.js cho hero banners.
    - **Định dạng:** Custom Vanilla CSS.
- **Build Tool:** Maven

## Dự án hiện tại
- Tài khoản Admin mẫu: `admin1` / `admin1` (Vai trò: ROLE_ADMIN)
- Tài khoản User mẫu: Tự đăng ký qua trang `/register`.

# Danh sách công việc (To-do List)

### 1. Phân hệ dành cho Khách hàng (User)
- [x] **Đăng ký/Đăng nhập:** Hỗ trợ đăng ký tài khoản mới, đăng nhập, bảo mật bằng Spring Security.
- [x] **Trang chủ:** Hiển thị danh mục, sản phẩm nổi bật với giao diện chuẩn (1 lớn, 4 nhỏ).
- [x] **Tìm kiếm:** Tìm kiếm sản phẩm theo tên qua thanh điều hướng.
- [x] **Chi tiết sản phẩm:** Hiển thị hình ảnh (đã tối ưu kích thước), mô tả ngắn/chi tiết, giá, tồn kho và sản phẩm liên quan.
- [x] **Giỏ hàng:** Thêm/Sửa/Xóa sản phẩm, cập nhật số lượng & tổng tiền thời gian thực.
- [x] **Thanh toán:** Thông tin giao hàng, phương thức thanh toán (COD/Ví/Thẻ), xác nhận đơn hàng.
- [x] **Lịch sử đơn hàng:** Xem đơn đã đặt và trạng thái (Admin quản lý và User xem qua trang /Home/Orders).

### 2. Phân hệ dành cho Quản trị viên (Admin)
- [x] **Dashboard:** Thống kê cơ bản (Sản phẩm, Đơn hàng, Doanh thu) về hệ thống.
- [x] **Quản lý Danh mục:** CRUD (Thêm, sửa, xóa) các loại hàng hóa.
- [x] **Quản lý Sản phẩm:**
    - [x] CRUD Sản phẩm (Thêm, sửa, xóa, liệt kê).
    - [x] Quản lý tồn kho và trạng thái nổi bật.
- [x] **Quản lý Đơn hàng:** Xem danh sách, thay đổi trạng thái, in hóa đơn (cơ bản).
- [x] **Quản lý Người dùng:** Danh sách khách hàng, khóa/mở khóa tài khoản.
- [x] **Quản lý Đánh giá:** Kiểm duyệt/xóa bình luận không phù hợp.

### 3. Logic Nghiệp vụ (Backend)
- [x] **Quản lý Kho:** Tự động trừ tồn kho khi đặt hàng thành công.
- [x] **Hệ thống Khuyến mãi:** Tạo mã coupon, hiển thị giá sale (Đã hoàn thiện logic áp dụng mã).
- [x] **Luồng đơn hàng:** Chờ xác nhận -> Đã xác nhận -> Đang giao -> Hoàn thành.

### 4. Trải nghiệm Người dùng (UX/Frontend)
- [x] **Bộ lọc thông minh:** Lọc kết hợp nhiều điều kiện (loại, khoảng giá).
- [x] **Sắp xếp sản phẩm:** Theo giá, độ phổ biến, mới nhất.
- [x] **Phản hồi (Feedback):** Hiển thị Toast/Alert message khi thao tác thành công.

### 5. Bảo mật & Hệ thống
- [x] **Phân quyền (Authorization):** Bảo vệ các đường dẫn `/admin/**` chỉ dành cho ADMIN.
- [x] **Cơ sở dữ liệu:** Cấu hình kết nối MySQL thành công (Laragon).
- [x] **Lưu trữ hình ảnh:** Triển khai lưu ảnh vào thư mục server thành công.
