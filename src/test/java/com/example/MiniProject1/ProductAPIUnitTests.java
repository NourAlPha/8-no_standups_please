package com.example.MiniProject1;

import com.example.Controller.ProductController;
import com.example.model.Product;
import com.example.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static
        org.springframework.test.web.servlet.request.
                MockMvcRequestBuilders.get;
import static
        org.springframework.test.web.servlet.request.
                MockMvcRequestBuilders.post;
import static
        org.springframework.test.web.servlet.request.
                MockMvcRequestBuilders.put;
import static
        org.springframework.test.web.servlet.result.
                MockMvcResultMatchers.content;
import static
        org.springframework.test.web.servlet.result.
                MockMvcResultMatchers.jsonPath;
import static
        org.springframework.test.web.servlet.result.
                MockMvcResultMatchers.status;

public class ProductAPIUnitTests {

    private MockMvc mockMvc;

    private static final double TEST_PRODUCT_PRICE = 100.0;
    private static final double UPDATED_PRODUCT_PRICE = 80.0;

    @Mock
    private ProductService productService;

    @InjectMocks
    private ProductController productController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(productController).build();
    }

    // Tests for addProduct
    @Test
    void testAddProduct() throws Exception {
        Product product = new Product("Test Product", TEST_PRODUCT_PRICE);
        when(productService.addProduct(any())).thenReturn(product);

        mockMvc.perform(post("/product/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Test Product\", "
                                + "\"price\":100.0}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Test Product")))
                .andExpect(jsonPath("$.price", is(TEST_PRODUCT_PRICE)));
    }

    @Test
    void testAddProductWithEmptyName() throws Exception {
        mockMvc.perform(post("/product/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"\",\"price\":100.0}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testAddProductWithNegativePrice() throws Exception {
        mockMvc.perform(post("/product/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Test Product\", "
                                + "\"price\":-10.0}"))
                .andExpect(status().isBadRequest());
    }

    // Tests for getProducts
    @Test
    void testGetProducts() throws Exception {
        ArrayList<Product> products = new ArrayList<>();
        products.add(new Product("Test Product", TEST_PRODUCT_PRICE));
        when(productService.getProducts()).thenReturn(products);

        mockMvc.perform(get("/product/"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void testGetProductsEmptyList() throws Exception {
        when(productService.getProducts()).thenReturn(new ArrayList<>());

        mockMvc.perform(get("/product/"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void testGetProductsReturnsNull() throws Exception {
        when(productService.getProducts()).thenReturn(null);

        mockMvc.perform(get("/product/"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").doesNotExist());
    }

    // Tests for getProductById
    @Test
    void testGetProductById() throws Exception {
        UUID productId = UUID.randomUUID();
        Product product = new Product("Test Product", TEST_PRODUCT_PRICE);
        when(productService.getProductById(productId)).thenReturn(product);

        mockMvc.perform(get("/product/" + productId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Test Product")));
    }

    @Test
    void testGetProductByIdNotFound() throws Exception {
        UUID productId = UUID.randomUUID();
        when(productService.getProductById(productId)).thenReturn(null);

        mockMvc.perform(get("/product/" + productId))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetProductByInvalidId() throws Exception {
        mockMvc.perform(get("/product/invalid-uuid"))
                .andExpect(status().isBadRequest());
    }

    // Tests for updateProduct
    @Test
    void testUpdateProduct() throws Exception {
        UUID productId = UUID.randomUUID();
        Product updatedProduct = new Product("Updated Product",
                UPDATED_PRODUCT_PRICE);
        when(productService.updateProduct(eq(productId), anyString(),
                anyDouble())).thenReturn(updatedProduct);

        mockMvc.perform(put("/product/update/" + productId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"newName\":\"Updated Product\", "
                                + "\"newPrice\":80.0}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Updated Product")))
                .andExpect(jsonPath("$.price", is(UPDATED_PRODUCT_PRICE)));
    }

    @Test
    void testUpdateNonExistingProduct() throws Exception {
        UUID productId = UUID.randomUUID();
        when(productService.updateProduct(any(), any(),
                anyDouble())).thenReturn(null);

        mockMvc.perform(put("/product/update/" + productId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"newName\":\"Updated Product\", "
                                + "\"newPrice\":80.0}"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testUpdateProductWithInvalidData() throws Exception {
        UUID productId = UUID.randomUUID();

        mockMvc.perform(put("/product/update/" + productId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"newName\":\"\", \"newPrice\":-50.0}"))
                .andExpect(status().isBadRequest());
    }

    // Tests for applyDiscount
    @Test
    void testApplyDiscount() throws Exception {
        mockMvc.perform(put("/product/applyDiscount?discount=10.0")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("[]"))
                .andExpect(status().isOk())
                .andExpect(content().string("Discount Applied"));
    }

    @Test
    void testApplyDiscountEmptyList() throws Exception {
        mockMvc.perform(put("/product/applyDiscount?discount=10.0")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("[]"))
                .andExpect(status().isOk());
    }

    @Test
    void testApplyInvalidDiscount() throws Exception {
        mockMvc.perform(put("/product/applyDiscount?discount=-10.0")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("[]"))
                .andExpect(status().isBadRequest());
    }
}
