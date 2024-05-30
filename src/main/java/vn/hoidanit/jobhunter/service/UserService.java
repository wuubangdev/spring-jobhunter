package vn.hoidanit.jobhunter.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import vn.hoidanit.jobhunter.domain.MetaDTO;
import vn.hoidanit.jobhunter.domain.ResultPaginate;
import vn.hoidanit.jobhunter.domain.user.ResCreateUserDTO;
import vn.hoidanit.jobhunter.domain.user.ResUserDTO;
import vn.hoidanit.jobhunter.domain.user.User;
import vn.hoidanit.jobhunter.repository.UserRepository;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User handleCreateUser(User user) {
        return this.userRepository.save(user);
    }

    public User fetchUserById(long id) {
        Optional<User> userOp = this.userRepository.findById(id);
        if (userOp.isPresent()) {
            return userOp.get();
        }
        return null;
    }

    public ResultPaginate fetchAllUsers(Specification<User> spec, Pageable pageable) {
        Page<User> userCompanies = this.userRepository.findAll(spec, pageable);
        MetaDTO mt = new MetaDTO();
        ResultPaginate rsp = new ResultPaginate();
        mt.setCurrent(userCompanies.getNumber() + 1);
        mt.setPageSize(userCompanies.getSize());
        mt.setTotalPages(userCompanies.getTotalPages());
        mt.setTotalIteams(userCompanies.getTotalElements());
        rsp.setMeta(mt);

        List<ResUserDTO> listUserDTO = userCompanies.getContent()
                .stream().map(item -> new ResUserDTO(
                        item.getId(),
                        item.getName(),
                        item.getEmail(),
                        item.getGender(),
                        item.getAddress(),
                        item.getAge(),
                        item.getCreatedAt(),
                        item.getUpdatedAt()))
                .collect(Collectors.toList());
        rsp.setResult(listUserDTO);
        return rsp;
    }

    public User updateUser(User user) {
        User currentUser = this.fetchUserById(user.getId());
        if (currentUser != null) {
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
