package com.zcqiand.ecommerce.controller;

import com.zcqiand.ecommerce.dto.ApiResponse;
import com.zcqiand.ecommerce.entity.Inventory;
import com.zcqiand.ecommerce.service.InventoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
@Tag(name = "Inventory", description = "库存管理接口")
public class InventoryController {

    private final InventoryService inventoryService;

    @Operation(summary = "获取所有库存", description = "查询所有库存记录")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "查询成功",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class)))
    })
    @GetMapping
    public ResponseEntity<ApiResponse<List<Inventory>>> getAllInventory() {
        List<Inventory> inventory = inventoryService.getAllInventory();
        return ResponseEntity.ok(ApiResponse.success(inventory));
    }

    @Operation(summary = "根据产品ID获取库存", description = "查询指定产品的库存")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "查询成功",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class))),
            @ApiResponse(responseCode = "404", description = "库存不存在",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class)))
    })
    @GetMapping("/product/{productId}")
    public ResponseEntity<ApiResponse<Inventory>> getInventoryByProductId(@PathVariable Long productId) {
        Inventory inventory = inventoryService.getInventoryByProductId(productId);
        return ResponseEntity.ok(ApiResponse.success(inventory));
    }

    @Operation(summary = "创建库存", description = "为产品创建新的库存记录")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "创建成功",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class))),
            @ApiResponse(responseCode = "404", description = "产品不存在",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class)))
    })
    @PostMapping
    public ResponseEntity<ApiResponse<Inventory>> createInventory(@RequestParam Long productId,
                                                                  @RequestParam Long quantity,
                                                                  @RequestParam(required = false) String warehouseLocation) {
        Inventory inventory = inventoryService.createInventory(productId, quantity, warehouseLocation);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("创建成功", inventory));
    }

    @Operation(summary = "更新库存", description = "更新现有库存记录")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "更新成功",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class))),
            @ApiResponse(responseCode = "404", description = "库存不存在",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class)))
    })
    @PutMapping("/product/{productId}")
    public ResponseEntity<ApiResponse<Inventory>> updateInventory(@PathVariable Long productId,
                                                                   @RequestParam Long quantity,
                                                                   @RequestParam(required = false) String warehouseLocation) {
        Inventory inventory = inventoryService.updateInventory(productId, quantity, warehouseLocation);
        return ResponseEntity.ok(ApiResponse.success("更新成功", inventory));
    }

    @Operation(summary = "锁定库存", description = "为订单锁定库存")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "锁定成功",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class))),
            @ApiResponse(responseCode = "400", description = "库存不足",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class)))
    })
    @PostMapping("/lock")
    public ResponseEntity<ApiResponse<Void>> lockInventory(@RequestParam Long productId,
                                                           @RequestParam Long quantity) {
        inventoryService.lockInventory(productId, quantity);
        return ResponseEntity.ok(ApiResponse.success("锁定成功", null));
    }

    @Operation(summary = "解锁库存", description = "解锁之前锁定的库存")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "解锁成功",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class)))
    })
    @PostMapping("/unlock")
    public ResponseEntity<ApiResponse<Void>> unlockInventory(@RequestParam Long productId,
                                                               @RequestParam Long quantity) {
        inventoryService.unlockInventory(productId, quantity);
        return ResponseEntity.ok(ApiResponse.success("解锁成功", null));
    }
}
