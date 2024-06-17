package vn.hoidanit.jobhunter.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import vn.hoidanit.jobhunter.domain.Skills;
import vn.hoidanit.jobhunter.domain.Subscriber;
import vn.hoidanit.jobhunter.repository.SkillRepository;
import vn.hoidanit.jobhunter.repository.SubscriberRepository;

@Service
public class SubscriberService {
    private final SubscriberRepository subscriberRepository;
    private final SkillRepository skillRepository;

    public SubscriberService(SubscriberRepository subscriberRepository, SkillRepository skillRepository) {
        this.subscriberRepository = subscriberRepository;
        this.skillRepository = skillRepository;
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
}
