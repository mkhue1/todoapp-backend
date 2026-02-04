package com.angular_training.demo.controller;


import com.angular_training.demo.dto.CreateOrderRequest;
import com.angular_training.demo.dto.OrderItemRequest;
import com.angular_training.demo.dto.OrderItemResponse;
import com.angular_training.demo.dto.OrderResponse;
import com.angular_training.demo.model.*;
import com.angular_training.demo.repository.OrderRepository;
import com.angular_training.demo.repository.ProductRepository;
import com.angular_training.demo.repository.UserRepository;
import com.angular_training.demo.security.CustomUserDetails;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.angular_training.demo.model.OrderStatus.NEW;


@RestController
@RequestMapping("/order")
public class OrderController {
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    public OrderController(OrderRepository orderRepository, UserRepository userRepository, ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
    }

    @GetMapping
    public List<OrderResponse> getOrders() {
        User currentUser = getCurrentUser();
        return orderRepository.findByUserOrderByCreatedAtDesc(currentUser)
                .stream()
                .map(this::toResponse)
                .toList();
    }


    @PostMapping
    public OrderResponse createOrder(@RequestBody CreateOrderRequest request) {
        User currentUser = getCurrentUser();
        List<OrderItem> orderItems = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;

        // duyệt từng item client gửi lên
        for (OrderItemRequest itemReq : request.getItems()) {
            Product product = productRepository.findById(itemReq.getProductId())
                    .orElseThrow();

            if (product.getQuantity() < itemReq.getQuantity()) {
                throw new IllegalArgumentException(
                        "Sản phẩm " + product.getProductName() + " không đủ tồn kho"
                );
            }

            // trừ tồn kho
            product.setQuantity(product.getQuantity() - itemReq.getQuantity());
            productRepository.save(product);

            BigDecimal price = product.getPrice();
            BigDecimal subtotal = price.multiply(BigDecimal.valueOf(itemReq.getQuantity()));


            OrderItem orderItem = OrderItem.builder()
                    .product(product)
                    .quantity(itemReq.getQuantity())
                    .price(price)
                    .subtotal(subtotal)
                    .build();

            orderItems.add(orderItem);
            total = total.add(subtotal);
        }

        // tạo order
        Order order = new Order();
        order.setUser(currentUser);
        order.setTotalAmount(total);
        order.setStatus(NEW);
        order.setCreatedAt(LocalDateTime.now());
        order.setItems(orderItems);

        // set order cho từng item (vì mappedBy)
        orderItems.forEach(oi -> oi.setOrder(order));

        Order saved = orderRepository.save(order);
        return toResponse(saved);
    }


    private OrderResponse toResponse(Order order) {
        List<OrderItemResponse> itemResponses = order.getItems()
                .stream()
                .map(oi -> new OrderItemResponse(
                        oi.getProduct().getId(),
                        oi.getProduct().getProductName(),
                        oi.getQuantity(),
                        oi.getPrice(),
                        oi.getSubtotal()
                ))
                .toList();

        return new OrderResponse(
                order.getId(),
                order.getTotalAmount(),
                order.getStatus(),
                order.getCreatedAt(),
                itemResponses
        );
    }

    @DeleteMapping(value = "/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public void delete(@PathVariable Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        // ❗ Không cho xóa order đã CONFIRMED
        if (order.getStatus() == OrderStatus.CONFIRMED) {
            throw new IllegalStateException("Không thể xoá order đã CONFIRMED");
        }

        // hoàn kho cho từng item
        for (OrderItem item : order.getItems()) {
            Product product = item.getProduct();
            product.setQuantity(product.getQuantity() + item.getQuantity());
            productRepository.save(product);
        }

        orderRepository.delete(order);
    }

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails cud = (CustomUserDetails) auth.getPrincipal();
        String username = cud.getUsername();

        return userRepository.findByUsername(username) .orElseThrow();
    }

    @GetMapping("/{id}")
    public OrderResponse getOrderById(@ PathVariable Long id) {
        User currentUser = getCurrentUser();

        Order order = orderRepository.findById(id)
                .orElseThrow();

        // đảm bảo user chỉ xem được đơn của chính mình
        if (!order.getUser().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("Forbidden");
        }

        return toResponse(order);
    }
}
