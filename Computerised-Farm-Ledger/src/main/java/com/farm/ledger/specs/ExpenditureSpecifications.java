package com.farm.ledger.specs;

import com.farm.ledger.entity.Expenditure;
import com.farm.ledger.enumpkg.ExpenseCategory;

import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

public class ExpenditureSpecifications {

    public static Specification<Expenditure> farmerEquals(String farmerId) {
        return (root, query, cb) -> farmerId == null ? cb.conjunction() : cb.equal(root.get("farmerId"), farmerId);
    }

    public static Specification<Expenditure> plotEquals(Long plotId) {
        return (root, query, cb) -> plotId == null ? cb.conjunction() : cb.equal(root.get("plotId"), plotId);
    }

    public static Specification<Expenditure> categoryEquals(String category) {
        return (root, query, cb) -> {
            if (category == null) return cb.conjunction();
            try {
                ExpenseCategory.valueOf(category.toUpperCase());
                return cb.equal(root.get("category"), category.toUpperCase());
            } catch (Exception ex) {
                // invalid category -> return false predicate
                return cb.disjunction();
            }
        };
    }

    public static Specification<Expenditure> billDateBetween(LocalDate start, LocalDate end) {
        return (root, query, cb) -> {
            if (start == null && end == null) return cb.conjunction();
            if (start != null && end != null) return cb.between(root.get("billDate"), start, end);
            if (start != null) return cb.greaterThanOrEqualTo(root.get("billDate"), start);
            return cb.lessThanOrEqualTo(root.get("billDate"), end);
        };
    }
}
