package vn.hoidanit.jobhunter.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import vn.hoidanit.jobhunter.domain.Skills;
import vn.hoidanit.jobhunter.domain.Subscriber;
import vn.hoidanit.jobhunter.domain.email.ResEmailJob;
import vn.hoidanit.jobhunter.domain.job.Job;
import vn.hoidanit.jobhunter.repository.JobRepository;
import vn.hoidanit.jobhunter.repository.SkillRepository;
import vn.hoidanit.jobhunter.repository.SubscriberRepository;

@Service
public class SubscriberService {
    private final SubscriberRepository subscriberRepository;
    private final SkillRepository skillRepository;
    private final JobRepository jobRepository;
    private final EmailService emailService;

    public SubscriberService(SubscriberRepository subscriberRepository,
            SkillRepository skillRepository,
            EmailService emailService,
            JobRepository jobRepository) {
        this.subscriberRepository = subscriberRepository;
        this.skillRepository = skillRepository;
        this.jobRepository = jobRepository;
        this.emailService = emailService;
    }

    public Subscriber fetchById(long id) {
        Optional<Subscriber> opSub = this.subscriberRepository.findById(id);
        return opSub.orElse(null);
    }

    public Subscriber fetchByEmail(String email) {
        return this.subscriberRepository.findByEmail(email);
    }

    public Subscriber create(Subscriber subscriber) {
        if (subscriber.getSkills() != null) {
            List<Skills> skills = this.skillRepository
                    .findByIdIn(subscriber.getSkills()
                            .stream().map(s -> s.getId())
                            .collect(Collectors.toList()));
            subscriber.setSkills(skills);
        }
        subscriber = this.subscriberRepository.save(subscriber);
        return subscriber;
    }

    public Subscriber update(Subscriber currentSubs, Subscriber subscriber) {
        if (subscriber.getSkills() != null) {
            List<Skills> skills = this.skillRepository
                    .findByIdIn(subscriber.getSkills()
                            .stream().map(s -> s.getId())
                            .collect(Collectors.toList()));
            currentSubs.setSkills(skills);
        }
        currentSubs = this.subscriberRepository.save(currentSubs);
        return currentSubs;
    }

    public void sendSubscribersEmailJobs() {
        List<Subscriber> listSubs = this.subscriberRepository.findAll();
        if (listSubs != null && listSubs.size() > 0) {
            for (Subscriber sub : listSubs) {
                List<Skills> listSkills = sub.getSkills();
                if (listSkills != null && listSkills.size() > 0) {
                    List<Job> listJobs = this.jobRepository.findBySkillsIn(listSkills);
                    if (listJobs != null && listJobs.size() > 0) {
                        List<ResEmailJob> arr = listJobs.stream().map(
                                job -> this.convertJobToSendEmail(job)).collect(Collectors.toList());
                        this.emailService.sendEmailFromTemplateSync(
                                sub.getEmail(),
                                "Cơ hội việc làm hot đang chờ đón bạn, khám phá ngay",
                                "job",
                                sub.getName(),
                                arr);
                    }
                }
            }
        }
    }

    public ResEmailJob convertJobToSendEmail(Job job) {
        ResEmailJob rej = new ResEmailJob();
        ResEmailJob.CompanyEmail company = new ResEmailJob.CompanyEmail();
        rej.setCompany(company);
        company.setName(job.getCompany().getName());
        List<ResEmailJob.SkillEmail> skills = job.getSkills()
                .stream().map(s -> new ResEmailJob.SkillEmail(s.getName()))
                .collect(Collectors.toList());
        rej.setSkills(skills);
        rej.setName(job.getName());
        rej.setSalary(job.getSalary());
        return rej;
    }
}
