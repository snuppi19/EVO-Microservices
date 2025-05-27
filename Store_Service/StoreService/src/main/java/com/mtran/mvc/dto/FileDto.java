package com.mtran.mvc.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FileDto {
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
