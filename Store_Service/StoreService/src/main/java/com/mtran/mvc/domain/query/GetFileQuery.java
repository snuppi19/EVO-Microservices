package com.mtran.mvc.domain.query;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Builder
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GetFileQuery {
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
