package vn.hoidanit.jobhunter.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.turkraft.springfilter.boot.Filter;

import jakarta.validation.Valid;
import vn.hoidanit.jobhunter.domain.Permission;
import vn.hoidanit.jobhunter.domain.response.ResultPaginateDTO;
import vn.hoidanit.jobhunter.service.PermissionService;
import vn.hoidanit.jobhunter.util.anotation.ApiMessage;
import vn.hoidanit.jobhunter.util.error.IdInvalidException;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("api/v1")
public class PermissionController {
    private final PermissionService permissionService;

    public PermissionController(PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    @PostMapping("/permissions")
    @ApiMessage("Create permission")
    public ResponseEntity<Permission> createPermission(@Valid @RequestBody Permission permission)
            throws IdInvalidException {
        Boolean isExist = this.permissionService
                .checkExist(permission.getName(),
                        permission.getApiPath(),
                        permission.getMethod());
        if (isExist)
            throw new IdInvalidException("Permission da ton tai!");
        return ResponseEntity.status(HttpStatus.CREATED).body(this.permissionService.create(permission));
    }

    @GetMapping("/permissions/{id}")
    @ApiMessage("Get permission by id")
    public ResponseEntity<Permission> getPermissionById(@PathVariable("id") long id)
            throws IdInvalidException {
        Permission currentPer = this.permissionService.fetchById(id);
        if (currentPer == null)
            throw new IdInvalidException("Permission co id " + id + " khong ton tai!");
        return ResponseEntity.ok(currentPer);
    }

    @GetMapping("/permissions")
    @ApiMessage("Get all permissions")
    public ResponseEntity<ResultPaginateDTO> getAllPermission(
            @Filter Specification<Permission> spec,
            Pageable pageable)
            throws IdInvalidException {
        return ResponseEntity.ok(this.permissionService.fetchAll(spec, pageable));
    }

    @PutMapping("/permissions")
    @ApiMessage("Update permission")
    public ResponseEntity<Permission> updatePermission(@Valid @RequestBody Permission permission)
            throws IdInvalidException {
        Permission permissionDB = this.permissionService.fetchById(permission.getId());
        if (permissionDB == null)
            throw new IdInvalidException("Permission co id " + permission.getId() + " khong ton tai!");
        Boolean isExist = this.permissionService
                .checkExist(permission.getName(),
                        permission.getApiPath(),
                        permission.getMethod());
        if (isExist)
            throw new IdInvalidException("Permission da ton tai!");
        return ResponseEntity.status(HttpStatus.CREATED).body(this.permissionService.update(permissionDB, permission));
    }

    @DeleteMapping("/permissions/{id}")
    @ApiMessage("Delete permission by id")
    public ResponseEntity<Void> deletePermissionById(@PathVariable("id") long id)
            throws IdInvalidException {
        Permission currentPer = this.permissionService.fetchById(id);
        if (currentPer == null)
            throw new IdInvalidException("Permission co id " + id + " khong ton tai!");
        this.permissionService.delete(currentPer);
        return ResponseEntity.ok(null);
    }
}
