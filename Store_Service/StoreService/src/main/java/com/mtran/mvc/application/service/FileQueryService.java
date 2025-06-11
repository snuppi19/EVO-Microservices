package com.mtran.mvc.application.service;


import com.mtran.mvc.application.dto.request.GetFileRequest;
import com.mtran.mvc.application.dto.request.ViewFileRequest;
import com.mtran.mvc.application.dto.response.FileResponse;
import com.mtran.mvc.domain.File;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Optional;

@Service
public interface FileQueryService {
    Page<FileResponse> getAllFile(GetFileRequest request);
    Optional<File> getFileById(String id);
    byte[] viewFile(ViewFileRequest request) throws IOException;
}
