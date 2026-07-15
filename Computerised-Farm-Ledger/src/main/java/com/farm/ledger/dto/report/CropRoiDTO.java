package com.farm.ledger.dto.report;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CropRoiDTO {
    private String cropCategory;
    private BigDecimal revenue;
    private BigDecimal cost;
    private BigDecimal roi; // (revenue - cost) / cost ; null when cost == 0
}

