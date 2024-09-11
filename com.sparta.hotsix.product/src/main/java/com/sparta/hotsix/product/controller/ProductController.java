package com.sparta.hotsix.product.controller;

import com.sparta.hotsix.product.domain.dto.ProductDto;
import com.sparta.hotsix.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/products")
public class ProductController {

    private final ProductService productService;

    @PostMapping
    public ProductDto.Response createProduct(@RequestBody ProductDto.Create productDto) {
        return productService.createProduct(productDto);
    }

    @GetMapping("/{productId}")
    public ProductDto.Response getProduct(@PathVariable(value = "productId") UUID productId) {
        return productService.getProduct(productId);
    }

    @GetMapping
    public List<ProductDto.GetAllProductsResponse> getAllProducts(
            @RequestParam("page") int page,
            @RequestParam("size") int size,
            @RequestParam("sort") String sortBy
    ) {
        return productService.getAllProducts(page, size, sortBy);
    }

    @GetMapping("/search")
    public List<ProductDto.GetAllProductsResponse> searchProduct(
            @RequestParam("name") String name,
            @RequestParam("page") int page,
            @RequestParam("size") int size,
            @RequestParam("sort") String sortBy
    ) {
        return productService.searchProduct(name, page, size, sortBy);
    }

    @PatchMapping("/{productId}")
    public ProductDto.Response modifyProduct(@PathVariable(value = "productId") UUID productId,
                                             @RequestBody ProductDto.Modify productDto) {
        return productService.modifyProduct(productId, productDto);
    }

    @DeleteMapping("/{productId}")
    public ProductDto.DeleteResponse deleteProduct(@PathVariable(value = "productId") UUID productId,
                                                   @RequestBody ProductDto.Delete productDto) {
        return productService.deleteProduct(productId, productDto);
    }



}
