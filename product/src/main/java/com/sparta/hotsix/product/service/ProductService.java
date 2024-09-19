package com.sparta.hotsix.product.service;

import com.sparta.hotsix.product.client.CompanyClient;
import com.sparta.hotsix.product.client.HubClient;
import com.sparta.hotsix.product.domain.dto.CompanyDto;
import com.sparta.hotsix.product.domain.dto.HubDto;
import com.sparta.hotsix.product.domain.dto.ProductDto;
import com.sparta.hotsix.product.domain.entity.Product;
import com.sparta.hotsix.product.repository.ProductRepository;
import jakarta.ws.rs.ForbiddenException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final HubClient hubClient;
    private final CompanyClient companyClient;

    @Transactional
    public ProductDto.Response createProduct(Long userId, String userRole, ProductDto.Create productDto) {

        HubDto.Response hubDto = hubClient.getHubById(productDto.getHubId());

        if (hubDto == null) {
            throw new IllegalArgumentException("존재하지 않는 허브입니다.");
        }

        CompanyDto.Response companyDto = companyClient.getCompanyById(productDto.getCompanyId());

        if (companyDto == null) {
            throw new IllegalArgumentException("존재하지 않는 업체입니다.");
        }

        // 허브 관리자는 자신의 허브 상품만 생성 가능
        if (Objects.equals(userRole, "HUB_MANAGER") && !Objects.equals(userId, hubDto.getHubManagerId())) {
            throw new ForbiddenException("자신의 허브 상품만 생성할 수 있습니다.");
        }

        // 업체 관리자는 자신의 업체 상품만 생성 가능
        if (Objects.equals(userRole, "COMPANY_MANAGER") && !Objects.equals(userId, companyDto.getCompanyMangerId())) {
            throw new ForbiddenException("자신의 업체 상품만 생성할 수 있습니다.");
        }


        return ProductDto.Response.of(productRepository.save(new Product(
                productDto.getProductName(),
                productDto.getDescription(),
                productDto.getStock(),
                productDto.getHubId(),
                productDto.getCompanyId()
        )));
    }

    @Transactional(readOnly = true)
    public ProductDto.Response getProduct(UUID productId) {

        Product product = productRepository.findByIdNotDeleted(productId);

        if (product == null) {
            throw new IllegalArgumentException("존재하지 않는 상품입니다.");
        }

        return ProductDto.Response.of(product);
    }

    @Transactional(readOnly = true)
    public List<ProductDto.GetAllProductsResponse> getAllProducts(int page, int size, String sortBy) {

        Sort.Direction direction = Sort.Direction.ASC;
        Sort sort = Sort.by(direction, sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Product> productList = productRepository.findAllByIsDeletedFalse(pageable);

        return productList.map(ProductDto.GetAllProductsResponse::new).stream().toList();


    }

    @Transactional(readOnly = true)
    public List<ProductDto.GetAllProductsResponse> searchProduct(String name, int page, int size, String sortBy) {

        Sort.Direction direction = Sort.Direction.ASC;
        Sort sort = Sort.by(direction, sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Product> productList = productRepository.findAllByProductNameContainingAndIsDeletedFalse(name, pageable);

        return productList.map(ProductDto.GetAllProductsResponse::new).stream().toList();

    }

    @Transactional
    public ProductDto.Response modifyProduct(Long userId, String userRole, UUID productId, ProductDto.Modify productDto) {

        Product product = productRepository.findByIdNotDeleted(productId);

        if (product == null) {
            throw new IllegalArgumentException("존재하지 않는 상품입니다.");
        }

        HubDto.Response hubDto = hubClient.getHubById(productDto.getHubId());

        if (hubDto == null) {
            throw new IllegalArgumentException("존재하지 않는 허브입니다.");
        }

        CompanyDto.Response companyDto = companyClient.getCompanyById(productDto.getCompanyId());

        if (companyDto == null) {
            throw new IllegalArgumentException("존재하지 않는 업체입니다.");
        }

        // 허브 관리자는 자신의 허브 상품만 수정 가능
        if (Objects.equals(userRole, "HUB_MANAGER") && !Objects.equals(userId, hubDto.getHubManagerId())) {
            throw new ForbiddenException("자신의 허브 상품만 수정할 수 있습니다.");
        }

        // 업체 관리자는 자신의 업체 상품만 수정 가능
        if (Objects.equals(userRole, "COMPANY_MANAGER") && !Objects.equals(userId, companyDto.getCompanyMangerId())) {
            throw new ForbiddenException("자신의 업체 상품만 수정할 수 있습니다.");
        }

        product.modify(
                productDto.getProductName(),
                productDto.getDescription(),
                productDto.getStock(),
                productDto.getHubId(),
                productDto.getCompanyId());

        return ProductDto.Response.of(productRepository.save(product));

    }

    @Transactional
    public ProductDto.DeleteResponse deleteProduct(Long userId, String userRole, UUID productId, ProductDto.Delete productDto) {

        Product product = productRepository.findByIdNotDeleted(productId);

        if (product == null) {
            throw new IllegalArgumentException("존재하지 않는 상품입니다.");
        }

        HubDto.Response hubDto = hubClient.getHubById(product.getHubId());

        if (hubDto == null) {
            throw new IllegalArgumentException("존재하지 않는 허브입니다.");
        }

        CompanyDto.Response companyDto = companyClient.getCompanyById(product.getCompanyId());

        if (companyDto == null) {
            throw new IllegalArgumentException("존재하지 않는 업체입니다.");
        }

        // 허브 관리자는 자신의 허브 상품만 삭제 가능
        if (Objects.equals(userRole, "HUB_MANAGER") && !Objects.equals(userId, hubDto.getHubManagerId())) {
            throw new ForbiddenException("자신의 허브 상품만 삭제할 수 있습니다.");
        }

        // 업체 관리자는 자신의 업체 상품만 삭제 가능
        if (Objects.equals(userRole, "COMPANY_MANAGER") && !Objects.equals(userId, companyDto.getCompanyMangerId())) {
            throw new ForbiddenException("자신의 업체 상품만 삭제할 수 있습니다.");
        }

        productRepository.deleteProductById(productId, LocalDateTime.now(), productDto.getUserIdToDelete());

        return ProductDto.DeleteResponse.builder()
                .productId(productId)
                .build();
    }
}
