package com.aipm.mindforge.repository;

import com.aipm.mindforge.entity.KnowledgeNode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface KnowledgeNodeRepository extends JpaRepository<KnowledgeNode, Long> {

    List<KnowledgeNode> findByParentId(Long parentId);

    List<KnowledgeNode> findByLevel(Integer level);

    List<KnowledgeNode> findByCategory(String category);

    @Query("SELECT n FROM KnowledgeNode n WHERE n.parent IS NULL")
    List<KnowledgeNode> findRootNodes();
}