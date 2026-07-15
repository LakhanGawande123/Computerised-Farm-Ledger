package com.farm.ledger.controller;


import com.farm.ledger.dto.report.*;
import com.farm.ledger.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    // 1. Period summary
    @GetMapping("/period")
    public List<PeriodSummaryDTO> periodSummary(
            @RequestParam(required = false) String farmerId,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate,
            @RequestParam(defaultValue = "MONTH") String periodType
    ) {
        // defaults
        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("startDate and endDate are required");
        }
        return reportService.periodSummary(farmerId, startDate, endDate, periodType);
    }

    // 2. Farmer profitability
    @GetMapping("/farmer-profitability")
    public org.springframework.data.domain.Page<FarmerProfitDTO> farmerProfitability(
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        if (startDate == null || endDate == null) throw new IllegalArgumentException("startDate and endDate required");
        return reportService.farmerProfitability(startDate, endDate, PageRequest.of(page, size));
    }

    // 3. Plot profit
    @GetMapping("/plot-profit")
    public List<PlotProfitDTO> plotProfit(
            @RequestParam(required = false) String farmerId,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate
    ) {
        if (startDate == null || endDate == null) throw new IllegalArgumentException("startDate and endDate required");
        String fId = null;
        try { fId = farmerId == null ? null : String.valueOf(farmerId); } catch (Exception ex) { /* leave null */ }
        return reportService.plotProfit(fId, startDate, endDate);
    }

    // 4. Crop profitability
    @GetMapping("/crop-profitability")
    public List<CropProfitDTO> cropProfitability(
            @RequestParam(required = false) String farmerId,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate
    ) {
        if (startDate == null || endDate == null) throw new IllegalArgumentException("startDate and endDate required");
        return reportService.cropProfitability(farmerId, startDate, endDate);
    }

    // 5. ROI per crop
    @GetMapping("/roi-per-crop")
    public List<CropRoiDTO> roiPerCrop(
            @RequestParam(required = false) String farmerId,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate
    ) {
        if (startDate == null || endDate == null) throw new IllegalArgumentException("startDate and endDate required");
        return reportService.roiPerCrop(farmerId, startDate, endDate);
    }
}

