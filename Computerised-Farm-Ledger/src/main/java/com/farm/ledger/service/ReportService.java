package com.farm.ledger.service;

import com.farm.ledger.dto.report.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

public interface ReportService {

    // 1. Period summary (monthly/quarterly/yearly)
    List<PeriodSummaryDTO> periodSummary(String farmerId, LocalDate start, LocalDate end, String periodType); // periodType: MONTH/QUARTER/YEAR

    // 2. Farmer profitability (paginated)
    Page<FarmerProfitDTO> farmerProfitability(LocalDate start, LocalDate end, Pageable pageable);

    // 3. Plot profit/loss
    List<PlotProfitDTO> plotProfit(String farmerId, LocalDate start, LocalDate end);

    // 4. Crop profitability (group by Income.category)
    List<CropProfitDTO> cropProfitability(String farmerId, LocalDate start, LocalDate end);

    // 5. ROI per crop
    List<CropRoiDTO> roiPerCrop(String farmerId, LocalDate start, LocalDate end);
}

