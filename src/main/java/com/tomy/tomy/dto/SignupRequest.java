package com.tomy.tomy.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignupRequest {
    private String userId;
    private String password;
    private String birthday;
    private String nickname;
    private String gender;
    private Boolean allowNotification;
    // private List<String> interests; // ERD has UserInterest as separate entity
}
