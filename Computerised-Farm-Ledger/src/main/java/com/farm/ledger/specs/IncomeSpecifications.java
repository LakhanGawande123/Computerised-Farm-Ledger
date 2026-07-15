package com.farm.ledger.specs;

import com.farm.ledger.entity.Income;
import com.farm.ledger.enumpkg.IncomeCategory;

import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

public class IncomeSpecifications {

	public static Specification<Income> farmerId(String farmerId) {
		return (root, query, cb) -> farmerId == null ? null : cb.equal(root.get("farmerId"), farmerId);
	}

	public static Specification<Income> plotId(Long plotId) {
		return (root, query, cb) -> plotId == null ? null : cb.equal(root.get("plotId"), plotId);
	}

	public static Specification<Income> dateBetween(LocalDate start, LocalDate end) {
		return (root, query, cb) -> {
			if (start == null && end == null)
				return null;
			if (start != null && end != null)
				return cb.between(root.get("receivedDate"), start, end);
			if (start != null)
				return cb.greaterThanOrEqualTo(root.get("receivedDate"), start);
			return cb.lessThanOrEqualTo(root.get("receivedDate"), end);
		};
	}
	
	public static Specification<Income> category(String category) {
	    return (root, query, cb) -> {
	        if (category == null || category.isBlank()) return cb.conjunction();
	        try {
	            IncomeCategory ic = IncomeCategory.valueOf(category.trim().toUpperCase());
	            return cb.equal(root.get("category"), ic);
	        } catch (IllegalArgumentException ex) {
	            // invalid category -> return false predicate so no rows match
	            return cb.disjunction();
	        }
	    };
	}
}
