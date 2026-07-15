package com.farm.ledger.dto.report;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FarmerProfitDTO {
    private String farmerId;
    private BigDecimal totalIncome;
    private BigDecimal totalExpense;
    private BigDecimal profit; // income - expense
}

