package com.example.batch;

import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@XmlRootElement(name = "customer")
public class Customer {

    private Long id;
    private String name;
    private Integer age;
    private String email;
    private String phone;
}