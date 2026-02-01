package com.aipm.mindforge.repository;

import com.aipm.mindforge.entity.LearningRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface LearningRecordRepository extends JpaRepository<LearningRecord, Long> {

    List<LearningRecord> findByUserId(Long userId);

    Optional<LearningRecord> findByUserIdAndKnowledgeNodeId(Long userId, Long nodeId);

    @Query("SELECT lr FROM LearningRecord lr WHERE lr.user.id = :userId AND lr.masteryLevel >= 3")
    List<LearningRecord> findMasteredNodesByUserId(Long userId);
}