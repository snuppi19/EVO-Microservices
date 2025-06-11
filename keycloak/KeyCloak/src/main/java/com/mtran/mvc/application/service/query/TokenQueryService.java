package com.mtran.mvc.application.service.query;

import org.springframework.stereotype.Service;

@Service
public interface TokenQueryService {
    String getRefreshToken(String userEmail);
    String getAccessToken(String userEmail);
}
