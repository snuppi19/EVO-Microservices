package com.mtran.mvc.application.service.command;

import org.springframework.stereotype.Service;

@Service
public interface TokenCommandService {
    void saveRefreshToken(String userEmail, String refreshToken);
    void saveAccessToken(String userEmail, String accessToken);
}
