package vn.hoidanit.jobhunter.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.turkraft.springfilter.boot.Filter;

import jakarta.validation.Valid;
import vn.hoidanit.jobhunter.domain.response.ResultPaginateDTO;
import vn.hoidanit.jobhunter.domain.resume.ResFetchResumeDTO;
import vn.hoidanit.jobhunter.domain.resume.ResUpdateResumeDTO;
import vn.hoidanit.jobhunter.domain.resume.ResCreateResumeDTO;
import vn.hoidanit.jobhunter.domain.resume.Resume;
import vn.hoidanit.jobhunter.service.ResumeService;
import vn.hoidanit.jobhunter.util.anotation.ApiMessage;
import vn.hoidanit.jobhunter.util.error.IdInvalidException;

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

    public ResumeController(ResumeService resumeService) {
        this.resumeService = resumeService;
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
        return ResponseEntity.ok(this.resumeService.fetchAll(spec, pageable));
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

}
