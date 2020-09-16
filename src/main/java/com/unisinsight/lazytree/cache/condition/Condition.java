package com.unisinsight.lazytree.cache.condition;

import com.unisinsight.lazytree.cache.tree.TreeNode;

public interface Condition {
    boolean accord(TreeNode treeNode);
}
