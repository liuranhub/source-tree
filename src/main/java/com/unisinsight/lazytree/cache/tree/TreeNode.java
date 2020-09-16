package com.unisinsight.lazytree.cache.tree;


import com.unisinsight.lazytree.cache.condition.TypeCondition;
import lombok.Data;
import org.springframework.util.CollectionUtils;

import java.util.LinkedList;
import java.util.List;

@Data
public class TreeNode {
    private Integer id;
    private String name;
    private TypeCondition type;
    private TreeNode parent;
    private Integer sum;
    private List<TreeNode> children;

    public boolean leafNode(){
        if (CollectionUtils.isEmpty(children)) {
            return true;
        }

        return false;
    }


    synchronized public void addChild(TreeNode node) {
        if (children == null) {
            children = new LinkedList<>();
        }

        children.add(node);
    }

    synchronized public void deleteNode(TreeNode node) {
        if (children != null) {
            children.remove(node);
        }
    }
}
