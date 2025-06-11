package com.mtran.mvc.application.service.command.Impl;

import com.mtran.common.support.AppException;
import com.mtran.common.support.ErrorCode;
import com.mtran.mvc.application.dto.identity.Credential;
import com.mtran.mvc.application.dto.mapper.UserMapperKeycloak;
import com.mtran.mvc.application.dto.request.*;
import com.mtran.mvc.application.dto.response.TokenExchangeResponse;

import com.mtran.mvc.application.mapper.RequestCmdMapper;
import com.mtran.mvc.application.service.command.IdentityProvidersCmd;
import com.mtran.mvc.application.service.command.TokenCommandService;
import com.mtran.mvc.application.service.query.TokenQueryService;
import com.mtran.mvc.domain.Role;
import com.mtran.mvc.domain.User;
import com.mtran.mvc.domain.UserRole;
import com.mtran.mvc.domain.command.*;
import com.mtran.mvc.domain.repository.RoleDomainRepository;
import com.mtran.mvc.domain.repository.UserDomainRepository;
import com.mtran.mvc.domain.repository.UserRoleDomainRepository;
import com.mtran.mvc.infrastructure.mapper.DomainMapper;
import com.mtran.mvc.infrastructure.persistance.entity.KeycloakProperties;
import com.mtran.mvc.infrastructure.persistance.entity.UserEntity;
import com.mtran.mvc.infrastructure.persistance.repository.IdentityClient;
import feign.FeignException;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Service
@RequiredArgsConstructor
public class KeycloakIdentityProviderCmdImpl implements IdentityProvidersCmd {
    private final KeycloakProperties keycloakProperties;
    private final IdentityClient identityClient;
    private final UserDomainRepository userDomainRepository;
    private final RoleDomainRepository roleDomainRepository;
    private final UserRoleDomainRepository userRoleDomainRepository;
    private final RequestCmdMapper requestCmdMapper;
    private final TokenCommandService tokenCommandService;
    private final TokenQueryService tokenQueryService;


    @Override
    public void register(RegisterRequest request) {
        SaveUserCmd userCmd = requestCmdMapper.toUserCmd(request);
        try {
            // Lấy token admin để tạo user trên Keycloak
            TokenExchangeResponse adminToken = identityClient.exchangeToken(TokenExchangeParamRequest.builder()
                    .grant_type("client_credentials")
                    .scope("openid")
                    .client_id(keycloakProperties.getClientId())
                    .client_secret(keycloakProperties.getClientSecret())
                    .build());

            // Tạo user trên Keycloak
            ResponseEntity<?> creationResponse = identityClient.createUser(
                    "Bearer " + adminToken.getAccessToken(),
                    UserCreateParamRequest.builder()
                            .firstName(request.getName())
                            .email(request.getEmail())
                            .enabled(true)
                            .emailVerified(false)
                            .credentials(java.util.List.of(Credential.builder()
                                    .type("password")
                                    .temporary(false)
                                    .value(request.getPassword())
                                    .build()))
                            .build());

            // Lấy Keycloak ID từ response
            String keycloakId = extractUserId(creationResponse);
            User user = new User(userCmd);
            user.setKeycloakId(keycloakId);
            user.setPasswordSynced(true);
            PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(15);
            user.setPassword(passwordEncoder.encode(request.getPassword()));
            userDomainRepository.save(user);
            UserRole userRole = new UserRole();
            userRole.setUserId(user.getId());
            Role role = roleDomainRepository.findByRoleNameIgnoreCase("USER");
            userRole.setRoleId(role.getRoleId());
            userRoleDomainRepository.save(userRole);
        } catch (Exception e) {
            throw new AppException(ErrorCode.REGISTER_FAILD);
        }
    }

    private String extractUserId(ResponseEntity<?> response) {
        String location = response.getHeaders().get("Location").get(0);
        String[] split = location.split("/");
        return split[split.length - 1];
    }


    @Override
    public TokenExchangeResponse handleCallback(String code) {
        try {
            TokenExchangeResponse token = identityClient.exchangeToken(TokenExchangeParamRequest.builder()
                    .grant_type("authorization_code")
                    .client_id(keycloakProperties.getClientId())
                    .client_secret(keycloakProperties.getClientSecret())
                    .code(code)
                    .redirect_uri(keycloakProperties.getRedirectUri())
                    .scope("openid")
                    .build());

            // Lưu refresh token vào Redis
            String email = extractEmailFromToken(token.getAccessToken());
            tokenCommandService.saveRefreshToken(email, token.getRefreshToken());
            tokenCommandService.saveAccessToken(email, token.getAccessToken());
            return token;
        } catch (Exception e) {
            throw new AppException(ErrorCode.CANT_CALLBACK);
        }
    }

