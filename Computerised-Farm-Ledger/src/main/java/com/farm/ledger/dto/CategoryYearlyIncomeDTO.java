package com.farm.ledger.dto;


import com.farm.ledger.enumpkg.IncomeCategory;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CategoryYearlyIncomeDTO {
	private IncomeCategory category;
	private int year;
	private Double totalAmount;
}
