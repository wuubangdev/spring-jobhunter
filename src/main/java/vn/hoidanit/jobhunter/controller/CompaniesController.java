package vn.hoidanit.jobhunter.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import vn.hoidanit.jobhunter.domain.Company;
import vn.hoidanit.jobhunter.domain.ResultPaginate;
import vn.hoidanit.jobhunter.service.CompaniesService;
import vn.hoidanit.jobhunter.util.anotation.ApiMessage;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import com.turkraft.springfilter.boot.Filter;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api/v1")
public class CompaniesController {
    private final CompaniesService companiesService;

    public CompaniesController(CompaniesService companiesService) {
        this.companiesService = companiesService;
    }

    @PostMapping("/companies")
    @ApiMessage("Create company success")
    public ResponseEntity<Company> createCompany(@Valid @RequestBody Company company) {

        return ResponseEntity.status(HttpStatus.CREATED).body(this.companiesService.handleCreateCompany(company));
    }

    @GetMapping("/companies/{id}")
    @ApiMessage("Fetch company success")
    public ResponseEntity<Company> getCompaniesById(@PathVariable("id") long id) {
        return ResponseEntity.ok(this.companiesService.fetchCompanyById(id));
    }

    @GetMapping("/companies")
    @ApiMessage("Fetch list company success")
    public ResponseEntity<ResultPaginate> getAllCompany(
            @Filter Specification<Company> spec,
            Pageable pageable) {
        return ResponseEntity.ok(this.companiesService.fetchAllCompanies(spec,
                pageable));
    }

    @PutMapping("/companies")
    @ApiMessage("Update company success")
    public ResponseEntity<Company> putMethodName(@RequestBody Company company) {
        return ResponseEntity.ok().body(this.companiesService.updateCompany(company));
    }

    @DeleteMapping("/companies/{id}")
    @ApiMessage("Delete company success")
    public ResponseEntity<String> deleteCompaniesById(@PathVariable("id") long id) {
        this.companiesService.deleteCompanyById(id);
        return ResponseEntity.status(HttpStatus.OK).body("Delete user " + id + " success");
    }
}
