package vn.hoidanit.jobhunter.controller;

import org.springframework.web.bind.annotation.RestController;

import com.turkraft.springfilter.boot.Filter;

import vn.hoidanit.jobhunter.domain.response.ResCreateUserDTO;
import vn.hoidanit.jobhunter.domain.response.ResUserDTO;
import vn.hoidanit.jobhunter.domain.response.ResultPaginateDTO;
import vn.hoidanit.jobhunter.domain.user.User;
import vn.hoidanit.jobhunter.service.UserService;
import vn.hoidanit.jobhunter.util.anotation.ApiMessage;
import vn.hoidanit.jobhunter.util.error.IdInvalidException;

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

    @PostMapping("/users")
    @ApiMessage("Create new user success")
    public ResponseEntity<ResCreateUserDTO> createNewUser(@RequestBody User user) throws IdInvalidException {
        User reqUser = this.userService.handleGetUserByEmail(user.getEmail());
        if (reqUser != null) {
            throw new IdInvalidException("Email da ton tai vui long dang ky voi email khac!");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User createdUser = this.userService.handleCreateUser(user);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(this.userService.convertToCreateUserDTO(createdUser));
    }

    @GetMapping("/users/{id}")
    @ApiMessage("Fetch user success")
    public ResponseEntity<ResUserDTO> getUserById(@PathVariable("id") long id) throws IdInvalidException {
        User user = this.userService.fetchUserById(id);
        if (user == null) {
            throw new IdInvalidException("Id khong ton tai!");
        }
        return ResponseEntity.ok(this.userService.convertToUserDTO(user));
    }

    @GetMapping("/users")
    @ApiMessage("Fetch list user success")
    public ResponseEntity<ResultPaginateDTO> getAllUser(
            @Filter Specification<User> spec,
            Pageable pageable) throws IdInvalidException {
        return ResponseEntity.ok(this.userService.fetchAllUsers(spec, pageable));
    }

    @PutMapping("/users")
    @ApiMessage("Update user success")
    public ResponseEntity<ResUserDTO> putUpdateUser(@RequestBody User user) throws IdInvalidException {
        User updatedUser = this.userService.updateUser(user);
        return ResponseEntity.ok(this.userService.convertToUserDTO(updatedUser));
    }

    @DeleteMapping("/user/{id}")
    @ApiMessage("Delete user success")
    public ResponseEntity<Void> deleteUserById(@PathVariable("id") long id) throws IdInvalidException {
        User user = this.userService.fetchUserById(id);
        if (user == null) {
            throw new IdInvalidException("Id khong ton tai!");
        }
        this.userService.deleteUserById(id);
        return ResponseEntity.ok(null);
    }

}
