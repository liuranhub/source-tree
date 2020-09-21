package com.unisinsight.lazytree.cache.condition;

import com.unisinsight.lazytree.cache.tree.ChannelTreeNode;
import com.unisinsight.lazytree.cache.tree.TreeNode;

public enum BizType implements Condition{
    common{
        @Override
        public boolean accord(TreeNode treeNode) {
            return true;
        }
    },
    // 视频任务
    // 6_1、6_4、6_8
    video(){
        @Override
        public boolean accord(TreeNode treeNode) {
//            if (NodeType.COLLECT_DEVICE.accord(treeNode)) {
//                return false;
//            }
            if (NodeType.CHANNEL_1.equals(treeNode.getNodeType())
                    || NodeType.CHANNEL_4.equals(treeNode.getNodeType())
                    || NodeType.CHANNEL_8.equals(treeNode.getNodeType())){
                return true;
            }
            return false;
        }
    },
    image{
        @Override
        public boolean accord(TreeNode treeNode) {
            if (treeNode instanceof ChannelTreeNode) {
                boolean accord = false;

//                // 卡扣设备，这两个条件和下面的重复
//                if (NodeType.TOLLGATE_CAR.equals(((ChannelTreeNode) treeNode).getParentType())
//                        || NodeType.TOLLGATE_FACE.equals(((ChannelTreeNode) treeNode).getParentType())) {
//                    accord = true;
//                }
//
//                // 门禁
//                if (NodeType.ENTRANCE_GUARD.equals(treeNode.getNodeType())){
//                    accord = true;
//                }

                // 本级域设备
                if (NodeType.CHANNEL_5.equals(treeNode.getNodeType())
                        || NodeType.CHANNEL_4.equals(treeNode.getNodeType())
                        || NodeType.CHANNEL_8.equals(treeNode.getNodeType())){
                    accord = true;
                }

                // 采集器
                if (NodeType.COLLECT_DEVICE.equals(treeNode.getNodeType())) {
                    accord = true;
                }

                // 下级级联上来的设备
                if (new CascadedCondition().accord(treeNode)) {
                    accord = true;
                }
                return accord;
            }
            return false;
        }
    },
    // 在线录像任务资源树
    video_record{
        @Override
        public boolean accord(TreeNode treeNode) {
            if (NodeType.CHANNEL_1.equals(treeNode.getNodeType())
                || NodeType.CHANNEL_4.equals(treeNode.getNodeType())
                || NodeType.CHANNEL_8.equals(treeNode.getNodeType())){
                return true;
            }
            return false;
        }
    };
}
