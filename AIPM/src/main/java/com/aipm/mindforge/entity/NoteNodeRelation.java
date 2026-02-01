package com.aipm.mindforge.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "note_node_relations",
        uniqueConstraints = @UniqueConstraint(columnNames = {"note_id", "node_id"}))
@Data
public class NoteNodeRelation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "note_id", nullable = false)
    private Note note;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "node_id", nullable = false)
    private KnowledgeNode knowledgeNode;

    @Column(name = "relation_strength")
    private Integer relationStrength = 2;
}