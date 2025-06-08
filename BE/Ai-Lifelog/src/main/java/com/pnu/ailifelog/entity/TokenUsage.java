package com.pnu.ailifelog.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.ai.chat.metadata.Usage;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@Table(name = "usages")
@NoArgsConstructor
@AllArgsConstructor
public class TokenUsage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDate date;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User user;

    private Integer totalTokens;
    private Integer promptTokens;
    private Integer completionTokens;

    public TokenUsage(User user, LocalDate date, Usage usage) {
        this.user = user;
        this.date = date;
        this.totalTokens = usage.getTotalTokens();
        this.promptTokens = usage.getPromptTokens();
        this.completionTokens = usage.getCompletionTokens();
    }
}
