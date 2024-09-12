package com.sparta.hotsix.product.client;

import com.sparta.hotsix.product.domain.dto.CompanyDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "company-service")
public interface CompanyClient {
    @GetMapping("/api/v1/companys/{companyId}")
    CompanyDto.Response getCompanyById(@PathVariable("companyId") UUID companyId);
}
