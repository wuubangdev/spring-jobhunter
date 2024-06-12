package vn.hoidanit.jobhunter.service;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import vn.hoidanit.jobhunter.domain.Company;
import vn.hoidanit.jobhunter.domain.response.ResultPaginateDTO;
import vn.hoidanit.jobhunter.repository.CompaniesRepository;
import vn.hoidanit.jobhunter.repository.UserRepository;

@Service
public class CompaniesService {
    private final CompaniesRepository companiesRepository;
    private final UserRepository userRepository;

    public CompaniesService(
            CompaniesRepository companiesRepository,
            UserRepository userRepository) {
        this.companiesRepository = companiesRepository;
        this.userRepository = userRepository;
    }

    public Company handleCreateCompany(Company c) {
        return this.companiesRepository.save(c);
    }

    public Company fetchCompanyByName(String name) {
        return this.companiesRepository.findByName(name);
    }

    public Company fetchCompanyById(long id) {
        Optional<Company> comOptional = this.companiesRepository.findById(id);
        if (comOptional.isPresent()) {
            return this.companiesRepository.findById(id).get();
        }
        return null;
    }

    public ResultPaginateDTO fetchAllCompanies(Specification<Company> spec, Pageable pageable) {
        Page<Company> pageCompanies = this.companiesRepository.findAll(spec, pageable);
        ResultPaginateDTO.Meta mt = new ResultPaginateDTO.Meta();
        ResultPaginateDTO rsp = new ResultPaginateDTO();
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
        Company company = this.fetchCompanyById(id);
        userRepository.deleteAll(this.userRepository.findByCompany(company));
        this.companiesRepository.deleteById(id);
    }

}