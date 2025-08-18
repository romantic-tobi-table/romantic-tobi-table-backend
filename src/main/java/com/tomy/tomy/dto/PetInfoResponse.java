package com.tomy.tomy.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PetInfoResponse {
    private Integer level;
    private Long currentPoint;
    private Integer exp;
}
