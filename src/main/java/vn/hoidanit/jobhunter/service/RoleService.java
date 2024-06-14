package vn.hoidanit.jobhunter.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import vn.hoidanit.jobhunter.domain.Permission;
import vn.hoidanit.jobhunter.domain.Role;
import vn.hoidanit.jobhunter.domain.response.ResultPaginateDTO;
import vn.hoidanit.jobhunter.repository.PermissionRepository;
import vn.hoidanit.jobhunter.repository.RoleRepository;

@Service
public class RoleService {
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    public RoleService(RoleRepository roleRepository, PermissionRepository permissionRepository) {
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
    }

    public Role fetchById(long id) {
        Optional<Role> OpRole = this.roleRepository.findById(id);
        return OpRole.orElse(null);
    }

    public boolean isNameExist(String name) {
        Role role = this.roleRepository.findByName(name);
        if (role != null) {
            return true;
        }
        return false;
    }

    public Role create(Role role) {
        if (role.getPermissions() != null) {
            List<Permission> permissions = this.permissionRepository.findByIdIn(
                    role.getPermissions()
                            .stream().map(r -> r.getId())
                            .collect(Collectors.toList()));
            role.setPermissions(permissions);
        }
        return this.roleRepository.save(role);
    }

    public ResultPaginateDTO fetchAllRole(Specification<Role> spec, Pageable pageable) {
        Page<Role> pageRole = this.roleRepository.findAll(spec, pageable);
        ResultPaginateDTO rsp = new ResultPaginateDTO();
        ResultPaginateDTO.Meta meta = new ResultPaginateDTO.Meta();
        meta.setCurrent(pageRole.getNumber() + 1);
        meta.setPageSize(pageRole.getSize());
        meta.setTotalPages(pageRole.getTotalPages());
        meta.setTotalIteams(pageRole.getTotalElements());
        rsp.setMeta(meta);
        rsp.setResult(pageRole.getContent());
        return rsp;
    }

    public Role update(Role roleDB, Role role) {
        if (role.getPermissions() != null) {
            List<Permission> permissions = this.permissionRepository.findByIdIn(
                    role.getPermissions()
                            .stream().map(r -> r.getId())
                            .collect(Collectors.toList()));
            roleDB.setPermissions(permissions);
        }
        roleDB.setName(role.getName());
        roleDB.setDescription(role.getDescription());
        roleDB.setActive(role.isActive());
        return this.roleRepository.save(roleDB);
    }

    public void delete(Role role) {
        this.roleRepository.delete(role);
    }
}
