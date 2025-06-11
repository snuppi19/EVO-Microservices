package com.mtran.mvc.application.service.impl;


import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.mtran.common.support.AppException;
import com.mtran.common.support.ErrorCode;
import com.mtran.mvc.application.service.FileCommandService;
import com.mtran.mvc.domain.command.SaveFileCmd;
import com.mtran.mvc.domain.File;
import com.mtran.mvc.infrastructure.domain_repository.FileDomainRepositoryImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileCommandServiceImpl implements FileCommandService {
    private final FileDomainRepositoryImpl fileDomainRepository;
    private final Cloudinary cloudinary;

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Override
    public String uploadFileCloudinary(MultipartFile file, boolean visibility) throws IOException {
        assert file.getOriginalFilename() != null;
        String publicValue = generatePublicValue(file.getOriginalFilename());
        String extensions = getFileName(file.getOriginalFilename())[1];
        java.io.File fileUpload = convert(file);
        cloudinary.uploader().upload(fileUpload, ObjectUtils.asMap("public_id", publicValue));
        cleanDisk(fileUpload);
        SaveFileCmd saveFileCmd = SaveFileCmd.builder()
                .name(file.getOriginalFilename())
                .type(file.getContentType())
                .path(cloudinary.url().generate(StringUtils.join(publicValue, ".", extensions)))
                .size(file.getSize())
                .visibility(visibility)
                .owner("test_user")
                .cloudinaryPublicId(publicValue)
                .build();

        File fileModel = new File(saveFileCmd);
        fileDomainRepository.save(fileModel);

        return cloudinary.url().generate(StringUtils.join(publicValue, ".", extensions));
    }

    @Override
    public File uploadSingleFile(MultipartFile file, String owner, boolean isPublic) throws IOException {
        validateFile(file, uploadDir);
        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path filePath = Paths.get(uploadDir, fileName);
        SaveFileCmd saveFileCmd = SaveFileCmd.builder()
                .name(file.getOriginalFilename())
                .type(file.getContentType())
                .path(filePath.toString())
                .size(file.getSize())
                .owner(owner)
                .visibility(isPublic)
                .build();
        File fileModel = new File(saveFileCmd);
        return fileDomainRepository.save(fileModel);
    }

    @Override
    public List<File> uploadMultipleFiles(MultipartFile[] files, String owner, boolean isPublic) throws IOException {
        if (files.length == 0) {
            throw new AppException(ErrorCode.FILE_NOT_FOUND);
        }
        List<File> uploadedFileEntities = new ArrayList<>();
        for (MultipartFile file : files) {
            uploadedFileEntities.add(uploadSingleFile(file, owner, isPublic));
        }
        return uploadedFileEntities;
    }

    public static void validateFile(MultipartFile file, String uploadDir) throws IOException {
        if (file.isEmpty()) {
            throw new AppException(ErrorCode.FILE_NOT_FOUND);
        }
        if (file.getSize() > 1024 * 1024 * 10) {
            throw new AppException(ErrorCode.FILE_SIZE_LIMIT_AT_10MB);
        }
        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path filePath = Paths.get(uploadDir, fileName);
        Files.write(filePath, file.getBytes());
    }

    //114+ là của cloudinary code
    private java.io.File convert(MultipartFile file) throws IOException {
        assert file.getOriginalFilename() != null;
        java.io.File convFile = new java.io.File(StringUtils.join(generatePublicValue(file.getOriginalFilename())
                                                                    , getFileName(file.getOriginalFilename())[1]));

        try (InputStream is = file.getInputStream()) {
            Files.copy(is, convFile.toPath());
        }
        return convFile;
    }

    private String generatePublicValue(String originalName) {
        String fileName = getFileName(originalName)[0];
        return StringUtils.join(UUID.randomUUID().toString(), "_", fileName);
    }

    private String[] getFileName(String originalName) {
        return originalName.split("\\.");
    }

    private void cleanDisk(java.io.File file) {
        try {
            Path filePath = file.toPath();
            Files.delete(filePath);
        } catch (IOException e) {
            log.error("Error");
        }
    }
}
