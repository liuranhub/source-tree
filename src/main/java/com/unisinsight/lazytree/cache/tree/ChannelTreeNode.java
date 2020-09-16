package com.unisinsight.lazytree.cache.tree;

import lombok.Data;

@Data
public class ChannelTreeNode extends TreeNode{
    private String code;
    private int status;
    //是否有录像 0:无 1:有
    private int videoRecord;
    //是否有解析任务 0:无 1:有
    private int haveTask;
    //是否级联 0:无 1:有
    private int cascaded;
}
