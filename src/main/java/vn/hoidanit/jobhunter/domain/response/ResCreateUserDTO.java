package vn.hoidanit.jobhunter.domain.response;

import java.time.Instant;

import lombok.Getter;
import lombok.Setter;
import vn.hoidanit.jobhunter.domain.user.EnumGender;

@Getter
@Setter
public class ResCreateUserDTO {
    private long id;
    private String name;
    private String email;
    private EnumGender gender;
    private String address;
    private int age;
    private Instant createdAt;
    private UserCompany company;
    private UserRole userRole;

    @Getter
    @Setter
    public static class UserCompany {
        private long id;
        private String name;

    }

    @Getter
    @Setter
    public static class UserRole {
        private long id;
        private String name;

    }
}
