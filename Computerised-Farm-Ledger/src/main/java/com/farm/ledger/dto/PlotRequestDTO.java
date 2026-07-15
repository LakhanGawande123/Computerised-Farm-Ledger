package com.farm.ledger.dto;

import java.time.LocalDate;

import com.farm.ledger.constant.FarmLedgerExceptionConstants;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
//@Schema(description = "Plot Request Data")
public class PlotRequestDTO {

	//@Schema(description = "Name of the Plot", example = "Road-Plot, Jambhulni-Plot, Wadi")
	//@NotBlank(message = "plotName is required")
	@NotBlank(message = FarmLedgerExceptionConstants.PLOT_NAME_NOT_BLANK)
	@NotNull(message = FarmLedgerExceptionConstants.PLOT_NAME_NOT_NULL_OR_EMPTY)
	private String plotName;

	@Schema(description = "Surve Number of the Plot", example = "91, 32, 77")
	private String surveyNumber;
	
	@Schema(description = "Gat Number of the Plot", example = "91, 32, 77")
	private String gatNumber;

	@Schema(description = "Area of the Plot in acres", example = "1, 20, 7.4")
	@Min(value = 0, message = "area must be non-negative")
	private Double area;

	@Schema(description = "Soil type of the Plot", example = "Alluvial, Black, Red")
	private String soilType;
	@Schema(description = "Available water source for the Plot", example = "Well, Dam, Canal")
	private String waterSource;
	@Schema(description = "Type of the irrigation used in the Plot", example = "Open, Spinkler, Drip")
	private String irrigationType;
	@Schema(description = "Location of the Plot", example = "Washim, Pune, Bengaluru")
	private String location;
	@Schema(description = "Ownership type of the Plot", example = "OWNED, LEASED")
	private String ownershipType; // OWNED or LEASED
	
	@Schema(description = "Lease start date (if ownershipType is LEASED)", example = "2025-01-01")
	private LocalDate leaseStart;
	@Schema(description = "Lease end date (if ownershipType is LEASED)", example = "2026-01-01")
	private LocalDate leaseEnd;
}
