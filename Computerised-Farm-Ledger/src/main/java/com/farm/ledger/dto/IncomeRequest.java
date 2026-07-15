package com.farm.ledger.dto;

import lombok.Data;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class IncomeRequest {
	@NotBlank(message = "farmerId is required")
    private String farmerId;

    private Long plotId;

    @NotBlank(message = "category is required")
    private String category;

    @NotNull(message = "amount is required")
    @Positive(message = "amount must be > 0")
    private Double amount;

    private String paymentMode;

    // Jackson will parse "yyyy-MM-dd" into LocalDate automatically
    @NotNull(message = "receivedDate is required")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate receivedDate;

    @Size(max = 500)
    private String description;
}
