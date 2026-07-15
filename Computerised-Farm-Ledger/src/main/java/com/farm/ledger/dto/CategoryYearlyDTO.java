package com.farm.ledger.dto;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CategoryYearlyDTO {
	private String category;
	private BigDecimal total;
}
