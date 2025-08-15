package com.tomy.tomy.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PurchaseEquipClothesResponse {
    private String message;
    private Integer remainingPoint;
}
