package com.tomy.tomy.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LevelUpRequest {
    private Integer level;
    private Integer exceededexp;
}
