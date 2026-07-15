package com.farm.ledger.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.farm.ledger.entity.Farmer;

public interface FarmerRepository extends JpaRepository<Farmer, String> {
}
