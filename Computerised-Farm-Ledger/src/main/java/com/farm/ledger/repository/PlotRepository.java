package com.farm.ledger.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import com.farm.ledger.entity.Plot;

public interface PlotRepository extends JpaRepository<Plot, Long>, JpaSpecificationExecutor<Plot> {
}
