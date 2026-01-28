package com.angular_training.demo.controller;

import com.angular_training.demo.model.Product;

import com.angular_training.demo.repository.ProductRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/product")
public class ProductController {
    private ProductRepository productRepository;

    public ProductController(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public List<Product> findall() {
        return productRepository.findAll();
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN')")
    public Product save(@RequestBody Product product) {
        return productRepository.save(product);
    }

    @PutMapping(value = "/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public Product update(@PathVariable Long id, @RequestBody Product product) {
        Product existing = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Todo not found"));

        existing.setProductName(product.getProductName());
        existing.setPrice(product.getPrice());
        existing.setQuantity(product.getQuantity());
        existing.setDescription(product.getDescription());

        return productRepository.save(existing);
    }

    @DeleteMapping(value = "/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public void delete(@PathVariable Long id) {
        productRepository.deleteById(id);
    }

}
