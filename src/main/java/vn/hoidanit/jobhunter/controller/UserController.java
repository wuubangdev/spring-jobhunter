package vn.hoidanit.jobhunter.controller;

import org.springframework.web.bind.annotation.RestController;

import com.turkraft.springfilter.boot.Filter;

import jakarta.validation.Valid;
import vn.hoidanit.jobhunter.domain.ResultPaginate;
import vn.hoidanit.jobhunter.domain.user.ResCreateUserDTO;
import vn.hoidanit.jobhunter.domain.user.ResUserDTO;
import vn.hoidanit.jobhunter.domain.user.User;
import vn.hoidanit.jobhunter.service.UserService;
import vn.hoidanit.jobhunter.util.ApiMessage;
import vn.hoidanit.jobhunter.util.error.IdInvalidException;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
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
    @ApiMessage("Create a new user")
    public ResponseEntity<ResCreateUserDTO> createNewUser(@Valid @RequestBody User user)
            throws IdInvalidException {
        boolean isExistUser = this.userService.checkExistUser(user);
        if (isExistUser) {
            throw new IdInvalidException("Email đã tồn tại vui lòng nhập email khác!");
        }
        user.setPassword(this.passwordEncoder.encode(user.getPassword()));
        ResCreateUserDTO createdUser = this.userService.convertToCreateUserDTO(this.userService.handleSaveUser(user));
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }

    @GetMapping("/users/{id}")
    @ApiMessage("Get user by id")
    public ResponseEntity<ResUserDTO> getUser(@PathVariable("id") long id)
            throws IdInvalidException {
        User user = this.userService.fetchUserById(id);
        if (user == null) {
            throw new IdInvalidException("Id không tồn tại không tìm được tài khoản!");
        }
        ResUserDTO getUserDTO = this.userService.convertToResUserDTO(this.userService.fetchUserById(id));
        return ResponseEntity.ok(getUserDTO);
    }

    @GetMapping("/users")
    @ApiMessage("Get all user")
    public ResponseEntity<ResultPaginate> getAllUser(@Filter Specification<User> spec, Pageable page) {
        return ResponseEntity.ok(this.userService.fetchAllUser(spec, page));
    }

    @PutMapping("/users")
    @ApiMessage("Update a user")
    public ResponseEntity<ResUserDTO> putUpdateUser(@RequestBody User user)
            throws IdInvalidException {
        User curentUser = this.userService.fetchUserById(user.getId());
        if (curentUser == null) {
            throw new IdInvalidException("Id không tồn tại không tìm được tài khoản để cập nhật!");
        }
        User updatedUser = this.userService.handleUpdatUser(user);
        ResUserDTO resUserDTO = this.userService.convertToResUserDTO(updatedUser);
        return ResponseEntity.ok(resUserDTO);
    }

    @DeleteMapping("/users/{id}")
    @ApiMessage("Delete a user")
    public ResponseEntity<Void> deleteUser(@PathVariable("id") long id)
            throws IdInvalidException {
        User curentUser = this.userService.fetchUserById(id);
        if (curentUser == null) {
            throw new IdInvalidException("Id không tồn tại!");
        }
        this.userService.hadleDeleteUser(id);
        return ResponseEntity.ok(null);
    }
}
