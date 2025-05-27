package com.mtran.mvc.controller;

import com.mtran.common.support.AppException;
import com.mtran.common.support.ErrorCode;
import com.mtran.mvc.dto.FileDto;
import com.mtran.mvc.entity.File;
import com.mtran.mvc.service.impl.FileServiceImpl;
import com.mtran.mvc.service.impl.UploadImageFileServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/storage")
@RequiredArgsConstructor
public class HomeController {
    private final UploadImageFileServiceImpl uploadImageFile;
    private final FileServiceImpl fileService;

    @Operation(summary = "up file public tren cloudinary", description = "API up file public tren cloudinary")
    @PreAuthorize("hasPermission('file','upload')")
    @PostMapping("/upload-image")
    public ResponseEntity<?> uploadImage(@RequestParam("file") MultipartFile file,
                                         @RequestParam("visibility") boolean visibility) throws IOException {
        return ResponseEntity.ok(uploadImageFile.uploadImageFile(file, visibility));
    }

    @Operation(summary = "up file public", description = "API up file public")
    @PreAuthorize("hasPermission('file','upload')")
    @PostMapping("/public/upload")
    public ResponseEntity<?> uploadSinglePublicFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam("owner") String owner) throws IOException {
        File uploadedFile = fileService.uploadSingleFile(file, owner, true);
        return ResponseEntity.ok(uploadedFile);
    }

    // single file (private)
    @Operation(summary = "up file private", description = "API up file private")
    @PreAuthorize("hasPermission('contract_file','upload')")
    @PostMapping("/private/upload")
    public ResponseEntity<?> uploadSinglePrivateFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam("owner") String owner) throws IOException {
        File uploadedFile = fileService.uploadSingleFile(file, owner, false);
        return ResponseEntity.ok(uploadedFile);
    }

    //multiple files (public)
    @Operation(summary = "up multi file public", description = "API up multi file public")
    @PreAuthorize("hasPermission('file','upload')")
    @PostMapping("/public/upload/multiple")
    public ResponseEntity<?> uploadMultiplePublicFiles(
            @RequestParam("files") MultipartFile[] files,
            @RequestParam("owner") String owner) throws IOException {
        List<File> uploadedFiles = fileService.uploadMultipleFiles(files, owner, true);
        return ResponseEntity.ok(uploadedFiles);
    }

    // multiple files (private)
    @Operation(summary = "up multi file private", description = "API up multi file private")
    @PreAuthorize("hasPermission('contract_file','upload')")
    @PostMapping("/private/upload/multiple")
    public ResponseEntity<?> uploadMultiplePrivateFiles(
            @RequestParam("files") MultipartFile[] files,
            @RequestParam("owner") String owner) throws IOException {
        List<File> uploadedFiles = fileService.uploadMultipleFiles(files, owner, false);
        return ResponseEntity.ok(uploadedFiles);
    }

    @Operation(summary = "view file private", description = "API view file private")
    @PreAuthorize("hasPermission('contract_file','view')")
    @GetMapping("/private/get-file/{id}")
    public ResponseEntity<?> getPrivateFileById(@PathVariable("id") String id) {
        File file = fileService.getFileById(id);
        return ResponseEntity.ok(file);
    }

    @Operation(summary = "view file public", description = "API view file public")
    @PreAuthorize("hasPermission('file','view')")
    @GetMapping("/public/get-file/{id}")
    public ResponseEntity<?> getPublicFileById(@PathVariable("id") String id) {
        File file = fileService.getFileById(id);
        if (!file.getVisibility()) {
            throw new AppException(ErrorCode.FILE_PRIVATE);
        }
        return ResponseEntity.ok(file);
    }

    @Operation(summary = "view file ", description = "API view file co the chinh sua ratio,width,height theo y muon")
    @PreAuthorize("hasPermission('file','view')")
    //produces chỉ định Content-Type của response mà endpoint trả về
    @GetMapping(value = "/get-file/{id}/view", produces = {MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_PNG_VALUE})
    public ResponseEntity<byte[]> viewFile(
            @PathVariable String id,
            @RequestParam(required = false) Double ratio,
            @RequestParam(required = false) Integer width,
            @RequestParam(required = false) Integer height) {
        try {
            byte[] imageBytes = fileService.viewFile(id, ratio, width, height);
            File file = fileService.getFileById(id);
            String mediaType = file.getType();
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(mediaType))
                    .body(imageBytes);
        } catch (IOException e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @Operation(summary = "view all file public/private", description = "API view all file public/private va co the filter")
    @PreAuthorize("hasPermission('all_file','view')")
    @GetMapping("/get-all-files")
    public Page<FileDto> getAllFiles(@RequestParam(required = false, defaultValue = "0") int page,
                                     @RequestParam(required = false, defaultValue = "10") int size,
                                     @RequestParam(value = "sortBy", required = false, defaultValue = "createdAt") String sortBy,
                                     @RequestParam(value = "sortOrder", required = false, defaultValue = "false") boolean sortOrder,
                                     @RequestParam(value = "name", required = false, defaultValue = "") String name,
                                     @RequestParam(value = "owner", required = false, defaultValue = "") String owner,
                                     @RequestParam(value = "visibility", required = false, defaultValue = "true") boolean visibility,
                                     @RequestParam(value = "createAt", required = false, defaultValue = "") String CreateAt,
                                     @RequestParam(value = "updateAt", required = false, defaultValue = "") String UpdateAt) {
        return fileService.getAllFile(page, size, sortBy, sortOrder, name, owner, visibility, CreateAt, UpdateAt);
    }
}
