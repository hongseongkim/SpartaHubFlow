package com.sparta.order.client;

import com.sparta.order.domain.dto.ProductDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.UUID;

@FeignClient(name = "product-service")
public interface ProductClient {
    @GetMapping("api/v1/products/{productId}")
    ProductDto.Response getProductById(@PathVariable("productId") UUID productId);

    @PatchMapping("api/v1/products/{productId}")
    ProductDto.Response modifyProduct(@PathVariable UUID productId, @RequestBody ProductDto.Modify productDto);
}
