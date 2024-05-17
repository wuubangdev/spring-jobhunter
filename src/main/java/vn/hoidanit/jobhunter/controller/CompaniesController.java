package vn.hoidanit.jobhunter.controller;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import vn.hoidanit.jobhunter.domain.Company;
import vn.hoidanit.jobhunter.domain.ResultPaginate;
import vn.hoidanit.jobhunter.service.CompaniesService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

@Controller
public class CompaniesController {
    private final CompaniesService companiesService;

    public CompaniesController(CompaniesService companiesService) {
        this.companiesService = companiesService;
    }

    @PostMapping("/companies")
    public ResponseEntity<Company> createCompany(@Valid @RequestBody Company company) {

        return ResponseEntity.status(HttpStatus.CREATED).body(this.companiesService.handleCreateCompany(company));
    }

    @GetMapping("/companies/{id}")
    public ResponseEntity<Company> getCompaniesById(@PathVariable("id") long id) {
        return ResponseEntity.ok(this.companiesService.fetchCompanyById(id));
    }

    @GetMapping("/companies")
    public ResponseEntity<ResultPaginate> getAllCompany(
            @RequestParam("current") String sCurrent,
            @RequestParam("pageSize") String sPageSize) {
        Integer current = Integer.parseInt(sCurrent);
        Integer pageSize = Integer.parseInt(sPageSize);
        Pageable pageable = PageRequest.of(current - 1, pageSize);
        return ResponseEntity.ok(this.companiesService.fetchAllCompanies(pageable));
    }

    @PutMapping("/companies")
    public ResponseEntity<Company> putMethodName(@RequestBody Company company) {
        return ResponseEntity.ok().body(this.companiesService.updateCompany(company));
    }

    @DeleteMapping("/companies/{id}")
    public ResponseEntity<String> deleteCompaniesById(@PathVariable("id") long id) {
        this.companiesService.deleteCompanyById(id);
        return ResponseEntity.status(HttpStatus.OK).body("Delete user " + id + " success");
    }
}
