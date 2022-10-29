package com.myselectshop.springcore.integration;

import com.myselectshop.springcore.dto.ProductMypriceRequestDto;
import com.myselectshop.springcore.dto.ProductRequestDto;
import com.myselectshop.springcore.dto.SignupRequestDto;
import com.myselectshop.springcore.model.Product;
import com.myselectshop.springcore.model.User;
import com.myselectshop.springcore.model.UserRoleEnum;
import com.myselectshop.springcore.service.ProductService;
import com.myselectshop.springcore.service.UserService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;

//Test에서 스프링 부트 사용, 포트가 겹칠경우 에러발생하므로 랜덤포트를 지정
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ProductIntegrationTest {
    @Autowired
    ProductService productService;
    @Autowired
    UserService userService;
    @Autowired
    PasswordEncoder passwordEncoder;

    Long userId = null;
    Product createdProduct = null;
    int updatedMyPrice = -1;

    //숙제 회원가입 과정도 포함시키기
    private String title;
    private String imageUrl;
    private String linkUrl;
    private int lPrice;

    private ProductRequestDto requestDto;

    @BeforeEach
    void setup(){
        title = "Apple <b>에어팟</b> 2세대 유선충전 모델 (MV7N2KH/A)";
        imageUrl = "https://shopping-phinf.pstatic.net/main_1862208/18622086330.20200831140839.jpg";
        linkUrl = "https://search.shopping.naver.com/gate.nhn?id=18622086330";
        lPrice = 77000;

        requestDto = new ProductRequestDto(
                title,
                imageUrl,
                linkUrl,
                lPrice
        );
    }
    //회원 가입 전 관심상품 등록(실패)
    @Test
    @Order(1)
    @DisplayName("회원 가입 전 관심상품 등록(실패)")
    void nonSignupCreateProduct(){
        // given
        // when
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            productService.createProduct(requestDto, userId);
        });

        // then
        assertEquals("회원 Id 가 유효하지 않습니다.", exception.getMessage());
    }

    //회원 가입
    @Test
    @Order(2)
    @DisplayName("회원 가입")
    void signup(){
        //given
        String username = "asd";
        String password = "123";
        String email = "asd@sparta.com";
        SignupRequestDto signupRequestDto = new SignupRequestDto();
        signupRequestDto.setUsername(username);
        signupRequestDto.setPassword(password);
        signupRequestDto.setEmail(email);
        //when
        User user = userService.registerUser(signupRequestDto);
        //then
        assertNotNull(user.getId());
        assertEquals(user.getUsername(),username);
        assertTrue(passwordEncoder.matches(password,user.getPassword()));
        assertEquals(user.getEmail(),email);
        assertEquals(user.getRole(), UserRoleEnum.USER);

//        String pass = passwordEncoder.encode("123");
//        System.out.println(pass);

        userId = user.getId();
    }
    //숙제


    @Test
    @Order(3) //실행 순서
    @DisplayName("신규 관심상품 등록")
    void test1() {
        // given

        // when
        Product product = productService.createProduct(requestDto, userId);

        // then
        assertNotNull(product.getId());
        assertEquals(userId, product.getUserId());
        assertEquals(title, product.getTitle());
        assertEquals(imageUrl, product.getImage());
        assertEquals(linkUrl, product.getLink());
        assertEquals(lPrice, product.getLprice());
        assertEquals(0, product.getMyprice());
        createdProduct = product;
    }

    @Test
    @Order(4)
    @DisplayName("신규 등록된 관심상품의 희망 최저가 변경")
    void test2() {
        // given
        Long productId = this.createdProduct.getId();
        int myPrice = 70000;
        ProductMypriceRequestDto requestDto = new ProductMypriceRequestDto(myPrice);

        // when
        Product product = productService.updateProduct(productId, requestDto);

        // then
        assertNotNull(product.getId());
        assertEquals(userId, product.getUserId());
        assertEquals(this.createdProduct.getTitle(), product.getTitle());
        assertEquals(this.createdProduct.getImage(), product.getImage());
        assertEquals(this.createdProduct.getLink(), product.getLink());
        assertEquals(this.createdProduct.getLprice(), product.getLprice());
        assertEquals(myPrice, product.getMyprice());
        this.updatedMyPrice = myPrice;
    }

    @Test
    @Order(5)
    @DisplayName("회원이 등록한 모든 관심상품 조회")
    void test3() {
        // given
        int page  = 0;
        int size  = 10;
        String sortBy = "id";
        boolean isAsc = false;

        // when
        Page<Product> productList = productService.getProducts(userId, page, size, sortBy, isAsc);

        // then
        // 1. 전체 상품에서 테스트에 의해 생성된 상품 찾아오기 (상품의 id 로 찾음)
        Long createdProductId = this.createdProduct.getId();
        Product foundProduct = productList.stream()
                .filter(product -> product.getId().equals(createdProductId))
                .findFirst()
                .orElse(null);

        // 2. Order(1) 테스트에 의해 생성된 상품과 일치하는지 검증
        assertNotNull(foundProduct);
        assertEquals(userId, foundProduct.getUserId());
        assertEquals(this.createdProduct.getId(), foundProduct.getId());
        assertEquals(this.createdProduct.getTitle(), foundProduct.getTitle());
        assertEquals(this.createdProduct.getImage(), foundProduct.getImage());
        assertEquals(this.createdProduct.getLink(), foundProduct.getLink());
        assertEquals(this.createdProduct.getLprice(), foundProduct.getLprice());

        // 3. Order(2) 테스트에 의해 myPrice 가격이 정상적으로 업데이트되었는지 검증
        assertEquals(this.updatedMyPrice, foundProduct.getMyprice());
    }
}
