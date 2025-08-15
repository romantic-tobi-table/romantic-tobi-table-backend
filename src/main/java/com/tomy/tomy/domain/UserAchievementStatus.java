package com.tomy.tomy.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_achievement_status")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserAchievementStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "achievement_milestone_id", nullable = false)
    private AchievementMilestone achievementMilestone;

    @Column(nullable = false)
    private Boolean isAchieved = false;

    @Column(name = "achieved_at")
    private LocalDateTime achievedAt;

    @PrePersist
    protected void onCreate() {
        if (isAchieved) {
            achievedAt = LocalDateTime.now();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        if (isAchieved && achievedAt == null) {
            achievedAt = LocalDateTime.now();
        }
    }
}
