package com.mtran.mvc.application.dto.mapper;

import com.mtran.mvc.application.dto.response.FileResponse;
import com.mtran.mvc.domain.File;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

@Component
@Mapper(componentModel = "spring")
public interface FileToResponse {
    FileResponse toResponse(File file);
}
