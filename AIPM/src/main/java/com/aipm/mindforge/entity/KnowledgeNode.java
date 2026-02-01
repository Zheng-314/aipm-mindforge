package com.aipm.mindforge.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "knowledge_nodes")
@Data
public class KnowledgeNode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private KnowledgeNode parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
    private List<KnowledgeNode> children = new ArrayList<>();

    @Column(nullable = false)
    private Integer level = 0;

    private String icon;
    private String color;

    @Column(name = "category")
    private String category;

    @Column(name = "sort_order")
    private Integer sortOrder = 0;

    @Column(name = "estimated_hours")
    private Integer estimatedHours = 0;

    private String prerequisites;
    private String learningResources;
}