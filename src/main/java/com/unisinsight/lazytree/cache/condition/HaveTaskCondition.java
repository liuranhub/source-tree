package com.unisinsight.lazytree.cache.condition;

import com.unisinsight.lazytree.cache.tree.ChannelTreeNode;
import com.unisinsight.lazytree.cache.tree.TreeNode;

public class HaveTaskCondition implements Condition {
    @Override
    public boolean accord(TreeNode treeNode) {
        if (treeNode instanceof ChannelTreeNode) {
            return ((ChannelTreeNode) treeNode).getHaveTask() == 1;
        }
        return false;
    }
}
