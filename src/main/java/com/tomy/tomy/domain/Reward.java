package com.tomy.tomy.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "reward")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Reward {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "reward_name", nullable = false)
    private String rewardName;

    @Column(columnDefinition = "TEXT")
    private String description;

    private Integer value; // Value of the gifticon (e.g., 5000, 10000)

    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    private Integer stock; // Can be NULL
}