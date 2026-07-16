package com.farm.ledger.controller;

import com.farm.ledger.dto.report.*;
import com.farm.ledger.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Validated
public class ReportController {

	private static final Logger logger = LoggerFactory.getLogger(ReportController.class);

	private final ReportService reportService;

	// 1. Period summary
	@GetMapping(value = "/reports/period", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Get period summary report", description = "Fetches period-wise summary of income and expenditure")
	@ApiResponse(responseCode = "200", description = "Report retrieved successfully")
	@ApiResponse(responseCode = "400", description = "Invalid date parameters")
	public ResponseEntity<List<PeriodSummaryDTO>> periodSummary(
			@RequestParam(required = false) String farmerId,
			@RequestParam @NotNull(message = "startDate is required") LocalDate startDate,
			@RequestParam @NotNull(message = "endDate is required") LocalDate endDate,
			@RequestParam(defaultValue = "MONTH") String periodType) {
		logger.trace("Method start: periodSummary with farmerId={} startDate={} endDate={} periodType={}",
				farmerId, startDate, endDate, periodType);
		logger.info("Generating period summary report for farmerId={}", farmerId);

		List<PeriodSummaryDTO> result = reportService.periodSummary(farmerId, startDate, endDate, periodType);

		logger.info("Period summary report generated with {} periods", result.size());
		logger.trace("Method exit: periodSummary");

		return ResponseEntity.ok(result);
	}

	// 2. Farmer profitability
	@GetMapping(value = "/reports/farmer-profitability", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Get farmer profitability report", description = "Fetches profitability metrics for all farmers within a date range")
	@ApiResponse(responseCode = "200", description = "Report retrieved successfully")
	@ApiResponse(responseCode = "400", description = "Invalid date parameters")
	public ResponseEntity<Page<FarmerProfitDTO>> farmerProfitability(
			@RequestParam @NotNull(message = "startDate is required") LocalDate startDate,
			@RequestParam @NotNull(message = "endDate is required") LocalDate endDate,
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "20") int size) {
		logger.trace("Method start: farmerProfitability with startDate={} endDate={} page={} size={}",
				startDate, endDate, page, size);
		logger.info("Generating farmer profitability report");

		Page<FarmerProfitDTO> result = reportService.farmerProfitability(startDate, endDate, PageRequest.of(page, size));

		logger.info("Farmer profitability report generated with {} farmers (page {})", result.getNumberOfElements(), page);
		logger.trace("Method exit: farmerProfitability");

		return ResponseEntity.ok(result);
	}

	// 3. Plot profit
	@GetMapping(value = "/reports/plot-profit", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Get plot profit report", description = "Fetches profit analysis for plots within a date range")
	@ApiResponse(responseCode = "200", description = "Report retrieved successfully")
	@ApiResponse(responseCode = "400", description = "Invalid date parameters")
	public ResponseEntity<List<PlotProfitDTO>> plotProfit(
			@RequestParam(required = false) String farmerId,
			@RequestParam @NotNull(message = "startDate is required") LocalDate startDate,
			@RequestParam @NotNull(message = "endDate is required") LocalDate endDate) {
		logger.trace("Method start: plotProfit with farmerId={} startDate={} endDate={}",
				farmerId, startDate, endDate);
		logger.info("Generating plot profit report for farmerId={}", farmerId);

		List<PlotProfitDTO> result = reportService.plotProfit(farmerId, startDate, endDate);

		logger.info("Plot profit report generated with {} plots", result.size());
		logger.trace("Method exit: plotProfit");

		return ResponseEntity.ok(result);
	}

	// 4. Crop profitability
	@GetMapping(value = "/reports/crop-profitability", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Get crop profitability report", description = "Fetches profitability metrics grouped by crop")
	@ApiResponse(responseCode = "200", description = "Report retrieved successfully")
	@ApiResponse(responseCode = "400", description = "Invalid date parameters")
	public ResponseEntity<List<CropProfitDTO>> cropProfitability(
			@RequestParam(required = false) String farmerId,
			@RequestParam @NotNull(message = "startDate is required") LocalDate startDate,
			@RequestParam @NotNull(message = "endDate is required") LocalDate endDate) {
		logger.trace("Method start: cropProfitability with farmerId={} startDate={} endDate={}",
				farmerId, startDate, endDate);
		logger.info("Generating crop profitability report for farmerId={}", farmerId);

		List<CropProfitDTO> result = reportService.cropProfitability(farmerId, startDate, endDate);

		logger.info("Crop profitability report generated with {} crops", result.size());
		logger.trace("Method exit: cropProfitability");

		return ResponseEntity.ok(result);
	}

	// 5. ROI per crop
	@GetMapping(value = "/reports/roi-per-crop", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Get ROI per crop report", description = "Fetches Return on Investment (ROI) analysis for each crop")
	@ApiResponse(responseCode = "200", description = "Report retrieved successfully")
	@ApiResponse(responseCode = "400", description = "Invalid date parameters")
	public ResponseEntity<List<CropRoiDTO>> roiPerCrop(
			@RequestParam(required = false) String farmerId,
			@RequestParam @NotNull(message = "startDate is required") LocalDate startDate,
			@RequestParam @NotNull(message = "endDate is required") LocalDate endDate) {
		logger.trace("Method start: roiPerCrop with farmerId={} startDate={} endDate={}",
				farmerId, startDate, endDate);
		logger.info("Generating ROI per crop report for farmerId={}", farmerId);

		List<CropRoiDTO> result = reportService.roiPerCrop(farmerId, startDate, endDate);

		logger.info("ROI per crop report generated with {} crops", result.size());
		logger.trace("Method exit: roiPerCrop");

		return ResponseEntity.ok(result);
	}
}

