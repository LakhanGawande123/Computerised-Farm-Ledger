package com.farm.ledger.dto.report;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlotProfitDTO {
    private Long plotId;
    private String farmerId;
    private BigDecimal totalIncome;
    private BigDecimal totalExpense;
    private BigDecimal profit;
}

