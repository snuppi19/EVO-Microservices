package com.mtran.mvc.controller;

import com.mtran.mvc.dto.UserDTO;
import com.mtran.mvc.dto.response.PermissionDto;
import com.mtran.mvc.dto.response.RoleDto;
import com.mtran.mvc.entity.Permission.Permission;
import com.mtran.mvc.entity.Role.Role;
import com.mtran.mvc.entity.User;
import com.mtran.mvc.repository.*;
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
    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final RoleRepository roleRepository;
    private final RolePermissionRepository rolePermissionRepository;
    private final PermissionRepository permissionRepository;

    @GetMapping("/{keycloakId}")
    public UserDTO getUser(@PathVariable String keycloakId) {
        User user = userRepository.findByKeycloakId(keycloakId);
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
    public List<RoleDto> getUserRoles(@PathVariable String keycloakId) {
        User user = userRepository.findByKeycloakId(keycloakId);
        if (user == null) {
            throw new RuntimeException("User not found");
        }
        List<Integer> roleIds = userRoleRepository.findRoleIdsByUserId(user.getId());
        List<Role> roles = roleRepository.findAllById(roleIds);
        return roles.stream().map(role -> {
            RoleDto roleDto = new RoleDto();
            roleDto.setRoleId(role.getRoleId());
            roleDto.setRoleName(role.getRoleName());
            List<Integer> permissionIds = rolePermissionRepository.findPermissionIdsByRoleId(role.getRoleId());
            List<Permission> permissions = permissionRepository.findAllById(permissionIds);
            roleDto.setPermissions(permissions.stream()
                    .map(p -> new PermissionDto(p.getPermissionId(), p.getResourceCode(), p.getScope()))
                    .collect(Collectors.toList()));
            return roleDto;
        }).collect(Collectors.toList());
    }
}