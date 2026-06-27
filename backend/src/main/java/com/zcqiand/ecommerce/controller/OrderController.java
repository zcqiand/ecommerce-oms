package com.zcqiand.ecommerce.controller;

import com.zcqiand.ecommerce.dto.ApiResponse;
import com.zcqiand.ecommerce.dto.CreateOrderRequest;
import com.zcqiand.ecommerce.entity.Order;
import com.zcqiand.ecommerce.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Tag(name = "Order", description = "订单管理接口")
public class OrderController {

    private final OrderService orderService;

    @Operation(summary = "获取所有订单", description = "查询系统中所有订单")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "查询成功",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class)))
    })
    @GetMapping
    public ResponseEntity<ApiResponse<List<Order>>> getAllOrders() {
        List<Order> orders = orderService.getAllOrders();
        return ResponseEntity.ok(ApiResponse.success(orders));
    }

    @Operation(summary = "根据ID获取订单", description = "根据ID查询指定订单")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "查询成功",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class))),
            @ApiResponse(responseCode = "404", description = "订单不存在",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Order>> getOrderById(@PathVariable Long id) {
        Order order = orderService.getOrderById(id);
        return ResponseEntity.ok(ApiResponse.success(order));
    }

    @Operation(summary = "根据订单号获取订单", description = "根据订单号查询指定订单")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "查询成功",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class))),
            @ApiResponse(responseCode = "404", description = "订单不存在",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class)))
    })
    @GetMapping("/number/{orderNumber}")
    public ResponseEntity<ApiResponse<Order>> getOrderByNumber(@PathVariable String orderNumber) {
        Order order = orderService.getOrderByNumber(orderNumber);
        return ResponseEntity.ok(ApiResponse.success(order));
    }

    @Operation(summary = "创建订单", description = "创建新订单并锁定库存")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "创建成功",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class))),
            @ApiResponse(responseCode = "400", description = "输入无效",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class)))
    })
    @PostMapping
    public ResponseEntity<ApiResponse<Order>> createOrder(@Valid @RequestBody CreateOrderRequest request) {
        Order order = orderService.createOrder(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("创建成功", order));
    }

    @Operation(summary = "提交订单", description = "提交草稿订单进行审批")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "提交成功",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class))),
            @ApiResponse(responseCode = "400", description = "订单状态无效",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class)))
    })
    @PostMapping("/{id}/submit")
    public ResponseEntity<ApiResponse<Order>> submitOrder(@PathVariable Long id) {
        Order order = orderService.submitOrder(id);
        return ResponseEntity.ok(ApiResponse.success("提交成功", order));
    }

    @Operation(summary = "审批订单", description = "审批已提交的订单")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "审批成功",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class))),
            @ApiResponse(responseCode = "400", description = "订单状态无效",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class)))
    })
    @PostMapping("/{id}/approve")
    public ResponseEntity<ApiResponse<Order>> approveOrder(@PathVariable Long id,
                                                           @RequestParam(required = false) String notes) {
        Order order = orderService.approveOrder(id, notes);
        return ResponseEntity.ok(ApiResponse.success("审批成功", order));
    }

    @Operation(summary = "拒绝订单", description = "拒绝已提交的订单并填写原因")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "拒绝成功",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class))),
            @ApiResponse(responseCode = "400", description = "订单状态无效或缺少原因",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class)))
    })
    @PostMapping("/{id}/reject")
    public ResponseEntity<ApiResponse<Order>> rejectOrder(@PathVariable Long id,
                                                          @RequestParam String reason) {
        Order order = orderService.rejectOrder(id, reason);
        return ResponseEntity.ok(ApiResponse.success("拒绝成功", order));
    }

    @Operation(summary = "支付订单", description = "处理已审批订单的支付并扣减库存")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "支付成功",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class))),
            @ApiResponse(responseCode = "400", description = "订单状态无效",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class)))
    })
    @PostMapping("/{id}/pay")
    public ResponseEntity<ApiResponse<Order>> payOrder(@PathVariable Long id) {
        Order order = orderService.payOrder(id);
        return ResponseEntity.ok(ApiResponse.success("支付成功", order));
    }

    @Operation(summary = "发货订单", description = "将订单标记为已发货")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "发货成功",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class))),
            @ApiResponse(responseCode = "400", description = "订单状态无效",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class)))
    })
    @PostMapping("/{id}/ship")
    public ResponseEntity<ApiResponse<Order>> shipOrder(@PathVariable Long id) {
        Order order = orderService.shipOrder(id);
        return ResponseEntity.ok(ApiResponse.success("发货成功", order));
    }

    @Operation(summary = "完成订单", description = "将订单标记为已完成")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "完成成功",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class))),
            @ApiResponse(responseCode = "400", description = "订单状态无效",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class)))
    })
    @PostMapping("/{id}/complete")
    public ResponseEntity<ApiResponse<Order>> completeOrder(@PathVariable Long id) {
        Order order = orderService.completeOrder(id);
        return ResponseEntity.ok(ApiResponse.success("完成成功", order));
    }

    @Operation(summary = "取消订单", description = "取消订单并填写原因")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "取消成功",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class))),
            @ApiResponse(responseCode = "400", description = "订单状态无效",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class)))
    })
    @PostMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse<Order>> cancelOrder(@PathVariable Long id,
                                                          @RequestParam(required = false) String reason) {
        Order order = orderService.cancelOrder(id, reason);
        return ResponseEntity.ok(ApiResponse.success("取消成功", order));
    }
}
