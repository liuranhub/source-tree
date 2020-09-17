package com.unisinsight.lazytree.cache.tree;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Tree {
    private TreeNode root;
    private Map<Integer, TreeNode> cacheIndex = new HashMap<>();
    private Map<String, TreeNode> codeIndex = new HashMap<>();

    public Tree(TreeNode treeNode) {
        root = treeNode;
        cacheIndex.put(root.getId(), root);
    }

    public TreeNode getRoot() {
        return root;
    }

    public TreeNode get(Integer id) {
        return cacheIndex.get(id);
    }

    public boolean contain(Integer id) {
        return cacheIndex.containsKey(id);
    }

    public void addNode(Integer parentId, TreeNode node, boolean linkParent) {
        if (cacheIndex.get(parentId) == null || cacheIndex.get(node.getId()) != null) {
            return;
        }

        TreeNode parent = cacheIndex.get(parentId);
        parent.addChild(node);
        if (linkParent){
            node.setParent(parent);
        }
        cacheIndex.put(node.getId(), node);

        // 更新夫节点LeafTypes类型
        if (node instanceof ChannelTreeNode) {
            codeIndex.put(((ChannelTreeNode) node).getCode(),  node);
            parent = node.getParent();
            while (parent != null) {
                if (parent instanceof OrgTreeNode) {
                    ((OrgTreeNode) parent).addLeafTypes(node.getType());
                }
                parent = parent.getParent();
            }
        }
    }

    public void linkTree(Integer parentId, Tree subTree, boolean linkParent) {
        addNode(parentId, subTree.getRoot(), linkParent);
        cacheIndex.putAll(subTree.cacheIndex);
        codeIndex.putAll(subTree.codeIndex);
    }

    public void exchangeRoot(TreeNode newRoot, boolean linkParent) {
        if (linkParent) {
            root.setParent(newRoot);
        }
        newRoot.addChild(root);
        root = newRoot;
        cacheIndex.put(newRoot.getId(), root);
    }

    public void updateTaskStatus(String code, Integer taskStatus){
        TreeNode node = codeIndex.get(code);
        if (node == null) {
            return;
        }
        if (node instanceof ChannelTreeNode) {
            ((ChannelTreeNode) node).setHaveTask(taskStatus);
        }
    }

    public void updateVideoRecordStatus(String code, Integer videoRecordStatus) {
        TreeNode node = codeIndex.get(code);
        if (node == null) {
            return;
        }
        if (node instanceof ChannelTreeNode) {
            ((ChannelTreeNode) node).setVideoRecord(videoRecordStatus);
        }
    }

    public List<TreeNode> getChildren(Integer id) {
        TreeNode node = cacheIndex.get(id);
        if (node == null) {
            return new ArrayList<>();
        }
        List<TreeNode> result = new ArrayList<>(node.getChildren().size());
        for (TreeNode child : node.getChildren()){
            result.add(TreeNodeFactory.create(child));
        }

        return result;
    }
}
