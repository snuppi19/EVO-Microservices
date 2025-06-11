package com.mtran.mvc.infrastructure.config.sync_keycloak_iam;

import com.mtran.mvc.application.dto.identity.Credential;
import com.mtran.mvc.application.dto.request.TokenExchangeParamRequest;
import com.mtran.mvc.application.dto.response.TokenExchangeResponse;
import com.mtran.mvc.application.dto.request.UserCreateParamRequest;
import com.mtran.mvc.infrastructure.persistance.entity.KeycloakProperties;
import com.mtran.mvc.infrastructure.persistance.entity.UserEntity;
import com.mtran.mvc.infrastructure.persistance.repository.IdentityClient;
import com.mtran.mvc.infrastructure.persistance.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Data
@RequiredArgsConstructor
public class UserSyncService {
    private final UserRepository userRepository;
    private final IdentityClient identityClient;
    private final KeycloakProperties keycloakProperties;

    @PostConstruct
    public void syncUsers() {
        //tự động kiểm tra và tạo ngươif dùng trên keycloak dựa vào database
        if (keycloakProperties.isEnabled()) {
            List<UserEntity> userEntities = userRepository.findAll();
            TokenExchangeResponse adminToken = identityClient.exchangeToken(TokenExchangeParamRequest.builder()
                    .grant_type("client_credentials")
                    .scope("openid")
                    .client_id(keycloakProperties.getClientId())
                    .client_secret(keycloakProperties.getClientSecret())
                    .build());

            for (UserEntity userEntity : userEntities) {
                //nếu chưa có keycloak id
                if (userEntity.getKeycloakId() == null) {
                    String keycloakId = extractUserId(identityClient.createUser("Bearer " + adminToken.getAccessToken(),
                            UserCreateParamRequest.builder()
                                    .firstName(userEntity.getName())
                                    .email(userEntity.getEmail())
                                    .enabled(true)
                                    .emailVerified(false)
                                    .credentials(List.of(Credential.builder()
                                            .type("password")
                                            .temporary(false)
                                            .value(userEntity.getPassword())
                                            .build()))
                                    .build()));
                    userEntity.setKeycloakId(keycloakId);
                    //BCRYPT
                    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(15);
                    userEntity.setPassword(passwordEncoder.encode(userEntity.getPassword()));
                    userRepository.save(userEntity);
                }

                if (!userEntity.isPasswordSynced()) {
                    identityClient.resetPassword(
                            "Bearer " + adminToken.getAccessToken(),
                            keycloakProperties.getRealm(),
                            userEntity.getKeycloakId(),
                            Credential.builder()
                                    .type("password")
                                    .temporary(false)
                                    .value(userEntity.getPassword())
                                    .build()
                    );
                    userEntity.setPasswordSynced(true);
                    userRepository.save(userEntity);
                }
            }
        }
    }

    private String extractUserId(ResponseEntity<?> response) {
        String location = response.getHeaders().get("Location").get(0);
        String[] split = location.split("/");
        return split[split.length - 1];
    }
}