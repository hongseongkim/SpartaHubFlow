package com.sparta.company.repository;

import com.sparta.company.domain.entity.Company;
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
public interface CompanyRepository extends JpaRepository<Company, UUID> {

    @Query("SELECT c FROM Company c WHERE c.companyId = :companyId AND c.isDeleted = false")
    Company findByIdNotDeleted(@Param("companyId") UUID companyId);

    Page<Company> findAllByIsDeletedFalse(Pageable pageable);

    @Query("SELECT c FROM Company c WHERE c.companyName LIKE %:companyName% AND c.isDeleted = false")
    Page<Company> findAllByCompanyNameContainingAndIsDeletedFalse(@Param("companyName") String companyName,
                                                                  Pageable pageable);

    @Modifying
    @Query("UPDATE Company c SET c.deletedAt = :deletedAt, c.deletedBy = :deletedBy, c.isDeleted = true " +
            "WHERE c.companyId = :companyId")
    void deleteCompanyById(@Param("companyId") UUID companyId, @Param("deletedAt") LocalDateTime deletedAt,
                           @Param("deletedBy") Long deletedBy);
}
