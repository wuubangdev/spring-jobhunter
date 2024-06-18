package vn.hoidanit.jobhunter.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import vn.hoidanit.jobhunter.domain.job.Job;
import java.util.List;
import vn.hoidanit.jobhunter.domain.Skills;

@Repository
public interface JobRepository extends JpaRepository<Job, Long>, JpaSpecificationExecutor<Job> {
    Job findByName(String name);

    List<Job> findBySkillsIn(List<Skills> skills);
}
