package com.mtran.mvc.infrastructure.domain_repository;

import com.mtran.mvc.domain.File;
import com.mtran.mvc.infrastructure.persistence.mapper.DomainMapper;
import com.mtran.mvc.domain.repository.FileDomainRepository;
import com.mtran.mvc.infrastructure.persistence.entity.FileEntity;
import com.mtran.mvc.infrastructure.persistence.repository.FileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class FileDomainRepositoryImpl implements FileDomainRepository {
    private final FileRepository fileRepository;
    private final DomainMapper domainMapper;

    @Override
    public Optional<File> findById(String id) {
        FileEntity entity = fileRepository.findById(id).orElse(null);
        return Optional.ofNullable(domainMapper.toDomain(entity));
        // Đối tượng được truyền vào không phải `null`, nó bọc đối tượng này vào `Optional chứa File domain`.
        // Ngược lại, nếu đối tượng là `null`, nó trả về `Optional.empty()`.
    }

    @Override
    public File save(File file) {
        FileEntity entity = domainMapper.toEntity(file);
        return domainMapper.toDomain(fileRepository.save(entity));
    }
}
