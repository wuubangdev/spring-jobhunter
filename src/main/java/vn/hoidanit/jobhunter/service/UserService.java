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

    public User handleSaveUser(User user) {
        return this.userRepository.save(user);
    }

    public void hadleDeleteUser(long id) {
        this.userRepository.deleteById(id);
    }

    public User fetchUserById(long id) {
        Optional<User> userOp = this.userRepository.findById(id);
        if (userOp.isPresent()) {
            return userOp.get();
        }
        return null;
    }

    public ResultPaginate fetchAllUser(Specification<User> spec, Pageable pageable) {
        Page<User> pageUser = this.userRepository.findAll(spec, pageable);
        MetaDTO mt = new MetaDTO();
        ResultPaginate rsp = new ResultPaginate();
        mt.setCurrent(pageUser.getNumber() + 1);
        mt.setPageSize(pageUser.getSize());
        mt.setTotalPage(pageUser.getTotalPages());
        mt.setTotalItem(pageUser.getTotalElements());
        rsp.setMeta(mt);
        List<ResUserDTO> listUser = pageUser.getContent()
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
        rsp.setData(listUser);
        return rsp;
    }

    public User handleUpdatUser(User user) {
        User currentUser = this.fetchUserById(user.getId());
        currentUser.setName(user.getName());
        currentUser.setGender(user.getGender());
        currentUser.setAddress(user.getAddress());
        currentUser.setAge(user.getAge());
        currentUser = this.userRepository.save(currentUser);
        return currentUser;
    }

    public User getUserByEmail(String email) {
        return this.userRepository.findByEmail(email);
    }

    public boolean checkExistUser(User user) {
        return this.userRepository.existsByEmail(user.getEmail());
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

    public ResUserDTO convertToResUserDTO(User gotUser) {
        ResUserDTO getUserDTO = new ResUserDTO();
        getUserDTO.setId(gotUser.getId());
        getUserDTO.setName(gotUser.getName());
        getUserDTO.setEmail(gotUser.getEmail());
        getUserDTO.setAddress(gotUser.getAddress());
        getUserDTO.setAge(gotUser.getAge());
        getUserDTO.setGender(gotUser.getGender());
        getUserDTO.setCreatedAt(gotUser.getCreatedAt());
        getUserDTO.setUpdatedAt(gotUser.getUpdatedAt());
        return getUserDTO;
    }

    public void updateUserToken(String email, String refreshToken) {
        User currentUser = this.getUserByEmail(email);
        if (currentUser != null) {
            currentUser.setRefreshToken(refreshToken);
            this.userRepository.save(currentUser);
        }
    }
}
