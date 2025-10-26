package com.innoventes.test.app.controller;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.innoventes.test.app.dto.CompanyDTO;
import com.innoventes.test.app.entity.Company;
import com.innoventes.test.app.exception.ValidationException;
import com.innoventes.test.app.mapper.CompanyMapper;
import com.innoventes.test.app.service.CompanyService;

@RestController
@RequestMapping("/api/v1")
public class CompanyController {

	@Autowired
	private CompanyMapper companyMapper;

	@Autowired
	private CompanyService companyService;

	@Autowired
	private MessageSource messageSource;

	@GetMapping("/companies")
	public ResponseEntity<List<CompanyDTO>> getAllCompanies() {
		List<Company> companyList = companyService.getAllCompanies();
		
		List<CompanyDTO> companyDTOList = new ArrayList<CompanyDTO>();
		
		for (Company entity : companyList) {
			companyDTOList.add(companyMapper.getCompanyDTO(entity));
		}

		URI location = ServletUriComponentsBuilder.fromCurrentRequest().build().toUri();
		return ResponseEntity.status(HttpStatus.OK).location(location).body(companyDTOList);
	}
	//Task No 3
	@GetMapping("/companies/{id}")
	public ResponseEntity<CompanyDTO> getCompanyById(@PathVariable (value = "id")Long id){
		Company companyDetails=companyService.getCompanyById(id);

		CompanyDTO dtoDetails=companyMapper.getCompanyDTO(companyDetails);

		return ResponseEntity.status(HttpStatus.OK).body(dtoDetails);
	}

	//Task No 4
	@GetMapping("/company/{code}")
	public ResponseEntity<CompanyDTO> getCompanyByRecord(@PathVariable(value = "code") String code){
		Company cmpny=companyService.getCompanyByRecord(code);
		CompanyDTO cdto=companyMapper.getCompanyDTO(cmpny);

		return ResponseEntity.status(HttpStatus.OK).body(cdto);
	}
/*
 Notes: - Due to both getCompanyById and getCompanyByRecord  mappings use the same URL pattern (/api/v1/companies/{something})
so when you hit /api/v1/companies/IJ90E, Spring can’t tell which one to use:
Should it try to parse IJ90E as a Long?
Or should it treat it as a String?
Hence, the IllegalStateException: Ambiguous handler methods mapped.

so solution 1- change the url prefix
Solution 2-
Use Query Parameters (Cleaner URLs)
If you prefer to keep /api/v1/companies as the base path:

@GetMapping("/api/v1/companies")
public ResponseEntity<Company> getCompany(
        @RequestParam(required = false) Long id,
        @RequestParam(required = false) String code) {

    Company company = null;

    if (id != null) {
        company = companyService.findById(id);
    } else if (code != null) {
        company = companyService.findCompanyByRecord(code);
    }
    return company != null ? ResponseEntity.ok(company) : ResponseEntity.notFound().build();
}

Now you can call:
/api/v1/companies?id=1
/api/v1/companies?code=IJ90E

*/

	//adding new Data
	@PostMapping("/companies")
	public ResponseEntity<CompanyDTO> addCompany(@Valid @RequestBody CompanyDTO companyDTO)
			throws ValidationException {
		Company company = companyMapper.getCompany(companyDTO);
		Company newCompany = companyService.addCompany(company);
		CompanyDTO newCompanyDTO = companyMapper.getCompanyDTO(newCompany);

		URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(newCompany.getId())
				.toUri();
		return ResponseEntity.created(location).body(newCompanyDTO);
	}

	@PutMapping(value = "/companies/{id}")
	public ResponseEntity<CompanyDTO> updateCompany(@PathVariable(value = "id") Long id,
			@Valid @RequestBody CompanyDTO companyDTO) throws ValidationException {
		Company company = companyMapper.getCompany(companyDTO);
		Company updatedCompany = companyService.updateCompany(id, company);
		CompanyDTO updatedCompanyDTO = companyMapper.getCompanyDTO(updatedCompany);

		URI location = ServletUriComponentsBuilder.fromCurrentRequest().build().toUri();
		return ResponseEntity.status(HttpStatus.OK).location(location).body(updatedCompanyDTO);
	}

	@DeleteMapping(value = "/companies/{id}")
	public ResponseEntity<CompanyDTO> deleteCompany(@PathVariable(value = "id") Long id) {
		companyService.deleteCompany(id);
		return ResponseEntity.noContent().build();
	}

	public String getMessage(String exceptionCode) {
		return messageSource.getMessage(exceptionCode, null, LocaleContextHolder.getLocale());
	}

}
