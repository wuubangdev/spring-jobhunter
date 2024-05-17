package vn.hoidanit.jobhunter.controller;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import vn.hoidanit.jobhunter.domain.Company;
import vn.hoidanit.jobhunter.domain.ResultPaginate;
import vn.hoidanit.jobhunter.service.CompanyService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.turkraft.springfilter.boot.Filter;

import org.springframework.web.bind.annotation.PutMapping;

@Controller
public class CompanyController {
    private final CompanyService companyService;

    public CompanyController(CompanyService companyService) {
        this.companyService = companyService;
    }

    @PostMapping("/companies")
    public ResponseEntity<Company> postCreateCompany(@Valid @RequestBody Company company) {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.companyService.handleCreateCompany(company));
    }

    @GetMapping("/companies")
    public ResponseEntity<ResultPaginate> getAllCompany(
            @Filter Specification<Company> spec,
            Pageable pageable) {
        return ResponseEntity.ok(this.companyService.fetchAllCompany(spec, pageable));
    }

    @GetMapping("/companies/{id}")
    public ResponseEntity<Company> getCompanyById(@PathVariable("id") long id) {
        return ResponseEntity.ok(this.companyService.fetchCompanyById(id));
    }

    @PutMapping("companies")
    public ResponseEntity<Company> updateCompany(@RequestBody Company c) {
        return ResponseEntity.ok(this.companyService.handleUpdateCompany(c));
    }

    @DeleteMapping("/companies/{id}")
    public ResponseEntity<Void> deleteCompany(@PathVariable("id") long id) {
        this.companyService.handleDeleteCompany(id);
        return ResponseEntity.ok(null);
    }
}
