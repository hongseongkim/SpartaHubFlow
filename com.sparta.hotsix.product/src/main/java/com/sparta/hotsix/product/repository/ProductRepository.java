package com.sparta.hotsix.product.repository;

import com.sparta.hotsix.product.domain.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.UUID;

@Repository
public interface ProductRepository extends JpaRepository<Product, UUID> {


    @Query("SELECT p FROM Product p WHERE p.productId = :productId AND p.isDeleted = false")
    Product findByIdNotDeleted(@Param("productId") UUID productId);

    Page<Product> findAllByIsDeletedFalse(Pageable pageable);

    Page<Product> findAllByProductNameAndIsDeletedFalse(String productName, Pageable pageable);

    @Modifying
    @Query("UPDATE Product p SET p.deletedAt = :deletedAt, p.deletedBy = :deletedBy, p.isDeleted = true " +
            "WHERE p.productId = :productId")
    void deleteProductById(@Param("productId") UUID productId, @Param("deletedAt") LocalDateTime deletedAt,
                           @Param("deletedBy") Long deletedBy);
}
