package com.zcqiand.ecommerce.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zcqiand.ecommerce.entity.Product;
import com.zcqiand.ecommerce.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ProductRepository productRepository;

    @BeforeEach
    void setUp() {
        productRepository.deleteAll();
    }

    @Test
    void getAllProducts_shouldReturnEmptyList_whenNoProductsExist() throws Exception {
        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    void createProduct_shouldReturnCreatedProduct() throws Exception {
        Product product = new Product();
        product.setSku("TEST-SKU-001");
        product.setName("Test Product");
        product.setDescription("Test Description");
        product.setPrice(new BigDecimal("99.99"));
        product.setCategory("Test Category");

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(product)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.sku").value("TEST-SKU-001"))
                .andExpect(jsonPath("$.data.name").value("Test Product"));
    }

    @Test
    void getProductById_shouldReturnProduct_whenProductExists() throws Exception {
        Product product = new Product();
        product.setSku("TEST-SKU-002");
        product.setName("Test Product 2");
        product.setPrice(new BigDecimal("199.99"));
        product.setCategory("Test");
        Product saved = productRepository.save(product);

        mockMvc.perform(get("/api/products/{id}", saved.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(saved.getId()))
                .andExpect(jsonPath("$.data.sku").value("TEST-SKU-002"));
    }

    @Test
    void getProductById_shouldReturn404_whenProductNotFound() throws Exception {
        mockMvc.perform(get("/api/products/{id}", 99999))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.code").value("RESOURCE_NOT_FOUND"));
    }

    @Test
    void deleteProduct_shouldReturnSuccess_whenProductExists() throws Exception {
        Product product = new Product();
        product.setSku("TEST-SKU-003");
        product.setName("Test Product 3");
        product.setPrice(new BigDecimal("50.00"));
        product.setCategory("Test");
        Product saved = productRepository.save(product);

        mockMvc.perform(delete("/api/products/{id}", saved.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        mockMvc.perform(get("/api/products/{id}", saved.getId()))
                .andExpect(status().isNotFound());
    }
}
