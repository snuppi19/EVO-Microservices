package com.mtran.mvc.service;

import com.mtran.mvc.dto.FileDto;
import com.mtran.mvc.entity.File;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public interface FileService {
    File uploadSingleFile(MultipartFile file, String owner, boolean isPublic) throws IOException;
    List<File> uploadMultipleFiles(MultipartFile[] files, String owner, boolean isPublic) throws IOException;
    File getFileById(String id);
    byte[] viewFile(String id, Double ratio, Integer width, Integer height) throws IOException;
    Page<FileDto> getAllFile(int page, int size, String sortBy, Boolean sortOrder, String name, String owner, Boolean isPublic, String createdDate, String updatedDate);
}
