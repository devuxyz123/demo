package com.example.demo;

import com.model.UserRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
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
    public  ResponseEntity<String> deleteProduct(@PathVariable Long id) {
        if (id <= 0||id>=100) {
            throw new InvalidInputException("Product ID must be greater than zero.");
        }




//        if(id==0)
//            return String.valueOf(new NullPointerException("ID can not be zero"));

        return ResponseEntity.ok("Product deleted with ID "+id);
    }

    @GetMapping("product/{id}")
    public String getOrder(@PathVariable String id) {
        // Simulating a database lookup failure
        if ("404".equals(id)) {
            throw new ProductNotFoundException("ProductNotFoundException",id);
        }

        if (id.isEmpty()||id.isBlank()) { // Simulating database check
            throw new ResourceNotFoundException("ID is blank or empty");
        }

        return "Product details for ID: " + id;
    }

    // --- LOCAL EXCEPTION HANDLER ---
    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleProductNotFound(ProductNotFoundException ex) {
        Map<String, Object> errorBody = new HashMap<>();
        errorBody.put("timestamp", System.currentTimeMillis());
        errorBody.put("status", HttpStatus.NOT_FOUND.value());
        errorBody.put("error", "Handling locally Product Not Found");
        errorBody.put("message", ex.getMessage());
        errorBody.put("requestedId", ex.getOrderId());

        return new ResponseEntity<>(errorBody, HttpStatus.NOT_FOUND);
    }



    public static void main(String[] args) {

        SpringApplication.run(DemoApplication.class, args);
    }

}
