package com.unisinsight.lazytree.cache;

import com.unisinsight.lazytree.cache.condition.*;
import com.unisinsight.lazytree.cache.tree.*;
import com.unisinsight.lazytree.config.Constant;
import com.unisinsight.lazytree.exception.OutOfMaxsizeException;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.*;

public class TreeCache {
    private static ThreadLocal<Integer> currentThreadLeafCount = new ThreadLocal<>();

    private static Tree TREE;


    public static void init(TreeNode root) {
        TREE = new Tree(TreeNodeFactory.create(root));
    }

    public static void addNode(Integer parentId, TreeNode node){
        TREE.addNode(parentId, node, true);
    }

    public static void updateVideoRecord(String code, Integer status) {
        TREE.updateVideoRecordStatus(code, status);
    }

    public static void updateHaveTask(String code, Integer status){
        TREE.updateTaskStatus(code, status);
    }

    public static TreeNode getRoot(List<TypeCondition> includes) {
        return getChildren(TREE.getRoot().getId(), includes);
    }

    public static TreeNode getChildren(Integer id, List<TypeCondition> includes) {
        TreeNode node = TREE.get(id);
        TreeNode result = TreeNodeFactory.create(node);
        List<TreeNode> children = new ArrayList<>();

        for (TreeNode child : node.getChildren()) {
            if (accordConditionLeafType(includes, child)) {
                TreeNode target = TreeNodeFactory.create(child);

                int sum = 0;
                if (!CollectionUtils.isEmpty(child.getChildren())) {
                    for (TreeNode n : child.getChildren()) {
                        if (accordConditionLeafType(includes, n)) {
                            sum ++;
                        }
                    }
                }
                target.setSum(sum);
                children.add(target);
            }
        }
        result.setSum(children.size());
        result.setChildren(children);
        return result;
    }

    /**
     * 通过给定节点ID生成子树，且字数包含根节点
     * */
    public static Tree buildSubTree(List<Integer> nodeIds, List<Condition> conditions){
        currentThreadLeafCount.set(0);
        Tree newTree = null;
        for (Integer nodeId : nodeIds) {
            TreeNode currentNode = TREE.get(nodeId);

            if (newTree != null  && (newTree.contain(nodeId) || currentNode == null)) {
                continue;
            }
            Tree subTree = new Tree(TreeNodeFactory.createSimpleNode(currentNode));
            // 非叶子节点，判断是否可以生成子树
            if (new OrgNodeCondition().accord(currentNode)) {
                // 向下生成子孙节点
                boolean haveChild = downBuildSubTree(subTree, currentNode, conditions);
                if (!haveChild) {
                    continue;
                }
            }
            // 叶子节点，判断是否满足条件
            else if (!accordCondition(conditions, subTree.getRoot())){
                continue;
            }
            // 向上生成父节点
            while (true) {
                TreeNode parent = currentNode.getParent();
                if (parent == null) {
                    break;
                }
                TreeNode newRoot;
                // 父节点在子树中已存在
                if (newTree != null && (newRoot = newTree.get(parent.getId())) != null) {
                    newTree.linkTree(newRoot.getId(), subTree, false);
                    break;
                } else {
                    // 创建父节点
                    newRoot = TreeNodeFactory.createSimpleNode(parent);
                    subTree.exchangeRoot(newRoot, false);
                }
                currentNode = parent;
            }

            // 初始化root节点
            // 第一个棵子树必定会初始化到根节点结束
            if (newTree == null) {
                newTree = subTree;
            }
        }
        currentThreadLeafCount.remove();
        return newTree;
    }

    private static boolean accordCondition(List<Condition> conditions, TreeNode target) {
        if (currentThreadLeafCount.get() > Constant.LAZY_TREE_MAXSIZE) {
            throw new OutOfMaxsizeException("超过最大限制个数");
        }
        // condition为空默认不做条件限制
        if (CollectionUtils.isEmpty(conditions)) {
            currentThreadLeafCount.set(currentThreadLeafCount.get() + 1);
            return true;
        }

        // 判断录像
        if (accordConditionHaveTask(conditions, target)){
            return false;
        }

        // 判断解析任务
        if (!accordConditionVideoRecord(conditions, target)) {
            return false;
        }


        return accordConditionType(conditions, target);
    }

    private static boolean accordConditionLeafType(List<TypeCondition> conditions, TreeNode target) {
        if (CollectionUtils.isEmpty(conditions)) {
            return true;
        }
        if (target instanceof ChannelTreeNode) {
            return conditions.contains(target.getType());
        } else if (target instanceof OrgTreeNode) {
            for (TypeCondition condition : conditions) {
                if (((OrgTreeNode) target).getLeafTypes().contains(condition)) {
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean accordConditionType(List<Condition> conditions, TreeNode target) {
        int typeConditionSize = 0;
        for (Condition condition : conditions) {
            if (condition instanceof TypeCondition) {
                typeConditionSize ++;
                if (condition.accord(target)) {
                    currentThreadLeafCount.set(currentThreadLeafCount.get() + 1);
                    return true;
                }
            }
        }
        return typeConditionSize == 0;
    }

    private static boolean accordConditionVideoRecord(List<Condition> conditions, TreeNode target) {
        for (Condition condition : conditions) {
            if (condition instanceof VideoRecordCondition ) {
                return condition.accord(target);
            }
        }
        return true;
    }

    private static boolean accordConditionHaveTask(List<Condition> conditions, TreeNode target) {
        for (Condition condition : conditions) {
            if (condition instanceof HaveTaskCondition){
                return condition.accord(target);
            }
        }
        return false;
    }

    private static boolean downBuildSubTree(Tree tree, TreeNode currentNode, List<Condition> includes){
        if (!CollectionUtils.isEmpty(currentNode.getChildren())) {
            boolean haveChild = false;
            for (TreeNode node : currentNode.getChildren()) {
                Tree subTree = new Tree(TreeNodeFactory.createSimpleNode(node));
                boolean success = downBuildSubTree(subTree,  node, includes);
                if (success) {
                    tree.linkTree(currentNode.getId(),  subTree, false);
                    haveChild = true;
                }
            }
            return haveChild;
        } else {
            return accordCondition(includes, currentNode);
        }
    }
}
