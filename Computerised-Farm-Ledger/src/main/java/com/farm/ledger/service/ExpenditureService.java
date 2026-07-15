package com.farm.ledger.service;

import com.farm.ledger.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

public interface ExpenditureService {
	ExpenditureResponse create(ExpenditureRequest req);

	ExpenditureResponse getById(Long id);

	ExpenditureResponse update(Long id, ExpenditureRequest req);

	void delete(Long id);

	Page<ExpenditureResponse> list(String farmerId, Long plotId, String category, LocalDate startDate, LocalDate endDate,
			Pageable pageable);

	ExpenditureSummaryDTO summary(String farmerId, int year);

	List<CategoryYearlyDTO> categoryReport(String farmerId, int year);
}
