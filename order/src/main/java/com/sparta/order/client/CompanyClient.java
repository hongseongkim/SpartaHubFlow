package com.sparta.order.client;

import com.sparta.order.domain.dto.CompanyDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "company-service")
public interface CompanyClient {
    @GetMapping("api/v1/companys/{companyId}")
    CompanyDto.Response getCompany(@PathVariable("companyId") UUID companyId);
}
