package com.sparta.order.client;

import com.sparta.order.domain.dto.DeliveryDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "product-service")
public interface DeliveryClient {
    @PostMapping("api/v1/deliveries")
    void createDelivery(@RequestBody DeliveryDto.Create deliveryDto);
}
