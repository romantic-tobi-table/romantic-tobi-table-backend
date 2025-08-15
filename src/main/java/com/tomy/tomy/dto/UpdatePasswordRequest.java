package com.tomy.tomy.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdatePasswordRequest {
    private String userId;
    private String password;
    private String pass_check;
}
