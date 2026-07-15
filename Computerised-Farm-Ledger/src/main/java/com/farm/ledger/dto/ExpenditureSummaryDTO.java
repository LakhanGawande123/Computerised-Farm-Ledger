package com.farm.ledger.dto;

import java.math.BigDecimal;
import java.util.Map;

import lombok.Data;

@Data
public class ExpenditureSummaryDTO {
	private String farmerId;
    private int year;
    private BigDecimal totalExpense;
    private Map<String, BigDecimal> categoryWise;
}
