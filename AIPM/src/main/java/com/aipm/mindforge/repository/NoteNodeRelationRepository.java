package com.aipm.mindforge.repository;

import com.aipm.mindforge.entity.NoteNodeRelation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface NoteNodeRelationRepository extends JpaRepository<NoteNodeRelation, Long> {

    List<NoteNodeRelation> findByNoteId(Long noteId);

    List<NoteNodeRelation> findByKnowledgeNodeId(Long nodeId);

    void deleteByNoteId(Long noteId);
}