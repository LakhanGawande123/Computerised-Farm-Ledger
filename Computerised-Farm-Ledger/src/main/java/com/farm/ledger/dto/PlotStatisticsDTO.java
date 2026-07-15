package com.farm.ledger.dto;

import java.util.Map;

import lombok.Data;

@Data
public class PlotStatisticsDTO {
	private long totalPlots;
	private double totalArea;
	private long ownedPlots;
	private long leasedPlots;
	private Map<String, Long> irrigationTypesCount;
}
