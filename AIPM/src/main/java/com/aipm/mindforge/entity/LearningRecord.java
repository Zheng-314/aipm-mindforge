package com.aipm.mindforge.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "learning_records",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "node_id"}))
@Data
public class LearningRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "node_id", nullable = false)
    private KnowledgeNode knowledgeNode;

    @Column(name = "mastery_level")
    private Integer masteryLevel = 0;

    @Column(name = "last_studied")
    private LocalDateTime lastStudied;

    @Column(name = "total_study_time")
    private Integer totalStudyTime = 0;

    @Column(name = "study_count")
    private Integer studyCount = 0;
}