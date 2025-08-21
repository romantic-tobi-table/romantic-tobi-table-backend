package com.tomy.tomy.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class MyPageResponse {
    private String nickname;
    private LocalDate birthday;
    private int petLevel;
    private String latestAchievementName;
}
