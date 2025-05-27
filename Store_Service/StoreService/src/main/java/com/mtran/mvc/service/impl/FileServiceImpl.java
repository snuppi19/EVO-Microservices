package com.mtran.mvc.service.impl;

import com.mtran.common.support.AppException;
import com.mtran.common.support.ErrorCode;
import com.mtran.mvc.dto.FileDto;
import com.mtran.mvc.entity.File;
import com.mtran.mvc.mapper.FiledataMapper;
import com.mtran.mvc.repository.FileRepository;
import com.mtran.mvc.service.FileService;
import com.mtran.mvc.service.customquerry.CustomQuery;
import lombok.RequiredArgsConstructor;

import org.apache.tomcat.util.http.fileupload.ByteArrayOutputStream;
import org.imgscalr.Scalr;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {
    private final FiledataMapper filedataMapper;
    @Value("${file.upload-dir}")
    private String uploadDir;
    private final FileRepository fileRepository;
    @Override
    public File uploadSingleFile(MultipartFile file, String owner, boolean isPublic) throws IOException {
        if(file.isEmpty()){
            throw new AppException(ErrorCode.FILE_NOT_FOUND);
        }
        if(file.getSize() > 1024*1024*10){
            throw new AppException(ErrorCode.FILE_SIZE_LIMIT_AT_10MB);
        }
        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path filePath = Paths.get(uploadDir, fileName);
        Files.write(filePath, file.getBytes());

        File fileEntity=File.builder()
                .name(file.getOriginalFilename())
                .type(file.getContentType())
                .path(filePath.toString())
                .size(file.getSize())
                .owner(owner)
                .visibility(isPublic)
                .build();
        return fileRepository.save(fileEntity);
    }

    @Override
    public List<File> uploadMultipleFiles(MultipartFile[] files, String owner, boolean isPublic) throws IOException {
        if(files.length==0){
            throw new AppException(ErrorCode.FILE_NOT_FOUND);
        }
        List<File> uploadedFiles = new ArrayList<>();
        for (MultipartFile file : files) {
            File uploadFiles=uploadSingleFile(file,owner,isPublic);
            uploadedFiles.add(uploadFiles);
        }
        return uploadedFiles;
    }

    @Override
    public File getFileById(String id) {
        return fileRepository.findById(id).orElseThrow(()->new AppException(ErrorCode.FILE_NOT_FOUND));
    }

    @Override
    public byte[] viewFile(String id, Double ratio, Integer width, Integer height) throws IOException {
        File file = getFileById(id);

        if (!file.getType().startsWith("image/")) {
            throw new IOException("File is not an image: " + file.getName());
        }

        Path filePath = Paths.get(file.getPath());
        if (!Files.exists(filePath)) {
            throw new IOException("File not found on disk: " + file.getPath());
        }
        BufferedImage originalImage = ImageIO.read(filePath.toFile());
        BufferedImage resizedImage = originalImage;
        if (ratio != null && ratio > 0) {
            int newWidth = (int) (originalImage.getWidth() * ratio);
            int newHeight = (int) (originalImage.getHeight() * ratio);
            resizedImage = Scalr.resize(originalImage, Scalr.Method.QUALITY, newWidth, newHeight);
        } else if (width != null && height != null && width > 0 && height > 0) {
            resizedImage = Scalr.resize(originalImage, Scalr.Method.QUALITY, width, height);
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        String formatName = file.getType().substring("image/".length());
        ImageIO.write(resizedImage, formatName, baos);
        return baos.toByteArray();
    }

    @Override
    public Page<FileDto> getAllFile(int page, int size, String sortBy, Boolean sortOrder, String name, String owner, Boolean isPublic, String createdDate, String updatedDate) {
        Sort sort = sortOrder ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Specification<File> spec = CustomQuery.filterFiles(name, owner, isPublic, createdDate, updatedDate);
        Page<File> filePage = fileRepository.findAll(spec, pageable);
        return filePage.map(filedataMapper::toFileDto);
    }


}
