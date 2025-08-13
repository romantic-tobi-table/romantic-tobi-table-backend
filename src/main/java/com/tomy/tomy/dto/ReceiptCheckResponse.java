package com.tomy.tomy.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ReceiptCheckResponse {
    private Long id;
    private String storeName;
    private Integer amount;
    private String menu;
}
