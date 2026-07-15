package com.farm.ledger.entity;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@Entity
@Table(name = "plots")
@Getter
@Setter
@NoArgsConstructor
@ToString
public class Plot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long plotId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "farmer_id", nullable = false)
    private Farmer farmer;

    @Column(name = "plot_name", nullable = false)
    private String plotName;

    private String surveyNumber;
    private String gatNumber;

    //@Column(precision = 10, scale = 2)
    private Double area;

    private String soilType;
    private String waterSource;
    private String irrigationType;

    @Column(columnDefinition = "TEXT")
    private String location;

    @Enumerated(EnumType.STRING)
    @Column(length = 10)
    private OwnershipType ownershipType;

    private LocalDate leaseStart;
    private LocalDate leaseEnd;

    public enum OwnershipType {
        OWNED, LEASED
    }
}

