package com.kartikey.kartikey.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "form")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FormData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;   // ✅ fixed

    @Column(nullable = false)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WorkType workType;

    @Column(nullable = false)
    private String gid;

    @Column(nullable = false)
    private String decision;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public enum WorkType {
        NORMAL,
        REWORK
    }
}
