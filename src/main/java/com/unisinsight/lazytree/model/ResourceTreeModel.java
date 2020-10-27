package com.unisinsight.lazytree.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ResourceTreeModel {

    private List<TreeNode> data;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class TreeNode {
        private int id;
        private int parentId;
        private String resourceName;
        private String type;
        private String subType;
        private String status;
        private String enable;
        private String cascadedId;
        private String resourceCode;
        private int childNum;
        private List<TreeNode> child;
        private String path;
    }
}
