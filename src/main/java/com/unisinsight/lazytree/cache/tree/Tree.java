package com.unisinsight.lazytree.cache.tree;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Tree {
    private TreeNode root;
    private Map<Integer, TreeNode> cacheIndex = new HashMap<>();

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

        if (node instanceof ChannelTreeNode) {
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
    }

    public void exchangeRoot(TreeNode newRoot, boolean linkParent) {
        if (linkParent) {
            root.setParent(newRoot);
        }
        newRoot.addChild(root);
        root = newRoot;
        cacheIndex.put(newRoot.getId(), root);
    }

    public void updateTaskStatus(Integer id, Integer taskStatus){
        TreeNode node = cacheIndex.get(id);
        if (node instanceof ChannelTreeNode) {
            ((ChannelTreeNode) node).setHaveTask(taskStatus);
        }
    }

    public void updateVideoRecordStatus(Integer id, Integer videoRecordStatus) {
        TreeNode node = cacheIndex.get(id);
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
