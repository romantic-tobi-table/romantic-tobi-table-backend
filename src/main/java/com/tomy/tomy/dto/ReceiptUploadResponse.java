package com.tomy.tomy.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ReceiptUploadResponse {
    private String store_name;
    private String paid_at;
    private String address;
    private String amount;
    private Long pointsEarned;
    private Long currentPoint;
    private String message;
    private Object debug;
}