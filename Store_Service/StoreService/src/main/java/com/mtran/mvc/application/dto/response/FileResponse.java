package com.mtran.mvc.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FileResponse {
    String id;
    String name;
    String type;
    String path;
    String owner;
    Boolean visibility;
    Long size;
    String cloudinaryPublicId;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}
