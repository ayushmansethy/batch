package com.example.batch;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@XmlRootElement(name = "customer")
@Entity
// @Table(name = "customer")
public class Customer {
    @Id
    private Long id;
    private String name;
    private Integer age;
    private String email;
    private String phone;
}