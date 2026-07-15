package com.farm.ledger.service.impl;

import com.farm.ledger.dto.*;
import com.farm.ledger.entity.Income;
import com.farm.ledger.enumpkg.IncomeCategory;
import com.farm.ledger.enumpkg.PaymentMode;
import com.farm.ledger.exception.BadRequestException;
import com.farm.ledger.exception.ResourceNotFoundException;
import com.farm.ledger.repository.IncomeRepository;
import com.farm.ledger.service.IncomeService;
import com.farm.ledger.specs.IncomeSpecifications;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
public class IncomeServiceImpl implements IncomeService {

    private final IncomeRepository incomeRepository;

    @Override
    public IncomeResponse create(IncomeRequest req) {
        // basic validation already via @Valid, but double-check enums
        IncomeCategory category = parseCategory(req.getCategory());
        PaymentMode paymentMode = parsePaymentMode(req.getPaymentMode()); // may be null

        Income income = Income.builder()
                .farmerId(req.getFarmerId())
                .plotId(req.getPlotId())
                .category(category)
                .amount(req.getAmount())
                .paymentMode(paymentMode)
                .receivedDate(req.getReceivedDate()) // already LocalDate
                .description(req.getDescription())
                .build();

        incomeRepository.save(income);
        return mapToResponse(income);
    }

    @Override
    public IncomeResponse getById(Long id) {
        Income income = incomeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Income not found: " + id));
        return mapToResponse(income);
    }

    @Override
    public IncomeResponse update(Long id, IncomeRequest req) {
        Income income = incomeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Income not found: " + id));

        IncomeCategory category = parseCategory(req.getCategory());
        PaymentMode paymentMode = parsePaymentMode(req.getPaymentMode());

        income.setFarmerId(req.getFarmerId());
        income.setPlotId(req.getPlotId());
        income.setCategory(category);
        income.setAmount(req.getAmount());
        income.setPaymentMode(paymentMode);
        income.setReceivedDate(req.getReceivedDate());
        income.setDescription(req.getDescription());

        incomeRepository.save(income);
        return mapToResponse(income);
    }

    /* helpers */
    private IncomeCategory parseCategory(String cat) {
        if (cat == null) throw new IllegalArgumentException("category is required");
        try {
            return IncomeCategory.valueOf(cat.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new BadRequestException("Invalid category: " + cat);
        }
    }

    private PaymentMode parsePaymentMode(String pm) {
        if (pm == null || pm.isBlank()) return null;
        try {
            return PaymentMode.valueOf(pm.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new BadRequestException("Invalid paymentMode: " + pm);
        }
    }

    @Override
    public void delete(Long id) {
        if (!incomeRepository.existsById(id))
            throw new ResourceNotFoundException("Income not found: " + id);
        incomeRepository.deleteById(id);
    }

    @Override
    public Page<IncomeResponse> list(String farmerId, Long plotId, String category,
                                     LocalDate startDate, LocalDate endDate, Pageable pageable) {

        Specification<Income> spec = Specification
                .where(IncomeSpecifications.farmerId(farmerId))
                .and(IncomeSpecifications.plotId(plotId))
                .and(IncomeSpecifications.category(category))
                .and(IncomeSpecifications.dateBetween(startDate, endDate));

        Page<Income> page = incomeRepository.findAll(spec, pageable);

        return page.map(this::mapToResponse);
    }

    @Override
    public IncomeSummaryDTO summary(String farmerId, int year) {
        Double total = incomeRepository.getYearlyIncome(farmerId, year);
        return IncomeSummaryDTO.builder()
                .farmerId(farmerId)
                .year(year)
                .totalAmount(total != null ? total : 0.0)
                .build();
    }

    @Override
    public List<CategoryYearlyIncomeDTO> categoryReport(String farmerId, int year) {
        List<Object[]> data = incomeRepository.getCategoryWiseYearlyReport(farmerId, year);

        List<CategoryYearlyIncomeDTO> list = new ArrayList<>();

        for (Object[] row : data) {
            list.add(new CategoryYearlyIncomeDTO(
                    (IncomeCategory) row[0],
                    year,
                    (Double) row[1]
            ));
        }

        return list;
    }


    private IncomeResponse mapToResponse(Income i) {
        return IncomeResponse.builder()
                .id(i.getId())
                .farmerId(i.getFarmerId())
                .plotId(i.getPlotId())
                .category(i.getCategory())
                .amount(i.getAmount())
                .paymentMode(i.getPaymentMode())
                .receivedDate(i.getReceivedDate())
                .description(i.getDescription())
                .build();
    }
}

