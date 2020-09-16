package com.unisinsight.lazytree.cache.tree;

import com.unisinsight.lazytree.cache.condition.TypeCondition;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Data
public class OrgTreeNode extends TreeNode{
    private Set<TypeCondition> leafTypes = new HashSet<>();
    public void addLeafTypes(TypeCondition type){
        this.leafTypes.add(type);
    }
}
