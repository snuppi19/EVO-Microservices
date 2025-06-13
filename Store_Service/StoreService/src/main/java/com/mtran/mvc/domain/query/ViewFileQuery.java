package com.mtran.mvc.domain.query;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ViewFileQuery {
    String id;
    Double ratio;
    Integer width;
    Integer height;
}
