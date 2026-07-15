package com.farm.ledger.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.farm.ledger.enumpkg.PaymentMode;

import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name = "expenditures")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Expenditure {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;


	@Column(name = "farmer_id", nullable = false)
	private String farmerId; // FK to farmers.farmer_id


	@Column(name = "plot_id")
	private Long plotId; // nullable


	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 40)
	private com.farm.ledger.enumpkg.ExpenseCategory category;


	@Column(nullable = false, precision = 14, scale = 2)
	private BigDecimal amount;


	@Enumerated(EnumType.STRING)
	@Column(length = 20)
	private PaymentMode paymentMode;


	@Column(length = 1000)
	private String description;


	@Column(name = "bill_date", nullable = false)
	private LocalDate billDate;


	@Column(name = "created_at", nullable = false)
	private LocalDateTime createdAt;


	@Column(name = "updated_at")
	private LocalDateTime updatedAt;


	@PrePersist
	public void prePersist() {
	createdAt = LocalDateTime.now();
	}


	@PreUpdate
	public void preUpdate() {
	updatedAt = LocalDateTime.now();
	}

}
