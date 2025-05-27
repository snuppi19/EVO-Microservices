package com.mtran.mvc.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "custom_users")
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor
public class CustomUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username", nullable = false, unique = true)
    private String username;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(name = "birth_date", nullable = false)
    private LocalDate birthDate;

    @Column(name = "street", nullable = false)
    private String street;

    @Column(name = "ward", nullable = false)
    private String ward;

    @Column(name = "district", nullable = false)
    private String district;

    @Column(name = "province", nullable = false)
    private String province;

    @Column(name = "experience_years", nullable = false)
    private Integer experienceYears;

    @Column(name = "keycloak_id", nullable = true)
    private String keycloakId;

    @CreatedDate
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted = false;

    public CustomUser(String username, String fullName, LocalDate birthDate, String street, String ward, String district, String province, Integer experienceYears) {
        this.username = username;
        this.fullName = fullName;
        this.birthDate = birthDate;
        this.street = street;
        this.ward = ward;
        this.district = district;
        this.province = province;
        this.experienceYears = experienceYears;
    }
}