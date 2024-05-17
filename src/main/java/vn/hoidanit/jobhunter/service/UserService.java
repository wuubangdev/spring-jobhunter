package vn.hoidanit.jobhunter.service;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

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

    public ResultPaginate fetchAllUser(Pageable pageable) {
        Page<User> pageUser = this.userRepository.findAll(pageable);
        MetaDTO mt = new MetaDTO();
        ResultPaginate rsp = new ResultPaginate();
        mt.setCurrent(pageUser.getNumber());
        mt.setPageSize(pageUser.getSize());
        mt.setTotalPage(pageUser.getTotalPages());
        mt.setTotalItem(pageUser.getTotalElements());
        rsp.setMeta(mt);
        rsp.setData(pageUser.getContent());
        return rsp;
    }

    public User handleUpdatUser(User user) {
        User currentUser = this.fetchUserById(user.getId());
        currentUser.setEmail(user.getEmail());
        currentUser.setName(user.getName());
        currentUser.setPassword(user.getPassword());
        currentUser = this.userRepository.save(currentUser);
        return currentUser;
    }

    public User getUserByEmail(String email) {
        return this.userRepository.findByEmail(email);
    }
}
