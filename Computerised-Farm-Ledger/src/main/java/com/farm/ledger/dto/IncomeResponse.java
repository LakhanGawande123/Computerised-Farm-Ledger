package com.farm.ledger.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

import com.farm.ledger.enumpkg.IncomeCategory;
import com.farm.ledger.enumpkg.PaymentMode;

@Data
@Builder
public class IncomeResponse {
    private Long id;
    private String farmerId;
    private Long plotId;
    private IncomeCategory category;
    private Double amount;
    private PaymentMode paymentMode;
    private LocalDate receivedDate;
    private String description;
}

