package com.tomy.tomy.dto;

import com.tomy.tomy.domain.Receipt;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import java.time.format.DateTimeFormatter;

@Getter
@Setter
@AllArgsConstructor
public class ReceiptCheckResponse {
    private Long id;
    private String storeName;
    private Integer amount;
    private String menu;
    private String paymentDate;

    public static ReceiptCheckResponse from(Receipt receipt) {
        return new ReceiptCheckResponse(
                receipt.getId(),
                receipt.getStoreName(),
                receipt.getTotalPrice(),
                null, // Assuming 'menu' is not directly available in Receipt entity
                receipt.getRecognizedDate() != null ? receipt.getRecognizedDate().format(DateTimeFormatter.ISO_LOCAL_DATE) : null
        );
    }
}
