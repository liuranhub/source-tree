package com.unisinsight.lazytree.cache.tree;

import com.unisinsight.lazytree.cache.condition.NodeType;
import lombok.Data;

import java.util.Set;

@Data
public class ChannelTreeNode extends TreeNode{
    private NodeType parentType;
    private String code;
    private String tcode;
    private int status;
    //是否有录像 0:无 1:有
    private int videoRecord;
    //解析任务类型 1-实时，2-图片
    private Set<Integer> taskTypes;
    //是否级联 0:无 1:有
    private int cascaded;
}
