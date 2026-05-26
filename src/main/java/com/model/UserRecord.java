package com.model;

//import jakarta.persistence.Entity;
//import jakarta.persistence.Id;
import lombok.Data;

//@Entity
@Data // Generates getters, setters, and constructors automatically
public class UserRecord {
    //@Id
    private Long id;
    private String name;
}
