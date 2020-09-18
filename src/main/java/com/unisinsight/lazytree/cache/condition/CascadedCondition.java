package com.unisinsight.lazytree.cache.condition;

import com.unisinsight.lazytree.cache.tree.ChannelTreeNode;
import com.unisinsight.lazytree.cache.tree.TreeNode;

public class CascadedCondition implements Condition {
    @Override
    public boolean accord(TreeNode treeNode) {
        if (treeNode instanceof ChannelTreeNode) {
            return ((ChannelTreeNode) treeNode).getCascaded() == 1;
        }
        return false;
    }
}
