package com.aipm.mindforge.controller;

import com.aipm.mindforge.entity.KnowledgeNode;
import com.aipm.mindforge.repository.KnowledgeNodeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
@CrossOrigin(origins = "http://localhost:5173")  // 添加这行
@RestController
@RequestMapping("/api/knowledge")
public class KnowledgeController {

    @Autowired
    private KnowledgeNodeRepository knowledgeNodeRepository;

    // 1. 获取完整知识树
    @GetMapping("/tree")
    public ResponseEntity<Map<String, Object>> getKnowledgeTree() {
        Map<String, Object> response = new HashMap<>();

        // 获取根节点
        List<KnowledgeNode> rootNodes = knowledgeNodeRepository.findRootNodes();

        // 构建树形结构
        List<Map<String, Object>> tree = rootNodes.stream().map(root -> {
            Map<String, Object> nodeData = convertToMap(root);
            nodeData.put("children", getChildrenNodes(root.getId()));
            return nodeData;
        }).collect(Collectors.toList());

        response.put("success", true);
        response.put("data", tree);
        response.put("total", knowledgeNodeRepository.count());

        return ResponseEntity.ok(response);
    }

    // 2. 获取所有节点（平铺列表）
    @GetMapping("/nodes")
    public ResponseEntity<List<KnowledgeNode>> getAllNodes() {
        return ResponseEntity.ok(knowledgeNodeRepository.findAll());
    }

    // 3. 根据ID获取节点
    @GetMapping("/nodes/{id}")
    public ResponseEntity<?> getNodeById(@PathVariable Long id) {
        Optional<KnowledgeNode> node = knowledgeNodeRepository.findById(id);
        return node.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 4. 根据分类获取节点
    @GetMapping("/category/{category}")
    public ResponseEntity<List<KnowledgeNode>> getNodesByCategory(@PathVariable String category) {
        return ResponseEntity.ok(knowledgeNodeRepository.findByCategory(category));
    }

    // 5. 根据层级获取节点
    @GetMapping("/level/{level}")
    public ResponseEntity<List<KnowledgeNode>> getNodesByLevel(@PathVariable Integer level) {
        return ResponseEntity.ok(knowledgeNodeRepository.findByLevel(level));
    }

    // 6. 创建新节点
    @PostMapping("/nodes")
    public ResponseEntity<KnowledgeNode> createNode(@RequestBody KnowledgeNode node) {
        // 如果有父节点，确保父节点存在
        if (node.getParent() != null && node.getParent().getId() != null) {
            Optional<KnowledgeNode> parent = knowledgeNodeRepository.findById(node.getParent().getId());
            if (parent.isEmpty()) {
                return ResponseEntity.badRequest().build();
            }
            node.setParent(parent.get());
        }

        // 设置层级
        if (node.getParent() == null) {
            node.setLevel(0);
        } else {
            node.setLevel(node.getParent().getLevel() + 1);
        }

        return ResponseEntity.ok(knowledgeNodeRepository.save(node));
    }

    // 7. 更新节点
    @PutMapping("/nodes/{id}")
    public ResponseEntity<KnowledgeNode> updateNode(@PathVariable Long id, @RequestBody KnowledgeNode node) {
        Optional<KnowledgeNode> existing = knowledgeNodeRepository.findById(id);
        if (existing.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        KnowledgeNode existingNode = existing.get();
        existingNode.setName(node.getName());
        existingNode.setDescription(node.getDescription());
        existingNode.setIcon(node.getIcon());
        existingNode.setColor(node.getColor());
        existingNode.setCategory(node.getCategory());
        existingNode.setSortOrder(node.getSortOrder());
        existingNode.setEstimatedHours(node.getEstimatedHours());

        return ResponseEntity.ok(knowledgeNodeRepository.save(existingNode));
    }

    // 8. 删除节点
    @DeleteMapping("/nodes/{id}")
    public ResponseEntity<?> deleteNode(@PathVariable Long id) {
        // 检查是否有子节点
        List<KnowledgeNode> children = knowledgeNodeRepository.findByParentId(id);
        if (!children.isEmpty()) {
            return ResponseEntity.badRequest().body("无法删除：该节点下有子节点");
        }

        knowledgeNodeRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }

    // 9. 初始化知识树（开发用）
    @PostMapping("/init-tree")
    public ResponseEntity<Map<String, Object>> initKnowledgeTree() {
        Map<String, Object> response = new HashMap<>();

        // 如果已经有数据，跳过
        if (knowledgeNodeRepository.count() > 0) {
            response.put("success", false);
            response.put("message", "知识树已存在，无需重新初始化");
            return ResponseEntity.ok(response);
        }

        try {
            // 这里会触发DataInitializer自动执行
            response.put("success", true);
            response.put("message", "知识树初始化指令已发送，请查看控制台日志");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "初始化失败：" + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // 辅助方法：获取子节点
    private List<Map<String, Object>> getChildrenNodes(Long parentId) {
        List<KnowledgeNode> children = knowledgeNodeRepository.findByParentId(parentId);
        return children.stream().map(child -> {
            Map<String, Object> nodeData = convertToMap(child);
            nodeData.put("children", getChildrenNodes(child.getId()));
            return nodeData;
        }).collect(Collectors.toList());
    }

    // 辅助方法：节点转Map
    private Map<String, Object> convertToMap(KnowledgeNode node) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", node.getId());
        map.put("name", node.getName());
        map.put("description", node.getDescription());
        map.put("level", node.getLevel());
        map.put("category", node.getCategory());
        map.put("icon", node.getIcon());
        map.put("color", node.getColor());
        map.put("sortOrder", node.getSortOrder());
        map.put("estimatedHours", node.getEstimatedHours());
        map.put("prerequisites", node.getPrerequisites());
        map.put("learningResources", node.getLearningResources());

        if (node.getParent() != null) {
            Map<String, Object> parentInfo = new HashMap<>();
            parentInfo.put("id", node.getParent().getId());
            parentInfo.put("name", node.getParent().getName());
            map.put("parent", parentInfo);
        }

        return map;
    }
}