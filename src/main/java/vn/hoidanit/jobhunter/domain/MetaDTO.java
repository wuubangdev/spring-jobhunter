package vn.hoidanit.jobhunter.domain;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MetaDTO {
    private int current;
    private int pageSize;
    private int totalPage;
    private long totalItem;
}
