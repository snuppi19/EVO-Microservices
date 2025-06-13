package com.mtran.mvc.domain.repository;

import com.mtran.mvc.domain.File;

import java.util.Optional;

public interface FileDomainRepository {
    Optional<File> findById(String id);

    File save(File file);
}
