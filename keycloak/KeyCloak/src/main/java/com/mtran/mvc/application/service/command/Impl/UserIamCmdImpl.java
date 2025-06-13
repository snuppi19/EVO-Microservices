package com.mtran.mvc.application.service.command.Impl;

import com.mtran.common.support.AppException;
import com.mtran.common.support.ErrorCode;
import com.mtran.mvc.application.dto.mapper.UserMapper;
import com.mtran.mvc.application.dto.response.UserDTO;
import com.mtran.mvc.application.service.command.UserIamCmd;
import com.mtran.mvc.domain.Role;
import com.mtran.mvc.domain.User;
import com.mtran.mvc.domain.UserRole;
import com.mtran.mvc.domain.repository.RoleDomainRepository;
import com.mtran.mvc.domain.repository.UserDomainRepository;
import com.mtran.mvc.domain.repository.UserRoleDomainRepository;
import com.mtran.mvc.infrastructure.mapper.DomainMapper;
import com.mtran.mvc.infrastructure.persistance.entity.Role.RoleEntity;
import com.mtran.mvc.infrastructure.persistance.entity.Role.UserRoleEntity;
import com.mtran.mvc.infrastructure.persistance.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserIamCmdImpl implements UserIamCmd {
    private final UserDomainRepository userDomainRepository;
    private final RoleDomainRepository roleDomainRepository;
    private final UserRoleDomainRepository userRoleDomainRepository;
    private final UserMapper userMapper;
    private final DomainMapper domainMapper;


    @Override
    public void createUser(UserDTO user) {
        if (userDomainRepository.findByEmail(user.getEmail()) != null) {
            throw new RuntimeException("user name existed with : " + user.getEmail());
        }
        UserEntity userEntity = userMapper.toUserEntity(user);
        User userDomain = domainMapper.toDomainUser(userEntity);
        userDomain.setPasswordSynced(false);
        userDomainRepository.save(userDomain);
        UserRole userRole = new UserRole();
        userRole.setUserId(userEntity.getId());
        Role role = roleDomainRepository.findByRoleNameIgnoreCase("USER");
        userRole.setRoleId(role.getRoleId());
        userRoleDomainRepository.save(userRole);
    }

    @Override
    public void updateLastChangePassword(String email, LocalDateTime lastChangePassword) {
        User user = userDomainRepository.findByEmail(email);
        user.setLastChangePassword(lastChangePassword);
        userDomainRepository.save(user);
    }

    @Override
    public void changePassword(String email, String oldPassword, String newPassword) {
        User user = userDomainRepository.findByEmail(email);
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(15);
        if (!passwordEncoder.matches(oldPassword, user.getPassword()) && !oldPassword.equals(user.getPassword())) {
            throw new AppException(ErrorCode.PASSWORD_INVALID);
        }
        user.setPassword(newPassword);
        userDomainRepository.save(user);
    }
}
