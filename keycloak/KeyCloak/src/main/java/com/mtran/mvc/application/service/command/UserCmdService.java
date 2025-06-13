package com.mtran.mvc.application.service.command;

import com.mtran.mvc.application.dto.request.*;
import com.mtran.mvc.application.dto.response.TokenExchangeResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public interface UserCmdService {
    void register(RegisterRequest request);
    TokenExchangeResponse handleCallback(String code);
    void login(LoginRequest loginRequest);
    TokenExchangeResponse refresh(RefreshRequest refreshRequestKeyCloak);
    void logout(LogoutRequest request);
    ResponseEntity<?> changePassword(ChangePasswordRequest changePasswordRequest);
    ResponseEntity<?> softDelete(DeleteRequest deleteRequest);
    ResponseEntity<?> changeActiveStatus(ChangeActiveStatusRequest changeActiveStatusRequest);
    void assignRoleToUser(Integer userId, Integer roleId);
    void removeRoleFromUser(Integer userId, Integer roleId);
}
