package com.farm.ledger.entity;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity
@Table(name = "farmers")
@Getter
@Setter
@NoArgsConstructor
@ToString(exclude = "plots")
public class Farmer {

	@Id
    //@GeneratedValue(strategy = GenerationType.IDENTITY)
	@NotNull
    private String farmerId;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(unique = true)
    private String mobile;

    @Column(columnDefinition = "TEXT")
    private String address;

    @OneToMany(mappedBy = "farmer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Plot> plots = new ArrayList<>();
}

