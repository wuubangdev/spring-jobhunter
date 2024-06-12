package vn.hoidanit.jobhunter.service;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import vn.hoidanit.jobhunter.domain.Skills;
import vn.hoidanit.jobhunter.domain.response.ResultPaginateDTO;
import vn.hoidanit.jobhunter.repository.SkillRepository;
import vn.hoidanit.jobhunter.util.error.IdInvalidException;

@Service
public class SkillService {
    private final SkillRepository skillRepository;

    public SkillService(SkillRepository skillRepository) {
        this.skillRepository = skillRepository;
    }

    public Skills fetchSkillsByName(String name) {
        return this.skillRepository.findByName(name);
    }

    public Skills fetchSkillsById(long id) {
        Optional<Skills> opSkill = this.skillRepository.findById(id);
        return opSkill.orElse(null);
    }

    public Skills handleCreateSkill(Skills s) throws IdInvalidException {
        if (s.getName() == null || s.getName().equals("")) {
            throw new IdInvalidException("Ten skill khong duoc de trong!");
        }
        if (this.fetchSkillsByName(s.getName()) != null)
            throw new IdInvalidException("Skill da ton tai!");
        return this.skillRepository.save(s);
    }

    public Skills handleUpdateSkill(Skills s) throws IdInvalidException {
        if (s.getName() == null || s.getName().equals(""))
            throw new IdInvalidException("Ten skill khong duoc de trong!");
        if (this.fetchSkillsByName(s.getName()) != null) {
            throw new IdInvalidException("Skill da ton tai!");
        }
        Skills currentSkills = this.fetchSkillsById(s.getId());
        if (currentSkills != null) {
            currentSkills.setName(s.getName());
            return this.skillRepository.save(currentSkills);
        } else
            throw new IdInvalidException("Skill can chinh sua khong ton tai!");
    }

    public ResultPaginateDTO fetchAllSkill(Specification<Skills> spec, Pageable pageable) {
        Page<Skills> skillPage = this.skillRepository.findAll(spec, pageable);
        ResultPaginateDTO.Meta mt = new ResultPaginateDTO.Meta();
        ResultPaginateDTO rsp = new ResultPaginateDTO();
        mt.setCurrent(skillPage.getNumber() + 1);
        mt.setPageSize(skillPage.getSize());
        mt.setTotalPages(skillPage.getTotalPages());
        mt.setTotalIteams(skillPage.getTotalElements());
        rsp.setMeta(mt);
        rsp.setResult(skillPage.getContent());
        return rsp;
    }

}
