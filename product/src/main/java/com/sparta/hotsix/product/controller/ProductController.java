package com.sparta.hotsix.product.controller;

import com.sparta.hotsix.product.domain.dto.ProductDto;
import com.sparta.hotsix.product.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
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
    @Operation(summary = "상품 생성", description = "상품을 생성합니다.")
    public ProductDto.Response createProduct(@RequestHeader("User-Id") Long userId,
                                             @RequestHeader("User-Role") String userRole,
                                             @RequestBody ProductDto.Create productDto) {
        return productService.createProduct(userId, userRole, productDto);
    }

    @GetMapping("/{productId}")
    @Operation(summary = "상품 단건 조회", description = "상품을 단건 조회합니다.")
    public ProductDto.Response getProduct(@PathVariable(value = "productId") UUID productId) {
        return productService.getProduct(productId);
    }

    @GetMapping
    @Operation(summary = "상품 전체 조회", description = "상품을 페이징 조회합니다.")
    public List<ProductDto.GetAllProductsResponse> getAllProducts(
            @Schema(description = "페이지 번호(1부터 N까지)", defaultValue = "1")
            @RequestParam("page") int page,
            @Schema(description = "페이지에 출력할 개수를 입력합니다.", defaultValue = "10")
            @RequestParam("size") int size,
            @Schema(description = "정렬 기준을 입력합니다.")
            @RequestParam("sort") String sortBy
    ) {
        return productService.getAllProducts(page, size, sortBy);
    }

    @GetMapping("/search")
    @Operation(summary = "상품 검색", description = "상품을 검색합니다.")
    public List<ProductDto.GetAllProductsResponse> searchProduct(
            @RequestParam("name") String name,
            @Schema(description = "페이지 번호(1부터 N까지)", defaultValue = "1")
            @RequestParam("page") int page,
            @Schema(description = "페이지에 출력할 개수를 입력합니다.", defaultValue = "10")
            @RequestParam("size") int size,
            @Schema(description = "정렬 기준을 입력합니다.")
            @RequestParam("sort") String sortBy
    ) {
        return productService.searchProduct(name, page, size, sortBy);
    }

    @PatchMapping("/{productId}")
    @Operation(summary = "상품 수정", description = "상품을 수정합니다.")
    public ProductDto.Response modifyProduct(@RequestHeader("User-Id") Long userId,
                                             @RequestHeader("User-Role") String userRole,
                                             @PathVariable(value = "productId") UUID productId,
                                             @RequestBody ProductDto.Modify productDto) {
        return productService.modifyProduct(userId, userRole, productId, productDto);
    }

    @DeleteMapping("/{productId}")
    @Operation(summary = "상품 삭제", description = "상품을 삭제합니다.")
    public ProductDto.DeleteResponse deleteProduct(@RequestHeader("User-Id") Long userId,
                                                   @RequestHeader("User-Role") String userRole,
                                                   @PathVariable(value = "productId") UUID productId,
                                                   @RequestBody ProductDto.Delete productDto) {
        return productService.deleteProduct(userId, userRole, productId, productDto);
    }



}
