package com.tomy.tomy.dto;

import com.tomy.tomy.domain.UserProgress;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
public class AchievementResponse {
    private Integer attendancesequence;
    private Integer feeding;
    private Integer receipt;
    private String attendedAt;

    public static AchievementResponse from(UserProgress userProgress) {
        return new AchievementResponse(
                userProgress.getAttendanceSequence(),
                userProgress.getFeedingCount(),
                userProgress.getReceiptCount(),
                userProgress.getLastAttendedAt() != null ? userProgress.getLastAttendedAt().toString() : null
        );
    }
}
