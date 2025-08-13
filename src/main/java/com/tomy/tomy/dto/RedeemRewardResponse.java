package com.tomy.tomy.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class RedeemRewardResponse {
    private Long id;
    private String rewardName;
    private Integer point;
    private Boolean used;
}
