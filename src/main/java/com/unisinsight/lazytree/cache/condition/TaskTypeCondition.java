package com.unisinsight.lazytree.cache.condition;

import com.unisinsight.lazytree.cache.tree.ChannelTreeNode;
import com.unisinsight.lazytree.cache.tree.TreeNode;
import org.springframework.util.CollectionUtils;

public enum TaskTypeCondition implements Condition{
    image{
        @Override
        public boolean accord(TreeNode treeNode) {
            if (treeNode instanceof ChannelTreeNode) {
                if (!CollectionUtils.isEmpty(((ChannelTreeNode) treeNode).getTaskTypes())) {
                    return ((ChannelTreeNode) treeNode).getTaskTypes().contains(2);
                }
            }
            return false;
        }
    },
    video{
        @Override
        public boolean accord(TreeNode treeNode) {
            if (treeNode instanceof ChannelTreeNode) {
                if (!CollectionUtils.isEmpty(((ChannelTreeNode) treeNode).getTaskTypes())) {
                    return ((ChannelTreeNode) treeNode).getTaskTypes().contains(1);
                }
            }
            return false;
        }
    },
    video_record{
        @Override
        public boolean accord(TreeNode treeNode) {
            if (treeNode instanceof ChannelTreeNode) {
                return ((ChannelTreeNode) treeNode).getVideoRecord() == 1;
            }
            return false;
        }
    }
}
