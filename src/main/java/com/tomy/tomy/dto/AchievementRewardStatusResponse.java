package com.tomy.tomy.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class AchievementRewardStatusResponse {
    private String rewardName;
    private Integer value;
    private Boolean redeemed;
}
