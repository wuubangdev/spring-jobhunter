package vn.hoidanit.jobhunter.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import vn.hoidanit.jobhunter.domain.Company;
import vn.hoidanit.jobhunter.service.CompaniesService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import jakarta.validation.Valid;

@Controller
public class CompaniesController {
    private final CompaniesService companiesService;

    public CompaniesController(CompaniesService companiesService) {
        this.companiesService = companiesService;
    }

    @PostMapping("/companies")
    public ResponseEntity<Company> postMethodName(@Valid @RequestBody Company company) {

        return ResponseEntity.status(HttpStatus.CREATED).body(this.companiesService.handleCreateCompany(company));
    }

}
