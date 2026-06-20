package com.example.demo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

@SpringBootTest
class DemoApplicationTests {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @BeforeEach
    void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();
    }

    @Test
    void contextLoads() {
    }

    @Test
    void greetReturnsConfiguredTitle() throws Exception {
        mockMvc.perform(get("/api/greet"))
            .andExpect(status().isOk())
            .andExpect(content().string(notNullValue()));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createUserReturnsCreatedUserWithRole() throws Exception {
        String userJson = "{\"name\":\"testUser\",\"role\":\"ADMIN\"}";

        mockMvc.perform(post("/api/admin")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userJson))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.role").value("ADMIN"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void getAllProductsReturnsSuccessfullyForUserRole() throws Exception {
        mockMvc.perform(get("/api/products"))
            .andExpect(status().isOk())
            .andExpect(content().string("List of products"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllProductsReturnsSuccessfullyForAdminRole() throws Exception {
        mockMvc.perform(get("/api/products"))
            .andExpect(status().isOk())
            .andExpect(content().string("List of products"));
    }

    @Test
    void getAllProductsRequiresAuthentication() throws Exception {
        mockMvc.perform(get("/api/products"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteProductWithValidIdReturnsSuccessMessage() throws Exception {
        mockMvc.perform(delete("/api/delete/50"))
            .andExpect(status().isOk())
            .andExpect(content().string(containsString("Product deleted with ID 50")));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteProductWithIdZeroThrowsInvalidInputException() throws Exception {
        mockMvc.perform(delete("/api/delete/0"))
            .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteProductWithNegativeIdThrowsInvalidInputException() throws Exception {
        mockMvc.perform(delete("/api/delete/-5"))
            .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteProductWithIdGreaterOrEqualTo100ThrowsInvalidInputException() throws Exception {
        mockMvc.perform(delete("/api/delete/100"))
            .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "USER")
    void deleteProductWithUserRoleDeniesAccess() throws Exception {
        mockMvc.perform(delete("/api/delete/50"))
            .andExpect(status().isForbidden());
    }

    @Test
    void deleteProductRequiresAuthentication() throws Exception {
        mockMvc.perform(delete("/api/delete/50"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    void getOrderWithValidIdReturnsProductDetails() throws Exception {
        mockMvc.perform(get("/api/product/123"))
            .andExpect(status().isOk())
            .andExpect(content().string(containsString("Product details for ID: 123")));
    }

    @Test
    @WithMockUser
    void getOrderWith404IdThrowsProductNotFoundException() throws Exception {
        mockMvc.perform(get("/api/product/404"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.error").value("Handling locally Product Not Found"))
            .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    @WithMockUser
    void getOrderWithBlankIdThrowsResourceNotFoundException() throws Exception {
        mockMvc.perform(get("/api/product/ "))
            .andExpect(status().isNotFound());
    }


    @Test
    @WithMockUser
    void getOrderExceptionHandlerIncludesTimestampAndStatus() throws Exception {
        mockMvc.perform(get("/api/product/404"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.timestamp").isNumber())
            .andExpect(jsonPath("$.status").value(404))
            .andExpect(jsonPath("$.message").isNotEmpty());
    }

}