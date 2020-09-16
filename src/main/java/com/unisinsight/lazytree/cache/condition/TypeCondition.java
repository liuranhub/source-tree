package com.unisinsight.lazytree.cache.condition;

import com.unisinsight.lazytree.cache.tree.TreeNode;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum TypeCondition implements Condition{
    CHANNEL_1(6, 1),CHANNEL_4(6, 4),CHANNEL_8(6,8) ;

    private Integer type;
    private Integer subType;

    private static Map<String, TypeCondition> map ;
    static {
        map = new HashMap<>(values().length);
        for (TypeCondition type : values()) {
            map.put("" + type.type + "_" + type.subType, type);
        }
    }


    private TypeCondition(int type){
        this.type = type;
    }

    private TypeCondition(int type, int subType) {
        this(type);
        this.subType = subType;
    }

    @Override
    public boolean accord(TreeNode treeNode) {
        return type == treeNode.getType() && subType == treeNode.getSubType();
    }

    public static List<TypeCondition> get(List<String> keys) {
        if (CollectionUtils.isEmpty(keys)) {
            return new ArrayList<>();
        }
        List<TypeCondition> list = new ArrayList<>(keys.size());
        for (String key : keys) {
            list.add(map.get(key));
        }

        return list;
    }

}
