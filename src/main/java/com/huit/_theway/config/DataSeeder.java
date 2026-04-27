package com.huit._theway.config;

import com.huit._theway.model.Category;
import com.huit._theway.model.Product;
import com.huit._theway.model.User;
import com.huit._theway.repository.CategoryRepository;
import com.huit._theway.repository.ProductRepository;
import com.huit._theway.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.HashSet;

/**
 * Lớp khởi tạo dữ liệu mẫu cho Database
 */
@Configuration
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final com.huit._theway.repository.CouponRepository couponRepository;

    @Override
    public void run(String... args) throws Exception {
        // Tạo Coupon mẫu
        if (couponRepository.count() == 0) {
            com.huit._theway.model.Coupon c1 = com.huit._theway.model.Coupon.builder()
                    .code("WELCOME50")
                    .discountValue(50000.0)
                    .discountType("FIXED_AMOUNT")
                    .minOrderAmount(200000.0)
                    .active(true)
                    .build();
            com.huit._theway.model.Coupon c2 = com.huit._theway.model.Coupon.builder()
                    .code("SALE10")
                    .discountValue(10.0)
                    .discountType("PERCENTAGE")
                    .minOrderAmount(500000.0)
                    .active(true)
                    .build();
            couponRepository.saveAll(java.util.Arrays.asList(c1, c2));
            System.out.println("Mã giảm giá mẫu đã được tạo: WELCOME50, SALE10");
        }

        // Tạo tài khoản admin1 nếu chưa tồn tại
        if (userRepository.findByUsername("admin1").isEmpty()) {
            User admin1 = User.builder()
                    .username("admin1")
                    .password(passwordEncoder.encode("admin1"))
                    .email("admin1@5theway.com")
                    .fullName("Admin One")
                    .roles(new HashSet<>(Arrays.asList("ROLE_ADMIN", "ROLE_USER")))
                    .enabled(true)
                    .build();
            userRepository.save(admin1);
            System.out.println("Tài khoản admin mới đã được tạo: admin1 / admin1");
        }

        if (categoryRepository.count() == 0) {
            // Khởi tạo 4 Category chính
            Category tops = Category.builder().name("TOPS").slug("tops").build();
            Category hoodies = Category.builder().name("HOODIES").slug("hoodies").build();
            Category jackets = Category.builder().name("JACKETS").slug("jackets").build();
            Category accessories = Category.builder().name("ACCESSORIES").slug("accessories").build();
            
            categoryRepository.saveAll(Arrays.asList(tops, hoodies, jackets, accessories));

            // TOPS & HOODIES & JACKETS mẫu
            Product p1 = Product.builder()
                    .name("LED SIGN /teddy bear/ NEW TEE™")
                    .price(390000.0).salePrice(350000.0).shortDescription("") // Để trống để test Lorem
                    .stock(50).featured(true).category(tops)
                    .color("Black,White,Grey").sizes("S,M,L,XL")
                    .mainImageUrl("https://cdn2-retail-images.kiotviet.vn/2024/10/04/5theway/742e51eb86d04540b64d0b06df8a8c76.jpg").build();

            Product p2 = Product.builder()
                    .name("/peace out/ New Tee")
                    .price(430000.0).salePrice(399000.0).shortDescription("Phiên bản giới hạn trong bộ sưu tập New Tee.")
                    .stock(20).featured(true).category(tops)
                    .color("White,Blue").sizes("M,L,XL")
                    .mainImageUrl("https://i.imgur.com/VkG62KY.png").build();

            Product p3 = Product.builder()
                    .name("SHEEPSMAN FACE HOODIE")
                    .price(650000.0).stock(15).featured(true).category(hoodies)
                    .color("Grey,Black").sizes("M,L,XL")
                    .mainImageUrl("https://i.imgur.com/UZ4JuK5.png").build();

            Product p4 = Product.builder()
                    .name("/milky way/ Vertical Drop Shoulder Tee™")
                    .price(370000.0).stock(25).featured(true).category(tops)
                    .color("Blue").sizes("S,M,L")
                    .mainImageUrl("https://i.imgur.com/apFsKUV.jpeg").build();

            Product p5 = Product.builder()
                    .name("/public icon/ ZIGZAG HOODED KHAKI JACKET™")
                    .price(590000.0).stock(12).featured(true).category(jackets)
                    .color("Khaki").sizes("L,XL")
                    .mainImageUrl("https://cdn2-retail-images.kiotviet.vn/2024/10/19/5theway/7197cbe98f114871a2235b94bba49894.jpg").build();

            Product p6 = Product.builder()
                    .name("/sketch/ Embroider Traditional Fit Hoodie")
                    .price(790000.0).stock(8).featured(true).category(hoodies)
                    .color("Black").sizes("M,L,XL,XXL")
                    .mainImageUrl("https://i.imgur.com/jYP98si.png").build();

            Product p7 = Product.builder()
                    .name("GRAFITTI LETTERING SS TEE")
                    .price(370000.0).stock(30).featured(true).category(tops)
                    .color("Yellow").sizes("S,M,L")
                    .mainImageUrl("https://i.imgur.com/bNKlyfI.png").build();

            // ACCESSORIES mẫu (Tất cả gộp vào accessories)
            Product p8 = Product.builder()
                    .name("5THEWAY® 학교 PEN POUCH™")
                    .price(150000.0).stock(100).featured(true).category(accessories)
                    .color("Red").sizes("One Size")
                    .mainImageUrl("https://i.imgur.com/oFSNtju.jpeg").build();

            Product p9 = Product.builder()
                    .name("SHEEPSMAN FACE MASK")
                    .price(70000.0).stock(200).featured(true).category(accessories)
                    .color("Black").sizes("Free Size")
                    .mainImageUrl("https://i.imgur.com/F1Tqkq3.png").build();

            Product p10 = Product.builder()
                    .name("SHEEPSMAN STICKER SET")
                    .price(50000.0).stock(500).featured(true).category(accessories)
                    .color("Multi").sizes("N/A")
                    .mainImageUrl("https://i.imgur.com/xo2KbAR.jpeg").build();

            Product p11 = Product.builder()
                    .name("/solid/ Big Logo Mask")
                    .price(70000.0).stock(150).featured(true).category(accessories)
                    .color("White").sizes("Free Size")
                    .mainImageUrl("https://i.imgur.com/PpborwX.jpeg").build();

            Product p12 = Product.builder()
                    .name("/plastic/ VERTICAL SHOULDER BAG")
                    .price(350000.0).stock(40).featured(true).category(accessories)
                    .color("Transparent").sizes("Small")
                    .mainImageUrl("https://i.imgur.com/KhDxxvo.png").build();

            Product p13 = Product.builder()
                    .name("/two-tone line/ Letter Mask")
                    .price(70000.0).stock(120).featured(true).category(accessories)
                    .color("Grey").sizes("Free Size")
                    .mainImageUrl("https://i.imgur.com/V9LfCUM.jpeg").build();

            Product p14 = Product.builder()
                    .name("/FIRST DROP OF THE YEAR/ Sticker Set")
                    .price(90000.0).stock(300).featured(true).category(accessories)
                    .color("Multi").sizes("N/A")
                    .mainImageUrl("https://i.imgur.com/F8GtaP2.png").build();

            productRepository.saveAll(Arrays.asList(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14));
            System.out.println("Tất cả sản phẩm mẫu từ index đã được khởi tạo thành công!");
        }
    }
}
