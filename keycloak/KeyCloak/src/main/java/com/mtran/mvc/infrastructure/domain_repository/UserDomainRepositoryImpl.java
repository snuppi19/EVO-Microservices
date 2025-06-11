package com.mtran.mvc.infrastructure.domain_repository;

import com.mtran.common.support.AppException;
import com.mtran.common.support.ErrorCode;
import com.mtran.mvc.domain.User;
import com.mtran.mvc.domain.repository.UserDomainRepository;
import com.mtran.mvc.infrastructure.mapper.DomainMapper;
import com.mtran.mvc.infrastructure.persistance.entity.UserEntity;
import com.mtran.mvc.infrastructure.persistance.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UserDomainRepositoryImpl implements UserDomainRepository {
    private final UserRepository userRepository;
    private final DomainMapper domainMapper;
    @Override
    public User save(User user) {
        UserEntity userEntity= domainMapper.toEntityUser(user);
        return domainMapper.toDomainUser(userRepository.save(userEntity));
    }

    @Override
    public User findByKeycloakId(String keycloakId) {
        UserEntity userEntity = userRepository.findByKeycloakId(keycloakId);
        if(userEntity == null) {
            throw new AppException(ErrorCode.USER_NOT_FOUND);
        }
        return domainMapper.toDomainUser(userEntity);
    }

    @Override
    public Page<User> findAll(Pageable pageable) {
        return userRepository.findAll(pageable).map(domainMapper::toDomainUser);
    }

    @Override
    public User findByEmail(String email) {
        return domainMapper.toDomainUser(userRepository.findByEmail(email));
    }

    @Override
    public User findById(int id) {
        return domainMapper.toDomainUser(userRepository.findById(id));
    }

    @Override
    public void delete(User user) {
       userRepository.delete(domainMapper.toEntityUser(user));
    }
}
