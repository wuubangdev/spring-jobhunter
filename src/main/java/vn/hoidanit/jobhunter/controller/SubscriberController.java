package vn.hoidanit.jobhunter.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import vn.hoidanit.jobhunter.domain.Subscriber;
import vn.hoidanit.jobhunter.service.SubscriberService;
import vn.hoidanit.jobhunter.util.error.IdInvalidException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/v1")
public class SubscriberController {
    private final SubscriberService subscriberService;

    public SubscriberController(SubscriberService subscriberService) {
        this.subscriberService = subscriberService;
    }

    @PostMapping("/subscribers")
    public ResponseEntity<Subscriber> createSubscriber(@RequestBody Subscriber subscriber)
            throws IdInvalidException {
        Subscriber currentSubs = this.subscriberService.fetchByEmail(subscriber.getEmail());
        if (currentSubs != null)
            throw new IdInvalidException("Subscriber voi email " + subscriber.getEmail() + " da ton tai");
        return ResponseEntity.status(HttpStatus.CREATED).body(this.subscriberService.create(subscriber));
    }

    @PutMapping("/subscribers")
    public ResponseEntity<Subscriber> updateSubscriber(@RequestBody Subscriber subscriber)
            throws IdInvalidException {
        Subscriber currentSubs = this.subscriberService.fetchById(subscriber.getId());
        if (currentSubs == null)
            throw new IdInvalidException("Subscriber voi id " + subscriber.getId() + " khong ton tai");
        return ResponseEntity.ok(this.subscriberService.update(currentSubs, subscriber));
    }

}
