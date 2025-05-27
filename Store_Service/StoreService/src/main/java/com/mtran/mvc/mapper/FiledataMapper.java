package com.mtran.mvc.mapper;

import com.mtran.mvc.dto.FileDto;
import com.mtran.mvc.entity.File;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface FiledataMapper {
File toFile(FileDto filedto);
FileDto toFileDto(File file);
}
