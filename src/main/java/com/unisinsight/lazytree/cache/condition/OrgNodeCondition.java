package com.unisinsight.lazytree.cache.condition;

import com.unisinsight.lazytree.cache.tree.TreeNode;
import org.springframework.util.CollectionUtils;

public class OrgNodeCondition implements Condition{
    @Override
    public boolean accord(TreeNode treeNode) {
        return !CollectionUtils.isEmpty(treeNode.getChildren());
    }
}
