package com.farm.ledger.dto.report;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PeriodSummaryDTO {
    // identifying keys (month/quarter/year depends on periodType)
    private Integer year;
    private Integer month;   // 1..12 or null
    private Integer quarter; // 1..4 or null

    private BigDecimal totalIncome;
    private BigDecimal totalExpense;
    private BigDecimal net; // income - expense
}

