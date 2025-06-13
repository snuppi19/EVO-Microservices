package com.mtran.mvc.application.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetFileRequest {
    String name;
    String owner;
    Boolean visibility;
    String createdDate;
    String updatedDate;
    String sortBy;
    Boolean sortOrder;
    Integer page;
    Integer pageSize;
}
