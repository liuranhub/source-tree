package com.unisinsight.lazytree.cache.condition;

import com.unisinsight.lazytree.cache.tree.OrgTreeNode;
import com.unisinsight.lazytree.cache.tree.TreeNode;

public class OrgNodeCondition implements Condition{
    @Override
    public boolean accord(TreeNode treeNode) {
        if (treeNode instanceof OrgTreeNode) {
            return true;
        }
        return false;
    }
}
