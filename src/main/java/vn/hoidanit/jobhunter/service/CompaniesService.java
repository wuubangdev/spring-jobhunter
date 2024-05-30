package vn.hoidanit.jobhunter.service;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import vn.hoidanit.jobhunter.domain.Company;
import vn.hoidanit.jobhunter.domain.MetaDTO;
import vn.hoidanit.jobhunter.domain.ResultPaginate;
import vn.hoidanit.jobhunter.repository.CompaniesRepository;

@Service
public class CompaniesService {
    private final CompaniesRepository companiesRepository;

    public CompaniesService(CompaniesRepository companiesRepository) {
        this.companiesRepository = companiesRepository;
    }

    public Company handleCreateCompany(Company c) {
        return this.companiesRepository.save(c);
    }

    public Company fetchCompanyById(long id) {
        Optional<Company> comOptional = this.companiesRepository.findById(id);
        if (comOptional.isPresent()) {
            return this.companiesRepository.findById(id).get();
        }
        return null;
    }

    public ResultPaginate fetchAllCompanies(Specification<Company> spec, Pageable pageable) {
        Page<Company> pageCompanies = this.companiesRepository.findAll(spec, pageable);
        MetaDTO mt = new MetaDTO();
        ResultPaginate rsp = new ResultPaginate();
        mt.setCurrent(pageCompanies.getNumber() + 1);
        mt.setPageSize(pageCompanies.getSize());
        mt.setTotalPages(pageCompanies.getTotalPages());
        mt.setTotalIteams(pageCompanies.getTotalElements());
        rsp.setMeta(mt);
        rsp.setResult(pageCompanies.getContent());
        return rsp;
    }

    public Company updateCompany(Company c) {
        Company currentCompany = this.fetchCompanyById(c.getId());
        if (currentCompany != null) {
            currentCompany.setName(c.getName());
            currentCompany.setDescription(c.getDescription());
            currentCompany.setAddress(c.getAddress());
            currentCompany.setLogo(c.getLogo());
            return this.companiesRepository.save(currentCompany);
        }
        return null;
    }

    public void deleteCompanyById(long id) {
        this.companiesRepository.deleteById(id);
    }

}
