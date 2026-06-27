package com.zcqiand.ecommerce.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "inventory")
@Getter
@Setter
@NoArgsConstructor
public class Inventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false, unique = true)
    private Product product;

    @Column(nullable = false)
    private Long quantity;

    @Column(name = "locked_quantity", nullable = false)
    private Long lockedQuantity;

    @Column(name = "warehouse_location")
    private String warehouseLocation;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public Inventory(Product product, Long quantity, Long lockedQuantity, String warehouseLocation) {
        this.product = product;
        this.quantity = quantity;
        this.lockedQuantity = lockedQuantity;
        this.warehouseLocation = warehouseLocation;
    }

    public Long getAvailableQuantity() {
        return quantity - lockedQuantity;
    }
}
