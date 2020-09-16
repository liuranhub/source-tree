package com.unisinsight.lazytree.cache.tree;

import com.unisinsight.lazytree.model.ResourceTreeModel;
import org.springframework.beans.BeanUtils;
import org.springframework.util.StringUtils;

public class TreeNodeFactory {

    public static TreeNode create(TreeNode node) {
        TreeNode treeNode;
        if (node instanceof ChannelTreeNode) {
            treeNode = new ChannelTreeNode();
        } else {
            treeNode = new OrgTreeNode();
        }
        BeanUtils.copyProperties(node, treeNode);
        treeNode.setParent(null);
        treeNode.setChildren(null);

        return treeNode;
    }

    public static TreeNode create(ResourceTreeModel.TreeNode node){

        TreeNode treeNode = null;

        if (isChannel(node)) {
            treeNode = new ChannelTreeNode();
            ((ChannelTreeNode)treeNode).setCode(node.getResourceCode());
            if (StringUtils.isEmpty(node.getCascadedId())) {
                ((ChannelTreeNode)treeNode).setCascaded(0);
            } else {
                ((ChannelTreeNode)treeNode).setCascaded(1);
            }

            ((ChannelTreeNode)treeNode).setHaveTask(0);
            ((ChannelTreeNode)treeNode).setVideoRecord(0);
            ((ChannelTreeNode)treeNode).setStatus(Integer.parseInt(node.getStatus()));
        } else {
            treeNode = new OrgTreeNode();
        }
        treeNode.setId(node.getId());
        treeNode.setName(node.getResourceName());
        treeNode.setType(Integer.parseInt(node.getType()));
        treeNode.setSum(node.getChildNum());

        if (!StringUtils.isEmpty(node.getSubType())){
            treeNode.setSubType(Integer.parseInt(node.getSubType()));
        }

        return treeNode;
    }

    private static boolean isChannel(ResourceTreeModel.TreeNode node) {
        if ("6".equals(node.getType()) || "20".equals(node.getType())){
            return true;
        }
        return false;
    }
}
