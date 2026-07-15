package com.farm.ledger.controller;

import com.farm.ledger.dto.*;
import com.farm.ledger.service.IncomeService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/incomes")
@RequiredArgsConstructor
public class IncomeController {

    private final IncomeService incomeService;

    @PostMapping
    public IncomeResponse create(@Valid @RequestBody IncomeRequest req) {
        return incomeService.create(req);
    }

    @GetMapping("/{id}")
    public IncomeResponse get(@PathVariable Long id) {
        return incomeService.getById(id);
    }

    @PutMapping("/{id}")
    public IncomeResponse update(@PathVariable Long id, @Valid @RequestBody IncomeRequest req) {
        return incomeService.update(id, req);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        incomeService.delete(id);
    }

    
    @GetMapping
    public Page<IncomeResponse> list(
            @RequestParam(required = false) String farmerId,
            @RequestParam(required = false) Long plotId,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            Pageable pageable
    ) {
        return incomeService.list(farmerId, plotId, category, startDate, endDate, pageable);
    }

    @GetMapping("/summary")
    public IncomeSummaryDTO summary(@RequestParam String farmerId,
                                    @RequestParam int year) {
        return incomeService.summary(farmerId, year);
    }

    @GetMapping("/report/category")
    public List<CategoryYearlyIncomeDTO> categoryReport(
            @RequestParam String farmerId,
            @RequestParam int year
    ) {
        return incomeService.categoryReport(farmerId, year);
    }
}

