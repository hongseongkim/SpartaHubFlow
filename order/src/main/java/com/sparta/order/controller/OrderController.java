package com.sparta.order.controller;

import com.sparta.order.domain.dto.OrderDto;
import com.sparta.order.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/orders")
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    @Operation(summary = "주문 생성", description = "주문을 생성합니다.")
    public OrderDto.Response createOrder(@RequestHeader("User-Id") Long userId,
                                         @RequestBody OrderDto.Create orderDto) {
        return orderService.createOrder(userId, orderDto);
    }

    @GetMapping("/{orderId}")
    @Operation(summary = "주문 단건 조회", description = "주문을 단건 조회합니다.")
    public OrderDto.Response getOrder(@RequestHeader("User-Id") Long userId,
                                      @RequestHeader("User-Role") String userRole,
                                      @PathVariable(value = "orderId") UUID orderId) {
        return orderService.getOrder(userId, userRole, orderId);
    }

    @GetMapping
    @Operation(summary = "주문 전체 조회", description = "주문을 페이징 조회합니다.")
    public List<OrderDto.GetAllOrdersResponse> getAllOrders(
            @RequestHeader("User-Id") Long userId,
            @RequestHeader("User-Role") String userRole,
            @Schema(description = "페이지 번호(1부터 N까지)", defaultValue = "1")
            @RequestParam("page") int page,
            @Schema(description = "페이지에 출력할 개수를 입력합니다.", defaultValue = "10")
            @RequestParam("size") int size,
            @Schema(description = "정렬 기준을 입력합니다.")
            @RequestParam("sort") String sortBy
    ) {
        return orderService.getAllOrders(userId, userRole, page, size, sortBy);
    }

    @PatchMapping("/{orderId}")
    @Operation(summary = "주문 수정", description = "주문을 수정합니다.")
    public OrderDto.Response modifyOrder(@RequestHeader("User-Id") Long userId,
                                         @RequestHeader("User-Role") String userRole,
                                         @PathVariable(value = "orderId") UUID orderId,
                                         @RequestBody OrderDto.Modify orderDto) {
        return orderService.modifyOrder(userId, userRole, orderId, orderDto);
    }

    @DeleteMapping("/{orderId}")
    @Operation(summary = "주문 삭제", description = "주문을 삭제합니다.")
    public OrderDto.DeleteResponse deleteOrder(@RequestHeader("User-Id") Long userId,
                                               @RequestHeader("User-Role") String userRole,
                                               @PathVariable(value = "orderId") UUID orderId,
                                               @RequestBody OrderDto.Delete orderDto) {
        return orderService.deleteOrder(userId, userRole, orderId, orderDto);
    }
}
