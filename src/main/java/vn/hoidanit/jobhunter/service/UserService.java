package vn.hoidanit.jobhunter.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import vn.hoidanit.jobhunter.domain.Company;
import vn.hoidanit.jobhunter.domain.Role;
import vn.hoidanit.jobhunter.domain.response.ResCreateUserDTO;
import vn.hoidanit.jobhunter.domain.response.ResUserDTO;
import vn.hoidanit.jobhunter.domain.response.ResultPaginateDTO;
import vn.hoidanit.jobhunter.domain.user.User;
import vn.hoidanit.jobhunter.repository.UserRepository;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final CompaniesService companiesService;
    private final RoleService roleService;

    public UserService(UserRepository userRepository, CompaniesService companiesService, RoleService roleService) {
        this.userRepository = userRepository;
        this.companiesService = companiesService;
        this.roleService = roleService;
    }

    public User handleCreateUser(User user) {
        if (user.getCompany() != null) {
            Company company = this.companiesService.fetchCompanyById(user.getCompany().getId());
            user.setCompany(company != null ? company : null);
        }
        if (user.getRole() != null) {
            Role role = this.roleService.fetchById(user.getRole().getId());
            user.setRole(role != null ? role : null);
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
        mt.setPage(userCompanies.getNumber() + 1);
        mt.setPageSize(userCompanies.getSize());
        mt.setPages(userCompanies.getTotalPages());
        mt.setTotal(userCompanies.getTotalElements());
        rsp.setMeta(mt);
        List<ResUserDTO> listUserDTO = userCompanies
                .getContent().stream()
                .map(user -> this.convertToUserDTO(user))
                .collect(Collectors.toList());
        rsp.setResult(listUserDTO);
        return rsp;
    }

    public User updateUser(User user) {
        User currentUser = this.fetchUserById(user.getId());
        if (currentUser != null) {
            if (user.getCompany() != null) {
                Company company = this.companiesService.fetchCompanyById(user.getCompany().getId());
                currentUser.setCompany(company);
            }
            if (user.getRole() != null) {
                Role role = this.roleService.fetchById(user.getRole().getId());
                currentUser.setRole(role);
            }
            currentUser.setName(user.getName());
            currentUser.setGender(user.getGender());
            currentUser.setAddress(user.getAddress());
            currentUser.setAge(user.getAge());
            currentUser = this.userRepository.save(currentUser);
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
        ResCreateUserDTO.UserRole userRole = new ResCreateUserDTO.UserRole();
        if (createdUser.getRole() != null) {
            userRole.setId(createdUser.getRole().getId());
            userRole.setName(createdUser.getRole().getName());
            createUserDTO.setUserRole(userRole);
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
        ResUserDTO.UserRole userRole = new ResUserDTO.UserRole();
        if (user.getCompany() != null) {
            userCompany.setId(user.getCompany().getId());
            userCompany.setName(user.getCompany().getName());
            userDTO.setCompany(userCompany);
        }
        if (user.getRole() != null) {
            userRole.setId(user.getRole().getId());
            userRole.setName(user.getRole().getName());
            userDTO.setUserRole(userRole);
        }
        userDTO.setId(user.getId());
        userDTO.setName(user.getName());
        userDTO.setEmail(user.getEmail());
        userDTO.setAddress(user.getAddress());
        userDTO.setAge(user.getAge());
        userDTO.setGender(user.getGender());
        userDTO.setCreatedAt(user.getCreatedAt());
        userDTO.setCreatedBy(user.getCreatedBy());
        userDTO.setUpdatedAt(user.getUpdatedAt());
        userDTO.setUpdatedBy(user.getUpdatedBy());
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
