package com.sparta.company.service;

import com.sparta.company.client.HubClient;
import com.sparta.company.domain.dto.CompanyDto;
import com.sparta.company.domain.dto.HubDto;
import com.sparta.company.domain.entity.Company;
import com.sparta.company.repository.CompanyRepository;
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
public class CompanyService {

    private final CompanyRepository companyRepository;
    private final HubClient hubClient;

    @Transactional
    public CompanyDto.Response createCompany(Long userId, String userRole, CompanyDto.Create companyDto) {

        HubDto.Response hubDto = hubClient.getHubById(companyDto.getHubId());

        if (hubDto == null) {
            throw new IllegalArgumentException("존재하지 않는 허브입니다.");
        }

        // 허브 관리자는 자신의 허브에 소속된 업체만 생성 가능
        if (Objects.equals(userRole, "HUB_MANAGER") && !Objects.equals(hubDto.getHubManagerId(), userId)) {
            throw new ForbiddenException("자신의 허브에 소속된 업체만 생성할 수 있습니다.");
        }

        return CompanyDto.Response.of(companyRepository.save(new Company(
                companyDto.getCompanyName(),
                companyDto.getHubId(),
                companyDto.getCompanyType(),
                companyDto.getCompanyAddress()
        )));

    }

    @Transactional(readOnly = true)
    public CompanyDto.Response getCompany(UUID companyId) {

        Company company = companyRepository.findByIdNotDeleted(companyId);

        if (company == null) {
            throw new IllegalArgumentException("존재하지 않는 업체입니다.");
        }

        return CompanyDto.Response.of(company);

    }

    @Transactional(readOnly = true)
    public List<CompanyDto.GetAllCompanysResponse> getAllCompanys(int page, int size, String sortBy) {

        Sort.Direction direction = Sort.Direction.ASC;
        Sort sort = Sort.by(direction, sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Company> companyList = companyRepository.findAllByIsDeletedFalse(pageable);

        return companyList.map(CompanyDto.GetAllCompanysResponse::new).stream().toList();

    }

    @Transactional(readOnly = true)
    public List<CompanyDto.GetAllCompanysResponse> searchCompany(String companyName, int page, int size, String sortBy) {

        Sort.Direction direction = Sort.Direction.ASC;
        Sort sort = Sort.by(direction, sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Company> companyList = companyRepository.findAllByCompanyNameContainingAndIsDeletedFalse(companyName, pageable);

        return companyList.map(CompanyDto.GetAllCompanysResponse::new).stream().toList();

    }

    @Transactional
    public CompanyDto.Response modifyCompany(Long userId, String userRole, UUID companyId, CompanyDto.Modify companyDto) {

        Company company = companyRepository.findByIdNotDeleted(companyId);

        if (company == null) {
            throw new IllegalArgumentException("존재하지 않는 업체입니다.");
        }

        HubDto.Response hubDto = hubClient.getHubById(companyDto.getHubId());

        // 허브 관리자는 자신의 허브에 소속된 업체만 수정 가능
        if (Objects.equals(userRole, "HUB_MANAGER") && !Objects.equals(hubDto.getHubManagerId(), userId)) {
            throw new ForbiddenException("자신의 허브에 소속된 업체만 수정할 수 있습니다.");
        }


        company.modify(
                companyDto.getCompanyName(),
                companyDto.getHubId(),
                companyDto.getCompanyType(),
                companyDto.getCompanyAddress());

        return CompanyDto.Response.of(companyRepository.save(company));

    }

    @Transactional
    public CompanyDto.DeleteResponse deleteCompany(Long userId, String userRole, UUID companyId) {

        Company company = companyRepository.findByIdNotDeleted(companyId);

        if (company == null) {
            throw new IllegalArgumentException("존재하지 않는 업체입니다.");
        }

        // 업체 관리자는 자신의 업체만 삭제 가능
        if (Objects.equals(userRole, "COMPANY_MANAGER") && !Objects.equals(company.getCompanyMangerId(), userId)) {
            throw new ForbiddenException("자신의 허브에 소속된 업체만 삭제할 수 있습니다.");
        }

        HubDto.Response hubDto = hubClient.getHubById(company.getHubId());

        // 허브 관리자는 자신의 허브에 소속된 업체만 삭제 가능
        if (Objects.equals(userRole, "HUB_MANAGER") && !Objects.equals(hubDto.getHubManagerId(), userId)) {
            throw new ForbiddenException("자신의 허브에 소속된 업체만 삭제할 수 있습니다.");
        }

        companyRepository.deleteCompanyById(companyId, LocalDateTime.now(), userId);

        return CompanyDto.DeleteResponse.builder()
                .companyId(companyId)
                .build();

    }
}
