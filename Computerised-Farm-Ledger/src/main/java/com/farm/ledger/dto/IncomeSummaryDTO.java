package com.farm.ledger.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class IncomeSummaryDTO {
    private String farmerId;
    private int year;
    private Double totalAmount;
}

