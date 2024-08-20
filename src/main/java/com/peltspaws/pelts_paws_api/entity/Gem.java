package com.peltspaws.pelts_paws_api.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "gems")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Gem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pet_id", nullable = false)
    private Pet pet;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private GemType type;

    @Column(nullable = false)
    private Integer amount;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime earnedAt;

    public enum GemType {
        DAILY, ACHIEVEMENT, BONUS
    }
}
