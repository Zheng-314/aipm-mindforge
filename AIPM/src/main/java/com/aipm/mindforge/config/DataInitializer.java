package com.aipm.mindforge.config;

import com.aipm.mindforge.entity.KnowledgeNode;
import com.aipm.mindforge.repository.KnowledgeNodeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private KnowledgeNodeRepository knowledgeNodeRepository;

    @Override
    public void run(String... args) throws Exception {
        initKnowledgeTree();
    }

    private void initKnowledgeTree() {
        // 如果已经有数据，跳过
        if (knowledgeNodeRepository.count() > 0) {
            System.out.println("知识树已存在，跳过初始化");
            return;
        }

        System.out.println("正在初始化AI产品经理知识树...");

        // 1. 创建根节点
        KnowledgeNode root = new KnowledgeNode();
        root.setName("AI产品经理核心能力体系");
        root.setDescription("AI产品经理需要掌握的五大核心能力维度");
        root.setLevel(0);
        root.setCategory("ROOT");
        root.setIcon("🏆");
        root.setColor("#1890ff");
        root.setSortOrder(1);
        root = knowledgeNodeRepository.save(root);

        // 2. 创建五大能力领域
        createCategoryNodes(root, "战略与市场分析", "🎯", "#52c41a", 1);
        createCategoryNodes(root, "用户研究与体验设计", "👥", "#fa8c16", 2);
        createCategoryNodes(root, "技术理解与架构设计", "⚙️", "#722ed1", 3);
        createCategoryNodes(root, "数据与算法应用", "📊", "#13c2c2", 4);
        createCategoryNodes(root, "商业运营与产品增长", "📈", "#eb2f96", 5);

        System.out.println("知识树初始化完成！");
    }

    private void createCategoryNodes(KnowledgeNode parent, String categoryName, String icon, String color, int order) {
        KnowledgeNode categoryNode = new KnowledgeNode();
        categoryNode.setName(categoryName);
        categoryNode.setDescription(getCategoryDescription(categoryName));
        categoryNode.setParent(parent);
        categoryNode.setLevel(1);
        categoryNode.setCategory(categoryName);
        categoryNode.setIcon(icon);
        categoryNode.setColor(color);
        categoryNode.setSortOrder(order);
        categoryNode = knowledgeNodeRepository.save(categoryNode);

        // 为每个领域创建4个子节点
        String[] subNodes = getSubNodesForCategory(categoryName);
        for (int i = 0; i < subNodes.length; i++) {
            KnowledgeNode subNode = new KnowledgeNode();
            subNode.setName(subNodes[i]);
            subNode.setParent(categoryNode);
            subNode.setLevel(2);
            subNode.setCategory(categoryName);
            subNode.setSortOrder(i + 1);
            subNode.setEstimatedHours(10);
            knowledgeNodeRepository.save(subNode);
        }
    }

    private String getCategoryDescription(String category) {
        switch (category) {
            case "战略与市场分析": return "AI产品趋势洞察、市场分析、竞品研究、商业模式设计";
            case "用户研究与体验设计": return "用户需求分析、用户体验设计、用户测试、交互设计";
            case "技术理解与架构设计": return "AI技术原理、系统架构、API设计、技术选型";
            case "数据与算法应用": return "数据分析、算法评估、A/B测试、效果衡量";
            case "商业运营与产品增长": return "产品运营、用户增长、商业化策略、ROI分析";
            default: return "";
        }
    }

    private String[] getSubNodesForCategory(String category) {
        switch (category) {
            case "战略与市场分析":
                return new String[]{"AI行业趋势分析", "主流AI产品分析", "技术成熟度曲线", "商业模式创新"};
            case "用户研究与体验设计":
                return new String[]{"AI用户画像", "Prompt工程", "AI交互设计", "用户体验度量"};
            case "技术理解与架构设计":
                return new String[]{"机器学习基础", "深度学习原理", "大模型技术", "API设计与集成"};
            case "数据与算法应用":
                return new String[]{"数据分析方法", "算法效果评估", "A/B测试设计", "数据可视化"};
            case "商业运营与产品增长":
                return new String[]{"用户增长策略", "商业化模型", "ROI分析", "产品运营指标"};
            default:
                return new String[]{};
        }
    }
}