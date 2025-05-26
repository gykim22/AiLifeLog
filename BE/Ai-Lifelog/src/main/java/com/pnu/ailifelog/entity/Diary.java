package com.pnu.ailifelog.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "diaries")
public class Diary extends BaseEntity {
    @Id
    @UuidGenerator
    private UUID id;
    
    @NotBlank
    private String title;
    
    @NotBlank
    private String content;
    
    @NotNull
    private LocalDate date;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @NotNull
    private User user;
}
