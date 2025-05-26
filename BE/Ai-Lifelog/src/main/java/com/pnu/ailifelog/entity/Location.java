package com.pnu.ailifelog.entity;

import jakarta.persistence.*;

@Entity
public class Location extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private String tagName;
    private Double latitude;
    private Double longitude;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
