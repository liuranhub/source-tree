package com.unisinsight.lazytree.cache.tree;


import com.unisinsight.lazytree.cache.condition.BizType;
import com.unisinsight.lazytree.cache.condition.NodeType;
import lombok.Data;
import org.springframework.util.CollectionUtils;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

@Data
public class TreeNode {
    private Integer id;
    private String name;
    private NodeType nodeType;
    private TreeNode parent;
    private Integer sum;
    private List<TreeNode> children;
    private Set<BizType> bizType = new HashSet<>();


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public NodeType getNodeType() {
        return nodeType;
    }

    public void setNodeType(NodeType nodeType) {
        this.nodeType = nodeType;
    }

    public TreeNode getParent() {
        return parent;
    }

    public void setParent(TreeNode parent) {
        this.parent = parent;
    }

    public Integer getSum() {
        return sum;
    }

    public void setSum(Integer sum) {
        this.sum = sum;
    }

    public List<TreeNode> getChildren() {
        return children;
    }

    public void setChildren(List<TreeNode> children) {
        this.children = children;
    }

    public Set<BizType> getBizType() {
        return bizType;
    }

    public void setBizType(Set<BizType> bizType) {
        this.bizType = bizType;
    }

    public String getType(){
        if (nodeType != null) {
            return nodeType.toString();
        }
        return null;
    }

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
