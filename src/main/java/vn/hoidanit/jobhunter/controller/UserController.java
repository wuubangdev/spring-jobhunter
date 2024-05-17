package vn.hoidanit.jobhunter.controller;

import org.springframework.web.bind.annotation.RestController;

import com.turkraft.springfilter.boot.Filter;

import vn.hoidanit.jobhunter.domain.ResultPaginate;
import vn.hoidanit.jobhunter.domain.User;
import vn.hoidanit.jobhunter.service.UserService;
import vn.hoidanit.jobhunter.service.error.IdInvalidException;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequestMapping("/api/v1")
public class UserController {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public UserController(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/user")
    public ResponseEntity<User> createNewUser(@RequestBody User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User createdUser = this.userService.handleCreateUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<User> getUserById(@PathVariable("id") long id) {
        User user = this.userService.fetchUserById(id);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/user")
    public ResponseEntity<ResultPaginate> getAllUser(
            @Filter Specification<User> spec,
            Pageable pageable) {
        return ResponseEntity.ok(this.userService.fetchAllUsers(spec, pageable));
    }

    @PutMapping("/user")
    public ResponseEntity<User> putUpdateUser(@RequestBody User user) {
        User updatedUser = this.userService.updateUser(user);
        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/user/{id}")
    public ResponseEntity<String> deleteUserById(@PathVariable("id") long id) throws IdInvalidException {

        if (id >= 100) {
            throw new IdInvalidException("Id phai nho hon 100");
        }

        this.userService.deleteUserById(id);
        return ResponseEntity.ok("delete success");
    }

}
