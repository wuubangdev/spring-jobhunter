package vn.hoidanit.jobhunter.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import vn.hoidanit.jobhunter.domain.Company;
import vn.hoidanit.jobhunter.domain.response.ResCreateUserDTO;
import vn.hoidanit.jobhunter.domain.response.ResUserDTO;
import vn.hoidanit.jobhunter.domain.response.ResultPaginateDTO;
import vn.hoidanit.jobhunter.domain.user.User;
import vn.hoidanit.jobhunter.repository.UserRepository;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final CompaniesService companiesService;

    public UserService(
            UserRepository userRepository,
            CompaniesService companiesService) {
        this.userRepository = userRepository;
        this.companiesService = companiesService;
    }

    public User handleCreateUser(User user) {
        if (user.getCompany() != null) {
            Company company = this.companiesService.fetchCompanyById(user.getCompany().getId());
            user.setCompany(company != null ? company : null);
        }
        return this.userRepository.save(user);
    }

    public User fetchUserById(long id) {
        Optional<User> userOp = this.userRepository.findById(id);
        if (userOp.isPresent()) {
            return userOp.get();
        }
        return null;
    }

    public ResultPaginateDTO fetchAllUsers(Specification<User> spec, Pageable pageable) {
        Page<User> userCompanies = this.userRepository.findAll(spec, pageable);
        ResultPaginateDTO.Meta mt = new ResultPaginateDTO.Meta();
        ResultPaginateDTO rsp = new ResultPaginateDTO();
        mt.setCurrent(userCompanies.getNumber() + 1);
        mt.setPageSize(userCompanies.getSize());
        mt.setTotalPages(userCompanies.getTotalPages());
        mt.setTotalIteams(userCompanies.getTotalElements());
        rsp.setMeta(mt);
        List<ResUserDTO> listUserDTO = new ArrayList<>();
        for (User user : userCompanies.toList()) {
            ResUserDTO userDTO = new ResUserDTO();
            ResUserDTO.UserCompany userCompany = new ResUserDTO.UserCompany();
            if (user.getCompany() != null) {
                userCompany.setId(user.getCompany().getId());
                userCompany.setName(user.getCompany().getName());
                userDTO.setCompany(userCompany);
            }
            userDTO.setId(user.getId());
            userDTO.setName(user.getName());
            userDTO.setEmail(user.getEmail());
            userDTO.setGender(user.getGender());
            userDTO.setAddress(user.getAddress());
            userDTO.setAge(user.getAge());
            userDTO.setCreatedAt(user.getCreatedAt());
            userDTO.setUpdatedAt(user.getUpdatedAt());
            listUserDTO.add(userDTO);
        }
        rsp.setResult(listUserDTO);
        return rsp;
    }

    public User updateUser(User user) {
        User currentUser = this.fetchUserById(user.getId());
        if (currentUser != null) {
            Company company = this.companiesService.fetchCompanyById(user.getCompany().getId());
            currentUser.setCompany(company != null ? company : null);
            currentUser.setName(user.getName());
            currentUser.setGender(user.getGender());
            currentUser.setAddress(user.getAddress());
            currentUser.setAge(user.getAge());
            this.userRepository.save(currentUser);
            return currentUser;
        }
        return null;
    }

    public void deleteUserById(long id) {
        this.userRepository.deleteById(id);
    }

    public User handleGetUserByEmail(String email) {
        return this.userRepository.findByEmail(email);
    }

    public ResCreateUserDTO convertToCreateUserDTO(User createdUser) {
        ResCreateUserDTO createUserDTO = new ResCreateUserDTO();

        ResCreateUserDTO.UserCompany userCompany = new ResCreateUserDTO.UserCompany();
        if (createdUser.getCompany() != null) {
            userCompany.setId(createdUser.getCompany().getId());
            userCompany.setName(createdUser.getCompany().getName());
            createUserDTO.setCompany(userCompany);
        }

        createUserDTO.setId(createdUser.getId());
        createUserDTO.setName(createdUser.getName());
        createUserDTO.setEmail(createdUser.getEmail());
        createUserDTO.setAddress(createdUser.getAddress());
        createUserDTO.setAge(createdUser.getAge());
        createUserDTO.setGender(createdUser.getGender());
        createUserDTO.setCreatedAt(createdUser.getCreatedAt());

        return createUserDTO;
    }

    public ResUserDTO convertToUserDTO(User user) {
        ResUserDTO userDTO = new ResUserDTO();
        ResUserDTO.UserCompany userCompany = new ResUserDTO.UserCompany();
        if (user.getCompany() != null) {
            userCompany.setId(user.getCompany().getId());
            userCompany.setName(user.getCompany().getName());
            userDTO.setCompany(userCompany);
        }
        userDTO.setId(user.getId());
        userDTO.setName(user.getName());
        userDTO.setEmail(user.getEmail());
        userDTO.setAddress(user.getAddress());
        userDTO.setAge(user.getAge());
        userDTO.setGender(user.getGender());
        userDTO.setCreatedAt(user.getCreatedAt());
        userDTO.setUpdatedAt(user.getUpdatedAt());
        return userDTO;
    }

    public void updateUserToken(String refreshToken, String email) {
        User currentUser = this.userRepository.findByEmail(email);
        if (currentUser != null) {
            currentUser.setRefreshToken(refreshToken);
            this.userRepository.save(currentUser);
        }
    }

    public User getUserByRefreshTokenAndEmail(String refreshToken, String email) {
        return this.userRepository.findByRefreshTokenAndEmail(refreshToken, email);
    }

}
