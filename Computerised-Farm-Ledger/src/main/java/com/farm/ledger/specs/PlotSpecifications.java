package com.farm.ledger.specs;

import org.springframework.data.jpa.domain.Specification;

import com.farm.ledger.entity.Plot;

public class PlotSpecifications {

	public static Specification<Plot> hasSoilType(String soilType) {
		return (root, query, cb) -> soilType == null ? cb.conjunction()
				: cb.equal(cb.lower(root.get("soilType")), soilType.toLowerCase());
	}

	public static Specification<Plot> hasOwnershipType(String ownershipType) {
		return (root, query, cb) -> {
			if (ownershipType == null)
				return cb.conjunction();
			try {
				// exact match with enum string
				return cb.equal(root.get("ownershipType"), ownershipType.toUpperCase());
			} catch (Exception e) {
				return cb.disjunction();
			}
		};
	}
}
