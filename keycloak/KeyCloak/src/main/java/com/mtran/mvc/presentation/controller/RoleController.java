package com.mtran.mvc.presentation.controller;

import com.mtran.mvc.application.service.command.RoleServiceCmd;
import com.mtran.mvc.application.service.query.RoleServiceQuery;
import com.mtran.mvc.domain.Permission;
import com.mtran.mvc.domain.Role;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/roles")
@Tag(name = "Roles Controller")
public class RoleController {
    private final RoleServiceCmd roleServiceCmd;
    private final RoleServiceQuery roleServiceQuery;

    @Operation(summary = "Lấy danh sách tất cả vai trò", description = "API cho phép ADMIN lấy danh sách toàn bộ vai trò (roles) trong hệ thống")
    @PreAuthorize("hasPermission('role', 'view')")
    @GetMapping
    public ResponseEntity<List<Role>> getAllRoles() {
        return ResponseEntity.ok(roleServiceQuery.findAll());
    }

    @Operation(summary = "Lấy thông tin vai trò theo ID", description = "API cho phép ADMIN truy xuất thông tin chi tiết của một vai trò theo ID")
    @PreAuthorize("hasPermission('role', 'view')")
    @GetMapping("/{id}")
    public ResponseEntity<Role> getRoleById(@PathVariable Integer id) {
        return roleServiceQuery.findById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new RuntimeException("Role not found with id: " + id));
    }

    @Operation(summary = "Tạo vai trò mới", description = "API cho phép ADMIN tạo mới một vai trò trong hệ thống")
    @PreAuthorize("hasPermission('role', 'create')")
    @PostMapping
    public ResponseEntity<Role> createRole(@RequestBody Role roleEntity) {
        return ResponseEntity.ok(roleServiceCmd.save(roleEntity));
    }

    @Operation(summary = "Cập nhật vai trò", description = "API cho phép ADMIN cập nhật thông tin một vai trò cụ thể theo ID")
    @PreAuthorize("hasPermission('role', 'update')")
    @PutMapping("/{id}")
    public ResponseEntity<Role> updateRole(@PathVariable Integer id, @RequestBody Role updatedRoleEntity) {
        roleServiceCmd.update(id, updatedRoleEntity);
        return roleServiceQuery.findById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new RuntimeException("Role not found with id: " + id));
    }

    @Operation(summary = "Xóa vai trò theo ID", description = "API cho phép ADMIN xóa một vai trò ra khỏi hệ thống")
    @PreAuthorize("hasPermission('role', 'delete')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRole(@PathVariable Integer id) {
        if (roleServiceQuery.findById(id).isPresent()) {
            roleServiceCmd.deleteById(id);
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Lấy danh sách quyền của vai trò", description = "API cho phép ADMIN lấy danh sách tất cả quyền gán cho một vai trò cụ thể")
    @PreAuthorize("hasPermission('role', 'view')")
    @GetMapping("/{roleId}/permissions")
    public ResponseEntity<List<Permission>> getPermissionsByRoleId(@PathVariable Integer roleId) {
        return ResponseEntity.ok(roleServiceQuery.getPermissionsByRoleId(roleId));
    }

    @Operation(summary = "Thêm quyền cho vai trò", description = "API cho phép ADMIN thêm một quyền cụ thể vào vai trò")
    @PreAuthorize("hasPermission('role', 'update')")
    @PostMapping("/{roleId}/permissions/{permissionId}")
    public ResponseEntity<Void> addPermissionToRole(@PathVariable Integer roleId, @PathVariable Integer permissionId) {
        roleServiceCmd.addPermissionToRole(roleId, permissionId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Xóa quyền khỏi vai trò", description = "API cho phép ADMIN xóa một quyền đã gán khỏi vai trò")
    @PreAuthorize("hasPermission('role', 'delete')")
    @DeleteMapping("/{roleId}/permissions/{permissionId}")
    public ResponseEntity<Void> removePermissionFromRole(@PathVariable Integer roleId, @PathVariable Integer permissionId) {
        roleServiceCmd.removePermissionFromRole(roleId, permissionId);
        return ResponseEntity.ok().build();
    }
}