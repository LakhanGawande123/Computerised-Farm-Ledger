package com.farm.ledger.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

import com.farm.ledger.enumpkg.IncomeCategory;
import com.farm.ledger.enumpkg.PaymentMode;

@Entity
@Table(name = "income")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Income {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String farmerId;
	private Long plotId;

	@Enumerated(EnumType.STRING)
	private IncomeCategory category;

	private Double amount;

	@Enumerated(EnumType.STRING)
	private PaymentMode paymentMode;

	private LocalDate receivedDate;

	@Column(length = 500)
	private String description;
}
