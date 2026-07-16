package com.farm.ledger.controller;

import com.farm.ledger.dto.*;
import com.farm.ledger.service.IncomeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
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
public class IncomeController {

	private static final Logger logger = LoggerFactory.getLogger(IncomeController.class);

	private final IncomeService incomeService;

	// 1. Create Income
	@PostMapping(value = "/incomes",
			produces = MediaType.APPLICATION_JSON_VALUE,
			consumes = MediaType.APPLICATION_JSON_VALUE
	)
	@Operation(summary = "Create a new income record", description = "Creates a new income entry")
	@ApiResponse(responseCode = "201", description = "Income created successfully")
	@ApiResponse(responseCode = "400", description = "Invalid request body")
	public ResponseEntity<IncomeResponse> create(
			@Valid @RequestBody IncomeRequest req) {
		logger.trace("Method start: create");
		logger.info("Received request to create income. Payload: {}", req);

		IncomeResponse response = incomeService.create(req);

		logger.info("Income created successfully. Response: {}", response);
		logger.trace("Method exit: create");

		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	// 2. Get income by ID
	@GetMapping("/incomes/{id}")
	@Operation(summary = "Get income by ID", description = "Fetches income details by ID")
	@ApiResponse(responseCode = "200", description = "Income found")
	@ApiResponse(responseCode = "404", description = "Income not found")
	public ResponseEntity<IncomeResponse> get(
			@PathVariable @NotNull @Positive Long id) {
		logger.trace("Method start: get for id={}", id);
		IncomeResponse response = incomeService.getById(id);
		logger.info("Returning income id={}", id);
		logger.trace("Method exit: get for id={}", id);
		return ResponseEntity.ok(response);
	}

	// 3. Update income
	@PutMapping("/incomes/{id}")
	@Operation(summary = "Update income", description = "Updates an existing income record")
	@ApiResponse(responseCode = "200", description = "Income updated successfully")
	@ApiResponse(responseCode = "404", description = "Income not found")
	public ResponseEntity<IncomeResponse> update(
			@PathVariable @NotNull @Positive Long id,
			@Valid @RequestBody IncomeRequest req) {
		logger.trace("Method start: update for id={}", id);
		logger.info("Received update for id={}. Payload: {}", id, req);
		IncomeResponse response = incomeService.update(id, req);
		logger.info("Income updated successfully for id={}. Response: {}", id, response);
		logger.trace("Method exit: update for id={}", id);
		return ResponseEntity.ok(response);
	}

	// 4. Delete income
	@DeleteMapping("/incomes/{id}")
	@Operation(summary = "Delete income", description = "Deletes an income record")
	@ApiResponse(responseCode = "204", description = "Income deleted successfully")
	@ApiResponse(responseCode = "404", description = "Income not found")
	public ResponseEntity<Void> delete(
			@PathVariable @NotNull @Positive Long id) {
		logger.trace("Method start: delete for id={}", id);
		incomeService.delete(id);
		logger.info("Deleted income id={}", id);
		logger.trace("Method exit: delete for id={}", id);
		return ResponseEntity.noContent().build();
	}

	// 5. List incomes with filters
	@GetMapping("/incomes")
	@Operation(summary = "List incomes with filters", description = "Fetches paginated list of incomes with optional filters")
	@ApiResponse(responseCode = "200", description = "List retrieved successfully")
	public ResponseEntity<Page<IncomeResponse>> list(
			@RequestParam(required = false) String farmerId,
			@RequestParam(required = false) @Positive(message = "Plot ID must be positive") Long plotId,
			@RequestParam(required = false) String category,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "20") int size) {
		logger.trace("Method start: list with filters farmerId={} plotId={} category={} startDate={} endDate={} page={} size={}",
				farmerId, plotId, category, startDate, endDate, page, size);
		Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "saleDate"));
		Page<IncomeResponse> result = incomeService.list(farmerId, plotId, category, startDate, endDate, pageable);
		logger.info("Returning {} incomes (page {})", result.getNumberOfElements(), page);
		logger.trace("Method exit: list");
		return ResponseEntity.ok(result);
	}

	// 6. Income summary
	@GetMapping("/incomes/summary")
	@Operation(summary = "Get income summary", description = "Fetches income summary for a farmer and year")
	@ApiResponse(responseCode = "200", description = "Summary retrieved successfully")
	public ResponseEntity<IncomeSummaryDTO> summary(
			@RequestParam @NotNull(message = "farmerId is required") String farmerId,
			@RequestParam @Positive(message = "year must be positive") int year) {
		logger.trace("Method start: summary for farmerId={} year={}", farmerId, year);
		IncomeSummaryDTO response = incomeService.summary(farmerId, year);
		logger.info("Income summary retrieved for farmerId={} year={}", farmerId, year);
		logger.trace("Method exit: summary");
		return ResponseEntity.ok(response);
	}

	// 7. Category-wise report
	@GetMapping("/incomes/report/category")
	@Operation(summary = "Get category-wise income report", description = "Fetches yearly income report by category")
	@ApiResponse(responseCode = "200", description = "Report retrieved successfully")
	public ResponseEntity<List<CategoryYearlyIncomeDTO>> categoryReport(
			@RequestParam @NotNull(message = "farmerId is required") String farmerId,
			@RequestParam @Positive(message = "year must be positive") int year) {
		logger.trace("Method start: categoryReport for farmerId={} year={}", farmerId, year);
		List<CategoryYearlyIncomeDTO> response = incomeService.categoryReport(farmerId, year);
		logger.info("Category report retrieved for farmerId={} year={} with {} categories", farmerId, year, response.size());
		logger.trace("Method exit: categoryReport");
		return ResponseEntity.ok(response);
	}
}

