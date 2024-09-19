package com.sparta.company.controller;

import com.sparta.company.domain.dto.CompanyDto;
import com.sparta.company.service.CompanyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
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
    @Operation(summary = "업체 생성", description = "업체를 생성합니다.")
    public CompanyDto.Response createCompany(@RequestHeader("User-Id") Long userId,
                                             @RequestHeader("User-Role") String userRole,
                                             @RequestBody CompanyDto.Create companyDto
    ) {
        return companyService.createCompany(userId, userRole, companyDto);
    }

    @GetMapping("/{companyId}")
    @Operation(summary = "업체 단건 조회", description = "업체를 단건 조회합니다.")
    public CompanyDto.Response getCompany(@PathVariable(value = "companyId") UUID companyId) {
        return companyService.getCompany(companyId);
    }

    @GetMapping
    @Operation(summary = "업체 전체 조회", description = "업체를 페이징 조회합니다.")
    public List<CompanyDto.GetAllCompanysResponse> getAllCompanys(
            @Schema(description = "페이지 번호(1부터 N까지)", defaultValue = "1")
            @RequestParam("page") int page,
            @Schema(description = "페이지에 출력할 개수를 입력합니다.", defaultValue = "10")
            @RequestParam("size") int size,
            @Schema(description = "정렬 기준을 입력합니다.")
            @RequestParam("sort") String sortBy
    ) {
        return companyService.getAllCompanys(page, size, sortBy);
    }

    @GetMapping("/search/{companyName}")
    @Operation(summary = "업체 검색", description = "업체를 검색합니다.")
    public List<CompanyDto.GetAllCompanysResponse> searchCompany(
            @PathVariable(value = "companyName") String companyName,
            @Schema(description = "페이지 번호(1부터 N까지)", defaultValue = "1")
            @RequestParam("page") int page,
            @Schema(description = "페이지에 출력할 개수를 입력합니다.", defaultValue = "10")
            @RequestParam("size") int size,
            @Schema(description = "정렬 기준을 입력합니다.")
            @RequestParam("sort") String sortBy
    ) {
        return companyService.searchCompany(companyName, page, size, sortBy);
    }

    @PatchMapping("/companyId")
    @Operation(summary = "업체 수정", description = "업체를 수정합니다.")
    public CompanyDto.Response modifyCompany(@RequestHeader("User-Id") Long userId,
                                             @RequestHeader("User-Role") String userRole,
                                             @PathVariable(value = "companyId") UUID companyId,
                                             @RequestBody CompanyDto.Modify companyDto) {
        return companyService.modifyCompany(userId, userRole, companyId, companyDto);
    }

    @DeleteMapping("/companyId")
    @Operation(summary = "업체 삭제", description = "업체를 삭제합니다.")
    public CompanyDto.DeleteResponse deleteCompany(@RequestHeader("User-Id") Long userId,
                                                   @RequestHeader("User-Role") String userRole,
                                                   @PathVariable(value = "companyId") UUID companyId
    ) {
        return companyService.deleteCompany(userId, userRole, companyId);

    }

}
