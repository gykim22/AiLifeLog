package com.pnu.ailifelog.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@Table(name = "diaries")
public class Diary extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private String title;
    private String content;
    private LocalDate date;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
