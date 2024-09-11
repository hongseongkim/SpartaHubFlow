package com.sparta.company.service;

import com.sparta.company.client.HubClient;
import com.sparta.company.domain.dto.CompanyDto;
import com.sparta.company.domain.entity.Company;
import com.sparta.company.repository.CompanyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CompanyService {

    private final CompanyRepository companyRepository;
    private final HubClient hubClient;

    @Transactional
    public CompanyDto.Response createCompany(CompanyDto.Create companyDto) {

        if(hubClient.getHubById(companyDto.getHubId()) == null){
            throw new IllegalArgumentException("존재하지 않는 허브입니다.");
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

        if(company == null){
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
    public CompanyDto.Response modifyCompany(UUID companyId, CompanyDto.Modify companyDto) {

        Company company = companyRepository.findByIdNotDeleted(companyId);

        if(company == null) {
            throw new IllegalArgumentException("존재하지 않는 업체입니다.");
        }

        company.modify(
                companyDto.getCompanyName(),
                companyDto.getHubId(),
                companyDto.getCompanyType(),
                companyDto.getCompanyAddress());

        return CompanyDto.Response.of(companyRepository.save(company));
        
    }

    @Transactional
    public CompanyDto.DeleteResponse deleteCompany(UUID companyId, CompanyDto.Delete companyDto) {

        if(companyRepository.findByIdNotDeleted(companyId) == null){
            throw new IllegalArgumentException("존재하지 않는 업체입니다.");
        }

        companyRepository.deleteCompanyById(companyId, LocalDateTime.now(), companyDto.getUserIdToDelete());

        return CompanyDto.DeleteResponse.builder()
                .companyId(companyId)
                .build();
        
    }
}
