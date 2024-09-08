package com.sparta.hotsix.product.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "p_product")
public class Product extends Timestamped{

    @Id
    @UuidGenerator
    @Column(name = "product_id", updatable = false, nullable = false)
    private UUID productId;

    @Column(name = "name", nullable = false)
    private String productName;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "hub_id")
    private UUID hubId;

    @Column(name = "company_id")
    private UUID companyId;

    public Product(String ProductName, String description, UUID hubId, UUID companyId) {
        this.productName = ProductName;
        this.description = description;
        this.hubId = hubId;
        this.companyId = companyId;
    }

    public void modify(String ProductName, String description, UUID hubId, UUID companyId) {
        this.productName = ProductName;
        this.description = description;
        this.hubId = hubId;
        this.companyId = companyId;
    }

}
