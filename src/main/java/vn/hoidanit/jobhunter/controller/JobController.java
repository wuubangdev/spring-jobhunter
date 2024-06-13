package vn.hoidanit.jobhunter.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.turkraft.springfilter.boot.Filter;

import jakarta.validation.Valid;
import vn.hoidanit.jobhunter.domain.job.Job;
import vn.hoidanit.jobhunter.domain.job.ResCreateJobDTO;
import vn.hoidanit.jobhunter.domain.response.ResultPaginateDTO;
import vn.hoidanit.jobhunter.service.JobService;
import vn.hoidanit.jobhunter.util.anotation.ApiMessage;
import vn.hoidanit.jobhunter.util.error.IdInvalidException;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("/api/v1")
public class JobController {
    private final JobService jobService;

    public JobController(JobService jobService) {
        this.jobService = jobService;
    }

    @PostMapping("/jobs")
    @ApiMessage("Create a new job")
    public ResponseEntity<ResCreateJobDTO> create(@Valid @RequestBody Job job) {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.jobService.create(job));
    }

    @PutMapping("/jobs")
    @ApiMessage("Update a new job")
    public ResponseEntity<ResCreateJobDTO> update(@Valid @RequestBody Job job)
            throws IdInvalidException {
        Job currentJob = this.jobService.fetchJobById(job.getId());
        if (currentJob == null) {
            throw new IdInvalidException("Job khong ton tai!");
        }
        return ResponseEntity.ok(this.jobService.update(job, currentJob));
    }

    @DeleteMapping("/jobs/{id}")
    @ApiMessage("Delete job")
    public ResponseEntity<Void> update(@PathVariable("id") long id)
            throws IdInvalidException {
        Job currentJob = this.jobService.fetchJobById(id);
        if (currentJob == null) {
            throw new IdInvalidException("Job khong ton tai!");
        }
        this.jobService.delete(id);
        return ResponseEntity.ok(null);
    }

    @GetMapping("/jobs")
    public ResponseEntity<ResultPaginateDTO> getAllJob(
            @Filter Specification<Job> spec,
            Pageable pageable) {
        return ResponseEntity.ok(this.jobService.fetchAll(spec, pageable));
    }

    @GetMapping("/jobs/{id}")
    public ResponseEntity<ResCreateJobDTO> getById(@PathVariable("id") long id)
            throws IdInvalidException {
        Job currentJob = this.jobService.fetchJobById(id);
        if (currentJob == null) {
            throw new IdInvalidException("Job khong ton tai!");
        }
        return ResponseEntity.ok(this.jobService.getById(id));
    }
}
