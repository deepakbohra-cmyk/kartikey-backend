package com.kartikey.kartikey.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(unique = true, nullable = false)
    private String email;

    private String provider;
    private String providerId;

    @Column(nullable = false)
    private String location;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Column(name = "tl_email")
    private String tlEmail;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    // Reverse mappings
    @OneToMany(mappedBy = "agent")
    private List<FeedBack> feedbacksAsAgent;

    @OneToMany(mappedBy = "qcReviewer")
    private List<FeedBack> feedbacksAsQC;

    @OneToMany(mappedBy = "teamLead")
    private List<FeedBack> feedbacksAsTL;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public enum Role {
        ADMIN,
        SUPERADMIN,
        L1TEAM,
        QCTEAM
    }
}
