package com.farm.ledger.repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import org.springframework.data.repository.query.Param;
import com.farm.ledger.entity.Income;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IncomeRepository extends JpaRepository<Income, Long>, JpaSpecificationExecutor<Income> {

	@Query("SELECT COALESCE(SUM(i.amount),0) FROM Income i WHERE i.farmerId = :farmerId AND FUNCTION('YEAR', i.receivedDate) = :year")
	Double getYearlyIncome(@Param("farmerId") String farmerId, @Param("year") int year);

	@Query("SELECT i.category, COALESCE(SUM(i.amount),0) FROM Income i WHERE i.farmerId = :farmerId AND FUNCTION('YEAR', i.receivedDate) = :year GROUP BY i.category")
	List<Object[]> getCategoryWiseYearlyReport(@Param("farmerId") String farmerId, @Param("year") int year);
	
	
// >>>>>> New Functionality: Expense / Income Ledger
	
	@Query("SELECT COALESCE(SUM(i.amount),0) FROM Income i WHERE (:farmerId IS NULL OR i.farmerId = :farmerId) AND i.receivedDate BETWEEN :start AND :end")
	BigDecimal sumIncomeBetween(@Param("farmerId") String farmerId, @Param("start") LocalDate start, @Param("end") LocalDate end);

	@Query("SELECT i.category, COALESCE(SUM(i.amount),0) FROM Income i WHERE (:farmerId IS NULL OR i.farmerId = :farmerId) AND i.receivedDate BETWEEN :start AND :end GROUP BY i.category")
	List<Object[]> sumIncomeByCategoryBetween(@Param("farmerId") String farmerId, @Param("start") LocalDate start, @Param("end") LocalDate end);

	@Query("SELECT COALESCE(SUM(i.amount),0), FUNCTION('YEAR', i.receivedDate), FUNCTION('MONTH', i.receivedDate) " +
	       "FROM Income i WHERE (:farmerId IS NULL OR i.farmerId = :farmerId) AND i.receivedDate BETWEEN :start AND :end " +
	       "GROUP BY FUNCTION('YEAR', i.receivedDate), FUNCTION('MONTH', i.receivedDate) ORDER BY FUNCTION('YEAR', i.receivedDate), FUNCTION('MONTH', i.receivedDate)")
	List<Object[]> sumIncomeByMonth(@Param("farmerId") String farmerId, @Param("start") LocalDate start, @Param("end") LocalDate end);

	@Query("SELECT COALESCE(SUM(i.amount),0), FUNCTION('YEAR', i.receivedDate), FUNCTION('QUARTER', i.receivedDate) " +
	       "FROM Income i WHERE (:farmerId IS NULL OR i.farmerId = :farmerId) AND i.receivedDate BETWEEN :start AND :end " +
	       "GROUP BY FUNCTION('YEAR', i.receivedDate), FUNCTION('QUARTER', i.receivedDate) ORDER BY FUNCTION('YEAR', i.receivedDate), FUNCTION('QUARTER', i.receivedDate)")
	List<Object[]> sumIncomeByQuarter(@Param("farmerId") String farmerId, @Param("start") LocalDate start, @Param("end") LocalDate end);
	
	@Query("SELECT i.farmerId, COALESCE(SUM(i.amount),0) FROM Income i WHERE i.receivedDate BETWEEN :start AND :end GROUP BY i.farmerId")
	List<Object[]> findIncomeSumGroupedByFarmer(@Param("start") LocalDate start, @Param("end") LocalDate end);

	@Query("SELECT i.plotId, COALESCE(SUM(i.amount),0) FROM Income i WHERE (:farmerId IS NULL OR i.farmerId = :farmerId) AND i.receivedDate BETWEEN :start AND :end GROUP BY i.plotId")
	List<Object[]> findIncomeSumGroupedByPlot(@Param("start") LocalDate start, @Param("end") LocalDate end, @Param("farmerId") String farmerId);
}

