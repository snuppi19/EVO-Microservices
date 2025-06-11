package com.mtran.mvc.application.service.query;

import com.mtran.mvc.application.dto.request.*;
import com.mtran.mvc.application.dto.response.TokenExchangeResponse;
import com.mtran.mvc.application.dto.response.TokenResponse;
import com.mtran.mvc.application.dto.response.UserResponse;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;

public interface UserQueryService {
    Page<UserResponse> getAllProfiles(PagingRequest pagingRequest);
    String getLoginUrl();
    TokenResponse getTokensAfterLogin(String email);
    UserResponse getUserProfileById(Integer id);
}
