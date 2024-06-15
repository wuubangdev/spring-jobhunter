package vn.hoidanit.jobhunter.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import vn.hoidanit.jobhunter.domain.Company;
import vn.hoidanit.jobhunter.domain.Skills;
import vn.hoidanit.jobhunter.domain.job.Job;
import vn.hoidanit.jobhunter.domain.job.ResCreateJobDTO;
import vn.hoidanit.jobhunter.domain.response.ResultPaginateDTO;
import vn.hoidanit.jobhunter.repository.CompaniesRepository;
import vn.hoidanit.jobhunter.repository.JobRepository;
import vn.hoidanit.jobhunter.repository.SkillRepository;

@Service
public class JobService {
    private final JobRepository jobRepository;
    private final SkillRepository skillRepository;
    private final CompaniesRepository companiesRepository;

    public JobService(JobRepository jobRepository,
            SkillRepository skillRepository,
            CompaniesRepository companiesRepository) {
        this.jobRepository = jobRepository;
        this.skillRepository = skillRepository;
        this.companiesRepository = companiesRepository;
    }

    public Job fetchJobById(long id) {
        Optional<Job> opJob = this.jobRepository.findById(id);
        return opJob.orElse(null);
    }

    public Job fetchJobByName(String name) {
        return this.jobRepository.findByName(name);
    }

    public ResCreateJobDTO create(Job j) {
        // check skill
        if (j.getSkills() != null) {
            List<Long> reqSkills = j.getSkills()
                    .stream().map(x -> x.getId())
                    .collect(Collectors.toList());
            List<Skills> dbSkills = this.skillRepository.findByIdIn(reqSkills);
            j.setSkills(dbSkills);
        }
        if (j.getCompany() != null) {
            Optional<Company> opCompany = this.companiesRepository.findById(j.getCompany().getId());
            if (opCompany.isPresent()) {
                j.setCompany(opCompany.get());
            }
        }
        // Create Job
        Job currentJob = this.jobRepository.save(j);
        // Convert response
        ResCreateJobDTO res = new ResCreateJobDTO();
        res.setId(currentJob.getId());
        res.setName(currentJob.getName());
        res.setSalary(currentJob.getSalary());
        res.setQuantity(currentJob.getQuantity());
        res.setDescription(currentJob.getDescription());
        res.setLocation(currentJob.getLocation());
        res.setLevel(currentJob.getLevel());
        res.setStartDate(currentJob.getStartDate());
        res.setActive(currentJob.isActive());
        res.setCreatedAt(currentJob.getCreatedAt());
        res.setCreatedBy(currentJob.getCreatedBy());
        if (currentJob.getSkills() != null) {
            List<String> skills = currentJob.getSkills()
                    .stream().map(x -> x.getName())
                    .collect(Collectors.toList());
            res.setSkills(skills);
        }
        return res;
    }

    public ResCreateJobDTO update(Job j, Job jobInDB) {
        // check skill
        if (j.getSkills() != null) {
            List<Long> reqSkills = j.getSkills()
                    .stream().map(x -> x.getId())
                    .collect(Collectors.toList());
            List<Skills> dbSkills = this.skillRepository.findByIdIn(reqSkills);
            jobInDB.setSkills(dbSkills);
        }
        if (j.getCompany() != null) {
            Optional<Company> opCompany = this.companiesRepository.findById(j.getCompany().getId());
            if (opCompany.isPresent()) {
                jobInDB.setCompany(opCompany.get());
            }
        }
        // update Job
        jobInDB.setName(j.getName());
        jobInDB.setSalary(j.getSalary());
        jobInDB.setQuantity(j.getQuantity());
        jobInDB.setDescription(j.getDescription());
        jobInDB.setLocation(j.getLocation());
        jobInDB.setLevel(j.getLevel());
        jobInDB.setStartDate(j.getStartDate());
        jobInDB.setActive(j.isActive());
        // Create Job
        Job currentJob = this.jobRepository.save(jobInDB);
        // Convert response
        ResCreateJobDTO res = new ResCreateJobDTO();
        res.setId(currentJob.getId());
        res.setName(currentJob.getName());
        res.setSalary(currentJob.getSalary());
        res.setQuantity(currentJob.getQuantity());
        res.setDescription(currentJob.getDescription());
        res.setLocation(currentJob.getLocation());
        res.setLevel(currentJob.getLevel());
        res.setStartDate(currentJob.getStartDate());
        res.setActive(currentJob.isActive());
        res.setCreatedAt(currentJob.getCreatedAt());
        res.setCreatedBy(currentJob.getCreatedBy());
        res.setUpdatedAt(currentJob.getUpdatedAt());
        res.setUpdatedBy(currentJob.getUpdatedBy());
        if (currentJob.getSkills() != null) {
            List<String> skills = currentJob.getSkills()
                    .stream().map(x -> x.getName())
                    .collect(Collectors.toList());
            res.setSkills(skills);
        }
        return res;
    }

    public void delete(long id) {
        this.jobRepository.deleteById(id);
    }

    public ResultPaginateDTO fetchAll(Specification<Job> spec, Pageable pageable) {
        Page<Job> jobPage = this.jobRepository.findAll(spec, pageable);
        ResultPaginateDTO.Meta mt = new ResultPaginateDTO.Meta();
        ResultPaginateDTO rsp = new ResultPaginateDTO();
        mt.setPage(jobPage.getNumber() + 1);
        mt.setPageSize(jobPage.getSize());
        mt.setPages(jobPage.getTotalPages());
        mt.setTotal(jobPage.getTotalElements());
        rsp.setMeta(mt);
        rsp.setResult(jobPage.getContent());
        return rsp;
    }

    public ResCreateJobDTO getById(long id) {
        Job currentJob = this.fetchJobById(id);
        ResCreateJobDTO res = new ResCreateJobDTO();
        if (currentJob != null) {
            // Convert response
            res.setId(currentJob.getId());
            res.setName(currentJob.getName());
            res.setSalary(currentJob.getSalary());
            res.setCompany(currentJob.getCompany());
            res.setQuantity(currentJob.getQuantity());
            res.setDescription(currentJob.getDescription());
            res.setLocation(currentJob.getLocation());
            res.setLevel(currentJob.getLevel());
            res.setStartDate(currentJob.getStartDate());
            res.setActive(currentJob.isActive());
            res.setCreatedAt(currentJob.getCreatedAt());
            res.setCreatedBy(currentJob.getCreatedBy());
            res.setUpdatedAt(currentJob.getUpdatedAt());
            res.setUpdatedBy(currentJob.getUpdatedBy());
            if (currentJob.getSkills() != null) {
                List<String> skills = currentJob.getSkills()
                        .stream().map(x -> x.getName())
                        .collect(Collectors.toList());
                res.setSkills(skills);
            }
        }
        return res;
    }
}
