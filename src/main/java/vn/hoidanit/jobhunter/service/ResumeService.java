package vn.hoidanit.jobhunter.service;

import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import vn.hoidanit.jobhunter.domain.response.ResultPaginateDTO;
import vn.hoidanit.jobhunter.domain.resume.ResFetchResumeDTO;
import vn.hoidanit.jobhunter.domain.resume.ResUpdateResumeDTO;
import vn.hoidanit.jobhunter.domain.resume.ResCreateResumeDTO;
import vn.hoidanit.jobhunter.domain.resume.Resume;
import vn.hoidanit.jobhunter.repository.ResumeRepository;
import vn.hoidanit.jobhunter.util.error.IdInvalidException;

@Service
public class ResumeService {
    private final ResumeRepository resumeRepository;
    private final JobService jobService;
    private final UserService userService;

    public ResumeService(ResumeRepository resumeRepository, JobService jobService, UserService userService) {
        this.resumeRepository = resumeRepository;
        this.jobService = jobService;
        this.userService = userService;
    }

    public ResFetchResumeDTO convertToGet(Resume r) {
        ResFetchResumeDTO res = new ResFetchResumeDTO();
        ResFetchResumeDTO.JobResume jobResume = new ResFetchResumeDTO.JobResume(r.getJob().getId(),
                r.getJob().getName());
        ResFetchResumeDTO.UserResume userResume = new ResFetchResumeDTO.UserResume(r.getUser().getId(),
                r.getUser().getName());
        res.setId(r.getId());
        res.setEmail(r.getEmail());
        res.setUrl(r.getUrl());
        res.setStatus(r.getStatus());
        res.setJobResume(jobResume);
        res.setUserResume(userResume);
        res.setCreatedAt(r.getCreatedAt());
        res.setCreatedBy(r.getCreatedBy());
        res.setUpdatedAt(r.getUpdatedAt());
        res.setUpdatedBy(r.getUpdatedBy());
        return res;
    }

    public boolean checkResumeExistByUserAndJob(Resume resume) {
        if (resume.getUser() == null || resume.getJob() == null)
            return false;
        if (this.userService.fetchUserById(resume.getUser().getId()) == null
                || this.jobService.fetchJobById(resume.getJob().getId()) == null)
            return false;
        return true;
    }

    public ResCreateResumeDTO create(Resume resume) {
        this.resumeRepository.save(resume);
        ResCreateResumeDTO res = new ResCreateResumeDTO();
        res.setId(resume.getId());
        res.setCreatedAt(resume.getCreatedAt());
        res.setCreatedBy(resume.getCreatedBy());
        return res;
    }

    public Resume fetchById(long id) {
        Optional<Resume> opResume = this.resumeRepository.findById(id);
        return opResume.orElse(null);
    }

    public ResUpdateResumeDTO update(Resume r)
            throws IdInvalidException {
        Resume resume = this.fetchById(r.getId());
        if (resume == null)
            throw new IdInvalidException("Resume khong ton tai!");
        resume.setStatus(r.getStatus());
        this.resumeRepository.save(resume);
        ResUpdateResumeDTO res = new ResUpdateResumeDTO();
        res.setUpdatedAt(resume.getUpdatedAt());
        res.setUpdatedBy(resume.getUpdatedBy());
        return res;
    }

    public void delete(long id)
            throws IdInvalidException {
        Resume resume = this.fetchById(id);
        if (resume == null)
            throw new IdInvalidException("Resume khong ton tai!");
        this.resumeRepository.deleteById(id);
    }

    public ResultPaginateDTO fetchAll(Specification<Resume> spec, Pageable page) {
        Page<Resume> rPage = this.resumeRepository.findAll(spec, page);
        ResultPaginateDTO rsp = new ResultPaginateDTO();
        ResultPaginateDTO.Meta meta = new ResultPaginateDTO.Meta();
        meta.setCurrent(rPage.getNumber() + 1);
        meta.setPageSize(rPage.getSize());
        meta.setTotalPages(rPage.getTotalPages());
        meta.setTotalIteams(rPage.getTotalElements());
        rsp.setMeta(meta);
        rsp.setResult(rPage.getContent()
                .stream().map(r -> this.convertToGet(r))
                .collect(Collectors.toList()));
        return rsp;
    }
}
