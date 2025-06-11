package com.mtran.mvc.application.service.command.Impl;

import com.mtran.common.support.AppException;
import com.mtran.common.support.ErrorCode;
import com.mtran.mvc.application.service.command.RoleServiceCmd;
import com.mtran.mvc.domain.Permission;
import com.mtran.mvc.domain.Role;
import com.mtran.mvc.domain.RolePermission;
import com.mtran.mvc.domain.UserRole;
import com.mtran.mvc.domain.command.SaveRolePermissionCmd;
import com.mtran.mvc.domain.repository.PermissionDomainRepository;
import com.mtran.mvc.domain.repository.RoleDomainRepository;
import com.mtran.mvc.domain.repository.RolePermissionDomainRepository;
import com.mtran.mvc.domain.repository.UserRoleDomainRepository;
import com.mtran.mvc.infrastructure.mapper.DomainMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoleServiceCmdImpl implements RoleServiceCmd {
    private final RoleDomainRepository roleDomainRepository;
    private final PermissionDomainRepository permissionDomainRepository;
    private final UserRoleDomainRepository userRoleDomainRepository;
    private final RolePermissionDomainRepository rolePermissionDomainRepository;
    private final DomainMapper domainMapper;

    @Override
    public Role save(Role role) {
        return roleDomainRepository.save(domainMapper.toEntityRole(role));
    }

    @Override
    public ResponseEntity<?> update(Integer id, Role updatedRole) {
        Role role=roleDomainRepository.findById(id);
        if (role != null) {
            role.setRoleName(updatedRole.getRoleName());
        }else{
            throw new AppException(ErrorCode.ROLE_NOT_FOUND);
        }
        roleDomainRepository.save(domainMapper.toEntityRole(role));
        return ResponseEntity.ok().build();
    }

    @Override
    public void deleteById(Integer id) {
       roleDomainRepository.deleteById(id);
    }

    @Override
    public List<Permission> getPermissionsByRoleId(Integer roleId) {
        List<RolePermission> rolePermissionEntities = rolePermissionDomainRepository.findByRoleId(roleId);
        List<Permission> permissionEntityList = rolePermissionEntities.stream()
                //biến rolepermiss thành permission nhờ logic tìm ra perrmission từ permissionId có trong rolepermission
                .map(rolePermissionE -> permissionDomainRepository.findById(rolePermissionE.getRolePermissionId()))
                //lọc từ all permission thành những thằng khác null
                .filter(permission -> permission != null)
                .collect(Collectors.toList());
        return permissionEntityList;
    }

    @Override
    public void addPermissionToRole(Integer roleId, Integer permissionId) {
       SaveRolePermissionCmd rolePermission=new SaveRolePermissionCmd();
        rolePermission.setRoleId(roleId);
        rolePermission.setPermissionId(permissionId);
        RolePermission a=new RolePermission(rolePermission);
        rolePermissionDomainRepository.save(a);
    }

    @Override
    public void removePermissionFromRole(Integer roleId, Integer permissionId) {
        rolePermissionDomainRepository.findByRoleId(roleId).stream()
                // lọc ra những rolepermission có permissionId giống với permissionId truyền vào
                .filter(rp -> rp.getPermissionId().equals(permissionId))
                // lấy ra thằng đầu tiên
                .findFirst()
                //nếu tồn tại thì gọi ra phương thức delete của rolePerrmission để xóa đi
                .ifPresent(rolePermissionDomainRepository::delete);

    }
}
