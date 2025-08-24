package com.tomy.tomy.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_reward")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserReward {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reward_id", nullable = true)
    private Reward reward;

    @Column(name = "reward_name")
    private String rewardName; // To store the name of the achievement-based reward

    @Column(name = "value")
    private Integer value; // Value of the gifticon (e.g., 5000, 10000)

    @Column(nullable = false)
    private Boolean used;

    @Column(length = 128)
    private String code; // Can be NULL

    @Column(name = "issued_at", nullable = false)
    private LocalDateTime issuedAt;

    @Column(name = "used_at")
    private LocalDateTime usedAt; // Can be NULL
}