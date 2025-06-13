package com.mtran.mvc.application.config.webconfig;

import com.mtran.common.support.AppException;
import com.mtran.common.support.ErrorCode;
import com.mtran.mvc.application.service.query.Impl.RoleServiceQueryImpl;
import com.mtran.mvc.domain.User;
import com.mtran.mvc.domain.repository.UserDomainRepository;
import com.mtran.mvc.infrastructure.mapper.DomainMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomUserDetailService implements UserDetailsService {
    private final UserDomainRepository userDomainRepository;
    private final RoleServiceQueryImpl roleServiceQuery;
    private final DomainMapper domainMapper;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user= userDomainRepository.findByEmail(email);
        if (user == null) {
            throw new AppException(ErrorCode.USER_NOT_FOUND);
        }
        List<String> roles=roleServiceQuery.getRolesByUserId(user.getId());
        if(roles==null){
            throw new AppException(ErrorCode.USER_NOT_HAVE_ROLES);
        }
        return new CustomUserDetails(domainMapper.toEntityUser(user),roles);
    }
}
