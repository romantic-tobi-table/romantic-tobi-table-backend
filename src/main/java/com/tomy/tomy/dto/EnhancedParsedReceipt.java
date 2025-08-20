package com.tomy.tomy.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record EnhancedParsedReceipt(
    @JsonProperty("store_name") String storeName,
    String address,
    String amount,
    @JsonProperty("paid_at") String paidAt,
    DebugInfo debug
) {
    public record DebugInfo(
        @JsonProperty("largest_number_candidates") List<String> largestNumberCandidates,
        @JsonProperty("datetime_candidates") List<String> datetimeCandidates,
        @JsonProperty("chatgpt_reason") String chatgptReason
    ) {}
}