    private String extractEmailFromToken(String accessToken) throws Exception {
        String publicKeyPEM = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAzVVEQFu4gvR5gbC3QFwEeUp4NzwyoiSHVtwR5H5eMA59dJ0r1AYVhd6aq3mHmCS13eyg18Xmmp0b2PNvSPyr2M13IFfXuMe+0FM6uLuZcB4xxj9IAPooOSN9cmT9Xr0hx54fa0mK2JJ6JDSyvraLUX2YdrIDOkYydVUx0fUgdnvEoATEjdTbd4blBR0iu07ncTYPHrL14OGt7nQcl65Gv88jMSj60ugqVIip9yc6qBYDxjEaI4MZdVDemycOTn9mM2i1K9zR7Ua+lJULByzhNrSriGmoMHyTplIvYL9iq8oZ8bNcbNW8SDRWIL6IGC2kufYAj3b8Ti4+pCC1a4qIFQIDAQAB";
        PublicKey publicKey = getPublicKeyFromPEM(publicKeyPEM);
        return Jwts.parser()
                .setSigningKey(publicKey)
                .parseClaimsJws(accessToken)
                .getBody()
                .get("email", String.class);
    }

    public PublicKey getPublicKeyFromPEM(String pem) throws Exception {
        String publicKeyPEM = pem
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replaceAll("\\n", "")
                .replaceAll("\\s", "");
        byte[] decodedKey = Base64.getDecoder().decode(publicKeyPEM);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(decodedKey);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePublic(keySpec);
    }

