package com.tomy.tomy.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserAchievementSummaryResponse {
    // Overall progress
    private Integer attendancesequence;
    private Integer feeding;
    private Integer receipt;
    private String attendedAt;

    // Specific achievement statuses (e.g., "연속 3일 출석 성공": true)
    private Map<String, Boolean> specificAchievements;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SpecificAchievementStatus {
        private String name;
        private Boolean isAchieved;
    }
}
