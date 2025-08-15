package com.tomy.tomy.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AchievementUpdateRequest {
    public enum AchievementType {
        ATTENDANCE,
        FEEDING,
        RECEIPT
    }

    private AchievementType type;
}
