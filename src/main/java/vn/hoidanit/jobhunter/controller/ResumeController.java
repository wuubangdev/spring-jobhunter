package vn.hoidanit.jobhunter.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.turkraft.springfilter.boot.Filter;
import com.turkraft.springfilter.builder.FilterBuilder;
import com.turkraft.springfilter.converter.FilterSpecificationConverter;

import jakarta.validation.Valid;
import vn.hoidanit.jobhunter.domain.Company;
import vn.hoidanit.jobhunter.domain.job.Job;
import vn.hoidanit.jobhunter.domain.response.ResultPaginateDTO;
import vn.hoidanit.jobhunter.domain.resume.ResFetchResumeDTO;
import vn.hoidanit.jobhunter.domain.resume.ResUpdateResumeDTO;
import vn.hoidanit.jobhunter.domain.resume.ResCreateResumeDTO;
import vn.hoidanit.jobhunter.domain.resume.Resume;
import vn.hoidanit.jobhunter.domain.user.User;
import vn.hoidanit.jobhunter.service.ResumeService;
import vn.hoidanit.jobhunter.service.UserService;
import vn.hoidanit.jobhunter.util.SecurityUtil;
import vn.hoidanit.jobhunter.util.anotation.ApiMessage;
import vn.hoidanit.jobhunter.util.error.IdInvalidException;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/api/v1")
public class ResumeController {
    private final ResumeService resumeService;
    private final UserService userService;

    private final FilterBuilder filterBuilder;
    private final FilterSpecificationConverter filterSpecificationConverter;

    public ResumeController(ResumeService resumeService, UserService userService, FilterBuilder filterBuilder,
            FilterSpecificationConverter filterSpecificationConverter) {
        this.resumeService = resumeService;
        this.userService = userService;
        this.filterBuilder = filterBuilder;
        this.filterSpecificationConverter = filterSpecificationConverter;
    }

    @PostMapping("/resumes")
    @ApiMessage("Create resume")
    public ResponseEntity<ResCreateResumeDTO> create(@Valid @RequestBody Resume resume)
            throws IdInvalidException {
        boolean checkExist = this.resumeService.checkResumeExistByUserAndJob(resume);
        if (!checkExist)
            throw new IdInvalidException("Job id/User id truyen len khong ton tai!");
        return ResponseEntity.status(HttpStatus.CREATED).body(this.resumeService.create(resume));
    }

    @GetMapping("/resumes/{id}")
    @ApiMessage("Get resume by id")
    public ResponseEntity<ResFetchResumeDTO> getById(@PathVariable("id") long id)
            throws IdInvalidException {
        Resume resume = this.resumeService.fetchById(id);
        if (resume == null)
            throw new IdInvalidException("Resume voi id " + id + " khong ton tai!");
        return ResponseEntity.ok(this.resumeService.convertToGet(resume));
    }

    @GetMapping("/resumes")
    @ApiMessage("Get all resume")
    public ResponseEntity<ResultPaginateDTO> getAll(
            @Filter Specification<Resume> spec,
            Pageable pageable)
            throws IdInvalidException {
        List<Long> arrJobIds = null;
        String email = SecurityUtil.getCurrentUserLogin().isPresent() == true
                ? SecurityUtil.getCurrentUserLogin().get()
                : "";
        User currentUser = this.userService.handleGetUserByEmail(email);
        if (currentUser != null) {
            Company userCompany = currentUser.getCompany();
            if (userCompany != null) {
                List<Job> companyJobs = userCompany.getJobs();
                if (companyJobs != null && companyJobs.size() > 0) {
                    arrJobIds = companyJobs.stream().map(x -> x.getId())
                            .collect(Collectors.toList());
                }
            }
        }
        Specification<Resume> jobInSpec = filterSpecificationConverter.convert(filterBuilder.field("job")
                .in(filterBuilder.input(arrJobIds)).get());
        Specification<Resume> finalSpec = jobInSpec.and(spec);
        return ResponseEntity.ok(this.resumeService.fetchAll(finalSpec, pageable));
    }

    @PutMapping("/resumes")
    @ApiMessage("Update status resume")
    public ResponseEntity<ResUpdateResumeDTO> update(@RequestBody Resume resume)
            throws IdInvalidException {
        return ResponseEntity.ok(this.resumeService.update(resume));
    }

    @DeleteMapping("/resumes/{id}")
    @ApiMessage("Delete resume")
    public ResponseEntity<Void> delete(@PathVariable("id") long id)
            throws IdInvalidException {
        this.resumeService.delete(id);
        return ResponseEntity.ok(null);
    }

    @PostMapping("/resumes/by-user")
    @ApiMessage("Create resume")
    public ResponseEntity<ResultPaginateDTO> getByUser(Pageable pageable) {

        return ResponseEntity.ok(this.resumeService.fetchResumeByUser(pageable));
    }
}
