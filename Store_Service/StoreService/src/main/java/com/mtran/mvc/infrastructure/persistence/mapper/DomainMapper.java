package com.mtran.mvc.infrastructure.persistence.mapper;

import com.mtran.mvc.domain.File;
import com.mtran.mvc.infrastructure.persistence.entity.FileEntity;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

@Component
@Mapper(componentModel = "spring")
public interface DomainMapper {

   File toDomain(FileEntity entity);
   FileEntity toEntity(File file);
}
