package com.sparta.order.controller;

import com.sparta.order.domain.dto.OrderDto;
import com.sparta.order.service.OrderService;
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
    public OrderDto.Response createOrder(@RequestBody OrderDto.Response orderDto) {
        return orderService.createOrder(orderDto);
    }

    @GetMapping("/{orderId}")
    public OrderDto.Response getOrder(@PathVariable(value = "orderId") UUID orderId) {
        return orderService.getOrder(orderId);
    }

    @GetMapping
    public List<OrderDto.GetAllOrdersResponse> getAllOrders(
            @RequestParam("page") int page,
            @RequestParam("size") int size,
            @RequestParam("sort") String sortBy
    ) {
        return orderService.getAllOrders(page, size, sortBy);
    }

    @PatchMapping("/{orderId}")
    public OrderDto.Response modifyOrder(@PathVariable(value = "orderId") UUID orderId, @RequestBody OrderDto.Modify orderDto) {
        return orderService.modifyOrder(orderId, orderDto);
    }

    @DeleteMapping("/{orderId}")
    public OrderDto.DeleteResponse deleteOrder(@PathVariable(value = "orderId") UUID orderId, @RequestBody OrderDto.Delete orderDto) {
        return orderService.deleteOrder(orderId, orderDto);
    }
}
