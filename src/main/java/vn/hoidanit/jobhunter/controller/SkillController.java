package vn.hoidanit.jobhunter.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.turkraft.springfilter.boot.Filter;

import vn.hoidanit.jobhunter.domain.Skills;
import vn.hoidanit.jobhunter.domain.response.ResultPaginateDTO;
import vn.hoidanit.jobhunter.domain.user.User;
import vn.hoidanit.jobhunter.service.SkillService;
import vn.hoidanit.jobhunter.util.anotation.ApiMessage;
import vn.hoidanit.jobhunter.util.error.IdInvalidException;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("/api/v1")
public class SkillController {
    private final SkillService skillService;

    public SkillController(SkillService skillService) {
        this.skillService = skillService;
    }

    @PostMapping("/skills")
    @ApiMessage("Create skill")
    public ResponseEntity<Skills> createSkill(@RequestBody Skills skills) throws IdInvalidException {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.skillService.handleCreateSkill(skills));
    }

    @PutMapping("/skills")
    public ResponseEntity<Skills> updateSkill(@RequestBody Skills skills) throws IdInvalidException {
        return ResponseEntity.ok(this.skillService.handleUpdateSkill(skills));
    }

    @GetMapping("/skills")
    @ApiMessage("Fetch all skill")
    public ResponseEntity<ResultPaginateDTO> getAllUser(
            @Filter Specification<Skills> spec,
            Pageable pageable) throws IdInvalidException {
        return ResponseEntity.ok(this.skillService.fetchAllSkill(spec, pageable));
    }

}
