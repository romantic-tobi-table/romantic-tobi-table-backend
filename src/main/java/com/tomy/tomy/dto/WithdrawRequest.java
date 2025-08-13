package com.tomy.tomy.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WithdrawRequest {
    private String reason;
    private String detail;
}
