package com.example.demo;

import com.model.UserRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.logging.Logger;

@SpringBootApplication
@RestController
@RequestMapping("/api")
public class DemoApplication {


    @Value("${app.title}")
    private String title;

    private static final Logger logger = Logger.getLogger(DemoApplication.class.getName());

    @GetMapping("/greet")
    public String greet() {
       logger.info("logging msg");
       logger.warning("warning msg...");
        return title;
    }

    @PostMapping(value="/admin" )
    public ResponseEntity<UserRecord> createUser(@RequestBody UserRecord newUser) {
        System.out.println("userRecord created with role ..."+newUser.getRole());
        return ResponseEntity.ok(newUser);

    }

    @GetMapping("/products")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public String getAllProducts() {
        return "List of products";
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String deleteProduct(@PathVariable Long id) {
        return "Product deleted with ID "+10;
    }

    public static void main(String[] args) {

        SpringApplication.run(DemoApplication.class, args);
    }

}
