package vn.hoidanit.jobhunter.service;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import vn.hoidanit.jobhunter.domain.Permission;
import vn.hoidanit.jobhunter.domain.response.ResultPaginateDTO;
import vn.hoidanit.jobhunter.repository.PermissionRepository;

@Service
public class PermissionService {
    private final PermissionRepository permissionRepository;

    public PermissionService(PermissionRepository permissionRepository) {
        this.permissionRepository = permissionRepository;
    }

    public boolean checkExist(String name, String apiPath, String method) {
        Permission permission = this.permissionRepository.findByNameAndApiPathAndMethod(name, apiPath, method);
        if (permission != null) {
            return true;
        }
        return false;
    }

    public Permission fetchById(long id) {
        Optional<Permission> opPer = this.permissionRepository.findById(id);
        return opPer.orElse(null);
    }

    public Permission create(Permission p) {
        return this.permissionRepository.save(p);
    }

    public ResultPaginateDTO fetchAll(Specification<Permission> spec, Pageable pageable) {
        Page<Permission> pagePer = this.permissionRepository.findAll(spec, pageable);
        ResultPaginateDTO rsp = new ResultPaginateDTO();
        ResultPaginateDTO.Meta meta = new ResultPaginateDTO.Meta();
        meta.setPage(pagePer.getNumber() + 1);
        meta.setPageSize(pagePer.getSize());
        meta.setPages(pagePer.getTotalPages());
        meta.setTotal(pagePer.getTotalElements());
        rsp.setMeta(meta);
        rsp.setResult(pagePer.getContent());
        return rsp;
    }

    public Permission update(Permission permissionDB, Permission p) {
        permissionDB.setName(p.getName());
        permissionDB.setApiPath(p.getApiPath());
        permissionDB.setMethod(p.getMethod());
        permissionDB.setModule(p.getModule());
        return this.permissionRepository.save(permissionDB);
    }

    public void delete(Permission permission) {
        if (permission.getRoles() != null) {
            permission.getRoles()
                    .stream().forEach(role -> role.getPermissions().remove(permission));
        }
        this.permissionRepository.delete(permission);
    }
}
