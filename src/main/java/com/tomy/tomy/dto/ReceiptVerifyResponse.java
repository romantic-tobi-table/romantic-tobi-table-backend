package com.tomy.tomy.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ReceiptVerifyResponse {
    private String storeName;
    private Integer amount;
    private Integer pointsEarned;
    private Integer currentPoint;
}
