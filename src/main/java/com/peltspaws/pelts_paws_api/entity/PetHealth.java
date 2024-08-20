package com.peltspaws.pelts_paws_api.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "pet_health")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PetHealth {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "pet_id", unique = true, nullable = false)
    private Pet pet;

    private Double weight;

    private Double height;

    private LocalDate lastCheckup;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "pet_vaccinations", joinColumns = @JoinColumn(name = "pet_health_id"))
    @Column(name = "vaccination")
    @Builder.Default
    private List<String> vaccinations = new ArrayList<>();

    @Column(columnDefinition = "TEXT")
    private String notes;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
}
