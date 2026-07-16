package com.farm.ledger.controller;

import com.farm.ledger.dto.*;
import com.farm.ledger.service.ExpenditureService;
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
public class ExpenditureController {

	private static final Logger logger = LoggerFactory.getLogger(ExpenditureController.class);

	private final ExpenditureService expenditureService;

	// 1. Create Expenditure
	@PostMapping(value = "/expenditures",
			produces = MediaType.APPLICATION_JSON_VALUE,
			consumes = MediaType.APPLICATION_JSON_VALUE
	)
	@Operation(summary = "Create a new expenditure", description = "Creates a new expenditure record")
	@ApiResponse(responseCode = "201", description = "Expenditure created successfully")
	@ApiResponse(responseCode = "400", description = "Invalid request body")
	public ResponseEntity<ExpenditureResponse> create(
			@Valid @RequestBody ExpenditureRequest req) {
		logger.trace("Method start: create");
		logger.info("Received request to create expenditure. Payload: {}", req);

		ExpenditureResponse response = expenditureService.create(req);

		logger.info("Expenditure created successfully. Response: {}", response);
		logger.trace("Method exit: create");

		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	// 2. Get expenditure by ID
	@GetMapping("/expenditures/{id}")
	@Operation(summary = "Get expenditure by ID", description = "Fetches expenditure details by ID")
	@ApiResponse(responseCode = "200", description = "Expenditure found")
	@ApiResponse(responseCode = "404", description = "Expenditure not found")
	public ResponseEntity<ExpenditureResponse> getById(
			@PathVariable @NotNull @Positive Long id) {
		logger.trace("Method start: getById for id={}", id);
		ExpenditureResponse response = expenditureService.getById(id);
		logger.info("Returning expenditure id={}", id);
		logger.trace("Method exit: getById for id={}", id);
		return ResponseEntity.ok(response);
	}

	// 3. Update expenditure
	@PutMapping("/expenditures/{id}")
	@Operation(summary = "Update expenditure", description = "Updates an existing expenditure")
	@ApiResponse(responseCode = "200", description = "Expenditure updated successfully")
	@ApiResponse(responseCode = "404", description = "Expenditure not found")
	public ResponseEntity<ExpenditureResponse> update(
			@PathVariable @NotNull @Positive Long id,
			@Valid @RequestBody ExpenditureRequest req) {
		logger.trace("Method start: update for id={}", id);
		logger.info("Received update for id={}. Payload: {}", id, req);
		ExpenditureResponse response = expenditureService.update(id, req);
		logger.info("Expenditure updated successfully for id={}. Response: {}", id, response);
		logger.trace("Method exit: update for id={}", id);
		return ResponseEntity.ok(response);
	}

	// 4. Delete expenditure
	@DeleteMapping("/expenditures/{id}")
	@Operation(summary = "Delete expenditure", description = "Deletes an expenditure record")
	@ApiResponse(responseCode = "204", description = "Expenditure deleted successfully")
	@ApiResponse(responseCode = "404", description = "Expenditure not found")
	public ResponseEntity<Void> delete(
			@PathVariable @NotNull @Positive Long id) {
		logger.trace("Method start: delete for id={}", id);
		expenditureService.delete(id);
		logger.info("Deleted expenditure id={}", id);
		logger.trace("Method exit: delete for id={}", id);
		return ResponseEntity.noContent().build();
	}

	// 5. List expenditures with filters
	@GetMapping("/expenditures")
	@Operation(summary = "List expenditures with filters", description = "Fetches paginated list of expenditures with optional filters")
	@ApiResponse(responseCode = "200", description = "List retrieved successfully")
	public ResponseEntity<Page<ExpenditureResponse>> list(
			@RequestParam(required = false) String farmerId,
			@RequestParam(required = false) @Positive(message = "Plot ID must be positive") Long plotId,
			@RequestParam(required = false) String category,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "20") int size) {
		logger.trace("Method start: list with filters farmerId={} plotId={} category={} startDate={} endDate={} page={} size={}",
				farmerId, plotId, category, startDate, endDate, page, size);
		Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "billDate"));
		Page<ExpenditureResponse> result = expenditureService.list(farmerId, plotId, category, startDate, endDate, pageable);
		logger.info("Returning {} expenditures (page {})", result.getNumberOfElements(), page);
		logger.trace("Method exit: list");
		return ResponseEntity.ok(result);
	}

	// 6. Expenditure summary
	@GetMapping("/expenditures/summary")
	@Operation(summary = "Get expenditure summary", description = "Fetches expenditure summary for a farmer and year")
	@ApiResponse(responseCode = "200", description = "Summary retrieved successfully")
	public ResponseEntity<ExpenditureSummaryDTO> summary(
			@RequestParam @NotNull(message = "farmerId is required") String farmerId,
			@RequestParam @Positive(message = "year must be positive") int year) {
		logger.trace("Method start: summary for farmerId={} year={}", farmerId, year);
		ExpenditureSummaryDTO response = expenditureService.summary(farmerId, year);
		logger.info("Expenditure summary retrieved for farmerId={} year={}", farmerId, year);
		logger.trace("Method exit: summary");
		return ResponseEntity.ok(response);
	}

	// 7. Category-wise report
	@GetMapping("/expenditures/report/category")
	@Operation(summary = "Get category-wise expenditure report", description = "Fetches yearly expenditure report by category")
	@ApiResponse(responseCode = "200", description = "Report retrieved successfully")
	public ResponseEntity<List<CategoryYearlyDTO>> categoryReport(
			@RequestParam @NotNull(message = "farmerId is required") String farmerId,
			@RequestParam @Positive(message = "year must be positive") int year) {
		logger.trace("Method start: categoryReport for farmerId={} year={}", farmerId, year);
		List<CategoryYearlyDTO> response = expenditureService.categoryReport(farmerId, year);
		logger.info("Category report retrieved for farmerId={} year={} with {} categories", farmerId, year, response.size());
		logger.trace("Method exit: categoryReport");
		return ResponseEntity.ok(response);
	}

}
