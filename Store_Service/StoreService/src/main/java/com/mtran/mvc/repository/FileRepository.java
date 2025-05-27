package com.mtran.mvc.repository;


import com.mtran.mvc.entity.File;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FileRepository extends JpaRepository<File, String>, JpaSpecificationExecutor<File> {

}
