package com.unisinsight.tree.model;

import lombok.Data;

import java.util.List;

@Data
public class TreeNode {
    private String id;
    private String resource;
    private String resourceCode;
    private String resourceName;
    private String status;
    private String parentResource;
    private String type;
    private String subType;
    private String cascadedId;
    private String childNum;
    private String statisticsStatus;
    private String owner;
    private String identify;
    private String orgIndex;
    private List<TreeNode> child;
}
