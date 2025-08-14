package com.tomy.tomy.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "AchievementMilestone")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AchievementMilestone {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "achievement_id", nullable = false)
    private Achievement achievement; // Links to the general achievement type (e.g., ATTENDANCE_SEQ)

    @Column(nullable = false)
    private Integer milestoneValue; // e.g., 3, 7, 15 for attendance; 5, 10, 15 for feeding

    @Column(nullable = false)
    private String name; // e.g., "연속 3일 출석 성공"

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AchievementType type; // To easily categorize milestones

    public enum AchievementType {
        ATTENDANCE,
        FEEDING,
        RECEIPT
    }
}
