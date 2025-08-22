package com.tomy.tomy.domain;

import com.tomy.tomy.enums.ClothesStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_clothes", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"pet_id", "clothes_id"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserClothes {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pet_id", nullable = false)
    private Pet pet;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "clothes_id", nullable = false)
    private Clothes clothes;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 16)
    private ClothesStatus status;

    @Column(name = "purchased_at", nullable = false)
    private LocalDateTime purchasedAt;
}
