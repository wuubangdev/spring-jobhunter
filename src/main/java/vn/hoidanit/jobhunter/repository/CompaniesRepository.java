package vn.hoidanit.jobhunter.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import vn.hoidanit.jobhunter.domain.Company;

public interface CompaniesRepository extends JpaRepository<Company, Long> {

}
