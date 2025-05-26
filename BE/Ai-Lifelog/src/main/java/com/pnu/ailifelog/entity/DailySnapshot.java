package com.pnu.ailifelog.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "daily_snapshots")
public class DailySnapshot {
    @Id
    @UuidGenerator
    private UUID id;

    @Column(nullable = false)
    private LocalDate date;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "dailySnapshot")
    private List<Snapshot> lifeSnapshots;
}
