package vn.hoidanit.jobhunter.service;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import vn.hoidanit.jobhunter.domain.Company;
import vn.hoidanit.jobhunter.domain.MetaDTO;
import vn.hoidanit.jobhunter.domain.ResultPaginate;
import vn.hoidanit.jobhunter.repository.CompanyRepository;

@Service
public class CompanyService {

    private final CompanyRepository companyRepository;

    public CompanyService(CompanyRepository companyRepository) {
        this.companyRepository = companyRepository;
    }

    public Company handleCreateCompany(Company company) {
        return this.companyRepository.save(company);
    }

    public ResultPaginate fetchAllCompany(Specification<Company> spec, Pageable pageable) {
        Page<Company> pageCompany = this.companyRepository.findAll(spec, pageable);
        MetaDTO mt = new MetaDTO();
        ResultPaginate rsp = new ResultPaginate();
        mt.setCurrent(pageCompany.getNumber());
        mt.setPageSize(pageCompany.getSize());
        mt.setTotalPage(pageCompany.getTotalPages());
        mt.setTotalItem(pageCompany.getTotalElements());
        rsp.setMeta(mt);
        rsp.setData(pageCompany.getContent());
        return rsp;
    };

    public Company fetchCompanyById(long id) {
        Optional<Company> opCom = this.companyRepository.findById(id);
        if (opCom.isPresent()) {
            return opCom.get();
        }
        return null;
    }

    public Company handleUpdateCompany(Company c) {
        Company currentCom = this.fetchCompanyById(c.getId());
        if (currentCom != null) {
            currentCom.setName(c.getName());
            currentCom.setDescription(c.getDescription());
            currentCom.setAddress(c.getAddress());
            currentCom.setLogo(c.getLogo());
            return this.companyRepository.save(currentCom);
        }
        return null;
    }

    public void handleDeleteCompany(long id) {
        this.companyRepository.deleteById(id);
    }
}
