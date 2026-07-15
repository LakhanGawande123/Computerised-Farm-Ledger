package com.farm.ledger.dto;

import java.time.LocalDate;

import lombok.Data;

@Data
public class PlotResponseDTO {

	private Long plotId;
	private String farmerId;
	private String plotName;
	private String surveyNumber;
	private String gatNumber;
	private Double area;
	private String soilType;
	private String waterSource;
	private String irrigationType;
	private String location;
	private String ownershipType;
	private LocalDate leaseStart;
	private LocalDate leaseEnd;
}
