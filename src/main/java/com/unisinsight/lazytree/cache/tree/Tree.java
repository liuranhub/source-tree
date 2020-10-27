package com.unisinsight.lazytree.cache.tree;

import com.unisinsight.lazytree.cache.condition.BizType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.util.*;

public class Tree {

    private static Logger LOG = LoggerFactory.getLogger(Tree.class);

    private String treeId;
    private TreeNode root;
    private Map<Integer, TreeNode> cacheIndex = new HashMap<>();
    private Map<String, TreeNode> leafNodeIndex = new HashMap<>();

    public Tree(TreeNode treeNode) {
        treeId = UUID.randomUUID().toString();
        root = treeNode;
        cacheIndex.put(root.getId(), root);
    }

    public void clear(){
        root = null;
        cacheIndex.clear();
        cacheIndex.clear();
    }

    public String getTreeId() {
        return treeId;
    }

    public TreeNode getRoot() {
        return root;
    }

    public TreeNode get(String code){
        return leafNodeIndex.get(code);
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

        //FIXME 优化代码逻辑
        if (linkParent) {
            // 更新叶子节点数据
            if (node instanceof ChannelTreeNode) {
                leafNodeIndex.put(((ChannelTreeNode) node).getCode(),  node);
                if (node.getParent() != null && node.getParent().getParent() != null) {
                    TreeNode tollgate = node.getParent().getParent();
                    if (tollgate instanceof TollgateTreeNode) {
                        ((ChannelTreeNode) node).setParentType(tollgate.getNodeType());
                        ((ChannelTreeNode) node).setTcode(((TollgateTreeNode) tollgate).getCode());
                    }
                }
            }

            Set<BizType> bizTypes = new HashSet<>();
            bizTypes.add(BizType.common);
            for (BizType bizType : BizType.values()) {
                if (bizType.accord(node)) {
                    bizTypes.add(bizType);
                }
            }

            TreeNode currentNode = node;
            while (currentNode != null) {
                currentNode.getBizType().addAll(bizTypes);
                currentNode = currentNode.getParent();
            }
        }
    }

    public void linkTree(Integer parentId, Tree subTree, boolean linkParent) {
        addNode(parentId, subTree.getRoot(), linkParent);
        cacheIndex.putAll(subTree.cacheIndex);
        leafNodeIndex.putAll(subTree.leafNodeIndex);
    }

    public void exchangeRoot(TreeNode newRoot, boolean linkParent) {
        if (linkParent) {
            root.setParent(newRoot);
        }
        newRoot.addChild(root);
        root = newRoot;
        cacheIndex.put(newRoot.getId(), root);
    }

    public void updateTaskStatus(String code, Set<Integer> taskStatus){
        TreeNode node = leafNodeIndex.get(code);
        if (node == null) {
            LOG.info("更新任务类型节点不存在 code:{}", code);
            return;
        }
        if (node instanceof ChannelTreeNode) {
            ((ChannelTreeNode) node).setTaskTypes(taskStatus);
        }
    }

    public void updateVideoRecordStatus(String code, Integer videoRecordStatus) {
        TreeNode node = leafNodeIndex.get(code);
        updateParentVideoRecord(node, videoRecordStatus == 1);
        if (node == null) {
            LOG.info("更新录像计划节点不存在 code:{} status:{}", code, videoRecordStatus);
            return;
        }
        if (node instanceof ChannelTreeNode) {
            ((ChannelTreeNode) node).setVideoRecord(videoRecordStatus);
        }
    }

    private void updateParentVideoRecord(TreeNode node, boolean isAdd) {
        TreeNode currentNode = node;
        while (currentNode != null) {
            if (isAdd) {
                if (currentNode.getBizType().contains(BizType.video_record)) {
                    return;
                }
                currentNode.getBizType().add(BizType.video_record);
            } else {
                if (!CollectionUtils.isEmpty(currentNode.getChildren())) {
                    for (TreeNode child : currentNode.getChildren()) {
                        if (child.getBizType().contains(BizType.video_record)) {
                            return;
                        }
                    }
                }

                currentNode.getBizType().remove(BizType.video_record);
            }
            currentNode = currentNode.getParent();
        }
    }

}
