package com.farm.ledger.service.impl;

import com.farm.ledger.dto.report.*;
import com.farm.ledger.entity.Income;
import com.farm.ledger.entity.Expenditure;
import com.farm.ledger.repository.IncomeRepository;
import com.farm.ledger.repository.ExpenditureRepository;
import com.farm.ledger.service.ReportService;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final IncomeRepository incomeRepository;
    private final ExpenditureRepository expenditureRepository;

    // helper zero
    private static final BigDecimal ZERO = BigDecimal.ZERO;

    @Override
    public List<PeriodSummaryDTO> periodSummary(String farmerId, LocalDate start, LocalDate end, String periodType) {
        if (start == null || end == null) throw new IllegalArgumentException("start and end required");

        Map<String, PeriodSummaryDTO> map = new LinkedHashMap<>();

        if ("MONTH".equalsIgnoreCase(periodType) || "AUTO".equalsIgnoreCase(periodType)) {
            List<Object[]> incomeRows = incomeRepository.sumIncomeByMonth(farmerId, start, end);
            for (Object[] r : incomeRows) {
                // r[0] sum, r[1] year, r[2] month
                BigDecimal sum = toBigDecimal(r[0]);
                Integer year = toInteger(r[1]);
                Integer month = toInteger(r[2]);
                String key = year + "-" + String.format("%02d", month);
                PeriodSummaryDTO dto = map.computeIfAbsent(key, k -> new PeriodSummaryDTO(year, month, null, ZERO, ZERO, ZERO));
                dto.setTotalIncome(dto.getTotalIncome().add(sum));
                dto.setNet(dto.getTotalIncome().subtract(dto.getTotalExpense()));
            }
            List<Object[]> expRows = expenditureRepository.sumExpenseByMonth(nullifyFarmerIdForExpense(farmerId), start, end);
            for (Object[] r : expRows) {
                BigDecimal sum = toBigDecimal(r[0]);
                Integer year = toInteger(r[1]);
                Integer month = toInteger(r[2]);
                String key = year + "-" + String.format("%02d", month);
                PeriodSummaryDTO dto = map.computeIfAbsent(key, k -> new PeriodSummaryDTO(year, month, null, ZERO, ZERO, ZERO));
                dto.setTotalExpense(dto.getTotalExpense().add(sum));
                dto.setNet(dto.getTotalIncome().subtract(dto.getTotalExpense()));
            }
        } else if ("QUARTER".equalsIgnoreCase(periodType)) {
            List<Object[]> incomeRows = incomeRepository.sumIncomeByQuarter(farmerId, start, end);
            for (Object[] r : incomeRows) {
                BigDecimal sum = toBigDecimal(r[0]);
                Integer year = toInteger(r[1]);
                Integer quarter = toInteger(r[2]);
                String key = year + "-Q" + quarter;
                PeriodSummaryDTO dto = map.computeIfAbsent(key, k -> new PeriodSummaryDTO(year, null, quarter, ZERO, ZERO, ZERO));
                dto.setTotalIncome(dto.getTotalIncome().add(sum));
                dto.setNet(dto.getTotalIncome().subtract(dto.getTotalExpense()));
            }
            List<Object[]> expRows = expenditureRepository.sumExpenseByQuarter(nullifyFarmerIdForExpense(farmerId), start, end);
            for (Object[] r : expRows) {
                BigDecimal sum = toBigDecimal(r[0]);
                Integer year = toInteger(r[1]);
                Integer quarter = toInteger(r[2]);
                String key = year + "-Q" + quarter;
                PeriodSummaryDTO dto = map.computeIfAbsent(key, k -> new PeriodSummaryDTO(year, null, quarter, ZERO, ZERO, ZERO));
                dto.setTotalExpense(dto.getTotalExpense().add(sum));
                dto.setNet(dto.getTotalIncome().subtract(dto.getTotalExpense()));
            }
        } else if ("YEAR".equalsIgnoreCase(periodType)) {
            // we'll use month queries and collapse by year or add dedicated year queries
            List<Object[]> incomeRows = incomeRepository.sumIncomeByMonth(farmerId, start, end);
            for (Object[] r : incomeRows) {
                BigDecimal sum = toBigDecimal(r[0]); Integer year = toInteger(r[1]);
                String key = String.valueOf(year);
                PeriodSummaryDTO dto = map.computeIfAbsent(key, k -> new PeriodSummaryDTO(year, null, null, ZERO, ZERO, ZERO));
                dto.setTotalIncome(dto.getTotalIncome().add(sum));
                dto.setNet(dto.getTotalIncome().subtract(dto.getTotalExpense()));
            }
            List<Object[]> expRows = expenditureRepository.sumExpenseByMonth(nullifyFarmerIdForExpense(farmerId), start, end);
            for (Object[] r : expRows) {
                BigDecimal sum = toBigDecimal(r[0]); Integer year = toInteger(r[1]);
                String key = String.valueOf(year);
                PeriodSummaryDTO dto = map.computeIfAbsent(key, k -> new PeriodSummaryDTO(year, null, null, ZERO, ZERO, ZERO));
                dto.setTotalExpense(dto.getTotalExpense().add(sum));
                dto.setNet(dto.getTotalIncome().subtract(dto.getTotalExpense()));
            }
        } else {
            throw new IllegalArgumentException("Unknown periodType. Use MONTH / QUARTER / YEAR");
        }

        return new ArrayList<>(map.values());
    }

    @Override
    public Page<FarmerProfitDTO> farmerProfitability(LocalDate start, LocalDate end, Pageable pageable) {
        if (start == null || end == null) throw new IllegalArgumentException("start and end required");

        // Income grouped by farmer
        List<Object[]> incomeRows = incomeRepository.findIncomeSumGroupedByFarmer(start, end);
        Map<String, BigDecimal> incomeMap = incomeRows.stream()
                .collect(Collectors.toMap(r -> String.valueOf(r[0]), r -> toBigDecimal(r[1])));

        // Expense grouped by farmer (note Expenditure.farmerId type — adjust if Long)
        List<Object[]> expRows = expenditureRepository.findExpenseSumGroupedByFarmer(start, end);
        Map<String, BigDecimal> expMap = expRows.stream()
                .collect(Collectors.toMap(r -> String.valueOf(r[0]), r -> toBigDecimal(r[1])));

        // union keys
        Set<String> allFarmers = new HashSet<>();
        allFarmers.addAll(incomeMap.keySet());
        allFarmers.addAll(expMap.keySet());

        List<FarmerProfitDTO> list = allFarmers.stream().map(f -> {
            BigDecimal inc = incomeMap.getOrDefault(f, ZERO);
            BigDecimal exp = expMap.getOrDefault(f, ZERO);
            return new FarmerProfitDTO(f, inc, exp, inc.subtract(exp));
        }).sorted(Comparator.comparing(FarmerProfitDTO::getProfit).reversed()).collect(Collectors.toList());

        // manual pagination
        int startIdx = (int) pageable.getOffset();
        int endIdx = Math.min(startIdx + pageable.getPageSize(), list.size());
        List<FarmerProfitDTO> pageContent = startIdx < list.size() ? list.subList(startIdx, endIdx) : Collections.emptyList();

        return new PageImpl<>(pageContent, pageable, list.size());
    }

    @Override
    public List<PlotProfitDTO> plotProfit(String farmerId, LocalDate start, LocalDate end) {
        if (start == null || end == null) throw new IllegalArgumentException("start and end required");

        List<Object[]> incRows = incomeRepository.findIncomeSumGroupedByPlot(start, end, farmerId);
        Map<Long, BigDecimal> incMap = incRows.stream().collect(Collectors.toMap(r -> (Long) r[0], r -> toBigDecimal(r[1])));

        List<Object[]> expRows = expenditureRepository.findExpenseSumGroupedByPlot(start, end, farmerId);
        Map<Long, BigDecimal> expMap = expRows.stream().collect(Collectors.toMap(r -> (Long) r[0], r -> toBigDecimal(r[1])));

        Set<Long> allPlots = new HashSet<>();
        allPlots.addAll(incMap.keySet());
        allPlots.addAll(expMap.keySet());

        List<PlotProfitDTO> out = new ArrayList<>();
        for (Long p : allPlots) {
            BigDecimal inc = incMap.getOrDefault(p, ZERO);
            BigDecimal exp = expMap.getOrDefault(p, ZERO);
            // farmerId maybe null here; try to get from expense/income row? For brevity return farmerId as null.
            out.add(new PlotProfitDTO(p, null, inc, exp, inc.subtract(exp)));
        }
        return out;
    }

    @Override
    public List<CropProfitDTO> cropProfitability(String farmerId, LocalDate start, LocalDate end) {
        if (start == null || end == null) throw new IllegalArgumentException("start and end required");

        // incomes grouped by income category
        List<Object[]> incRows = incomeRepository.sumIncomeByCategoryBetween(farmerId, start, end);
        Map<String, BigDecimal> incMap = new HashMap<>();
        for (Object[] r : incRows) {
            String cat = r[0] == null ? "UNKNOWN" : r[0].toString();
            incMap.put(cat, toBigDecimal(r[1]));
        }

        // expenses grouped by expense category - map by name to income category names if they match
        List<Object[]> expRows = expenditureRepository.sumExpenseByCategoryBetween(nullifyFarmerIdForExpense(farmerId), start, end);
        Map<String, BigDecimal> expMap = new HashMap<>();
        for (Object[] r : expRows) {
            String cat = r[0] == null ? "UNKNOWN" : r[0].toString();
            expMap.put(cat, toBigDecimal(r[1]));
        }

        // Build crop list: income categories are primary
        List<CropProfitDTO> result = new ArrayList<>();
        for (String incCat : incMap.keySet()) {
            BigDecimal rev = incMap.getOrDefault(incCat, ZERO);
            BigDecimal cost = expMap.getOrDefault(incCat, ZERO); // matches by same name
            BigDecimal profit = rev.subtract(cost);
            result.add(new CropProfitDTO(incCat, rev, cost, profit));
        }
        // include expense-only categories (if any)
        for (String expCat : expMap.keySet()) {
            if (!incMap.containsKey(expCat)) {
                BigDecimal rev = ZERO;
                BigDecimal cost = expMap.get(expCat);
                result.add(new CropProfitDTO(expCat, rev, cost, rev.subtract(cost)));
            }
        }
        return result;
    }

    @Override
    public List<CropRoiDTO> roiPerCrop(String farmerId, LocalDate start, LocalDate end) {
        List<CropProfitDTO> profits = cropProfitability(farmerId, start, end);
        List<CropRoiDTO> out = new ArrayList<>();
        for (CropProfitDTO p : profits) {
            BigDecimal revenue = p.getRevenue();
            BigDecimal cost = p.getCost();
            BigDecimal roi = null;
            if (cost != null && cost.compareTo(ZERO) > 0) {
                roi = (revenue.subtract(cost)).divide(cost, 6, BigDecimal.ROUND_HALF_UP);
            }
            out.add(new CropRoiDTO(p.getCropCategory(), revenue, cost, roi));
        }
        return out;
    }

    /* -------------------- helpers -------------------- */

    private BigDecimal toBigDecimal(Object o) {
        if (o == null) return ZERO;
        if (o instanceof BigDecimal) return (BigDecimal) o;
        if (o instanceof Number) return BigDecimal.valueOf(((Number) o).doubleValue());
        try { return new BigDecimal(o.toString()); } catch (Exception ex) { return ZERO; }
    }

    private Integer toInteger(Object o) {
        if (o == null) return null;
        if (o instanceof Number) return ((Number) o).intValue();
        try { return Integer.parseInt(o.toString()); } catch (Exception ex) { return null; }
    }

    private Long nullifyFarmerIdForExpense(String farmerId) {
        // some repo methods expect Long farmerId for expenditures — convert or return null
        // if your Expenditure.farmerId is Long, parse; else if it's String, adjust the query signatures accordingly.
        if (farmerId == null) return null;
        try { return Long.valueOf(farmerId); } catch (Exception ex) { return null; }
    }
}

