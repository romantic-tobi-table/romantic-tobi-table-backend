package com.tomy.tomy.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReceiptVerifyRequest {
    private String recognizedText;
    private String recognizedDate;
}
