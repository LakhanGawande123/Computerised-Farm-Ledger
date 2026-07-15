package com.farm.ledger.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class ExpenditureRequest {

	@NotNull(message = "farmerId is required")
	private String farmerId;

	private Long plotId; // optional

	@NotNull(message = "category is required")
	private String category; // will map to enum

	@NotNull(message = "amount is required")
	@DecimalMin(value = "0.01", message = "amount must be >= 0.01")
	private BigDecimal amount;

	private String paymentMode;

	@Size(max = 1000)
	private String description;

	@NotNull(message = "billDate is required")
	private LocalDate billDate;
}