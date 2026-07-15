package com.farm.ledger.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class ExpenditureResponse {
	private Long id;
	private String farmerId;
	private Long plotId;
	private String category;
	private BigDecimal amount;
	private String paymentMode;
	private String description;
	private LocalDate billDate;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
}
