package com.mtran.mvc.presentation.controller;

import com.mtran.mvc.application.config.email.OtpService;
import com.mtran.mvc.application.dto.request.*;
import com.mtran.mvc.application.dto.response.TokenExchangeResponse;
import com.mtran.mvc.application.dto.response.TokenResponse;
import com.mtran.mvc.application.dto.response.UserResponse;
import com.mtran.mvc.application.service.command.UserActivityLogCmd;
import com.mtran.mvc.application.service.command.UserCmdService;
import com.mtran.mvc.application.service.command.UserIamCmd;
import com.mtran.mvc.application.service.query.UserQueryService;
import com.mtran.common.support.ActivityType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/home")
@Tag(name = "Home Controller")
public class HomeController {
    private final UserIamCmd userIamCmd;
    private final UserActivityLogCmd userActivityLogCmd;
    private final UserCmdService userCmdService;
    private final UserQueryService userQueryService;
    // là xử lý của email và bé nên không chia CQRS cho service gửi otp
    private final OtpService otpService;

    @Operation(summary = "Đăng ký tài khoản", description = "API tạo người dùng mới và gửi OTP xác thực email")
    @PostMapping("/register")
    ResponseEntity<?> register(@RequestBody RegisterRequest registerRequest, HttpServletRequest httpServletRequest) {
        userCmdService.register(registerRequest);
        userActivityLogCmd.logActivity(registerRequest.getEmail(), ActivityType.REGISTER, "user login",httpServletRequest);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Xác thực OTP", description = "API xác minh OTP sau khi đăng ký. Nếu hợp lệ, tài khoản sẽ được tạo")
    @PostMapping("/verify-otp")
    public ResponseEntity<String> verifyOtp(@RequestBody OtpVerificationRequest request, HttpServletRequest httpServletRequest) {
        if (otpService.verifyOtp(request.getEmail(), request.getOtp()) && request.getIsRegister()) {
            userIamCmd.createUser(request.getUserDTO());
            otpService.deleteOtp(request.getEmail());
            userActivityLogCmd.logActivity(request.getEmail(), ActivityType.VERIFY_OTP, "user verify otp ", httpServletRequest);
            return ResponseEntity.ok("Đăng ký thành công");
        } else {
            return ResponseEntity.badRequest().body("OTP không hợp lệ");
        }
    }

    @Operation(summary = "Đăng nhập",description = "API đăng nhập người dùng. Nếu dùng Keycloak sẽ trả về URL đăng nhập, nếu không thì trả về access token")
    @PostMapping("/login")
    ResponseEntity<?> login(@RequestBody LoginRequest loginRequest, HttpServletRequest httpServletRequest) {
        userCmdService.login(loginRequest);//xác thực password và tài khoản email
        if (userQueryService.getLoginUrl() != null) {
            //nếu có login url là đang sử dụng keycloak đẻ đăng nhập
            userActivityLogCmd.logActivity(loginRequest.getEmail(), ActivityType.LOGIN, "user login with Keycloak", httpServletRequest);
            return ResponseEntity.ok(Map.of("message", "Please login via Keycloak", "loginUrl", userQueryService.getLoginUrl()));
        } else {
            //nếu không dùng keycloak sẽ được trả về cặp token
            TokenResponse tokenResponse = userQueryService.getTokensAfterLogin(loginRequest.getEmail());
            userActivityLogCmd.logActivity(loginRequest.getEmail(), ActivityType.LOGIN, "user login without Keycloak", httpServletRequest);
            return ResponseEntity.ok(tokenResponse);
        }
    }

    @Operation(summary = "Xử lý callback từ Keycloak",description = "API xử lý mã code từ Keycloak để đổi lấy access token")
    @GetMapping("/callback")
    ResponseEntity<?> handleCallback(@RequestParam("code") String code, HttpServletRequest httpServletRequest) {
        TokenExchangeResponse token = userCmdService.handleCallback(code);
        return ResponseEntity.ok(token);
    }

    @Operation(summary = "Làm mới access token",description = "API làm mới access token bằng refresh token từ Keycloak" )
    @PostMapping("/refresh-token")
    String refreshToken(@RequestBody RefreshRequest refreshRequestKeyCloak, HttpServletRequest httpServletRequest) {
        TokenExchangeResponse tokenExchangeResponse = userCmdService.refresh(refreshRequestKeyCloak);
        userActivityLogCmd.logActivity(refreshRequestKeyCloak.getEmail(), ActivityType.REFRESH_TOKEN, "provide new access token ", httpServletRequest);
        return tokenExchangeResponse.getAccessToken();
    }

    @Operation(summary = "Đăng xuất",description = "API đăng xuất người dùng khỏi hệ thống")
    @PostMapping("/logout")
    ResponseEntity<?> logout(@RequestBody LogoutRequest request, HttpServletRequest httpServletRequest) throws Exception {
        userCmdService.logout(request);
        userActivityLogCmd.logActivity(request.getEmail(), ActivityType.LOGOUT, "User Logout", httpServletRequest);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Lấy thông tin người dùng theo ID",description = "API chỉ cho STAFF sử dụng để lấy profile chi tiết của người dùng dựa trên ID")
    @PreAuthorize("hasPermission('user', 'view')")
    @GetMapping("/user-profile/{id}")
    UserResponse getUserProfile(@PathVariable Integer id, HttpServletRequest httpServletRequest) {
        return userQueryService.getUserProfileById(id);
    }

    @Operation(summary = "Lấy danh sách tất cả người dùng",description = "API phân trang danh sách người dùng, chỉ dành cho STAFF")
    @PreAuthorize("hasPermission('user', 'view')")
    @GetMapping("/all-profiles")
    Page<UserResponse> getAllProfiles(  @RequestParam(defaultValue = "0") int page,
                                        @RequestParam(defaultValue = "10") int size,
                                        @RequestParam(defaultValue = "id") String sortBy,
                                        @RequestParam(defaultValue = "false") boolean descending,
                                        HttpServletRequest httpServletRequest) {
        PagingRequest pagingRequest = new PagingRequest();
        pagingRequest.setPage(page);
        pagingRequest.setSize(size);
        pagingRequest.setSortBy(sortBy);
        pagingRequest.setDescending(descending);
        return userQueryService.getAllProfiles(pagingRequest);
    }

    @Operation(summary = "Đặt lại mật khẩu",description = "API cho phép người dùng đổi mật khẩu. Yêu cầu vai trò USER")
    @PreAuthorize("hasPermission('user', 'update')")
    @PutMapping("/reset-password")
    ResponseEntity<?> changePassword(@RequestBody ChangePasswordRequest changePasswordRequest, HttpServletRequest httpServletRequest) {
        userActivityLogCmd.logActivity(changePasswordRequest.getUser().getEmail(), ActivityType.RESET_PASSWORD, "User reset password", httpServletRequest);
        return userCmdService.changePassword(changePasswordRequest);
    }

    @Operation(summary = "Xóa mềm người dùng",description = "API chỉ cho ADMIN sử dụng để đánh dấu xóa tài khoản người dùng")
    @PreAuthorize("hasPermission('user', 'delete')")
    @PutMapping("/soft-delete")
    ResponseEntity<?> softDelete(@RequestBody DeleteRequest deleteRequest, HttpServletRequest httpServletRequest) {
        userActivityLogCmd.logActivity(deleteRequest.getUser().getEmail(), ActivityType.DELETE_ACCOUNT, "Delete account", httpServletRequest);
        return userCmdService.softDelete(deleteRequest);
    }

    @Operation(summary = "Thay đổi trạng thái hoạt động của người dùng",description = "API cho phép STAFF khóa hoặc mở khóa tài khoản người dùng")
    @PreAuthorize("hasPermission('user', 'update')")
    @PutMapping("/change-active-status")
    ResponseEntity<?> changeActiveStatus(@RequestBody ChangeActiveStatusRequest changeActiveStatusRequest, HttpServletRequest httpServletRequest) {
        boolean checkStatus = changeActiveStatusRequest.getIsActive();
        if (checkStatus) {
            userActivityLogCmd.logActivity(changeActiveStatusRequest.getUser().getEmail(), ActivityType.UNLOCK_USER, "Change active status to ACTIVE", httpServletRequest);
        } else {
            userActivityLogCmd.logActivity(changeActiveStatusRequest.getUser().getEmail(), ActivityType.BLOCK_USER, "Change active status to INACTIVE", httpServletRequest);
        }
        return userCmdService.changeActiveStatus(changeActiveStatusRequest);
    }

    @Operation(summary = "Gán vai trò cho người dùng",description = "API cho ADMIN sử dụng để gán role cho người dùng")
    @PreAuthorize("hasPermission('role', 'create')")
    @PostMapping("/assign-role")
    ResponseEntity<?> assignRole(@RequestBody AssignRoleRequest assignRoleRequest, HttpServletRequest httpServletRequest) {
        userCmdService.assignRoleToUser(assignRoleRequest.getUserId(), assignRoleRequest.getRoleId());
        userActivityLogCmd.logActivity(
                SecurityContextHolder.getContext().getAuthentication().getName(),
                ActivityType.ASSIGN_ROLE,
                "Assigned role " + assignRoleRequest.getRoleId() + " to user " + assignRoleRequest.getUserId(),
                httpServletRequest
        );
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Gỡ vai trò khỏi người dùng",description = "API cho ADMIN sử dụng để gỡ role khỏi người dùng")
    @PreAuthorize("hasPermission('role', 'delete')")
    @DeleteMapping("/remove-role")
    ResponseEntity<?> removeRole(@RequestBody AssignRoleRequest assignRoleRequest, HttpServletRequest httpServletRequest) {
        userCmdService.removeRoleFromUser(assignRoleRequest.getUserId(), assignRoleRequest.getRoleId());
        userActivityLogCmd.logActivity(
                SecurityContextHolder.getContext().getAuthentication().getName(),
                ActivityType.REMOVE_ROLE,
                "Remove role " + assignRoleRequest.getRoleId() + " From user " + assignRoleRequest.getUserId(),
                httpServletRequest
        );
        return ResponseEntity.ok().build();
    }
}
