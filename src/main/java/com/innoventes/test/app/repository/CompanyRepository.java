package com.innoventes.test.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.innoventes.test.app.entity.Company;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface CompanyRepository extends JpaRepository<Company, Long> {

        //Task No 4
        @Query("SELECT c FROM Company c WHERE c.companyCode = :code")
        Company findCompanyByRecord(@Param("code") String code);

}