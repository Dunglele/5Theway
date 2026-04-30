<div align="center">
  <img src="https://cdn.kiotvietweb.vn/merchant/1fb4ae1f6d40d69dfa13e50dcfeb6e6c/other/1725866622/OUTLETTRANG.png" alt="5THEWAY OUTLET Logo" width="300" />

  <h3 align="center">5THEWAY® OUTLET™ E-Commerce Platform</h3>

  <p align="center">
    Một hệ thống thương mại điện tử hoàn chỉnh, đầy đủ tính năng, thiết kế hiện đại và tối ưu hóa trải nghiệm người dùng (UX/UI).
    <br />
    <strong>Thực hiện bởi: Lê Đỗ Quang Dũng</strong>
  </p>
</div>

<details open>
  <summary>Mục lục</summary>
  <ol>
    <li><a href="#-giới-thiệu">Giới thiệu</a></li>
    <li><a href="#-công-nghệ-sử-dụng">Công nghệ sử dụng</a></li>
    <li><a href="#-user-story-câu-chuyện-người-dùng">User Story (Câu chuyện người dùng)</a></li>
    <li><a href="#-tính-năng-nổi-bật">Tính năng nổi bật</a></li>
    <li><a href="#-hướng-dẫn-cài-đặt">Hướng dẫn cài đặt</a></li>
  </ol>
</details>
<img width="1806" height="959" alt="image" src="https://github.com/user-attachments/assets/3709ce6b-7fdf-4cf5-80b9-dcf3a3217377" />

## 🚀 Giới thiệu

**5THEWAY® OUTLET™** là một dự án website thương mại điện tử phát triển dựa trên ngôn ngữ Java (Spring Boot framework). 
Mục tiêu của hệ thống là cung cấp một nền tảng bán hàng trực tuyến mượt mà, đầy đủ các tính năng quản lý nội dung cho Admin và tối ưu hóa trải nghiệm mua sắm cho Khách hàng.

## 💻 Công nghệ sử dụng

*   **Backend:** Java 17, Spring Boot 3, Spring Security, Spring Data JPA, Hibernate.
*   **Frontend:** HTML5, CSS3, JavaScript, Bootstrap 4, Thymeleaf.
*   **Cơ sở dữ liệu:** MySQL.
*   **Build Tool:** Maven.
*   **Kiểm tra tính hợp lệ (Validation):** Jakarta Validation (Spring Boot Starter Validation).

## 👤 User Story (Câu chuyện người dùng)

Dưới đây là một số User Story chính được phân tích và thiết kế cho hệ thống:

**Dành cho Khách hàng (User):**
*   *Là một Khách hàng, tôi muốn có thể đăng ký tài khoản và đăng nhập dễ dàng, để tôi có thể theo dõi đơn hàng của mình.*
*   *Là một Khách hàng, tôi muốn được hệ thống lưu phiên đăng nhập (Remember me), để tôi không phải nhập lại mật khẩu nhiều lần.*
*   *Là một Khách hàng, tôi muốn xem nhanh (Quick View) thông tin sản phẩm ngay tại trang chủ mà không cần tải trang mới, để tiết kiệm thời gian mua sắm.*
*   *Là một Khách hàng, tôi muốn lọc sản phẩm theo mức giá và màu sắc kết hợp, để tôi tìm được chiếc áo ưng ý nhất.*
*   *Là một Khách hàng, tôi muốn nhận được thông báo rõ ràng (Toast message) khi thêm sản phẩm vào giỏ hàng hoặc khi mã giảm giá không hợp lệ.*
*   *Là một Khách hàng, tôi muốn thanh toán bằng mã QR chuyển khoản, để quá trình giao dịch diễn ra tiện lợi.*

**Dành cho Quản trị viên (Admin):**
*   *Là một Admin, tôi muốn xem thống kê doanh thu và đơn hàng trên Dashboard, để theo dõi tình hình kinh doanh.*
*   *Là một Admin, tôi muốn hệ thống tự động cảnh báo sản phẩm sắp hết hàng, để tôi kịp thời nhập thêm.*
*   *Là một Admin, tôi muốn dữ liệu nhập vào (giá tiền, tên sản phẩm) phải được hệ thống kiểm tra chặt chẽ, để tránh sai sót cơ sở dữ liệu.*

## ✨ Tính năng nổi bật

### 🛒 Dành cho Khách hàng
*   **Giao diện UX/UI Cao cấp:** Thiết kế responsive, animations mượt mà, Modal xem nhanh (Quick View), Toast Notifications.
*   **Bộ lọc thông minh (Multi-filtering):** Tìm kiếm và kết hợp nhiều bộ lọc (Khoảng giá, Tình trạng tồn kho, Phân loại).
*   **Giỏ hàng & Thanh toán:** Tự động tính toán tổng tiền, áp dụng Coupon giảm giá, xác nhận mã QR chuyển khoản.
*   **Kiểm soát Tồn kho:** Ngăn chặn việc đặt quá số lượng cho phép trong kho thời gian thực.
*   **Bảo mật:** Mật khẩu được mã hóa an toàn, phân quyền chặt chẽ bằng Spring Security.

### ⚙️ Dành cho Quản trị viên
*   **Dashboard Phân tích:** Thống kê trực quan.
*   **Quản lý Sản phẩm:** Thêm mới, chỉnh sửa thông tin, tải ảnh lên server tự động (Không lưu vào DB làm chậm hệ thống).
*   **Bảo mật & Toàn vẹn dữ liệu (Backend Validation):** Ngăn chặn lưu trữ sai định dạng (giá tiền âm, tên rỗng) bằng `spring-boot-starter-validation`.
*   **Quản lý Đơn hàng:** Cập nhật trạng thái luồng đi của đơn hàng (Pending -> Shipping -> Completed), hoàn trả tồn kho nếu hủy đơn.

## 🛠 Hướng dẫn cài đặt

1.  **Clone mã nguồn từ kho lưu trữ về máy:**
    ```bash
    git clone https://github.com/Dunglele/5Theway.git
    cd 5Theway
    ```

2.  **Cấu hình Cơ sở dữ liệu:**
    *   Tạo cơ sở dữ liệu MySQL mới (ví dụ: `5theway_db`).
    *   Mở file `src/main/resources/application.properties` và cấu hình lại thông tin kết nối:
    ```properties
    spring.datasource.url=jdbc:mysql://localhost:3306/5theway_db
    spring.datasource.username=root
    spring.datasource.password=your_password
    spring.jpa.hibernate.ddl-auto=update
    ```

3.  **Khởi động ứng dụng:**
    *   Mở Terminal tại thư mục gốc của dự án và chạy lệnh:
    ```bash
    ./mvnw spring-boot:run
    ```
    *   Hệ thống sẽ chạy tại `http://localhost:8080`.

<img width="1813" height="961" alt="image" src="https://github.com/user-attachments/assets/676a7b50-edb8-4246-b1e2-eab817e6bf71" />

---
*Dự án được xây dựng với mục đích học thuật và mô phỏng thực tế. Cảm ơn bạn đã quan tâm.*
