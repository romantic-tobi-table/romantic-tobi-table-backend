package com.tomy.tomy.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ReceiptUploadResponse {
    private String recognizedText;
    private String recognizedDate;
    private String status;
}
