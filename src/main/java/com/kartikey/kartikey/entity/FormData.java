package com.kartikey.kartikey.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "form")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FormData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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

    @OneToMany(mappedBy = "formData", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<FeedBack> feedbackList;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public enum WorkType {
        NORMAL,
        REWORK
    }
}
