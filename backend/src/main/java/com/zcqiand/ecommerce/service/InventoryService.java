package com.zcqiand.ecommerce.service;

import com.zcqiand.ecommerce.entity.Inventory;
import com.zcqiand.ecommerce.entity.Product;
import com.zcqiand.ecommerce.exception.BusinessException;
import com.zcqiand.ecommerce.exception.ResourceNotFoundException;
import com.zcqiand.ecommerce.repository.InventoryRepository;
import com.zcqiand.ecommerce.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private final ProductRepository productRepository;

    @Transactional(readOnly = true)
    public List<Inventory> getAllInventory() {
        return inventoryRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Inventory getInventoryByProductId(Long productId) {
        return inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory", "productId", productId));
    }

    @Transactional
    public void lockInventory(Long productId, Long quantity) {
        Inventory inventory = inventoryRepository.findByProductIdWithLock(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory", "productId", productId));

        if (inventory.getAvailableQuantity() < quantity) {
            throw new BusinessException("INSUFFICIENT_INVENTORY",
                    "Not enough inventory available for product ID: " + productId);
        }

        inventory.setLockedQuantity(inventory.getLockedQuantity() + quantity);
        inventoryRepository.save(inventory);
    }

    @Transactional
    public void unlockInventory(Long productId, Long quantity) {
        Inventory inventory = inventoryRepository.findByProductIdWithLock(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory", "productId", productId));

        long newLockedQuantity = Math.max(0, inventory.getLockedQuantity() - quantity);
        inventory.setLockedQuantity(newLockedQuantity);
        inventoryRepository.save(inventory);
    }

    @Transactional
    public void deductInventory(Long productId, Long quantity) {
        Inventory inventory = inventoryRepository.findByProductIdWithLock(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory", "productId", productId));

        if (inventory.getQuantity() < quantity) {
            throw new BusinessException("INSUFFICIENT_INVENTORY",
                    "Not enough inventory for product ID: " + productId);
        }

        inventory.setQuantity(inventory.getQuantity() - quantity);
        inventory.setLockedQuantity(Math.max(0, inventory.getLockedQuantity() - quantity));
        inventoryRepository.save(inventory);
    }

    @Transactional
    public void restoreInventory(Long productId, Long quantity) {
        Inventory inventory = inventoryRepository.findByProductIdWithLock(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory", "productId", productId));

        inventory.setQuantity(inventory.getQuantity() + quantity);
        inventoryRepository.save(inventory);
    }

    @Transactional
    public Inventory createInventory(Long productId, Long quantity, String warehouseLocation) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));

        Inventory inventory = new Inventory(product, quantity, 0L, warehouseLocation);
        return inventoryRepository.save(inventory);
    }

    @Transactional
    public Inventory updateInventory(Long productId, Long quantity, String warehouseLocation) {
        Inventory inventory = getInventoryByProductId(productId);
        inventory.setQuantity(quantity);
        inventory.setWarehouseLocation(warehouseLocation);
        return inventoryRepository.save(inventory);
    }
}
