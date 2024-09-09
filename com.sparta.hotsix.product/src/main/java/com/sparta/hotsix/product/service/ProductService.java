package com.sparta.hotsix.product.service;

import com.sparta.hotsix.product.client.CompanyClient;
import com.sparta.hotsix.product.client.HubClient;
import com.sparta.hotsix.product.domain.dto.ProductDto;
import com.sparta.hotsix.product.domain.entity.Product;
import com.sparta.hotsix.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final HubClient hubClient;
    private final CompanyClient companyClient;


    public ProductDto.Response createProduct(ProductDto.Create productDto) {

        if (hubClient.getHubById(productDto.getHubId()) == null) {
            throw new IllegalArgumentException("존재하지 않는 허브입니다.");
        }

        if (companyClient.getCompanyById(productDto.getCompanyId()) == null) {
            throw new IllegalArgumentException("존재하지 않는 업체입니다.");
        }

        return ProductDto.Response.of(productRepository.save(new Product(
                productDto.getProductName(),
                productDto.getDescription(),
                productDto.getHubId(),
                productDto.getCompanyId()
        )));
    }

    public ProductDto.Response getProduct(UUID productId) {

        Product product = productRepository.findByIdNotDeleted(productId);

        if(product == null) {
            throw new IllegalArgumentException("존재하지 않는 상품입니다.");
        }

        return ProductDto.Response.of(product);
    }

    public List<ProductDto.GetAllProductsResponse> getAllProducts(int page, int size, String sortBy) {

        Sort.Direction direction = Sort.Direction.ASC;
        Sort sort = Sort.by(direction, sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Product> productList = productRepository.findAllByIsDeletedFalse(pageable);

        return productList.map(ProductDto.GetAllProductsResponse::new).stream().toList();


    }

    public List<ProductDto.GetAllProductsResponse> searchProduct(String name, int page, int size, String sortBy) {

        Sort.Direction direction = Sort.Direction.ASC;
        Sort sort = Sort.by(direction, sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Product> productList = productRepository.findAllByProductNameAndIsDeletedFalse(name, pageable);

        return productList.map(ProductDto.GetAllProductsResponse::new).stream().toList();

    }

    public ProductDto.Response modifyProduct(UUID productId, ProductDto.Modify productDto) {

        Product product = productRepository.findByIdNotDeleted(productId);

        if(product == null) {
            throw new IllegalArgumentException("존재하지 않는 상품입니다.");
        }

        product.modify(
                productDto.getProductName(),
                productDto.getDescription(),
                productDto.getHubId(),
                productDto.getCompanyId());

        return ProductDto.Response.of(productRepository.save(product));

    }

    public ProductDto.DeleteResponse deleteProduct(UUID productId, ProductDto.Delete productDto) {

        if(productRepository.findByIdNotDeleted(productId) == null){
            throw new IllegalArgumentException("존재하지 않는 상품입니다.");
        }

        productRepository.deleteProductById(productId, LocalDateTime.now(), productDto.getUserIdToDelete());

        return ProductDto.DeleteResponse.builder()
                .productId(productId)
                .build();
    }
}
