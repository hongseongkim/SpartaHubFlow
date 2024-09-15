package com.sparta.delivery.infrastructure.client;

import com.sparta.delivery.domain.delivery.dto.order.OrderDto;
import java.util.UUID;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "order-service", configuration = FeignConfig.class)
public interface OrderFeignClient {
    @GetMapping("api/v1/orders/{orderId}")
    OrderDto.Response getOrder(@PathVariable(value = "orderId") UUID orderId);
}
