package com.farm.ledger.dto.report;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CropProfitDTO {
    private String cropCategory; // IncomeCategory.name()
    private BigDecimal revenue;
    private BigDecimal cost;
    private BigDecimal profit;
}

