package com.angular_training.demo.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "product")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String productName;
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal price;
    @Column(nullable = false)
    private int quantity;
    @Column
    private String description;
    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String image;
}
