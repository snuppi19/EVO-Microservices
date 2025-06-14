package com.mtran.mvc.presentation.controller;

import com.mtran.mvc.application.service.command.PermissionServiceCmd;
import com.mtran.mvc.application.service.query.PermissionServiceQuery;
import com.mtran.mvc.domain.Permission;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/permissions")
@Tag(name = "Permission Controller")
public class PermissionController {
    private final PermissionServiceCmd permissionServiceCmd;
    private final PermissionServiceQuery permissionServiceQuery;

    @Operation(summary = "Lấy danh sách tất cả quyền", description = "API dành cho ADMIN để truy xuất danh sách toàn bộ quyền (permissions)")
    @PreAuthorize("hasPermission('permission', 'view')")
    @GetMapping
    public ResponseEntity<List<Permission>> getAllPermissions() {
        return ResponseEntity.ok(permissionServiceQuery.findAll());
    }

    @Operation(summary = "Lấy thông tin quyền theo ID", description = "API dành cho ADMIN để lấy thông tin chi tiết của quyền theo ID")
    @PreAuthorize("hasPermission('permission', 'view')")
    @GetMapping("/{id}")
    public ResponseEntity<Permission> getPermissionById(@PathVariable Integer id) {
        return permissionServiceQuery.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Tạo mới quyền", description = "API cho phép ADMIN tạo một quyền mới trong hệ thống")
    @PreAuthorize("hasPermission('permission', 'create')")
    @PostMapping
    public ResponseEntity<Permission> createPermission(@RequestBody Permission permission) {
        return ResponseEntity.ok(permissionServiceCmd.save(permission));
    }

    @Operation(summary = "Cập nhật thông tin quyền", description = "API cho phép ADMIN cập nhật thông tin của quyền dựa trên ID")
    @PreAuthorize("hasPermission('permission', 'update')")
    @PutMapping("/{id}")
    public ResponseEntity<Permission> updatePermission(@PathVariable Integer id, @RequestBody Permission permissionEntity) {
        return permissionServiceQuery.findById(id).map(existingPermission -> {
            existingPermission.setResourceCode(permissionEntity.getResourceCode());
            existingPermission.setScope(permissionEntity.getScope());
            return ResponseEntity.ok(permissionServiceCmd.save(existingPermission));
        }).orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Xóa quyền theo ID", description = "API cho phép ADMIN xóa quyền khỏi hệ thống nếu quyền tồn tại")
    @PreAuthorize("hasPermission('permission', 'delete')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePermission(@PathVariable Integer id) {
        if (permissionServiceQuery.findById(id).isPresent()) {
            permissionServiceCmd.deleteById(id);
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}