package com.mtran.mvc.service.customquerry;

import com.mtran.common.support.AppException;
import com.mtran.common.support.ErrorCode;
import com.mtran.mvc.entity.File;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class CustomQuery {
    public static Specification<File>filterFiles(String name, String owner, Boolean visibility,
                                                 String createdAt, String updatedAt){
        return (root, query, criteriaBuilder) ->{
            List<Predicate> predicates=new ArrayList<>();

            if(name!=null && !name.isEmpty()){
                predicates.add(criteriaBuilder.like(root.get("name"),"%"+name+"%"));
            }
            if(owner!=null && !owner.isEmpty()){
                predicates.add(criteriaBuilder.equal(root.get("owner"),owner));
            }
            if(visibility!=null){
                predicates.add(criteriaBuilder.equal(root.get("visibility"),visibility));
            }
            if (createdAt != null && !createdAt.isEmpty()) {
                try {
                    LocalDate date = LocalDate.parse(createdAt, DateTimeFormatter.ISO_LOCAL_DATE);
                    predicates.add(criteriaBuilder.equal(
                            criteriaBuilder.function("DATE", LocalDate.class, root.get("createdAt")),
                            date
                    ));
                } catch (Exception e) {
                   throw new AppException(ErrorCode.CREATE_DATE_INVALID);
                }
            }

            if (updatedAt != null && !updatedAt.isEmpty()) {
                try {
                    LocalDate date = LocalDate.parse(updatedAt, DateTimeFormatter.ISO_LOCAL_DATE);
                    predicates.add(criteriaBuilder.equal(
                            criteriaBuilder.function("DATE", LocalDate.class, root.get("updatedAt")),
                            date
                    ));
                } catch (Exception e) {
                    throw new AppException(ErrorCode.UPDATE_DATE_INVALID);
                }
            }

           return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
