package com.farm.ledger.service.impl;

import com.farm.ledger.dto.*;
import com.farm.ledger.entity.Expenditure;
import com.farm.ledger.enumpkg.ExpenseCategory;
import com.farm.ledger.enumpkg.PaymentMode;
import com.farm.ledger.exception.BadRequestException;
import com.farm.ledger.exception.ResourceNotFoundException;
import com.farm.ledger.repository.ExpenditureRepository;
import com.farm.ledger.repository.FarmerRepository;
import com.farm.ledger.repository.PlotRepository;
import com.farm.ledger.service.ExpenditureService;
import com.farm.ledger.specs.ExpenditureSpecifications;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ExpenditureServiceImpl implements ExpenditureService {

    private final ExpenditureRepository expenditureRepository;
    // repositories for existence checks (assumed available in project)
    private final FarmerRepository farmerRepository;
    private final PlotRepository plotRepository;

    @Override
    public ExpenditureResponse create(ExpenditureRequest req) {
        validateCreateRequest(req);

        Expenditure e = new Expenditure();
        e.setFarmerId(req.getFarmerId());
        e.setPlotId(req.getPlotId());
        e.setCategory(parseCategory(req.getCategory()));
        e.setAmount(req.getAmount());
        e.setPaymentMode(parsePaymentMode(req.getPaymentMode()));
        e.setDescription(req.getDescription());
        e.setBillDate(req.getBillDate());

        Expenditure saved = expenditureRepository.save(e);
        return toDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public ExpenditureResponse getById(Long id) {
        Expenditure e = expenditureRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Expenditure not found with id " + id));
        return toDto(e);
    }

    @Override
    public ExpenditureResponse update(Long id, ExpenditureRequest req) {
        Expenditure e = expenditureRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Expenditure not found with id " + id));

        validateCreateRequest(req); // same validations

        e.setFarmerId(req.getFarmerId());
        e.setPlotId(req.getPlotId());
        e.setCategory(parseCategory(req.getCategory()));
        e.setAmount(req.getAmount());
        e.setPaymentMode(parsePaymentMode(req.getPaymentMode()));
        e.setDescription(req.getDescription());
        e.setBillDate(req.getBillDate());

        Expenditure saved = expenditureRepository.save(e);
        return toDto(saved);
    }

    @Override
    public void delete(Long id) {
        Expenditure e = expenditureRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Expenditure not found with id " + id));
        expenditureRepository.delete(e);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ExpenditureResponse> list(String farmerId, Long plotId, String category, LocalDate startDate, LocalDate endDate, Pageable pageable) {
        Specification<Expenditure> spec = Specification.where(ExpenditureSpecifications.farmerEquals(farmerId))
                .and(ExpenditureSpecifications.plotEquals(plotId))
                .and(ExpenditureSpecifications.categoryEquals(category))
                .and(ExpenditureSpecifications.billDateBetween(startDate, endDate));

        Page<Expenditure> page = expenditureRepository.findAll(spec, pageable);
        return page.map(this::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public ExpenditureSummaryDTO summary(String farmerId, int year) {
        // validate farmer exists
        if (!farmerRepository.existsById(farmerId)) {
            throw new ResourceNotFoundException("Farmer not found with id " + farmerId);
        }

        BigDecimal total = expenditureRepository.sumByFarmerAndYear(farmerId, year);
        List<Object[]> rows = expenditureRepository.sumByCategoryForYear(farmerId, year);

        Map<String, BigDecimal> catMap = rows.stream()
                .collect(Collectors.toMap(r -> r[0].toString(), r -> (BigDecimal) r[1]));

        ExpenditureSummaryDTO dto = new ExpenditureSummaryDTO();
        dto.setFarmerId(farmerId);
        dto.setYear(year);
        dto.setTotalExpense(total == null ? BigDecimal.ZERO : total);
        dto.setCategoryWise(catMap);
        return dto;
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryYearlyDTO> categoryReport(String farmerId, int year) {
        if (!farmerRepository.existsById(farmerId)) {
            throw new ResourceNotFoundException("Farmer not found with id " + farmerId);
        }
        List<Object[]> rows = expenditureRepository.sumByCategoryForYear(farmerId, year);
        List<CategoryYearlyDTO> out = new ArrayList<>();
        for (Object[] r : rows) {
            out.add(new CategoryYearlyDTO(r[0].toString(), (BigDecimal) r[1]));
        }
        return out;
    }

    /* ---------- helpers ---------- */

    private void validateCreateRequest(ExpenditureRequest req) {
        if (req.getFarmerId() == null) throw new BadRequestException("farmerId is required");
        if (req.getCategory() == null || req.getCategory().isBlank()) throw new BadRequestException("category is required");
        if (req.getAmount() == null || req.getAmount().compareTo(BigDecimal.ZERO) <= 0) throw new BadRequestException("amount must be > 0");
        if (req.getBillDate() == null) throw new BadRequestException("billDate is required");

        // farmer existence
        if (!farmerRepository.existsById(req.getFarmerId())) {
            throw new ResourceNotFoundException("Farmer not found with id " + req.getFarmerId());
        }

        // if plotId provided, validate plot exists and belongs to farmer
        if (req.getPlotId() != null) {
            if (!plotRepository.existsById(req.getPlotId())) {
                throw new ResourceNotFoundException("Plot not found with id " + req.getPlotId());
            }
            // optional: ensure owner match
            plotRepository.findById(req.getPlotId()).ifPresent(plot -> {
                if (plot.getFarmer() != null && !Objects.equals(plot.getFarmer().getFarmerId(), req.getFarmerId())) {
                    throw new BadRequestException("Plot " + req.getPlotId() + " does not belong to Farmer " + req.getFarmerId());
                }
            });
        }
    }

    private ExpenseCategory parseCategory(String cat) {
        try {
            return ExpenseCategory.valueOf(cat.toUpperCase());
        } catch (Exception ex) {
            throw new BadRequestException("Invalid category: " + cat);
        }
    }

    private PaymentMode parsePaymentMode(String pm) {
        if (pm == null) return null;
        try {
            return PaymentMode.valueOf(pm.toUpperCase());
        } catch (Exception ex) {
            throw new BadRequestException("Invalid paymentMode: " + pm);
        }
    }

    private ExpenditureResponse toDto(Expenditure e) {
        ExpenditureResponse r = new ExpenditureResponse();
        r.setId(e.getId());
        r.setFarmerId(e.getFarmerId());
        r.setPlotId(e.getPlotId());
        r.setCategory(e.getCategory() == null ? null : e.getCategory().name());
        r.setAmount(e.getAmount());
        r.setPaymentMode(e.getPaymentMode() == null ? null : e.getPaymentMode().name());
        r.setDescription(e.getDescription());
        r.setBillDate(e.getBillDate());
        r.setCreatedAt(e.getCreatedAt());
        r.setUpdatedAt(e.getUpdatedAt());
        return r;
    }
}
