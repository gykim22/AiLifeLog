package com.pnu.ailifelog.entity;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
public class Location extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String tagName;
    private Double latitude;
    private Double longitude;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
