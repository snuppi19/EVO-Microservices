package com.mtran.mvc.application.service;

import com.mtran.mvc.domain.command.SaveFileCmd;
import com.mtran.mvc.domain.File;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public interface FileCommandService {
     String uploadFileCloudinary(MultipartFile file, boolean visibility) throws IOException;
     File uploadSingleFile(MultipartFile file, String owner, boolean isPublic) throws IOException;
    List<File> uploadMultipleFiles(MultipartFile[] files, String owner, boolean isPublic) throws IOException;
}
