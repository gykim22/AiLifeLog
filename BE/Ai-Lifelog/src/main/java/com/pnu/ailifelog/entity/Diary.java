package com.pnu.ailifelog.entity;

import jakarta.persistence.*;
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
    private String title;
    private String content;
    private LocalDate date;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
