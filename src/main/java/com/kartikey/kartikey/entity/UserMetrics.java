package com.kartikey.kartikey.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "usermetrics")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserMetrics {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @Builder.Default
    @Column(name = "form_filled", nullable = false)
    private Long formFilled = 0L;

    @Builder.Default
    @Column(name = "form_checked", nullable = false)
    private Long formChecked = 0L;

    @Builder.Default
    @Column(name = "feedback_given", nullable = false)
    private Long feedbackGiven = 0L;

    @Builder.Default
    @Column(name = "score", nullable = false)
    private Double score = 0D;

    @Builder.Default
    @Column(name = "tl_score", nullable = false)
    private Double tlScore = 0D;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
