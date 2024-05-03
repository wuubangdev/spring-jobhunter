package vn.hoidanit.jobhunter.controller;

import org.springframework.web.bind.annotation.RestController;

import vn.hoidanit.jobhunter.domain.User;
import vn.hoidanit.jobhunter.service.UserService;

import java.util.List;
import java.util.Optional;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/user")
    public User createNewUser(@RequestBody User user) {
        User newUser = this.userService.handleCreateUser(user);
        return newUser;
    }

    @DeleteMapping("/user/{id}")
    public String deleteUser(@PathVariable("id") long id) {
        this.userService.hadleDeleteUser(id);
        return "delete success";
    }

    @GetMapping("/user/{id}")
    public User getUser(@PathVariable("id") long id) {
        User user = null;
        Optional<User> userOp = this.userService.fetchUserById(id);
        if (userOp.isPresent()) {
            user = userOp.get();
        }
        return user;
    }

    @GetMapping("/user")
    public List<User> getAllUser() {
        List<User> users = this.userService.fetchAllUser();
        return users;
    }

}
