package vn.hoidanit.jobhunter.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import vn.hoidanit.jobhunter.domain.Company;
import vn.hoidanit.jobhunter.domain.MetaDTO;
import vn.hoidanit.jobhunter.domain.ResultPaginate;
import vn.hoidanit.jobhunter.domain.User;
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

    public ResultPaginate fetchAllUsers(Pageable pageable) {
        Page<User> userCompanies = this.userRepository.findAll(pageable);
        MetaDTO mt = new MetaDTO();
        ResultPaginate rsp = new ResultPaginate();
        mt.setCurrent(userCompanies.getNumber());
        mt.setPageSize(userCompanies.getSize());
        mt.setTotalPages(userCompanies.getTotalPages());
        mt.setTotalIteams(userCompanies.getTotalElements());
        rsp.setMeta(mt);
        rsp.setData(userCompanies.getContent());
        return rsp;
    }

    public User updateUser(User user) {
        User currentUser = this.fetchUserById(user.getId());
        if (currentUser != null) {
            currentUser.setName(user.getName());
            currentUser.setEmail(user.getEmail());
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
}
