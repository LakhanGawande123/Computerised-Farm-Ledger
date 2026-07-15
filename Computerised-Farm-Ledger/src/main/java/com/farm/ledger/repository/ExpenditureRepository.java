package com.farm.ledger.repository;

import com.farm.ledger.entity.Expenditure;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface ExpenditureRepository extends JpaRepository<Expenditure, Long>, JpaSpecificationExecutor<Expenditure> {

	@Query("SELECT COALESCE(SUM(e.amount),0) FROM Expenditure e WHERE e.farmerId = :farmerId AND FUNCTION('YEAR', e.billDate) = :year")
	BigDecimal sumByFarmerAndYear(@Param("farmerId") String farmerId, @Param("year") int year);

	@Query("SELECT e.category, COALESCE(SUM(e.amount),0) FROM Expenditure e WHERE e.farmerId = :farmerId AND FUNCTION('YEAR', e.billDate) = :year GROUP BY e.category")
	List<Object[]> sumByCategoryForYear(@Param("farmerId") String farmerId, @Param("year") int year);
	
	
// >>>>>> New Functionality: Expense / Income Ledger
	
	@Query("SELECT COALESCE(SUM(e.amount),0) FROM Expenditure e WHERE (:farmerId IS NULL OR e.farmerId = :farmerId) AND e.billDate BETWEEN :start AND :end")
	BigDecimal sumExpenseBetween(@Param("farmerId") Long farmerId /*note: if farmerId type is Long change accordingly*/, @Param("start") LocalDate start, @Param("end") LocalDate end);

	@Query("SELECT e.category, COALESCE(SUM(e.amount),0) FROM Expenditure e WHERE (:farmerId IS NULL OR e.farmerId = :farmerId) AND e.billDate BETWEEN :start AND :end GROUP BY e.category")
	List<Object[]> sumExpenseByCategoryBetween(@Param("farmerId") Long farmerId, @Param("start") LocalDate start, @Param("end") LocalDate end);

	@Query("SELECT COALESCE(SUM(e.amount),0), FUNCTION('YEAR', e.billDate), FUNCTION('MONTH', e.billDate) " +
	       "FROM Expenditure e WHERE (:farmerId IS NULL OR e.farmerId = :farmerId) AND e.billDate BETWEEN :start AND :end " +
	       "GROUP BY FUNCTION('YEAR', e.billDate), FUNCTION('MONTH', e.billDate) ORDER BY FUNCTION('YEAR', e.billDate), FUNCTION('MONTH', e.billDate)")
	List<Object[]> sumExpenseByMonth(@Param("farmerId") Long farmerId, @Param("start") LocalDate start, @Param("end") LocalDate end);

	@Query("SELECT COALESCE(SUM(e.amount),0), FUNCTION('YEAR', e.billDate), FUNCTION('QUARTER', e.billDate) " +
	       "FROM Expenditure e WHERE (:farmerId IS NULL OR e.farmerId = :farmerId) AND e.billDate BETWEEN :start AND :end " +
	       "GROUP BY FUNCTION('YEAR', e.billDate), FUNCTION('QUARTER', e.billDate) ORDER BY FUNCTION('YEAR', e.billDate), FUNCTION('QUARTER', e.billDate)")
	List<Object[]> sumExpenseByQuarter(@Param("farmerId") Long farmerId, @Param("start") LocalDate start, @Param("end") LocalDate end);
	
	@Query("SELECT e.farmerId, COALESCE(SUM(e.amount),0) FROM Expenditure e WHERE e.billDate BETWEEN :start AND :end GROUP BY e.farmerId")
	List<Object[]> findExpenseSumGroupedByFarmer(@Param("start") LocalDate start, @Param("end") LocalDate end);

	@Query("SELECT e.plotId, COALESCE(SUM(e.amount),0) FROM Expenditure e WHERE (:farmerId IS NULL OR e.farmerId = :farmerId) AND e.billDate BETWEEN :start AND :end GROUP BY e.plotId")
	List<Object[]> findExpenseSumGroupedByPlot(@Param("start") LocalDate start, @Param("end") LocalDate end, @Param("farmerId") String farmerId);

}
