package com.mtran.mvc.application.mapper;

import com.mtran.mvc.application.dto.request.GetFileRequest;
import com.mtran.mvc.application.dto.request.ViewFileRequest;
import com.mtran.mvc.domain.query.GetFileQuery;
import com.mtran.mvc.domain.query.ViewFileQuery;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

@Component
@Mapper(componentModel = "spring")
public interface RequestMapperQuery {
     GetFileQuery toFileQuery(GetFileRequest request);
     ViewFileQuery toViewFileQuery(ViewFileRequest request);
}
