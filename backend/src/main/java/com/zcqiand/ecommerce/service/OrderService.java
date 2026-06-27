package com.zcqiand.ecommerce.service;

import com.zcqiand.ecommerce.dto.CreateOrderRequest;
import com.zcqiand.ecommerce.entity.*;
import com.zcqiand.ecommerce.exception.BusinessException;
import com.zcqiand.ecommerce.exception.ResourceNotFoundException;
import com.zcqiand.ecommerce.repository.OrderRepository;
import com.zcqiand.ecommerce.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ProductService productService;
    private final InventoryService inventoryService;

    private static final BigDecimal TWO_LEVEL_APPROVAL_THRESHOLD = new BigDecimal("1000");

    @Transactional(readOnly = true)
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Order getOrderById(Long id) {
        return orderRepository.findByIdWithItems(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", id));
    }

    @Transactional(readOnly = true)
    public Order getOrderByNumber(String orderNumber) {
        return orderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "orderNumber", orderNumber));
    }

    @Transactional
    public Order createOrder(CreateOrderRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", request.getUserId()));

        Order order = new Order(generateOrderNumber(), user);

        for (CreateOrderRequest.OrderItemRequest itemRequest : request.getItems()) {
            Product product = productService.getProductById(itemRequest.getProductId());
            OrderItem item = new OrderItem(order, product, itemRequest.getQuantity(), product.getPrice());
            order.addItem(item);
        }

        order.calculateTotalAmount();

        if (order.getTotalAmount().compareTo(TWO_LEVEL_APPROVAL_THRESHOLD) >= 0) {
            order.setApprovalLevel(2);
        } else {
            order.setApprovalLevel(1);
        }

        for (OrderItem item : order.getItems()) {
            inventoryService.lockInventory(item.getProduct().getId(), item.getQuantity());
        }

        return orderRepository.save(order);
    }

    @Transactional
    public Order submitOrder(Long orderId) {
        Order order = getOrderById(orderId);

        if (order.getStatus() != OrderStatus.DRAFT) {
            throw new BusinessException("INVALID_ORDER_STATUS",
                    "Order can only be submitted from DRAFT status");
        }

        order.setStatus(OrderStatus.SUBMITTED);
        order.setSubmittedAt(LocalDateTime.now());
        return orderRepository.save(order);
    }

    @Transactional
    public Order approveOrder(Long orderId, String notes) {
        Order order = getOrderById(orderId);

        if (order.getStatus() != OrderStatus.SUBMITTED) {
            throw new BusinessException("INVALID_ORDER_STATUS",
                    "Order can only be approved from SUBMITTED status");
        }

        order.setStatus(OrderStatus.APPROVED);
        order.setApprovalNotes(notes);
        return orderRepository.save(order);
    }

    @Transactional
    public Order rejectOrder(Long orderId, String reason) {
        if (reason == null || reason.isBlank()) {
            throw new BusinessException("REJECTION_REASON_REQUIRED",
                    "Rejection reason is required");
        }

        Order order = getOrderById(orderId);

        if (order.getStatus() != OrderStatus.SUBMITTED) {
            throw new BusinessException("INVALID_ORDER_STATUS",
                    "Order can only be rejected from SUBMITTED status");
        }

        order.setStatus(OrderStatus.REJECTED);
        order.setApprovalNotes(reason);
        order.setCancelledAt(LocalDateTime.now());
        order.setCancellationReason(reason);

        for (OrderItem item : order.getItems()) {
            inventoryService.unlockInventory(item.getProduct().getId(), item.getQuantity());
        }

        return orderRepository.save(order);
    }

    @Transactional
    public Order payOrder(Long orderId) {
        Order order = getOrderById(orderId);

        if (order.getStatus() != OrderStatus.APPROVED) {
            throw new BusinessException("INVALID_ORDER_STATUS",
                    "Order can only be paid from APPROVED status");
        }

        for (OrderItem item : order.getItems()) {
            inventoryService.deductInventory(item.getProduct().getId(), item.getQuantity());
        }

        order.setStatus(OrderStatus.PAID);
        order.setPaidAt(LocalDateTime.now());
        return orderRepository.save(order);
    }

    @Transactional
    public Order shipOrder(Long orderId) {
        Order order = getOrderById(orderId);

        if (order.getStatus() != OrderStatus.PAID) {
            throw new BusinessException("INVALID_ORDER_STATUS",
                    "Order can only be shipped from PAID status");
        }

        order.setStatus(OrderStatus.SHIPPED);
        order.setShippedAt(LocalDateTime.now());
        return orderRepository.save(order);
    }

    @Transactional
    public Order completeOrder(Long orderId) {
        Order order = getOrderById(orderId);

        if (order.getStatus() != OrderStatus.SHIPPED) {
            throw new BusinessException("INVALID_ORDER_STATUS",
                    "Order can only be completed from SHIPPED status");
        }

        order.setStatus(OrderStatus.COMPLETED);
        order.setCompletedAt(LocalDateTime.now());
        return orderRepository.save(order);
    }

    @Transactional
    public Order cancelOrder(Long orderId, String reason) {
        Order order = getOrderById(orderId);

        if (order.getStatus() == OrderStatus.COMPLETED || order.getStatus() == OrderStatus.SHIPPED) {
            throw new BusinessException("INVALID_ORDER_STATUS",
                    "Cannot cancel order in status: " + order.getStatus());
        }

        if (order.getStatus() == OrderStatus.PAID) {
            for (OrderItem item : order.getItems()) {
                inventoryService.restoreInventory(item.getProduct().getId(), item.getQuantity());
            }
        } else {
            for (OrderItem item : order.getItems()) {
                inventoryService.unlockInventory(item.getProduct().getId(), item.getQuantity());
            }
        }

        order.setStatus(OrderStatus.CANCELLED);
        order.setCancelledAt(LocalDateTime.now());
        order.setCancellationReason(reason);
        return orderRepository.save(order);
    }

    private String generateOrderNumber() {
        return "ORD-" + System.currentTimeMillis() + "-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
