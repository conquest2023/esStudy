package es.board.repository.entity;

import jakarta.persistence.*;
import jakarta.persistence.GeneratedValue;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Entity
    @Table(name = "resume")
    public class Resume {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @Column(nullable = false)
        private Long userId;

        @Column(nullable = false)
        private String title; // 이력서 제목

        @Column(nullable = false)
        private String name; // 이름

        private String phone;
        private String email;
        private String linkedin;
        private String github;

        @Lob
        private String skills; // 기술 스택 (JSON 저장)

        @Lob
        private String experience; // 경력 사항 (JSON 저장)

        @Lob
        private String education; // 학력 사항 (JSON 저장)

        @Column(updatable = false)
        private LocalDateTime createdAt;

    }
