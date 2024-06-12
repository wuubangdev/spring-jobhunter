package vn.hoidanit.jobhunter.domain.job;

import java.time.Instant;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import vn.hoidanit.jobhunter.domain.Company;
import vn.hoidanit.jobhunter.domain.level;

@Getter
@Setter
public class ResCreateJobDTO {
    private long id;
    private String name;
    private String location;
    private double salary;
    private int quantity;
    private level level;
    private String description;
    private Instant startDate;
    private Instant endDate;
    private boolean isActive;
    private Company company;
    private List<String> skills;
    private Instant createdAt;
    private Instant updatedAt;
    private String createdBy;
    private String updatedBy;
}