    @Override
    public void login(LoginRequest loginRequest) {
        UserLoginCmd userLoginCmd = requestCmdMapper.toUserLoginCmd(loginRequest);
        String email = userLoginCmd.getEmail();
        String password = userLoginCmd.getPassword();
        if (email == null || email.isBlank()) {
            throw new AppException(ErrorCode.EMAIL_INVALID);
        }
        if (password == null || password.isBlank()) {
            throw new AppException(ErrorCode.PASSWORD_INVALID);
        }
        User user = userDomainRepository.findByEmail(email);
        if (user == null) {
            throw new AppException(ErrorCode.USER_NOT_FOUND);
        }
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(15);
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new AppException(ErrorCode.PASSWORD_INVALID);
        }
    }


    @Override
    public TokenExchangeResponse refresh(RefreshRequest refreshRequestKeyCloak) {
        RefreshCmd refreshCmd = requestCmdMapper.toRefreshCmd(refreshRequestKeyCloak);
        String storedRefreshToken = tokenQueryService.getRefreshToken(refreshCmd.getEmail());
        String storedAccessToken = tokenQueryService.getAccessToken(refreshCmd.getEmail());
        if (storedRefreshToken == null) {
            throw new AppException(ErrorCode.REFRESH_TOKEN_NOT_FOUND);
        }
        try {
            TokenExchangeResponse tokenResponse = identityClient.exchangeToken(TokenExchangeParamRequest.builder()
                    .grant_type("refresh_token")
                    .client_id(keycloakProperties.getClientId())
                    .client_secret(keycloakProperties.getClientSecret())
                    .refresh_token(storedRefreshToken)
                    .scope("openid")
                    .build());
            if (storedAccessToken != null) {
                identityClient.logout("Bearer " + storedAccessToken,
                        TokenExchangeParamRequest.builder()
                                .client_id(keycloakProperties.getClientId())
                                .client_secret(keycloakProperties.getClientSecret())
                                .refresh_token(storedRefreshToken)
                                .build());
            }
            //xoa access token va refresh token cu di
            tokenCommandService.saveAccessToken(refreshRequestKeyCloak.getEmail(), null);
            tokenCommandService.saveRefreshToken(refreshRequestKeyCloak.getEmail(), null);
            //luu access token moi va refresh token moi
            tokenCommandService.saveRefreshToken(refreshRequestKeyCloak.getEmail(), tokenResponse.getRefreshToken());
            tokenCommandService.saveAccessToken(refreshRequestKeyCloak.getEmail(), tokenResponse.getAccessToken());
            return tokenResponse;
        } catch (FeignException e) {
            throw new AppException(ErrorCode.TOKEN_GENERATION_FAILED);
        }
    }


    @Override
    public void logout(LogoutRequest request) {
        LogoutCmd logoutCmd = requestCmdMapper.toLogoutCmd(request);
        try {
            TokenExchangeResponse adminToken = identityClient.exchangeToken(TokenExchangeParamRequest.builder()
                    .grant_type("client_credentials")
                    .scope("openid")
                    .client_id(keycloakProperties.getClientId())
                    .client_secret(keycloakProperties.getClientSecret())
                    .build());
            identityClient.logout("Bearer " + adminToken.getAccessToken(),
                    TokenExchangeParamRequest.builder()
                            .client_id(keycloakProperties.getClientId())
                            .client_secret(keycloakProperties.getClientSecret())
                            .refresh_token(logoutCmd.getRefreshToken())
                            .build());
            String email = extractEmailFromToken(logoutCmd.getToken());
            tokenCommandService.saveRefreshToken(email, null);
            tokenCommandService.saveAccessToken(email, null);
        } catch (Exception e) {
            throw new AppException(ErrorCode.LOGOUT_FAILED);
        }
    }

    @Override
    public ResponseEntity<?> changePassword(ChangePasswordRequest changePasswordRequest) {
        ChangePassCmd changePassCmd = requestCmdMapper.toChangePassCmd(changePasswordRequest);
        try {
            String email = changePassCmd.getUser().getEmail();
            String oldPassword = changePassCmd.getUser().getPassword();
            String newPassword = changePassCmd.getNewPassword();
            User user = userDomainRepository.findByEmail(email);
            if (user == null) {
                throw new AppException(ErrorCode.USER_NOT_FOUND);
            }
            PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(15);
            if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
                throw new AppException(ErrorCode.PASSWORD_INVALID);
            }
            TokenExchangeResponse adminToken = identityClient.exchangeToken(TokenExchangeParamRequest.builder()
                    .grant_type("client_credentials")
                    .scope("openid")
                    .client_id(keycloakProperties.getClientId())
                    .client_secret(keycloakProperties.getClientSecret())
                    .build());
            ResponseEntity<?> response = identityClient.resetPassword(
                    "Bearer " + adminToken.getAccessToken(),
                    keycloakProperties.getRealm(),
                    user.getKeycloakId(),
                    Credential.builder()
                            .type("password")
                            .temporary(false)
                            .value(newPassword)
                            .build()
            );
            user.setPassword(passwordEncoder.encode(newPassword));
            userDomainRepository.save(user);
            String accessToken = tokenQueryService.getAccessToken(email);
            String refreshToken = tokenQueryService.getRefreshToken(email);
            LogoutRequest logoutRequest = new LogoutRequest();
            if (accessToken != null && refreshToken != null) {
                logoutRequest.setToken(accessToken);
                logoutRequest.setRefreshToken(refreshToken);
                logoutRequest.setEmail(email);
                logout(logoutRequest);
            }
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            throw new AppException(ErrorCode.CHANGE_PASSWORD_FAILED);
        }
    }

    @Override
    public ResponseEntity<?> softDelete(DeleteRequest deleteRequest) {
        DeleteCmd deleteCmd = requestCmdMapper.toDeleteCmd(deleteRequest);
        try {
            String email = deleteCmd.getUser().getEmail();
            User user = userDomainRepository.findByEmail(email);
            if (user == null) {
                throw new AppException(ErrorCode.USER_NOT_FOUND);
            }
            userDomainRepository.delete(user);
            keycloakBlockUser(email, false);
            return ResponseEntity.ok().body("User soft deleted successfully");
        } catch (Exception e) {
            throw new AppException(ErrorCode.DELETE_USER_FAILED);
        }
    }

    @Override
    public ResponseEntity<?> changeActiveStatus(ChangeActiveStatusRequest changeActiveStatusRequest) {
        ChangeActiveStatusCmd changeActiveStatusCmd = requestCmdMapper.toChangeActiveStatusCmd(changeActiveStatusRequest);
        try {
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
                keycloakBlockUser(email, activeStatus);
            }
            return ResponseEntity.ok().body("User change active status successfully");
        } catch (Exception e) {
            throw new AppException(ErrorCode.CHANGE_ACTIVE_STATUS_FAILED);
        }
    }

    private void keycloakBlockUser(String email, boolean status) {
        User user = userDomainRepository.findByEmail(email);
        TokenExchangeResponse adminToken = identityClient.exchangeToken(TokenExchangeParamRequest.builder()
                .grant_type("client_credentials")
                .scope("openid")
                .client_id(keycloakProperties.getClientId())
                .client_secret(keycloakProperties.getClientSecret())
                .build());

        identityClient.ChangeStatusUser(
                "Bearer " + adminToken.getAccessToken(),
                keycloakProperties.getRealm(),
                user.getKeycloakId(),
                UserUpdateParamRequest.builder()
                        .enabled(status)
                        .build()
        );
        String accessToken = tokenQueryService.getAccessToken(email);
        String refreshToken = tokenQueryService.getRefreshToken(email);
        LogoutRequest logoutRequest = new LogoutRequest();
        if (accessToken != null && refreshToken != null) {
            logoutRequest.setToken(accessToken);
            logoutRequest.setRefreshToken(refreshToken);
            logoutRequest.setEmail(email);
            logout(logoutRequest);
        }
    }
}
