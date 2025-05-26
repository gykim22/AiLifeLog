package com.pnu.ailifelog.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "snapshots")
public class Snapshot extends BaseEntity {
    @Id
    @UuidGenerator
    private UUID id;

    @NotBlank
    private String content;
    
    @NotNull
    private LocalDateTime timestamp;

    @ManyToOne
    @JoinColumn(name = "location_id")
    @OnDelete(action = OnDeleteAction.SET_NULL)
    private Location location;

    @ManyToOne
    @JoinColumn(name = "daily_snapshot_id")
    @NotNull
    private DailySnapshot dailySnapshot;
}
