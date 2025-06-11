package com.mtran.mvc.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name="file")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class FileEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name="id", nullable = false)
    String Id;
    @Column(name="name", nullable = false)
    String name;
    @Column(name="type", nullable = false)
    String type;
    @Column(name="path", nullable = false)
    String path;
    @Column(name="owner", nullable = false)
    String owner;
    @Column(name="visibility", nullable = false)
    Boolean visibility;
    @Column(name="size", nullable = false)
    Long size;
    @Column(name = "public_id")
    private String cloudinaryPublicId;
    @CreatedDate
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
