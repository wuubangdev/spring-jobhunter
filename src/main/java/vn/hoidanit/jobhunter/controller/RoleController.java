package vn.hoidanit.jobhunter.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.turkraft.springfilter.boot.Filter;

import vn.hoidanit.jobhunter.domain.Role;
import vn.hoidanit.jobhunter.domain.response.ResultPaginateDTO;
import vn.hoidanit.jobhunter.service.RoleService;
import vn.hoidanit.jobhunter.util.anotation.ApiMessage;
import vn.hoidanit.jobhunter.util.error.IdInvalidException;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequestMapping("/api/v1")
public class RoleController {
    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @PostMapping("/roles")
    @ApiMessage("Create role")
    public ResponseEntity<Role> createRole(@RequestBody Role role)
            throws IdInvalidException {
        boolean isNameExist = this.roleService.isNameExist(role.getName());
        if (isNameExist)
            throw new IdInvalidException("Role name da ton tai.");
        return ResponseEntity.status(HttpStatus.CREATED).body(this.roleService.create(role));
    }

    @GetMapping("/roles/{id}")
    @ApiMessage("Get role by id")
    public ResponseEntity<Role> getRoleById(@PathVariable("id") long id)
            throws IdInvalidException {
        Role role = this.roleService.fetchById(id);
        if (role == null)
            throw new IdInvalidException("Id khong ton tai.");
        return ResponseEntity.ok(role);
    }

    @GetMapping("/roles")
    @ApiMessage("Get all role")
    public ResponseEntity<ResultPaginateDTO> getAllRole(
            @Filter Specification<Role> spec,
            Pageable pageable)
            throws IdInvalidException {
        return ResponseEntity.ok(this.roleService.fetchAllRole(spec, pageable));
    }

    @PutMapping("roles")
    public ResponseEntity<Role> updateRole(@RequestBody Role role)
            throws IdInvalidException {
        Role roleDB = this.roleService.fetchById(role.getId());
        if (roleDB == null)
            throw new IdInvalidException("Id khong ton tai.");
        if (this.roleService.isNameExist(role.getName()))
            throw new IdInvalidException("Role name da ton tai.");
        return ResponseEntity.ok(this.roleService.update(roleDB, role));
    }

    @DeleteMapping("/roles/{id}")
    @ApiMessage("Delete role")
    public ResponseEntity<Role> deleteRole(@PathVariable("id") long id)
            throws IdInvalidException {
        Role role = this.roleService.fetchById(id);
        if (role == null)
            throw new IdInvalidException("Id khong ton tai.");
        this.roleService.delete(role);
        return ResponseEntity.ok(null);
    }
}
