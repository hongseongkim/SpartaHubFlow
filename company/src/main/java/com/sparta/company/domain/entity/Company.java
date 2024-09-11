package com.sparta.company.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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
@Table(name = "p_company")
public class Company extends Timestamped {

    @Id
    @UuidGenerator
    @Column(name = "company_id", updatable = false, nullable = false)
    private UUID companyId;

    @Column(name = "company_name", nullable = false)
    private String companyName;

    @Column(name = "hub_id", nullable = false)
    private UUID hubId;

    @Column(name = "company_type")
    private CompanyTypeEnum companyType;

    @Column(name = "company_address")
    private String companyAddress;

    public Company(String companyName, UUID hubId, CompanyTypeEnum companyType, String companyAddress) {
        this.companyName = companyName;
        this.hubId = hubId;
        this.companyType = companyType;
        this.companyAddress = companyAddress;
    }

    public void modify(String companyName, UUID hubId, CompanyTypeEnum companyType, String companyAddress) {
        this.companyName = companyName;
        this.hubId = hubId;
        this.companyType = companyType;
        this.companyAddress = companyAddress;
    }

}
