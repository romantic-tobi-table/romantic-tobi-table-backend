package com.tomy.tomy.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "receipt", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"user_id", "store_id", "recognized_date"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Receipt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;

    @Column(name = "total_price", nullable = false)
    private Integer totalPrice;

    @Column(name = "recognized_text", nullable = false)
    private String recognizedText;

    @Column(name = "recognized_date", nullable = false)
    private LocalDate recognizedDate;

    @Column(nullable = false)
    private Boolean verified;

    @Column(name = "ocr_raw_json", columnDefinition = "TEXT")
    private String ocrRawJson;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}
