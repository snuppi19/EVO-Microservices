package com.mtran.mvc.application.service.command.Impl;

import com.mtran.common.support.AppException;
import com.mtran.common.support.ErrorCode;
import com.mtran.mvc.application.config.email.EmailSender;
import com.mtran.mvc.application.config.email.OtpService;
import com.mtran.mvc.application.config.utils.jwt.JwtUtil;
import com.mtran.mvc.application.dto.request.*;
import com.mtran.mvc.application.dto.response.TokenExchangeResponse;
import com.mtran.mvc.application.mapper.RequestCmdMapper;
import com.mtran.mvc.application.service.command.IdentityProvidersCmd;
import com.mtran.mvc.application.service.command.TokenCommandService;
import com.mtran.mvc.application.service.command.UserIamCmd;
import com.mtran.mvc.application.service.query.TokenQueryService;
import com.mtran.mvc.domain.User;
import com.mtran.mvc.domain.command.*;
import com.mtran.mvc.domain.repository.UserDomainRepository;
import com.mtran.mvc.infrastructure.persistance.entity.KeycloakProperties;
import com.mtran.mvc.infrastructure.persistance.repository.IdentityClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class SelfIdentityProviderCmdImpl implements IdentityProvidersCmd {
    private final UserDomainRepository userDomainRepository;
    private final JwtUtil jwtUtil;
    private final OtpService otpService;
    private final EmailSender emailSender;
    private final TokenCommandService tokenCommandService;
    private final UserIamCmd userIamCmd;
    private final TokenQueryService tokenQueryService;
    private final IdentityClient identityClient;
    private final KeycloakProperties keycloakProperties;
    private final RequestCmdMapper requestCmdMapper;


    @Override
    public void register(RegisterRequest request) {
        if (userDomainRepository.findByEmail(request.getEmail()) == null) {
            throw new AppException(ErrorCode.EMAIL_EXISTS);
        }
        String otp = otpService.generateOtp(request.getEmail());
        emailSender.sendEmail(request.getEmail(), "Mã xác thực OTP", "Mã OTP của bạn là: " + otp);
    }

    @Override
    public TokenExchangeResponse handleCallback(String code) {
        throw new AppException(ErrorCode.CANT_CALLBACK);
    }

    @Override
    public void login(LoginRequest loginRequest) {
        UserLoginCmd userLoginCmd = requestCmdMapper.toUserLoginCmd(loginRequest);
        User user = userDomainRepository.findByEmail(userLoginCmd.getEmail());
        if (user == null) {
            throw new AppException(ErrorCode.USER_NOT_FOUND);
        }
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(15);
        if (!passwordEncoder.matches(userLoginCmd.getPassword(), user.getPassword()) && !userLoginCmd.getPassword().equals(user.getPassword())) {
            throw new AppException(ErrorCode.PASSWORD_INVALID);
        }
        try {
            String accessToken = jwtUtil.generateToken(user.getEmail());
            String refreshToken = jwtUtil.generateRefreshToken(user.getEmail());
            tokenCommandService.saveAccessToken(user.getEmail(), accessToken);
            tokenCommandService.saveRefreshToken(user.getEmail(), refreshToken);
        } catch (Exception e) {
            throw new AppException(ErrorCode.TOKEN_GENERATION_FAILED);
        }
    }

    @Override
    public TokenExchangeResponse refresh(RefreshRequest refreshRequestKeyCloak) {
        RefreshCmd refreshCmd = requestCmdMapper.toRefreshCmd(refreshRequestKeyCloak);
        String refreshToken = tokenQueryService.getRefreshToken(refreshCmd.getEmail());
        if (refreshToken == null) {
            throw new AppException(ErrorCode.TOKEN_GENERATION_FAILED);
        }
        try {
            String accessTokenNEW = jwtUtil.refreshToken(refreshRequestKeyCloak);
            tokenCommandService.saveRefreshToken(refreshCmd.getEmail(), accessTokenNEW);
            return new TokenExchangeResponse(accessTokenNEW, refreshToken);
        } catch (Exception e) {
            throw new AppException(ErrorCode.TOKEN_GENERATION_FAILED);
        }
    }

    @Override
    public void logout(LogoutRequest request) {
        LogoutCmd logoutCmd = requestCmdMapper.toLogoutCmd(request);
        try {
            String email = jwtUtil.extractEmail(logoutCmd.getRefreshToken());
            tokenCommandService.saveRefreshToken(email, null);
            tokenCommandService.saveAccessToken(email, null);
            jwtUtil.logout(request);
        } catch (Exception e) {
            throw new AppException(ErrorCode.LOGOUT_FAILED);
        }
    }

    @Override
    public ResponseEntity<?> changePassword(ChangePasswordRequest changePasswordRequest) {
        ChangePassCmd changePassCmd = requestCmdMapper.toChangePassCmd(changePasswordRequest);
        String email = changePassCmd.getUser().getEmail();
        String oldPassword = changePassCmd.getUser().getPassword();
        String newPassword = changePassCmd.getNewPassword();
        String token = changePassCmd.getToken();
        String refreshToken = changePassCmd.getRefreshToken();

        userIamCmd.changePassword(email,  oldPassword,newPassword);
        // Luu thoi gian password bi doi
        userIamCmd.updateLastChangePassword(email, LocalDateTime.now());
        if (token != null || refreshToken != null) {
            LogoutRequest logoutRequest = new LogoutRequest();
            logoutRequest.setToken(token);
            logoutRequest.setRefreshToken(refreshToken);
            try {
                jwtUtil.logout(logoutRequest);
            } catch (Exception e) {
                log.info("Token không hợp lệ : {}", e.getMessage());
            }
        }
        return ResponseEntity.ok("User thay đổi mật khẩu thành công !");
    }

    @Override
    public ResponseEntity<?> softDelete(DeleteRequest deleteRequest) {
        DeleteCmd deleteCmd = requestCmdMapper.toDeleteCmd(deleteRequest);
        String email = deleteCmd.getUser().getEmail();
        User user = userDomainRepository.findByEmail(email);
        if (user == null) {
            throw new AppException(ErrorCode.USER_NOT_FOUND);
        }
        userDomainRepository.delete(user);
        blockUSER(email, false);
        return ResponseEntity.ok("User soft delete sucesssfully !");
    }

    @Override
    public ResponseEntity<?> changeActiveStatus(ChangeActiveStatusRequest changeActiveStatusRequest) {
        ChangeActiveStatusCmd changeActiveStatusCmd = requestCmdMapper.toChangeActiveStatusCmd(changeActiveStatusRequest);
        String email = changeActiveStatusCmd.getUser().getEmail();
        boolean activeStatus = changeActiveStatusCmd.getIsActive();
        User user = userDomainRepository.findByEmail(email);
        if (user == null) {
            throw new AppException(ErrorCode.USER_NOT_FOUND);
        }
        if (user.isActive() == activeStatus) {
            return ResponseEntity.ok("User already same status!");
        } else {
            user.setActive(activeStatus);
            userDomainRepository.save(user);
        }
        if (!activeStatus) {
            blockUSER(email, false);
        }
        return ResponseEntity.ok("User change status sucesssfully !");
    }

    private void blockUSER(String email, boolean status) {
        User userEntity = userDomainRepository.findByEmail(email);
        String accessToken = tokenQueryService.getAccessToken(email);
        String refreshToken = tokenQueryService.getRefreshToken(email);
        if (accessToken != null && refreshToken != null) {
            LogoutRequest logoutRequest = new LogoutRequest();
            logoutRequest.setToken(accessToken);
            logoutRequest.setRefreshToken(refreshToken);
            try {
                jwtUtil.logout(logoutRequest);
            } catch (Exception e) {
                throw new AppException(ErrorCode.INVALID_KEY);
            }
        }
        //vo hieu hoa user tren keycloak ngay lap tuc
        TokenExchangeResponse adminToken = identityClient.exchangeToken(TokenExchangeParamRequest.builder()
                .grant_type("client_credentials")
                .scope("openid")
                .client_id(keycloakProperties.getClientId())
                .client_secret(keycloakProperties.getClientSecret())
                .build());

        identityClient.ChangeStatusUser(
                "Bearer " + adminToken.getAccessToken(),
                keycloakProperties.getRealm(),
                userEntity.getKeycloakId(),
                UserUpdateParamRequest.builder()
                        .enabled(status)
                        .build()
        );
    }
}
