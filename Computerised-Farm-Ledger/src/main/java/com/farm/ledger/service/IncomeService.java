package com.farm.ledger.service;

import com.farm.ledger.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

public interface IncomeService {

    IncomeResponse create(IncomeRequest req);
    IncomeResponse getById(Long id);
    IncomeResponse update(Long id, IncomeRequest req);
    void delete(Long id);

    Page<IncomeResponse> list(String farmerId, Long plotId, String category,
                              LocalDate startDate, LocalDate endDate, Pageable pageable);

    IncomeSummaryDTO summary(String farmerId, int year);

    List<CategoryYearlyIncomeDTO> categoryReport(String farmerId, int year);
}

