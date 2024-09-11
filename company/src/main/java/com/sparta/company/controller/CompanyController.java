package com.sparta.company.controller;

import com.sparta.company.domain.dto.CompanyDto;
import com.sparta.company.service.CompanyService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/companys")
public class CompanyController {

    private final CompanyService companyService;

    @PostMapping
    public CompanyDto.Response createCompany(@RequestBody CompanyDto.Create companyDto) {
        return companyService.createCompany(companyDto);
    }

    @GetMapping("/{companyId}")
    public CompanyDto.Response getCompany(@PathVariable(value = "companyId") UUID companyId) {
        return companyService.getCompany(companyId);
    }

    @GetMapping
    public List<CompanyDto.GetAllCompanysResponse> getAllCompanys(
            @RequestParam("page") int page,
            @RequestParam("size") int size,
            @RequestParam("sort") String sortBy
    ) {
        return companyService.getAllCompanys(page, size, sortBy);
    }

    @GetMapping("/search/{companyName}")
    public List<CompanyDto.GetAllCompanysResponse> searchCompany(
            @PathVariable(value = "companyName") String companyName,
            @RequestParam("page") int page,
            @RequestParam("size") int size,
            @RequestParam("sort") String sortBy
    ) {
        return companyService.searchCompany(companyName, page, size, sortBy);
    }

    @PatchMapping("/companyId")
    public CompanyDto.Response modifyCompany(@PathVariable(value = "companyId") UUID companyId,
                                             @RequestBody CompanyDto.Modify companyDto){
        return companyService.modifyCompany(companyId, companyDto);
    }

    @DeleteMapping("/companyId")
    public CompanyDto.DeleteResponse deleteCompany(@PathVariable(value = "companyId") UUID companyId,
                                                   @RequestBody CompanyDto.Delete companyDto) {
        return companyService.deleteCompany(companyId, companyDto);

    }

}
