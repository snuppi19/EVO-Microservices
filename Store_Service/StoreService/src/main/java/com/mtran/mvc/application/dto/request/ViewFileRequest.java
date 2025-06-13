package com.mtran.mvc.application.dto.request;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ViewFileRequest {
    String id;
    Double ratio;
    Integer width;
    Integer height;
}
