package com.tomy.tomy.dto;

import java.time.LocalDate;

public record ParsedReceipt(String storeName, Integer totalPrice, LocalDate recognizedDate) {
}
