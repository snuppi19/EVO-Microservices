package com.mtran.mvc.presentation.controller;

import com.mtran.mvc.application.dto.response.UserDTO;
import com.mtran.mvc.application.dto.response.PermissionDTO;
import com.mtran.mvc.application.dto.response.RoleDTO;
import com.mtran.mvc.domain.Permission;
import com.mtran.mvc.domain.Role;
import com.mtran.mvc.domain.User;
import com.mtran.mvc.domain.repository.*;
import com.mtran.mvc.infrastructure.persistance.entity.Permission.PermissionEntity;
import com.mtran.mvc.infrastructure.persistance.entity.Role.RoleEntity;
import com.mtran.mvc.infrastructure.persistance.entity.UserEntity;
import com.mtran.mvc.infrastructure.persistance.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    // class này là từ bên storage gọi sang để lấy được danh sách và phân quyền , do dài quá nên em để thẳng repository
    // xử lý luôn vì nó k phức tạp + nhiều class quá rồi ạ
    private final UserDomainRepository userDomainRepository;
    private final UserRoleDomainRepository userRoleDomainRepository;
    private final RoleDomainRepository roleDomainRepository;
    private final RolePermissionDomainRepository rolePermissionDomainRepository;
    private final PermissionDomainRepository permissionDomainRepository;

    @GetMapping("/{keycloakId}")
    public UserDTO getUser(@PathVariable String keycloakId) {
        User user = userDomainRepository.findByKeycloakId(keycloakId);
        if (user == null) {
            throw new RuntimeException("User not found");
        }
        return UserDTO.builder()
                      .keycloakId(user.getKeycloakId())
                      .id(String.valueOf(user.getId()))
                      .email(user.getEmail())
                      .build();
    }

    @GetMapping("/{keycloakId}/roles")
    public List<RoleDTO> getUserRoles(@PathVariable String keycloakId) {
        User user = userDomainRepository.findByKeycloakId(keycloakId);
        if (user == null) {
            throw new RuntimeException("User not found");
        }
        List<Integer> roleIds = userRoleDomainRepository.findRoleIdsByUserId(user.getId());
        List<Role> roleEntities = roleDomainRepository.findAllById(roleIds);
        return roleEntities.stream().map(role -> {
            RoleDTO roleDto = new RoleDTO();
            roleDto.setRoleId(role.getRoleId());
            roleDto.setRoleName(role.getRoleName());
            List<Integer> permissionIds = rolePermissionDomainRepository.findPermissionIdsByRoleId(role.getRoleId());
            List<Permission> permissionEntities = permissionDomainRepository.findAllById(permissionIds);
            roleDto.setPermissions(permissionEntities.stream()
                    .map(p -> new PermissionDTO(p.getPermissionId(), p.getResourceCode(), p.getScope()))
                    .collect(Collectors.toList()));
            return roleDto;
        }).collect(Collectors.toList());
    }
}