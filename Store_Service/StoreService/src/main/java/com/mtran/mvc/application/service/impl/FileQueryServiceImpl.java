package com.mtran.mvc.application.service.impl;


import com.mtran.common.support.AppException;
import com.mtran.common.support.ErrorCode;
import com.mtran.mvc.application.dto.mapper.FileToResponse;
import com.mtran.mvc.infrastructure.persistence.mapper.DomainMapper;
import com.mtran.mvc.application.mapper.RequestMapperQuery;
import com.mtran.mvc.application.dto.request.GetFileRequest;
import com.mtran.mvc.application.dto.request.ViewFileRequest;
import com.mtran.mvc.application.dto.response.FileResponse;
import com.mtran.mvc.application.service.FileQueryService;
import com.mtran.mvc.domain.File;
import com.mtran.mvc.domain.query.GetFileQuery;
import com.mtran.mvc.domain.query.ViewFileQuery;
import com.mtran.mvc.domain.repository.FileDomainRepository;
import com.mtran.mvc.infrastructure.persistence.customquerry.CustomQuery;
import com.mtran.mvc.infrastructure.persistence.entity.FileEntity;
import com.mtran.mvc.infrastructure.persistence.repository.FileRepository;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.http.fileupload.ByteArrayOutputStream;
import org.imgscalr.Scalr;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FileQueryServiceImpl implements FileQueryService {
    private final FileRepository fileRepository;
    private final FileDomainRepository fileDomainRepository;
    private final DomainMapper domainMapper;
    private final FileToResponse fileToResponse;
    private final RequestMapperQuery requestMapperQuery;


    @Override
    public Page<FileResponse> getAllFile(GetFileRequest request) {
        GetFileQuery getFileQuery = requestMapperQuery.toFileQuery(request);
        Specification<FileEntity> spec = CustomQuery.filterFiles(
                getFileQuery.getName(),
                getFileQuery.getOwner(),
                getFileQuery.getVisibility(),
                getFileQuery.getCreatedDate(),
                getFileQuery.getUpdatedDate()
        );

        Sort sort = getFileQuery.getSortOrder()
                ? Sort.by(getFileQuery.getSortBy()).descending()
                : Sort.by(getFileQuery.getSortBy()).ascending();
        Pageable pageable = PageRequest.of(getFileQuery.getPage(), getFileQuery.getPageSize(), sort);
        Page<FileEntity> filePage = fileRepository.findAll(spec, pageable);

        return filePage.map(domainMapper::toDomain).map(fileToResponse::toResponse);
    }

    @Override
    public Optional<File> getFileById(String id) {
        return fileDomainRepository.findById(id);
    }

    @Override
    public byte[] viewFile(ViewFileRequest request) throws IOException {
        Double ratio = request.getRatio();
        Integer width = request.getWidth();
        Integer height = request.getHeight();
        ViewFileQuery viewFileQuery = requestMapperQuery.toViewFileQuery(request);
        FileEntity fileEntity = fileRepository.findById(viewFileQuery.getId()).orElseThrow(() -> new AppException(ErrorCode.FILE_NOT_FOUND));

        if (!fileEntity.getType().startsWith("image/")) {
            throw new IOException("File is not an image: " + fileEntity.getName());
        }

        Path filePath = Paths.get(fileEntity.getPath());
        if (!Files.exists(filePath)) {
            throw new IOException("File not found on disk: " + fileEntity.getPath());
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
        String formatName = fileEntity.getType().substring("image/".length());
        ImageIO.write(resizedImage, formatName, baos);
        return baos.toByteArray();
    }
}


