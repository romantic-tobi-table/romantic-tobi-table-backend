package com.tomy.tomy.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "greeting_log", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"user_id", "greeted_date"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GreetingLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "greeted_date", nullable = false)
    private LocalDate greetedDate;

    @Column(name = "point_awarded", nullable = false)
    private Integer pointAwarded;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}
