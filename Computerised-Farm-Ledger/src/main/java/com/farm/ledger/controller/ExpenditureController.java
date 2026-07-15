package com.farm.ledger.controller;

import com.farm.ledger.dto.*;
import com.farm.ledger.service.ExpenditureService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/api/v1/expenditures")
@RequiredArgsConstructor
public class ExpenditureController {

	private final ExpenditureService service;

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public ExpenditureResponse create(@RequestBody ExpenditureRequest req) {
		return service.create(req);
	}

	@GetMapping("/{id}")
	public ExpenditureResponse getById(@PathVariable Long id) {
		return service.getById(id);
	}

	@PutMapping("/{id}")
	public ExpenditureResponse update(@PathVariable Long id, @RequestBody ExpenditureRequest req) {
		return service.update(id, req);
	}

	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void delete(@PathVariable Long id) {
		service.delete(id);
	}

	@GetMapping
	public Page<ExpenditureResponse> list(@RequestParam(required = false) String farmerId,
			@RequestParam(required = false) Long plotId, @RequestParam(required = false) String category,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
			@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size) {
		Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "billDate"));
		return service.list(farmerId, plotId, category, startDate, endDate, pageable);
	}

	@GetMapping("/summary")
	public ExpenditureSummaryDTO summary(@RequestParam String farmerId, @RequestParam int year) {
		return service.summary(farmerId, year);
	}

	@GetMapping("/report/category")
	public List<CategoryYearlyDTO> categoryReport(@RequestParam String farmerId, @RequestParam int year) {
		return service.categoryReport(farmerId, year);
	}
	
}
