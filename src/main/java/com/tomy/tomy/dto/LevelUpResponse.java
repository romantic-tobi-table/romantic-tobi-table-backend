package com.tomy.tomy.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class LevelUpResponse {
    private String message;
    private Integer level;
    private Integer exceededexp;
}
