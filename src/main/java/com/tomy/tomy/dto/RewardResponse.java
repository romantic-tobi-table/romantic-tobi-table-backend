package com.tomy.tomy.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RewardResponse {
    private Long id;
    private String rewardName;
    private Integer value;
    private Boolean used;

    public RewardResponse(Long id, String rewardName, Integer value, Boolean used) {
        this.id = id;
        this.rewardName = rewardName;
        this.value = value;
        this.used = used;
    }
}
