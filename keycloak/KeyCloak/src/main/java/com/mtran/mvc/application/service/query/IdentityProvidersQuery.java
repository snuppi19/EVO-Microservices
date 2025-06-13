package com.mtran.mvc.application.service.query;

import com.mtran.mvc.application.dto.request.PagingRequest;
import com.mtran.mvc.application.dto.response.TokenExchangeResponse;
import com.mtran.mvc.application.dto.response.TokenResponse;
import com.mtran.mvc.application.dto.response.UserResponse;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
public interface IdentityProvidersQuery {
    String getLoginUrl();
    TokenResponse getTokensAfterLogin(String email);
    UserResponse getUserProfile(String email);
}
