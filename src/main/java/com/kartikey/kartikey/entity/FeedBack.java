package com.kartikey.kartikey.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "feedback")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FeedBack {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "form_id", nullable = false)
    private FormData formData;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "agent_id", nullable = false)
    private UserEntity agent;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "qc_id")
    private UserEntity qcReviewer;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "tl_id")
    private UserEntity teamLead;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private Status status = Status.OPEN;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        updatedAt = LocalDateTime.now();
    }

    public enum Status {
        OPEN,
        AGENTACTIONREQUIRED,
        QAREVIEWPENDING,
        ACTION
    }
}
