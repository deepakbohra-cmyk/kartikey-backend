    package com.kartikey.kartikey.entity;

    import com.fasterxml.jackson.annotation.JsonIgnore;
    import jakarta.persistence.*;
    import lombok.AllArgsConstructor;
    import lombok.Builder;
    import lombok.Data;
    import lombok.NoArgsConstructor;

    import java.time.LocalDateTime;

    @Entity
    @Table(name = "qcform")
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public class QcFormData {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "form_id", nullable = false)
        @JsonIgnore
        private FormData formId;

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

        @PrePersist
        protected void onCreate() {
            createdAt = LocalDateTime.now();
        }

        public enum WorkType {
            NORMAL,
            REWORK
        }
    }
