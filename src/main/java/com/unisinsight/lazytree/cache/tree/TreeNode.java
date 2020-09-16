package com.unisinsight.lazytree.cache.tree;


import lombok.Data;
import org.springframework.util.CollectionUtils;

import java.util.LinkedList;
import java.util.List;

@Data
public class TreeNode {
    private int id;
    private String name;
    private int type;
    private int subType;
    private TreeNode parent;
    private int sum;
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
